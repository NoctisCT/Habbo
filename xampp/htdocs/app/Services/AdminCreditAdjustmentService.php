<?php

namespace App\Services;

use App\Exceptions\AdminCreditAdjustmentException;
use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;
use Throwable;

class AdminCreditAdjustmentService
{
    private const LEASE_SECONDS = 300;

    public function __construct(
        private readonly CreditBridgeClient $bridge
    ) {
    }

    public function adjust(
        int $actorUserId,
        int $targetUserId,
        int $delta,
        string $reason,
        string $adjustmentId
    ): array {
        $reason = trim($reason);

        $this->validateInput(
            $delta,
            $reason,
            $adjustmentId
        );

        $this->assertMaximumAdmin(
            $actorUserId
        );

        $target = DB::table('users')
            ->where('id', $targetUserId)
            ->first([
                'id',
                'username',
                'credits',
                'online',
            ]);

        if (! $target) {
            throw new AdminCreditAdjustmentException(
                'target_not_found',
                'No se encontro el personaje objetivo.'
            );
        }

        $accountId = DB::table('account_characters')
            ->where('user_id', $targetUserId)
            ->value('account_id');

        if (! $accountId) {
            throw new AdminCreditAdjustmentException(
                'target_without_account',
                'El personaje objetivo no pertenece a ninguna cuenta.'
            );
        }

        $fingerprint = $this->fingerprint(
            (int) $accountId,
            $actorUserId,
            $targetUserId,
            $delta,
            $reason
        );

        $claim = $this->claimAdjustment(
            $adjustmentId,
            (int) $accountId,
            $actorUserId,
            $targetUserId,
            $delta,
            $reason,
            $fingerprint
        );

        if ($claim['completed']) {
            return $claim['result'];
        }

        $attemptToken = $claim['attempt_token'];

        $recovered = $this->recoverBridgeAdjustment(
            (int) $accountId,
            $actorUserId,
            $targetUserId,
            $delta,
            $reason,
            $adjustmentId,
            $attemptToken
        );

        if ($recovered !== null) {
            return $recovered;
        }

        $target = DB::table('users')
            ->where('id', $targetUserId)
            ->first([
                'id',
                'credits',
                'online',
            ]);

        if (! $target) {
            throw new AdminCreditAdjustmentException(
                'target_not_found',
                'No se encontro el personaje objetivo.'
            );
        }

        if ((string) $target->online === '0') {
            try {
                return $this->adjustOffline(
                    (int) $accountId,
                    $actorUserId,
                    $targetUserId,
                    $delta,
                    $reason,
                    $adjustmentId,
                    $attemptToken
                );
            } catch (AdminCreditAdjustmentException $exception) {
                if (
                    $exception->errorCode !==
                    'target_became_online'
                ) {
                    throw $exception;
                }
            }
        }

        return $this->adjustOnline(
            (int) $accountId,
            $actorUserId,
            $targetUserId,
            $delta,
            $reason,
            $adjustmentId,
            $attemptToken
        );
    }

    private function validateInput(
        int $delta,
        string $reason,
        string $adjustmentId
    ): void {
        if ($delta === 0) {
            throw new AdminCreditAdjustmentException(
                'invalid_delta',
                'El ajuste no puede ser cero.'
            );
        }

        if (
            mb_strlen($reason) < 3 ||
            mb_strlen($reason) > 500
        ) {
            throw new AdminCreditAdjustmentException(
                'invalid_reason',
                'El motivo debe tener entre 3 y 500 caracteres.'
            );
        }

        if (! $this->validUuid($adjustmentId)) {
            throw new AdminCreditAdjustmentException(
                'invalid_adjustment_id',
                'El identificador del ajuste no es valido.'
            );
        }
    }

