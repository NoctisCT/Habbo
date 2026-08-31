<?php

namespace App\Services;

use App\Exceptions\CreditBridgeException;
use App\Exceptions\CreditRefundException;
use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Str;
use Throwable;

class CreditRefundService
{
    private const LEASE_SECONDS = 300;

    public function __construct(
        private readonly CreditBridgeClient $bridge,
        private readonly EmulatorPresenceService $presence
    ) {
    }

    public function refund(
        string $refundId,
        string $originalPurchaseId,
        int $amount,
        string $reason,
        array $metadata = []
    ): array {
        $reason = trim($reason);

        $this->validateInput(
            $refundId,
            $originalPurchaseId,
            $amount,
            $reason
        );

        $fingerprint = $this->fingerprint(
            $originalPurchaseId,
            $amount,
            $reason,
            $metadata
        );

        $claim = $this->claimRefund(
            $refundId,
            $originalPurchaseId,
            $amount,
            $reason,
            $metadata,
            $fingerprint
        );

        if ($claim['completed']) {
            return $claim['result'];
        }

        $attemptToken = $claim['attempt_token'];
        $accountId = $claim['account_id'];
        $userId = $claim['user_id'];

        $recovered = $this->recoverAppliedBridgeCredit(
            $refundId,
            $originalPurchaseId,
            $accountId,
            $userId,
            $amount,
            $attemptToken
        );

        if ($recovered !== null) {
            return $recovered;
        }

        $user = DB::table('users')
            ->where('id', $userId)
            ->first([
                'id',
                'credits',
                'online',
            ]);

        if (! $user) {
            throw new CreditRefundException(
                'user_not_found',
                'No se encontro el personaje que debe recibir el reembolso.'
            );
        }

        $user->online =
            $this->presence->effectiveOnlineState(
                $userId,
                $user->online
            )
                ? '1'
                : '0';

        if ((string) $user->online === '0') {
            try {
                return $this->refundOffline(
                    $refundId,
                    $originalPurchaseId,
                    $accountId,
                    $userId,
                    $amount,
                    $attemptToken
                );
            } catch (CreditRefundException $exception) {
                if (
                    $exception->reason !==
                    'user_became_online'
                ) {
                    throw $exception;
                }
            }
        }

        return $this->refundOnline(
            $refundId,
            $originalPurchaseId,
            $accountId,
            $userId,
            $amount,
            $attemptToken
        );
    }

    private function validateInput(
        string $refundId,
        string $originalPurchaseId,
        int $amount,
        string $reason
    ): void {
        if (! $this->validUuid($refundId)) {
            throw new CreditRefundException(
                'invalid_refund_id',
                'El identificador del reembolso no es valido.'
            );
        }

        if ($originalPurchaseId === '') {
            throw new CreditRefundException(
                'invalid_purchase_id',
                'La compra original no es valida.'
            );
        }

        if ($amount <= 0) {
            throw new CreditRefundException(
                'invalid_amount',
                'El importe del reembolso debe ser mayor que cero.'
            );
        }

        if (
            mb_strlen($reason) < 3 ||
            mb_strlen($reason) > 500
        ) {
            throw new CreditRefundException(
                'invalid_reason',
                'El motivo debe tener entre 3 y 500 caracteres.'
            );
        }
    }

