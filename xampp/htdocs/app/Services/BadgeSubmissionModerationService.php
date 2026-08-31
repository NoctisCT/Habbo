<?php

namespace App\Services;

use App\Exceptions\CreditRefundException;
use App\Models\BadgeSubmission;
use App\Models\CreatorBadge;
use App\Models\User;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;
use RuntimeException;
use Throwable;

class BadgeSubmissionModerationService
{
    private const PRICE = 10;

    public function __construct(
        private readonly CreditRefundService $refunds,
        private readonly RconService $rcon,
        private readonly AccountNotificationService $notifications
    ) {
    }

    public function approve(
        BadgeSubmission $submission,
        User $moderator
    ): array {
        $this->assertModerator($moderator);

        return DB::transaction(function () use (
            $submission,
            $moderator
        ) {
            $locked = BadgeSubmission::query()
                ->whereKey($submission->getKey())
                ->lockForUpdate()
                ->firstOrFail();

            if ($locked->status === 'approved') {
                return [
                    'already_completed' => true,
                    'badge_code' => (string) $locked->badge_code,
                ];
            }

            if ($locked->status !== 'pending') {
                throw new RuntimeException(
                    'Esta solicitud ya no está pendiente y no puede aprobarse.'
                );
            }

            $creator = User::query()
                ->whereKey($locked->creator_user_id)
                ->first();

            if (! $creator) {
                throw new RuntimeException(
                    'No existe el personaje creador de la placa.'
                );
            }

            if (
                ! Storage::disk('local')->exists(
                    (string) $locked->processed_path
                )
            ) {
                throw new RuntimeException(
                    'No se encuentra el GIF procesado de la solicitud.'
                );
            }

            $gif = Storage::disk('local')->get(
                (string) $locked->processed_path
            );

            $size = @getimagesizefromstring($gif);

            if (
                ! is_array($size) ||
                (int) ($size[0] ?? 0) !== 40 ||
                (int) ($size[1] ?? 0) !== 40 ||
                (string) ($size['mime'] ?? '') !== 'image/gif'
            ) {
                throw new RuntimeException(
                    'El archivo final no es un GIF válido de 40×40.'
                );
            }

            $badgeCode = $this->resolveBadgeCode(
                $locked,
                $gif
            );

            $this->publishBadgeImage(
                $badgeCode,
                $gif
            );

            /*
             * website_badges es la fuente durable que ya utiliza
             * el Badge Editor existente del hotel.
             */
            $this->syncWebsiteBadge(
                $badgeCode,
                (string) $locked->badge_name,
                (string) $locked->badge_description
            );

            /*
             * Compatibilidad con la antigua BadgePage:
             * si algún día vuelve a existir ExternalTextsParser,
             * también sincronizamos sus ficheros. Su ausencia NO
             * bloquea la nueva moderación.
             */
            $this->syncLegacyExternalTextsIfAvailable(
                $badgeCode,
                (string) $locked->badge_name,
                (string) $locked->badge_description
            );

            if (! $this->rcon->isConnected()) {
                throw new RuntimeException(
                    'No hay conexión RCON con el emulador. La aprobación no se ha cerrado; puedes reintentarlo cuando el emulador esté disponible.'
                );
            }

            $sent = $this->rcon->sendCommand(
                'givebadge',
                [
                    'user_id' => (int) $creator->id,
                    'badge' => $badgeCode,
                ]
            );

            if ($sent !== true) {
                throw new RuntimeException(
                    'El emulador no aceptó la entrega de la placa. La solicitud sigue pendiente para poder reintentarla.'
                );
            }

            CreatorBadge::query()->firstOrCreate(
                [
                    'badge_submission_id' => (int) $locked->id,
                ],
                [
                    'account_id' => (int) $locked->account_id,
                    'creator_user_id' => (int) $locked->creator_user_id,
                    'badge_code' => $badgeCode,
                    'badge_name' => (string) $locked->badge_name,
                    'badge_description' =>
                        (string) $locked->badge_description,
                    'image_path' =>
                        $this->badgePublicPath($badgeCode),
                    'marketplace_enabled' => false,
                    'created_at' => now(),
                    'updated_at' => null,
                ]
            );

            $locked->forceFill([
                'status' => 'approved',
                'badge_code' => $badgeCode,
                'moderator_user_id' => (int) $moderator->id,
                'moderation_reason' => null,
                'approved_at' => now(),
                'rejected_at' => null,
            ])->save();

            $this->notifications->send(
                (int) $locked->account_id,
                'badge.approved',
                'Placa aprobada',
                'Tu placa "' .
                    (string) $locked->badge_name .
                    '" ha sido aprobada y añadida a tu inventario.',
                route(
                    'marketplace.badges.index',
                    [],
                    false
                ),
                [
                    'badge_submission_id' =>
                        (int) $locked->id,
                    'badge_code' => $badgeCode,
                    'creator_user_id' =>
                        (int) $locked->creator_user_id,
                ],
                'badge-submission:' .
                    (int) $locked->id .
                    ':approved'
            );

            return [
                'already_completed' => false,
                'badge_code' => $badgeCode,
            ];
        }, 3);
    }