    private function claimAdjustment(
        string $adjustmentId,
        int $accountId,
        int $actorUserId,
        int $targetUserId,
        int $delta,
        string $reason,
        string $fingerprint
    ): array {
        $attemptToken = (string) Str::uuid();

        $inserted = DB::table(
            'admin_credit_adjustments'
        )->insertOrIgnore([
            'id' => $adjustmentId,
            'account_id' => $accountId,
            'actor_user_id' => $actorUserId,
            'target_user_id' => $targetUserId,
            'delta' => $delta,
            'reason' => $reason,
            'fingerprint' => $fingerprint,
            'status' => 'processing',
            'attempt_token' => $attemptToken,
            'lease_expires_at' =>
                now()->addSeconds(self::LEASE_SECONDS),
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        return DB::transaction(function () use (
            $adjustmentId,
            $fingerprint,
            $attemptToken,
            $inserted
        ) {
            $row = DB::table(
                'admin_credit_adjustments'
            )
                ->where('id', $adjustmentId)
                ->lockForUpdate()
                ->first();

            if (! $row) {
                throw new AdminCreditAdjustmentException(
                    'adjustment_missing',
                    'No se pudo crear el ajuste administrativo.'
                );
            }

            if (
                ! hash_equals(
                    (string) $row->fingerprint,
                    $fingerprint
                )
            ) {
                throw new AdminCreditAdjustmentException(
                    'adjustment_conflict',
                    'Este identificador ya fue usado con otros datos.'
                );
            }

            if ((string) $row->status === 'completed') {
                return [
                    'completed' => true,
                    'result' => $this->decodeResult(
                        $row->result_json
                    ),
                ];
            }

            if ((string) $row->status === 'manual_review') {
                throw new AdminCreditAdjustmentException(
                    'adjustment_manual_review',
                    'Este ajuste requiere revision manual.'
                );
            }

            if ((string) $row->status === 'failed') {
                throw new AdminCreditAdjustmentException(
                    'adjustment_failed',
                    'Este ajuste fue marcado como fallido.'
                );
            }

            if ((string) $row->status !== 'processing') {
                throw new AdminCreditAdjustmentException(
                    'invalid_adjustment_status',
                    'El ajuste tiene un estado no reconocido.'
                );
            }

            if (
                $inserted === 1 &&
                (string) $row->attempt_token ===
                    $attemptToken
            ) {
                return [
                    'completed' => false,
                    'attempt_token' => $attemptToken,
                ];
            }

            $leaseActive = false;

            if ($row->lease_expires_at) {
                $leaseActive = Carbon::parse(
                    $row->lease_expires_at
                )->isFuture();
            }

            if ($leaseActive) {
                throw new AdminCreditAdjustmentException(
                    'adjustment_in_progress',
                    'Este ajuste ya se esta procesando.'
                );
            }

            DB::table('admin_credit_adjustments')
                ->where('id', $adjustmentId)
                ->update([
                    'attempt_token' => $attemptToken,
                    'lease_expires_at' =>
                        now()->addSeconds(
                            self::LEASE_SECONDS
                        ),
                    'error_code' => null,
                    'updated_at' => now(),
                ]);

            return [
                'completed' => false,
                'attempt_token' => $attemptToken,
            ];
        }, 5);
    }

    private function recoverBridgeAdjustment(
        int $accountId,
        int $actorUserId,
        int $targetUserId,
        int $delta,
        string $reason,
        string $adjustmentId,
        string $attemptToken
    ): ?array {
        $bridge = DB::table(
            'credit_bridge_transactions'
        )
            ->where(
                'transaction_id',
                $adjustmentId
            )
            ->first();

        if (! $bridge) {
            return null;
        }

        $this->assertBridgeRecord(
            $bridge,
            $targetUserId,
            $delta
        );

        if ((string) $bridge->status === 'pending') {
            $this->markManualReview(
                $adjustmentId,
                $attemptToken,
                'bridge_pending'
            );

            throw new AdminCreditAdjustmentException(
                'adjustment_manual_review',
                'CreditBridge dejo el ajuste pendiente.'
            );
        }

        if ((string) $bridge->status !== 'applied') {
            $this->markManualReview(
                $adjustmentId,
                $attemptToken,
                'bridge_invalid_status'
            );

            throw new AdminCreditAdjustmentException(
                'adjustment_manual_review',
                'CreditBridge tiene un estado inesperado.'
            );
        }

        return $this->finalizeBridgeAdjustment(
            $accountId,
            $actorUserId,
            $targetUserId,
            $delta,
            $reason,
            $adjustmentId,
            $attemptToken,
            $bridge
        );
    }

    private function adjustOffline(
        int $accountId,
        int $actorUserId,
        int $targetUserId,
        int $delta,
        string $reason,
        string $adjustmentId,
        string $attemptToken
    ): array {
        return DB::transaction(function () use (
            $accountId,
            $actorUserId,
            $targetUserId,
            $delta,
            $reason,
            $adjustmentId,
            $attemptToken
        ) {
            $adjustment = $this->lockAdjustment(
                $adjustmentId
            );

            if (
                (string) $adjustment->status ===
                'completed'
            ) {
                return $this->decodeResult(
                    $adjustment->result_json
                );
            }

            $this->assertAttempt(
                $adjustment,
                $attemptToken
            );

            $this->assertMaximumAdmin(
                $actorUserId,
                true
            );

            $target = DB::table('users')
                ->where('id', $targetUserId)
                ->lockForUpdate()
                ->first([
                    'id',
                    'credits',
                    'online',
                ]);

            if (! $target) {
                throw new AdminCreditAdjustmentException(
                    'target_not_found',
                    'No se encontro el personaje objetivo.'
                );
            }

            if ((string) $target->online !== '0') {
                throw new AdminCreditAdjustmentException(
                    'target_became_online',
                    'El personaje se conecto durante el ajuste.'
                );
            }

            $before = (int) $target->credits;
            $after = $before + $delta;

            $this->assertValidBalance(
                $after
            );

            DB::table('users')
                ->where('id', $targetUserId)
                ->update([
                    'credits' => $after,
                ]);

            $result = $this->buildResult(
                $adjustmentId,
                $accountId,
                $actorUserId,
                $targetUserId,
                $delta,
                $reason,
                $before,
                $after,
                'offline_db'
            );

            $this->writeLedger(
                $accountId,
                $actorUserId,
                $targetUserId,
                $delta,
                $reason,
                $adjustmentId,
                $before,
                $after,
                'offline_db'
            );

            $this->completeAdjustment(
                $adjustmentId,
                $attemptToken,
                $before,
                $after,
                null,
                $result
            );

            return $result;
        }, 5);
    }

    private function adjustOnline(
        int $accountId,
        int $actorUserId,
        int $targetUserId,
        int $delta,
        string $reason,
        string $adjustmentId,
        string $attemptToken
    ): array {
        $this->assertMaximumAdmin(
            $actorUserId
        );

        DB::table('admin_credit_adjustments')
            ->where('id', $adjustmentId)
            ->where(
                'attempt_token',
                $attemptToken
            )
            ->update([
                'bridge_transaction_id' =>
                    $adjustmentId,
                'updated_at' => now(),
            ]);

        try {
            if ($delta > 0) {
                $response = $this->bridge->credit(
                    $targetUserId,
                    $delta,
                    $adjustmentId
                );
            } else {
                $response = $this->bridge->debit(
                    $targetUserId,
                    abs($delta),
                    $adjustmentId
                );
            }
        } catch (Throwable $exception) {
            $bridge = DB::table(
                'credit_bridge_transactions'
            )
                ->where(
                    'transaction_id',
                    $adjustmentId
                )
                ->first();

            if ($bridge) {
                $this->assertBridgeRecord(
                    $bridge,
                    $targetUserId,
                    $delta
                );

                if (
                    (string) $bridge->status ===
                    'applied'
                ) {
                    return $this->finalizeBridgeAdjustment(
                        $accountId,
                        $actorUserId,
                        $targetUserId,
                        $delta,
                        $reason,
                        $adjustmentId,
                        $attemptToken,
                        $bridge
                    );
                }

                $this->markManualReview(
                    $adjustmentId,
                    $attemptToken,
                    'bridge_transport_pending'
                );

                throw new AdminCreditAdjustmentException(
                    'adjustment_manual_review',
                    'El estado del ajuste en CreditBridge es incierto.',
                    $exception
                );
            }

            $this->releaseForRetry(
                $adjustmentId,
                $attemptToken,
                'bridge_transport_error'
            );

            throw new AdminCreditAdjustmentException(
                'bridge_transport_error',
                'No se pudo comunicar con CreditBridge.',
                $exception
            );
        }

        $bridge = DB::table(
            'credit_bridge_transactions'
        )
            ->where(
                'transaction_id',
                $adjustmentId
            )
            ->first();

        if (
            (int) ($response['status'] ?? -1) !== 0
        ) {
            if (! $bridge) {
                $targetOnline = DB::table('users')
                    ->where('id', $targetUserId)
                    ->value('online');

                if (
                    (string) $targetOnline === '0'
                ) {
                    return $this->adjustOffline(
                        $accountId,
                        $actorUserId,
                        $targetUserId,
                        $delta,
                        $reason,
                        $adjustmentId,
                        $attemptToken
                    );
                }

                $this->releaseForRetry(
                    $adjustmentId,
                    $attemptToken,
                    'bridge_rejected'
                );

                throw new AdminCreditAdjustmentException(
                    'bridge_rejected',
                    'CreditBridge rechazo el ajuste.'
                );
            }
        }

        if (! $bridge) {
            $this->markManualReview(
                $adjustmentId,
                $attemptToken,
                'bridge_journal_missing'
            );

            throw new AdminCreditAdjustmentException(
                'adjustment_manual_review',
                'CreditBridge respondio sin registrar la operacion.'
            );
        }

        $this->assertBridgeRecord(
            $bridge,
            $targetUserId,
            $delta
        );

        if ((string) $bridge->status !== 'applied') {
            $this->markManualReview(
                $adjustmentId,
                $attemptToken,
                'bridge_not_applied'
            );

            throw new AdminCreditAdjustmentException(
                'adjustment_manual_review',
                'CreditBridge no confirmo el ajuste.'
            );
        }

        return $this->finalizeBridgeAdjustment(
            $accountId,
            $actorUserId,
            $targetUserId,
            $delta,
            $reason,
            $adjustmentId,
            $attemptToken,
            $bridge
        );
    }

    private function finalizeBridgeAdjustment(
        int $accountId,
        int $actorUserId,
        int $targetUserId,
        int $delta,
        string $reason,
        string $adjustmentId,
        string $attemptToken,
        object $bridge
    ): array {
        $this->assertBridgeRecord(
            $bridge,
            $targetUserId,
            $delta
        );

        return DB::transaction(function () use (
                $accountId,
                $actorUserId,
                $targetUserId,
                $delta,
                $reason,
                $adjustmentId,
                $attemptToken,
                $bridge
            ) {
                $adjustment = $this->lockAdjustment(
                    $adjustmentId
                );

                if (
                    (string) $adjustment->status ===
                    'completed'
                ) {
                    return $this->decodeResult(
                        $adjustment->result_json
                    );
                }

                $this->assertAttempt(
                    $adjustment,
                    $attemptToken
                );

                $this->assertMaximumAdmin(
                    $actorUserId,
                    true
                );

                $target = DB::table('users')
                    ->where('id', $targetUserId)
                    ->lockForUpdate()
                    ->first([
                        'id',
                        'credits',
                    ]);

                if (! $target) {
                    throw new AdminCreditAdjustmentException(
                        'target_not_found',
                        'No se encontro el personaje objetivo.'
                    );
                }

                $before = (int) $bridge->balance_before;
                $after = (int) $bridge->balance_after;


                $result = $this->buildResult(
                    $adjustmentId,
                    $accountId,
                    $actorUserId,
                    $targetUserId,
                    $delta,
                    $reason,
                    $before,
                    $after,
                    'online_credit_bridge'
                );

                $this->writeLedger(
                    $accountId,
                    $actorUserId,
                    $targetUserId,
                    $delta,
                    $reason,
                    $adjustmentId,
                    $before,
                    $after,
                    'online_credit_bridge'
                );

                $this->completeAdjustment(
                    $adjustmentId,
                    $attemptToken,
                    $before,
                    $after,
                    $adjustmentId,
                    $result
                );

                return $result;
        }, 5);
    }

    private function assertBridgeRecord(
        object $bridge,
        int $targetUserId,
        int $delta
    ): void {
        $expectedOperation =
            $delta > 0 ? 'credit' : 'debit';

        $amount = abs($delta);

        if (
            (int) $bridge->user_id !==
                $targetUserId ||
            (int) $bridge->amount !==
                $amount ||
            (string) $bridge->operation !==
                $expectedOperation
        ) {
            throw new AdminCreditAdjustmentException(
                'bridge_conflict',
                'El registro de CreditBridge no coincide con este ajuste.'
            );
        }

        if ((string) $bridge->status !== 'applied') {
            return;
        }

        $before = (int) $bridge->balance_before;
        $after = (int) $bridge->balance_after;

        $valid = $delta > 0
            ? ($after - $before === $amount)
            : ($before - $after === $amount);

        if (! $valid) {
            throw new AdminCreditAdjustmentException(
                'bridge_balance_mismatch',
                'Los saldos de CreditBridge no son coherentes.'
            );
        }
    }

    private function writeLedger(
        int $accountId,
        int $actorUserId,
        int $targetUserId,
        int $delta,
        string $reason,
        string $adjustmentId,
        int $before,
        int $after,
        string $channel
    ): void {
        DB::table('credit_transactions')->insert([
            'account_id' => $accountId,
            'user_id' => $targetUserId,
            'purchase_id' => $adjustmentId,
            'type' => 'admin_credit_adjustment',
            'amount' => $delta,
            'balance_before' => $before,
            'balance_after' => $after,
            'metadata' => json_encode([
                'kind' => 'admin_credit_adjustment',
                'adjustment_id' => $adjustmentId,
                'actor_user_id' => $actorUserId,
                'target_user_id' => $targetUserId,
                'reason' => $reason,
                'payment_channel' => $channel,
                'transaction_id' =>
                    $channel === 'online_credit_bridge'
                        ? $adjustmentId
                        : null,
            ], JSON_THROW_ON_ERROR),
            'created_at' => now(),
        ]);
    }

    private function completeAdjustment(
        string $adjustmentId,
        string $attemptToken,
        int $before,
        int $after,
        ?string $bridgeTransactionId,
        array $result
    ): void {
        $updated = DB::table(
            'admin_credit_adjustments'
        )
            ->where('id', $adjustmentId)
            ->where(
                'attempt_token',
                $attemptToken
            )
            ->update([
                'status' => 'completed',
                'balance_before' => $before,
                'balance_after' => $after,
                'bridge_transaction_id' =>
                    $bridgeTransactionId,
                'result_json' => json_encode(
                    $result,
                    JSON_THROW_ON_ERROR
                ),
                'error_code' => null,
                'attempt_token' => null,
                'lease_expires_at' => null,
                'updated_at' => now(),
                'completed_at' => now(),
            ]);

        if ($updated !== 1) {
            throw new AdminCreditAdjustmentException(
                'attempt_lost',
                'Se perdio la propiedad del ajuste.'
            );
        }
    }

    private function buildResult(
        string $adjustmentId,
        int $accountId,
        int $actorUserId,
        int $targetUserId,
        int $delta,
        string $reason,
        int $before,
        int $after,
        string $channel
    ): array {
        return [
            'adjustment_id' => $adjustmentId,
            'account_id' => $accountId,
            'actor_user_id' => $actorUserId,
            'target_user_id' => $targetUserId,
            'delta' => $delta,
            'reason' => $reason,
            'balance_before' => $before,
            'balance_after' => $after,
            'channel' => $channel,
        ];
    }

    private function assertMaximumAdmin(
        int $actorUserId,
        bool $lock = false
    ): object {
        $query = DB::table('users')
            ->where('id', $actorUserId);

        if ($lock) {
            $query->lockForUpdate();
        }

        $actor = $query->first([
            'id',
            'username',
            'rank',
        ]);

        if (! $actor) {
            throw new AdminCreditAdjustmentException(
                'actor_not_found',
                'No se encontro el administrador.'
            );
        }

        $actorLevel = DB::table('permissions')
            ->where('id', $actor->rank)
            ->value('level');

        $maxLevel = DB::table('permissions')
            ->max('level');

        if (
            $actorLevel === null ||
            $maxLevel === null ||
            (int) $actorLevel !== (int) $maxLevel
        ) {
            throw new AdminCreditAdjustmentException(
                'forbidden',
                'Solo el rango maximo puede ajustar creditos.'
            );
        }

        return $actor;
    }

    private function lockAdjustment(
        string $adjustmentId
    ): object {
        $row = DB::table(
            'admin_credit_adjustments'
        )
            ->where('id', $adjustmentId)
            ->lockForUpdate()
            ->first();

        if (! $row) {
            throw new AdminCreditAdjustmentException(
                'adjustment_missing',
                'No existe el ajuste administrativo.'
            );
        }

        return $row;
    }

    private function assertAttempt(
        object $adjustment,
        string $attemptToken
    ): void {
        if (
            (string) $adjustment->status !==
                'processing' ||
            ! $adjustment->attempt_token ||
            ! hash_equals(
                (string) $adjustment->attempt_token,
                $attemptToken
            )
        ) {
            throw new AdminCreditAdjustmentException(
                'attempt_lost',
                'Este intento ya no controla el ajuste.'
            );
        }
    }

    private function releaseForRetry(
        string $adjustmentId,
        string $attemptToken,
        string $errorCode
    ): void {
        DB::table('admin_credit_adjustments')
            ->where('id', $adjustmentId)
            ->where(
                'attempt_token',
                $attemptToken
            )
            ->where('status', 'processing')
            ->update([
                'lease_expires_at' =>
                    now()->subSecond(),
                'error_code' => $errorCode,
                'updated_at' => now(),
            ]);
    }

    private function markManualReview(
        string $adjustmentId,
        string $attemptToken,
        string $errorCode
    ): void {
        DB::table('admin_credit_adjustments')
            ->where('id', $adjustmentId)
            ->where(
                'attempt_token',
                $attemptToken
            )
            ->where('status', 'processing')
            ->update([
                'status' => 'manual_review',
                'lease_expires_at' => null,
                'error_code' => $errorCode,
                'updated_at' => now(),
            ]);
    }

    private function assertValidBalance(
        int $balance
    ): void {
        if (
            $balance < 0 ||
            $balance > 2147483647
        ) {
            throw new AdminCreditAdjustmentException(
                'invalid_resulting_balance',
                'El ajuste produciria un saldo no valido.'
            );
        }
    }

    private function fingerprint(
        int $accountId,
        int $actorUserId,
        int $targetUserId,
        int $delta,
        string $reason
    ): string {
        return hash(
            'sha256',
            json_encode([
                'account_id' => $accountId,
                'actor_user_id' => $actorUserId,
                'target_user_id' => $targetUserId,
                'delta' => $delta,
                'reason' => $reason,
            ], JSON_THROW_ON_ERROR)
        );
    }

    private function validUuid(
        string $value
    ): bool {
        return preg_match(
            '/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i',
            $value
        ) === 1;
    }

    private function decodeResult(
        ?string $json
    ): array {
        if (! $json) {
            throw new AdminCreditAdjustmentException(
                'missing_result',
                'El ajuste completado no tiene resultado guardado.'
            );
        }

        $result = json_decode(
            $json,
            true,
            512,
            JSON_THROW_ON_ERROR
        );

        if (! is_array($result)) {
            throw new AdminCreditAdjustmentException(
                'invalid_result',
                'El resultado guardado no es valido.'
            );
        }

        return $result;
    }
}