    private function claimRefund(
        string $refundId,
        string $originalPurchaseId,
        int $amount,
        string $reason,
        array $metadata,
        string $fingerprint
    ): array {
        return DB::transaction(function () use (
            $refundId,
            $originalPurchaseId,
            $amount,
            $reason,
            $metadata,
            $fingerprint
        ) {
            $purchase = DB::table('purchase_operations')
                ->where('id', $originalPurchaseId)
                ->lockForUpdate()
                ->first();

            if (! $purchase) {
                throw new CreditRefundException(
                    'purchase_not_found',
                    'No se encontro la compra original.'
                );
            }

            $existing = DB::table('credit_refunds')
                ->where('id', $refundId)
                ->lockForUpdate()
                ->first();

            if ($existing) {
                $this->assertFingerprint(
                    $existing,
                    $fingerprint
                );

                if (
                    (string) $existing->status ===
                    'completed'
                ) {
                    return [
                        'completed' => true,
                        'result' => $this->decodeResult(
                            $existing->result_json
                        ),
                        'attempt_token' => null,
                        'account_id' =>
                            (int) $existing->account_id,
                        'user_id' =>
                            (int) $existing->user_id,
                    ];
                }

                if (
                    (string) $existing->status ===
                    'manual_review'
                ) {
                    throw new CreditRefundException(
                        'refund_manual_review',
                        'Este reembolso requiere revision administrativa.'
                    );
                }
            }

            if ((string) $purchase->status !== 'completed') {
                throw new CreditRefundException(
                    'purchase_not_refundable',
                    'La compra no esta en un estado reembolsable.'
                );
            }

            $purchaseAmount = (int) $purchase->amount;

            if ($purchaseAmount <= 0) {
                throw new CreditRefundException(
                    'invalid_purchase_amount',
                    'La compra original no tiene un importe valido.'
                );
            }

            $accountId = (int) $purchase->account_id;
            $userId = (int) $purchase->user_id;

            $reservedByOthers = (int) DB::table(
                'credit_refunds'
            )
                ->where(
                    'original_purchase_id',
                    $originalPurchaseId
                )
                ->where('id', '<>', $refundId)
                ->whereIn(
                    'status',
                    [
                        'processing',
                        'completed',
                        'manual_review',
                    ]
                )
                ->sum('amount');

            if (
                $reservedByOthers + $amount >
                $purchaseAmount
            ) {
                $available =
                    max(
                        0,
                        $purchaseAmount -
                        $reservedByOthers
                    );

                throw new CreditRefundException(
                    'refund_exceeds_purchase',
                    "Solo quedan {$available} creditos reembolsables de esta compra."
                );
            }

            $attemptToken = (string) Str::uuid();
            $lease = now()->addSeconds(
                self::LEASE_SECONDS
            );

            if (! $existing) {
                DB::table('credit_refunds')->insert([
                    'id' => $refundId,
                    'original_purchase_id' =>
                        $originalPurchaseId,
                    'account_id' => $accountId,
                    'user_id' => $userId,
                    'amount' => $amount,
                    'reason' => $reason,
                    'fingerprint' => $fingerprint,
                    'status' => 'processing',
                    'attempt_token' => $attemptToken,
                    'lease_expires_at' => $lease,
                    'metadata' => json_encode(
                        $metadata,
                        JSON_THROW_ON_ERROR
                    ),
                    'created_at' => now(),
                    'updated_at' => now(),
                ]);

                return [
                    'completed' => false,
                    'result' => null,
                    'attempt_token' => $attemptToken,
                    'account_id' => $accountId,
                    'user_id' => $userId,
                ];
            }

            if (
                (string) $existing->status !==
                'processing'
            ) {
                throw new CreditRefundException(
                    'invalid_refund_status',
                    'El reembolso tiene un estado no reconocido.'
                );
            }

            $leaseActive = false;

            if ($existing->lease_expires_at) {
                $leaseActive = Carbon::parse(
                    $existing->lease_expires_at
                )->isFuture();
            }

            if ($leaseActive) {
                throw new CreditRefundException(
                    'refund_in_progress',
                    'Este reembolso ya se esta procesando.'
                );
            }

            DB::table('credit_refunds')
                ->where('id', $refundId)
                ->update([
                    'attempt_token' => $attemptToken,
                    'lease_expires_at' => $lease,
                    'error_code' => null,
                    'updated_at' => now(),
                ]);

            return [
                'completed' => false,
                'result' => null,
                'attempt_token' => $attemptToken,
                'account_id' => $accountId,
                'user_id' => $userId,
            ];
        }, 5);
    }

