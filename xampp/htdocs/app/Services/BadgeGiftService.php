<?php

namespace App\Services;

use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;
use RuntimeException;
use Throwable;

class BadgeGiftService
{
    public const PRICE = 3;

    public function __construct(
        private readonly CreditTransactionService $creditTransactions,
        private readonly CreditRefundService $creditRefunds,
        private readonly EmulatorPresenceService $presence,
        private readonly RconService $rcon,
        private readonly AccountNotificationService $notifications
    ) {
    }

    public function gift(
        int $senderAccountId,
        int $creatorBadgeId,
        int $payerUserId,
        string $recipientUsername,
        string $purchaseId
    ): array {
        $recipientUsername = trim($recipientUsername);

        if ($recipientUsername === '') {
            throw ValidationException::withMessages([
                'recipient_username' =>
                    'Escribe el nombre del personaje que recibirá la placa.',
            ]);
        }

        $badge = $this->creatorBadge(
            $senderAccountId,
            $creatorBadgeId
        );

        $recipient = $this->recipient(
            $recipientUsername
        );

        if (
            $this->recipientHasBadge(
                (int) $recipient->id,
                (string) $badge->badge_code
            )
        ) {
            throw ValidationException::withMessages([
                'recipient_username' =>
                    $recipient->username .
                    ' ya tiene esta placa. No se ha realizado ningún cobro.',
            ]);
        }

        $pending = DB::table('badge_gifts')
            ->where(
                'sender_account_id',
                $senderAccountId
            )
            ->where(
                'creator_badge_id',
                $creatorBadgeId
            )
            ->where(
                'recipient_user_id',
                $recipient->id
            )
            ->where(
                'status',
                'paid_pending_delivery'
            )
            ->orderByDesc('id')
            ->first();

        if ($pending) {
            $delivered = $this->deliver(
                (int) $pending->id
            );

            return [
                'gift_id' => (int) $delivered->id,
                'recipient_username' =>
                    (string) $delivered->recipient_username,
                'charged' => false,
            ];
        }

        $charge = $this->creditTransactions->debitAndRun(
            $senderAccountId,
            $payerUserId,
            self::PRICE,
            'badge_gift',
            $purchaseId,
            [
                'creator_badge_id' => $creatorBadgeId,
                'creator_user_id' =>
                    (int) $badge->creator_user_id,
                'badge_code' =>
                    (string) $badge->badge_code,
                'recipient_account_id' =>
                    (int) $recipient->account_id,
                'recipient_user_id' =>
                    (int) $recipient->id,
            ],
            function () use (
                $senderAccountId,
                $creatorBadgeId,
                $payerUserId,
                $recipient,
                $purchaseId
            ): array {
                $lockedBadge = $this->creatorBadge(
                    $senderAccountId,
                    $creatorBadgeId,
                    true
                );

                $existingGift = DB::table('badge_gifts')
                    ->where(
                        'purchase_id',
                        $purchaseId
                    )
                    ->lockForUpdate()
                    ->first();

                if ($existingGift) {
                    return [
                        'result' => [
                            'gift_id' =>
                                (int) $existingGift->id,
                        ],
                        'metadata' => [
                            'gift_id' =>
                                (int) $existingGift->id,
                            'creator_badge_id' =>
                                $creatorBadgeId,
                            'recipient_user_id' =>
                                (int) $recipient->id,
                        ],
                    ];
                }

                $alreadyOwned = DB::table(
                    'users_badges'
                )
                    ->where(
                        'user_id',
                        $recipient->id
                    )
                    ->where(
                        'badge_code',
                        $lockedBadge->badge_code
                    )
                    ->lockForUpdate()
                    ->first([
                        'id',
                    ]);

                if ($alreadyOwned) {
                    throw ValidationException::withMessages([
                        'recipient_username' =>
                            $recipient->username .
                            ' ya tiene esta placa. No se ha realizado ningún cobro.',
                    ]);
                }

                $refundId = (string) Str::uuid();

                $giftId = DB::table('badge_gifts')
                    ->insertGetId([
                        'purchase_id' => $purchaseId,
                        'refund_id' => $refundId,
                        'sender_account_id' =>
                            $senderAccountId,
                        'creator_badge_id' =>
                            $creatorBadgeId,
                        'creator_user_id' =>
                            (int) $lockedBadge->creator_user_id,
                        'payer_user_id' =>
                            $payerUserId,
                        'recipient_account_id' =>
                            (int) $recipient->account_id,
                        'recipient_user_id' =>
                            (int) $recipient->id,
                        'badge_code' =>
                            (string) $lockedBadge->badge_code,
                        'badge_name' =>
                            (string) $lockedBadge->badge_name,
                        'sender_username' =>
                            (string) $lockedBadge->creator_username,
                        'recipient_username' =>
                            (string) $recipient->username,
                        'amount' => self::PRICE,
                        'status' =>
                            'paid_pending_delivery',
                        'created_at' => now(),
                        'updated_at' => now(),
                    ]);

                return [
                    'result' => [
                        'gift_id' => $giftId,
                    ],
                    'metadata' => [
                        'gift_id' => $giftId,
                        'creator_badge_id' =>
                            $creatorBadgeId,
                        'creator_user_id' =>
                            (int) $lockedBadge->creator_user_id,
                        'badge_code' =>
                            (string) $lockedBadge->badge_code,
                        'recipient_account_id' =>
                            (int) $recipient->account_id,
                        'recipient_user_id' =>
                            (int) $recipient->id,
                    ],
                ];
            }
        );

        $giftId =
            (int) (
                $charge['result']['gift_id'] ??
                0
            );

        if ($giftId <= 0) {
            throw new RuntimeException(
                'El cobro se completó sin identificar el regalo.'
            );
        }

        $delivered = $this->deliver(
            $giftId
        );

        return [
            'gift_id' => (int) $delivered->id,
            'recipient_username' =>
                (string) $delivered->recipient_username,
            'charged' => true,
        ];
    }