    public function reject(
        BadgeSubmission $submission,
        User $moderator,
        string $reason
    ): array {
        $this->assertModerator($moderator);

        $reason = trim($reason);

        if (
            mb_strlen($reason) < 3 ||
            mb_strlen($reason) > 500
        ) {
            throw new RuntimeException(
                'El motivo debe tener entre 3 y 500 caracteres.'
            );
        }

        $claim = DB::transaction(function () use (
            $submission,
            $moderator,
            $reason
        ) {
            $locked = BadgeSubmission::query()
                ->whereKey($submission->getKey())
                ->lockForUpdate()
                ->firstOrFail();

            if ($locked->status === 'rejected') {
                return [
                    'already_completed' => true,
                    'refund_id' => (string) $locked->refund_id,
                    'purchase_id' => (string) $locked->purchase_id,
                    'reason' =>
                        (string) $locked->moderation_reason,
                    'submission_id' => (int) $locked->id,
                ];
            }

            if (
                ! in_array(
                    $locked->status,
                    ['pending', 'rejecting'],
                    true
                )
            ) {
                throw new RuntimeException(
                    'Esta solicitud no puede rechazarse en su estado actual.'
                );
            }

            if ($locked->status === 'rejecting') {
                return [
                    'already_completed' => false,
                    'refund_id' => (string) $locked->refund_id,
                    'purchase_id' => (string) $locked->purchase_id,
                    'reason' =>
                        (string) $locked->moderation_reason,
                    'submission_id' => (int) $locked->id,
                ];
            }

            $refundId = (string) (
                $locked->refund_id ?: Str::uuid()
            );

            $locked->forceFill([
                'status' => 'rejecting',
                'refund_id' => $refundId,
                'moderator_user_id' => (int) $moderator->id,
                'moderation_reason' => $reason,
                'approved_at' => null,
            ])->save();

            return [
                'already_completed' => false,
                'refund_id' => $refundId,
                'purchase_id' => (string) $locked->purchase_id,
                'reason' => $reason,
                'submission_id' => (int) $locked->id,
            ];
        }, 3);

        if ($claim['already_completed']) {
            return $claim;
        }

        $refundReason = Str::limit(
            'Rechazo de placa: ' . $claim['reason'],
            500,
            ''
        );

        try {
            $this->refunds->refund(
                $claim['refund_id'],
                $claim['purchase_id'],
                self::PRICE,
                $refundReason,
                [
                    'source' =>
                        'badge_submission_rejection',
                    'badge_submission_id' =>
                        $claim['submission_id'],
                    'moderator_user_id' =>
                        (int) $moderator->id,
                    'price' => self::PRICE,
                ]
            );
        } catch (CreditRefundException $exception) {
            throw new RuntimeException(
                'No se pudo completar el reembolso: ' .
                $exception->getMessage(),
                0,
                $exception
            );
        }

        DB::transaction(function () use ($claim) {
            $locked = BadgeSubmission::query()
                ->whereKey($claim['submission_id'])
                ->lockForUpdate()
                ->firstOrFail();

            if ($locked->status === 'rejected') {
                return;
            }

            if ($locked->status !== 'rejecting') {
                throw new RuntimeException(
                    'El reembolso se completó, pero la solicitud cambió de estado. Revisión manual necesaria.'
                );
            }

            $locked->forceFill([
                'status' => 'rejected',
                'rejected_at' => now(),
                'approved_at' => null,
            ])->save();

            $this->notifications->send(
                (int) $locked->account_id,
                'badge.rejected',
                'Placa rechazada',
                'Tu placa "' .
                    (string) $locked->badge_name .
                    '" ha sido rechazada. Motivo: ' .
                    (string) $locked->moderation_reason .
                    '. Se te han devuelto ' .
                    self::PRICE .
                    ' créditos.',
                route(
                    'marketplace.badges.index',
                    [],
                    false
                ),
                [
                    'badge_submission_id' =>
                        (int) $locked->id,
                    'refund_id' =>
                        (string) $locked->refund_id,
                    'creator_user_id' =>
                        (int) $locked->creator_user_id,
                    'amount' => self::PRICE,
                ],
                'badge-submission:' .
                    (int) $locked->id .
                    ':rejected'
            );
        }, 3);

        return $claim;
    }