    private function refundOffline(
        string $refundId,
        string $originalPurchaseId,
        int $accountId,
        int $userId,
        int $amount,
        string $attemptToken
    ): array {
        return DB::transaction(function () use (
            $refundId,
            $originalPurchaseId,
            $accountId,
            $userId,
            $amount,
            $attemptToken
        ) {
            $refund = $this->lockRefund($refundId);

            if (
                (string) $refund->status ===
                'completed'
            ) {
                return $this->decodeResult(
                    $refund->result_json
                );
            }

            $this->assertAttempt(
                $refund,
                $attemptToken
            );

            $purchase = DB::table(
                'purchase_operations'
            )
                ->where('id', $originalPurchaseId)
                ->lockForUpdate()
                ->first();

            if (! $purchase) {
                throw new CreditRefundException(
                    'purchase_not_found',
                    'No se encontro la compra original.'
                );
            }

            $user = DB::table('users')
                ->where('id', $userId)
                ->lockForUpdate()
                ->first([
                    'id',
                    'credits',
                    'online',
                ]);

            if (! $user) {
                throw new CreditRefundException(
                    'user_not_found',
                    'No se encontro el personaje del reembolso.'
                );
            }

            if ((string) $user->online !== '0') {
                throw new CreditRefundException(
                    'user_became_online',
                    'El personaje se conecto durante el reembolso.'
                );
            }

            $balanceBefore = (int) $user->credits;
            $balanceAfter =
                $balanceBefore + $amount;

            $updated = DB::table('users')
                ->where('id', $userId)
                ->increment('credits', $amount);

            if ($updated !== 1) {
                throw new CreditRefundException(
                    'offline_credit_failed',
                    'No se pudo aplicar el reembolso.'
                );
            }

            $payment = [
                'channel' => 'offline_db',
                'transaction_id' =>
                    'refund-' . $refundId,
                'balance_before' => $balanceBefore,
                'balance_after' => $balanceAfter,
            ];

            return $this->finishRefund(
                $refundId,
                $originalPurchaseId,
                $accountId,
                $userId,
                $amount,
                $attemptToken,
                $payment
            );
        }, 5);
    }

    private function refundOnline(
        string $refundId,
        string $originalPurchaseId,
        int $accountId,
        int $userId,
        int $amount,
        string $attemptToken
    ): array {
        $bridgeTransactionId =
            'credit-refund-' . $refundId;

        try {
            try {
                $response = $this->bridge->credit(
                    $userId,
                    $amount,
                    $bridgeTransactionId
                );
            } catch (CreditBridgeException) {
                $response = $this->bridge->credit(
                    $userId,
                    $amount,
                    $bridgeTransactionId
                );
            }

            $payment = $this->resolveOnlineCredit(
                $userId,
                $amount,
                $bridgeTransactionId,
                $response
            );
        } catch (Throwable $exception) {
            $bridge = DB::table(
                'credit_bridge_transactions'
            )
                ->where(
                    'transaction_id',
                    $bridgeTransactionId
                )
                ->first();

            if (
                $bridge &&
                (string) $bridge->status === 'pending'
            ) {
                $this->markManualReview(
                    $refundId,
                    $attemptToken,
                    'bridge_credit_pending'
                );

                throw new CreditRefundException(
                    'refund_manual_review',
                    'El reembolso quedo pendiente de confirmar y requiere revision administrativa.'
                );
            }

            throw $exception;
        }

        try {
            return DB::transaction(function () use (
                $refundId,
                $originalPurchaseId,
                $accountId,
                $userId,
                $amount,
                $attemptToken,
                $payment
            ) {
                $refund = $this->lockRefund(
                    $refundId
                );

                if (
                    (string) $refund->status ===
                    'completed'
                ) {
                    return $this->decodeResult(
                        $refund->result_json
                    );
                }

                $this->assertAttempt(
                    $refund,
                    $attemptToken
                );

                DB::table('purchase_operations')
                    ->where(
                        'id',
                        $originalPurchaseId
                    )
                    ->lockForUpdate()
                    ->first();

                $user = DB::table('users')
                    ->where('id', $userId)
                    ->lockForUpdate()
                    ->first([
                        'id',
                        'credits',
                    ]);

                if (! $user) {
                    throw new CreditRefundException(
                        'user_not_found',
                        'No se encontro el personaje del reembolso.'
                    );
                }

                if (
                    (int) $user->credits !==
                    (int) $payment['balance_after']
                ) {
                    throw new CreditRefundException(
                        'post_credit_balance_mismatch',
                        'El saldo persistido no coincide con el reembolso confirmado.'
                    );
                }

                return $this->finishRefund(
                    $refundId,
                    $originalPurchaseId,
                    $accountId,
                    $userId,
                    $amount,
                    $attemptToken,
                    $payment
                );
            }, 5);
        } catch (Throwable $exception) {
            Log::critical(
                'Credit refund was applied by CreditBridge but could not be finalized.',
                [
                    'refund_id' => $refundId,
                    'original_purchase_id' =>
                        $originalPurchaseId,
                    'account_id' => $accountId,
                    'user_id' => $userId,
                    'amount' => $amount,
                    'error' =>
                        $exception->getMessage(),
                ]
            );

            throw $exception;
        }
    }