    private function creatorBadge(
        int $accountId,
        int $creatorBadgeId,
        bool $lock = false
    ): object {
        $query = DB::table(
            'creator_badges as cb'
        )
            ->join(
                'users as creator',
                'creator.id',
                '=',
                'cb.creator_user_id'
            )
            ->join(
                'account_characters as ac',
                function ($join) use ($accountId) {
                    $join->on(
                        'ac.user_id',
                        '=',
                        'cb.creator_user_id'
                    )
                        ->where(
                            'ac.account_id',
                            '=',
                            $accountId
                        )
                        ->whereNull(
                            'ac.archived_at'
                        );
                }
            )
            ->where(
                'cb.id',
                $creatorBadgeId
            )
            ->where(
                'cb.account_id',
                $accountId
            );

        if ($lock) {
            $query->lockForUpdate();
        }

        $badge = $query->first([
            'cb.id',
            'cb.creator_user_id',
            'cb.badge_code',
            'cb.badge_name',
            'creator.username as creator_username',
        ]);

        if (! $badge) {
            throw ValidationException::withMessages([
                'gift' =>
                    'Esta placa no pertenece a uno de tus personajes activos.',
            ]);
        }

        return $badge;
    }

    private function recipient(
        string $username
    ): object {
        $recipient = DB::table(
            'users as u'
        )
            ->join(
                'account_characters as ac',
                'ac.user_id',
                '=',
                'u.id'
            )
            ->where(
                'u.username',
                $username
            )
            ->whereNull(
                'ac.archived_at'
            )
            ->first([
                'u.id',
                'u.username',
                'u.online',
                'ac.account_id',
            ]);

        if (! $recipient) {
            throw ValidationException::withMessages([
                'recipient_username' =>
                    'No existe un personaje activo con ese nombre.',
            ]);
        }

        return $recipient;
    }