    public function retryReject(
        BadgeSubmission $submission,
        User $moderator
    ): array {
        $fresh = BadgeSubmission::query()
            ->whereKey($submission->getKey())
            ->firstOrFail();

        if ($fresh->status !== 'rejecting') {
            throw new RuntimeException(
                'Esta solicitud no tiene un reembolso pendiente.'
            );
        }

        return $this->reject(
            $fresh,
            $moderator,
            (string) $fresh->moderation_reason
        );
    }

    private function assertModerator(User $moderator): void
    {
        if ((int) $moderator->rank < 6) {
            throw new RuntimeException(
                'Se requiere rango 6 o superior para moderar placas.'
            );
        }
    }

    private function resolveBadgeCode(
        BadgeSubmission $submission,
        string $gif
    ): string {
        if (! empty($submission->badge_code)) {
            return strtoupper(
                (string) $submission->badge_code
            );
        }

        $base = 'USRBDG' . str_pad(
            strtoupper(
                base_convert(
                    (string) $submission->id,
                    10,
                    36
                )
            ),
            8,
            '0',
            STR_PAD_LEFT
        );

        $fallback = $base . substr(
            strtoupper(
                sha1(
                    (string) $submission->purchase_id
                )
            ),
            0,
            6
        );

        foreach ([$base, $fallback] as $candidate) {
            $dbCollision =
                BadgeSubmission::query()
                    ->where('badge_code', $candidate)
                    ->where('id', '!=', $submission->id)
                    ->exists()
                ||
                CreatorBadge::query()
                    ->where('badge_code', $candidate)
                    ->where(
                        'badge_submission_id',
                        '!=',
                        $submission->id
                    )
                    ->exists();

            if ($dbCollision) {
                continue;
            }

            $target =
                $this->badgeAssetPath($candidate);

            if (is_file($target)) {
                $existing =
                    @file_get_contents($target);

                if (
                    ! is_string($existing) ||
                    ! hash_equals(
                        hash('sha256', $gif),
                        hash(
                            'sha256',
                            $existing
                        )
                    )
                ) {
                    continue;
                }
            }

            return $candidate;
        }

        throw new RuntimeException(
            'No se pudo reservar un código único para la placa.'
        );
    }