    private function recoverAppliedBridgeCredit(
        string $refundId,
        string $originalPurchaseId,
        int $accountId,
        int $userId,
        int $amount,
        string $attemptToken
    ): ?array {
        $bridgeTransactionId =
            'credit-refund-' . $refundId;

        $bridge = DB::table(
            'credit_bridge_transactions'
        )
            ->where(
                'transaction_id',
                $bridgeTransactionId
            )
            ->first([
                'user_id',
                'amount',
                'operation',
                'balance_before',
                'balance_after',
                'status',
            ]);

        if (! $bridge) {
            return null;
        }

        if (
            (int) $bridge->user_id !== $userId ||
            (int) $bridge->amount !== $amount ||
            (string) $bridge->operation !== 'credit'
        ) {
            $this->markManualReview(
                $refundId,
                $attemptToken,
                'bridge_transaction_conflict'
            );

            throw new CreditRefundException(
                'bridge_transaction_conflict',
                'El registro de CreditBridge no coincide con este reembolso.'
            );
        }

        if ((string) $bridge->status === 'pending') {
            $this->markManualReview(
                $refundId,
                $attemptToken,
                'bridge_credit_pending'
            );

            throw new CreditRefundException(
                'refund_manual_review',
                'Existe un reembolso pendiente de confirmar.'
            );
        }

        if ((string) $bridge->status !== 'applied') {
            return null;
        }

        if (
            (
                (int) $bridge->balance_after -
                (int) $bridge->balance_before
            ) !== $amount
        ) {
            $this->markManualReview(
                $refundId,
                $attemptToken,
                'bridge_balance_mismatch'
            );

            throw new CreditRefundException(
                'bridge_balance_mismatch',
                'El saldo registrado por CreditBridge no es coherente.'
            );
        }

        $payment = [
            'channel' => 'online_credit_bridge',
            'transaction_id' =>
                $bridgeTransactionId,
            'balance_before' =>
                (int) $bridge->balance_before,
            'balance_after' =>
                (int) $bridge->balance_after,
        ];

        return DB::transaction(function () use (
            $refundId,
            $originalPurchaseId,
            $accountId,
            $userId,
            $amount,
            $attemptToken,
            $payment
        ) {
            $refund = $this->lockRefund(
                $refundId
            );

            if (
                (string) $refund->status ===
                'completed'
            ) {
                return $this->decodeResult(
                    $refund->result_json
                );
            }

            $this->assertAttempt(
                $refund,
                $attemptToken
            );

            return $this->finishRefund(
                $refundId,
                $originalPurchaseId,
                $accountId,
                $userId,
                $amount,
                $attemptToken,
                $payment
            );
        }, 5);
    }