    private function recipientHasBadge(
        int $recipientUserId,
        string $badgeCode
    ): bool {
        return DB::table('users_badges')
            ->where(
                'user_id',
                $recipientUserId
            )
            ->where(
                'badge_code',
                $badgeCode
            )
            ->exists();
    }

    private function deliver(
        int $giftId
    ): object {
        $gift = DB::table('badge_gifts')
            ->where('id', $giftId)
            ->first();

        if (! $gift) {
            throw new RuntimeException(
                'No se encontró el regalo cobrado.'
            );
        }

        if ((string) $gift->status === 'delivered') {
            $this->notifyRecipient($gift);

            return $gift;
        }

        if ((string) $gift->status === 'refunded') {
            throw ValidationException::withMessages([
                'gift' =>
                    'Este regalo no pudo entregarse y ya fue reembolsado.',
            ]);
        }

        if ((string) $gift->status === 'manual_review') {
            throw ValidationException::withMessages([
                'gift' =>
                    'Este regalo requiere revisión administrativa. No repitas el cobro.',
            ]);
        }

        if (
            (string) $gift->status !==
            'paid_pending_delivery'
        ) {
            throw new RuntimeException(
                'El regalo tiene un estado no reconocido.'
            );
        }

        if (
            $this->recipientHasBadge(
                (int) $gift->recipient_user_id,
                (string) $gift->badge_code
            )
        ) {
            return $this->markDelivered(
                $gift
            );
        }

        $recipient = DB::table('users')
            ->where(
                'id',
                $gift->recipient_user_id
            )
            ->first([
                'id',
                'username',
                'online',
            ]);

        if (! $recipient) {
            return $this->refundDeliveryFailure(
                $gift,
                new RuntimeException(
                    'El personaje destinatario ya no existe.'
                )
            );
        }

        try {
            $effectiveOnline =
                $this->presence->effectiveOnlineState(
                    (int) $recipient->id,
                    $recipient->online
                );

            $granted = false;

            if (! $effectiveOnline) {
                $granted =
                    $this->grantOfflineIfStillOffline(
                        (int) $recipient->id,
                        (string) $gift->badge_code
                    );
            }

            if (! $granted) {
                $recipient = DB::table('users')
                    ->where(
                        'id',
                        $gift->recipient_user_id
                    )
                    ->first([
                        'id',
                        'username',
                        'online',
                    ]);

                if (! $recipient) {
                    throw new RuntimeException(
                        'El personaje destinatario ya no existe.'
                    );
                }

                $this->rcon->giveBadge(
                    $recipient,
                    (string) $gift->badge_code
                );
            }

            if (
                ! $this->recipientHasBadge(
                    (int) $gift->recipient_user_id,
                    (string) $gift->badge_code
                )
            ) {
                throw new RuntimeException(
                    'No se confirmó la placa en users_badges.'
                );
            }
        } catch (Throwable $deliveryException) {
            if (
                $this->recipientHasBadge(
                    (int) $gift->recipient_user_id,
                    (string) $gift->badge_code
                )
            ) {
                return $this->markDelivered(
                    $gift
                );
            }

            return $this->refundDeliveryFailure(
                $gift,
                $deliveryException
            );
        }

        return $this->markDelivered(
            $gift
        );
    }

    private function grantOfflineIfStillOffline(
        int $userId,
        string $badgeCode
    ): bool {
        return DB::transaction(
            function () use (
                $userId,
                $badgeCode
            ): bool {
                $user = DB::table('users')
                    ->where('id', $userId)
                    ->lockForUpdate()
                    ->first([
                        'id',
                        'online',
                    ]);

                if (! $user) {
                    throw new RuntimeException(
                        'El personaje destinatario ya no existe.'
                    );
                }

                if ((string) $user->online !== '0') {
                    return false;
                }

                $alreadyOwned = DB::table(
                    'users_badges'
                )
                    ->where('user_id', $userId)
                    ->where(
                        'badge_code',
                        $badgeCode
                    )
                    ->exists();

                if (! $alreadyOwned) {
                    DB::table('users_badges')
                        ->insert([
                            'user_id' => $userId,
                            'slot_id' => 0,
                            'badge_code' => $badgeCode,
                        ]);
                }

                return true;
            },
            5
        );
    }