    private function publishBadgeImage(
        string $badgeCode,
        string $gif
    ): void {
        $target =
            $this->badgeAssetPath($badgeCode);

        $directory = dirname($target);

        if (
            ! is_dir($directory) &&
            ! @mkdir(
                $directory,
                0775,
                true
            ) &&
            ! is_dir($directory)
        ) {
            throw new RuntimeException(
                'No se pudo crear el directorio de placas.'
            );
        }

        $temporary =
            $target .
            '.tmp.' .
            Str::uuid();

        if (
            @file_put_contents(
                $temporary,
                $gif
            ) === false
        ) {
            throw new RuntimeException(
                'No se pudo escribir el archivo temporal de la placa.'
            );
        }

        try {
            if (! @rename($temporary, $target)) {
                if (
                    ! @copy(
                        $temporary,
                        $target
                    )
                ) {
                    throw new RuntimeException(
                        'No se pudo publicar el GIF de la placa.'
                    );
                }

                @unlink($temporary);
            }
        } finally {
            if (is_file($temporary)) {
                @unlink($temporary);
            }
        }
    }

    private function syncWebsiteBadge(
        string $badgeCode,
        string $name,
        string $description
    ): void {
        if (! Schema::hasTable('website_badges')) {
            throw new RuntimeException(
                'No existe website_badges; no se pueden registrar los textos de la placa.'
            );
        }

        $data = [
            'badge_name' => $name,
            'badge_description' => $description,
        ];

        if (
            Schema::hasColumn(
                'website_badges',
                'updated_at'
            )
        ) {
            $data['updated_at'] = now();
        }

        $key =
            'badge_desc_' .
            $badgeCode;

        $exists = DB::table('website_badges')
            ->where('badge_key', $key)
            ->exists();

        if ($exists) {
            DB::table('website_badges')
                ->where('badge_key', $key)
                ->update($data);

            return;
        }

        $data['badge_key'] = $key;

        if (
            Schema::hasColumn(
                'website_badges',
                'created_at'
            )
        ) {
            $data['created_at'] = now();
        }

        DB::table('website_badges')
            ->insert($data);
    }

        private function syncLegacyExternalTextsIfAvailable(
        string $badgeCode,
        string $name,
        string $description
    ): void {
        $class =
            'App\\Services\\Parsers\\ExternalTextsParser';

        if (! class_exists($class)) {
            return;
        }

        try {
            $parser = app($class);

            if (
                method_exists(
                    $parser,
                    'updateNitroBadgeTexts'
                )
            ) {
                $parser->updateNitroBadgeTexts(
                    $badgeCode,
                    $name,
                    $description
                );
            }

            if (
                method_exists(
                    $parser,
                    'updateFlashBadgeTexts'
                )
            ) {
                $parser->updateFlashBadgeTexts(
                    $badgeCode,
                    $name,
                    $description
                );
            }
        } catch (Throwable $exception) {
            /*
             * La fuente durable ya se ha sincronizado en
             * website_badges. El parser legacy no debe tumbar
             * la nueva moderación.
             */
            report($exception);
        }
    }

    private function badgeAssetPath(
        string $badgeCode
    ): string {
        $configured =
            trim(
                (string) setting('badges_path')
            );

        if ($configured === '') {
            $configured =
                '/gamedata/c_images/album1584/';
        }

        $urlPath =
            parse_url(
                $configured,
                PHP_URL_PATH
            );

        $relative =
            trim(
                str_replace(
                    '\\',
                    '/',
                    is_string($urlPath)
                        ? $urlPath
                        : $configured
                ),
                '/'
            );

        if ($relative === '') {
            $relative =
                'gamedata/c_images/album1584';
        }

        return public_path(
            $relative .
            '/' .
            $badgeCode .
            '.gif'
        );
    }

    private function badgePublicPath(
        string $badgeCode
    ): string {
        $base =
            rtrim(
                (string) setting(
                    'badges_path'
                ),
                '/'
            );

        if ($base === '') {
            $base =
                '/gamedata/c_images/album1584';
        }

        return
            $base .
            '/' .
            $badgeCode .
            '.gif';
    }
}