    private function resolveOnlineCredit(
        int $userId,
        int $amount,
        string $transactionId,
        array $response
    ): array {
        $message =
            (string) ($response['message'] ?? '');

        $parts = explode('|', $message);

        if (
            (int) ($response['status'] ?? -1) === 0 &&
            count($parts) === 3 &&
            $parts[0] === 'credited' &&
            is_numeric($parts[1]) &&
            is_numeric($parts[2]) &&
            (
                (int) $parts[2] -
                (int) $parts[1]
            ) === $amount
        ) {
            return [
                'channel' =>
                    'online_credit_bridge',
                'transaction_id' =>
                    $transactionId,
                'balance_before' =>
                    (int) $parts[1],
                'balance_after' =>
                    (int) $parts[2],
            ];
        }

        $bridge = DB::table(
            'credit_bridge_transactions'
        )
            ->where(
                'transaction_id',
                $transactionId
            )
            ->first([
                'user_id',
                'amount',
                'operation',
                'balance_before',
                'balance_after',
                'status',
            ]);

        if (
            $bridge &&
            (int) $bridge->user_id === $userId &&
            (int) $bridge->amount === $amount &&
            (string) $bridge->operation === 'credit' &&
            (string) $bridge->status === 'applied' &&
            (
                (int) $bridge->balance_after -
                (int) $bridge->balance_before
            ) === $amount
        ) {
            return [
                'channel' =>
                    'online_credit_bridge',
                'transaction_id' =>
                    $transactionId,
                'balance_before' =>
                    (int) $bridge->balance_before,
                'balance_after' =>
                    (int) $bridge->balance_after,
            ];
        }

        throw new CreditRefundException(
            'bridge_rejected',
            'Morningstar no pudo confirmar el reembolso.'
        );
    }

    private function finishRefund(
        string $refundId,
        string $originalPurchaseId,
        int $accountId,
        int $userId,
        int $amount,
        string $attemptToken,
        array $payment
    ): array {
        $refund = DB::table('credit_refunds')
            ->where('id', $refundId)
            ->first();

        if (! $refund) {
            throw new CreditRefundException(
                'refund_not_found',
                'No se encontro el reembolso.'
            );
        }

        $metadata = [];

        if ($refund->metadata) {
            $decoded = json_decode(
                $refund->metadata,
                true
            );

            if (is_array($decoded)) {
                $metadata = $decoded;
            }
        }

        $purchase = DB::table(
            'purchase_operations'
        )
            ->where('id', $originalPurchaseId)
            ->first();

        $ledgerPurchaseId =
            'refund-' . $refundId;

        DB::table('credit_transactions')
            ->insert([
                'account_id' => $accountId,
                'user_id' => $userId,
                'purchase_id' =>
                    $ledgerPurchaseId,
                'type' => 'credit_refund',
                'amount' => $amount,
                'balance_before' =>
                    $payment['balance_before'],
                'balance_after' =>
                    $payment['balance_after'],
                'metadata' => json_encode(
                    array_merge(
                        $metadata,
                        [
                            'refund_id' =>
                                $refundId,
                            'original_purchase_id' =>
                                $originalPurchaseId,
                            'original_purchase_type' =>
                                $purchase->type ?? null,
                            'refund_reason' =>
                                $refund->reason,
                            'payment_channel' =>
                                $payment['channel'],
                            'transaction_id' =>
                                $payment['transaction_id'],
                        ]
                    ),
                    JSON_THROW_ON_ERROR
                ),
                'created_at' => now(),
            ]);

        $result = [
            'refund_id' => $refundId,
            'original_purchase_id' =>
                $originalPurchaseId,
            'amount' => $amount,
            'balance_before' =>
                (int) $payment['balance_before'],
            'balance_after' =>
                (int) $payment['balance_after'],
            'channel' =>
                $payment['channel'],
        ];

        $updated = DB::table('credit_refunds')
            ->where('id', $refundId)
            ->where('status', 'processing')
            ->where(
                'attempt_token',
                $attemptToken
            )
            ->update([
                'status' => 'completed',
                'attempt_token' => null,
                'lease_expires_at' => null,
                'payment_channel' =>
                    $payment['channel'],
                'bridge_transaction_id' =>
                    $payment['channel'] ===
                    'online_credit_bridge'
                        ? $payment['transaction_id']
                        : null,
                'balance_before' =>
                    $payment['balance_before'],
                'balance_after' =>
                    $payment['balance_after'],
                'result_json' => json_encode(
                    $result,
                    JSON_THROW_ON_ERROR
                ),
                'error_code' => null,
                'completed_at' => now(),
                'updated_at' => now(),
            ]);

        if ($updated !== 1) {
            throw new CreditRefundException(
                'refund_attempt_lost',
                'El reembolso perdio su bloqueo de procesamiento.'
            );
        }

        $this->markPurchaseRefundedIfComplete(
            $originalPurchaseId
        );

        return $result;
    }