    private function markDelivered(
        object $gift
    ): object {
        DB::table('badge_gifts')
            ->where('id', $gift->id)
            ->where(
                'status',
                'paid_pending_delivery'
            )
            ->update([
                'status' => 'delivered',
                'delivered_at' => now(),
                'error_message' => null,
                'updated_at' => now(),
            ]);

        $current = DB::table('badge_gifts')
            ->where('id', $gift->id)
            ->first();

        if (! $current) {
            throw new RuntimeException(
                'No se pudo releer el regalo entregado.'
            );
        }

        if (
            (string) $current->status !==
            'delivered'
        ) {
            throw new RuntimeException(
                'No se pudo confirmar el estado final del regalo.'
            );
        }

        $this->notifyRecipient(
            $current
        );

        return $current;
    }

    private function notifyRecipient(
        object $gift
    ): void {
        try {
            $this->notifications->send(
                (int) $gift->recipient_account_id,
                'badge.gift_received',
                'Has recibido una placa',
                $gift->sender_username .
                    ' ha regalado la placa "' .
                    $gift->badge_name .
                    '" a tu personaje ' .
                    $gift->recipient_username .
                    '.',
                null,
                [
                    'badge_gift_id' =>
                        (int) $gift->id,
                    'badge_code' =>
                        (string) $gift->badge_code,
                    'sender_user_id' =>
                        (int) $gift->creator_user_id,
                    'recipient_user_id' =>
                        (int) $gift->recipient_user_id,
                ],
                'badge-gift:' .
                    $gift->id .
                    ':received'
            );
        } catch (Throwable $exception) {
            report($exception);
        }
    }

    private function refundDeliveryFailure(
        object $gift,
        Throwable $deliveryException
    ): never {
        try {
            $this->creditRefunds->refund(
                (string) $gift->refund_id,
                (string) $gift->purchase_id,
                (int) $gift->amount,
                'badge_gift_delivery_failed',
                [
                    'badge_gift_id' =>
                        (int) $gift->id,
                    'badge_code' =>
                        (string) $gift->badge_code,
                    'recipient_user_id' =>
                        (int) $gift->recipient_user_id,
                    'delivery_error' =>
                        mb_substr(
                            $deliveryException->getMessage(),
                            0,
                            500
                        ),
                ]
            );
        } catch (Throwable $refundException) {
            DB::table('badge_gifts')
                ->where('id', $gift->id)
                ->update([
                    'status' => 'manual_review',
                    'error_message' =>
                        mb_substr(
                            'Entrega: ' .
                            $deliveryException->getMessage() .
                            ' | Reembolso: ' .
                            $refundException->getMessage(),
                            0,
                            2000
                        ),
                    'updated_at' => now(),
                ]);

            report($deliveryException);
            report($refundException);

            throw ValidationException::withMessages([
                'gift' =>
                    'La entrega falló y el reembolso no pudo confirmarse. No repitas la operación y contacta con un administrador.',
            ]);
        }

        DB::table('badge_gifts')
            ->where('id', $gift->id)
            ->update([
                'status' => 'refunded',
                'refunded_at' => now(),
                'error_message' =>
                    mb_substr(
                        $deliveryException->getMessage(),
                        0,
                        2000
                    ),
                'updated_at' => now(),
            ]);

        report($deliveryException);

        throw ValidationException::withMessages([
            'gift' =>
                'No se pudo entregar la placa. Los ' .
                self::PRICE .
                ' créditos se han devuelto automáticamente.',
        ]);
    }
}