    private function markPurchaseRefundedIfComplete(
        string $originalPurchaseId
    ): void {
        $purchase = DB::table(
            'purchase_operations'
        )
            ->where('id', $originalPurchaseId)
            ->lockForUpdate()
            ->first();

        if (! $purchase) {
            return;
        }

        $refunded = (int) DB::table(
            'credit_refunds'
        )
            ->where(
                'original_purchase_id',
                $originalPurchaseId
            )
            ->where('status', 'completed')
            ->sum('amount');

        if (
            $refunded >= (int) $purchase->amount &&
            (string) $purchase->status ===
            'completed'
        ) {
            DB::table('purchase_operations')
                ->where('id', $originalPurchaseId)
                ->where('status', 'completed')
                ->update([
                    'status' => 'refunded',
                    'refunded_at' => now(),
                    'updated_at' => now(),
                ]);
        }
    }

    private function markManualReview(
        string $refundId,
        string $attemptToken,
        string $errorCode
    ): void {
        DB::table('credit_refunds')
            ->where('id', $refundId)
            ->where('status', 'processing')
            ->where(
                'attempt_token',
                $attemptToken
            )
            ->update([
                'status' => 'manual_review',
                'attempt_token' => null,
                'lease_expires_at' => null,
                'error_code' => $errorCode,
                'updated_at' => now(),
            ]);
    }

    private function lockRefund(
        string $refundId
    ): object {
        $refund = DB::table('credit_refunds')
            ->where('id', $refundId)
            ->lockForUpdate()
            ->first();

        if (! $refund) {
            throw new CreditRefundException(
                'refund_not_found',
                'No se encontro el reembolso.'
            );
        }

        return $refund;
    }

    private function assertAttempt(
        object $refund,
        string $attemptToken
    ): void {
        if (
            (string) $refund->status !==
                'processing' ||
            (string) $refund->attempt_token !==
                $attemptToken
        ) {
            throw new CreditRefundException(
                'refund_attempt_lost',
                'El reembolso ya esta siendo procesado por otro intento.'
            );
        }
    }

    private function assertFingerprint(
        object $refund,
        string $fingerprint
    ): void {
        if (
            ! hash_equals(
                (string) $refund->fingerprint,
                $fingerprint
            )
        ) {
            throw new CreditRefundException(
                'refund_conflict',
                'Este identificador de reembolso ya fue usado con otros datos.'
            );
        }
    }

    private function fingerprint(
        string $originalPurchaseId,
        int $amount,
        string $reason,
        array $metadata
    ): string {
        $payload = [
            'original_purchase_id' =>
                $originalPurchaseId,
            'amount' => $amount,
            'reason' => $reason,
            'metadata' =>
                $this->canonicalize($metadata),
        ];

        return hash(
            'sha256',
            json_encode(
                $payload,
                JSON_THROW_ON_ERROR |
                JSON_UNESCAPED_SLASHES
            )
        );
    }

    private function canonicalize(
        mixed $value
    ): mixed {
        if (! is_array($value)) {
            return $value;
        }

        if (array_is_list($value)) {
            return array_map(
                fn ($item) =>
                    $this->canonicalize($item),
                $value
            );
        }

        ksort($value);

        foreach ($value as $key => $item) {
            $value[$key] =
                $this->canonicalize($item);
        }

        return $value;
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
            throw new CreditRefundException(
                'refund_result_missing',
                'El reembolso completado no tiene resultado almacenado.'
            );
        }

        $decoded = json_decode(
            $json,
            true,
            512,
            JSON_THROW_ON_ERROR
        );

        if (! is_array($decoded)) {
            throw new CreditRefundException(
                'refund_result_invalid',
                'El resultado almacenado del reembolso no es valido.'
            );
        }

        return $decoded;
    }
}