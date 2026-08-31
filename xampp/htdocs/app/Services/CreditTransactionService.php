<?php

namespace App\Services;

use App\Exceptions\CreditBridgeException;
use App\Exceptions\CreditTransactionException;
use Closure;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Str;
use Throwable;

class CreditTransactionService
{
    private const PURCHASE_LEASE_SECONDS = 300;

    public function __construct(
        private readonly CreditBridgeClient $bridge,
        private readonly EmulatorPresenceService $presence
    ) {
    }

    public function debitAndRun(
        int $accountId,
        int $userId,
        int $amount,
        string $type,
        string $purchaseId,
        array $purchaseContext,
        Closure $operation
    ): array {
        if ($amount <= 0) {
            throw new CreditTransactionException(
                'invalid_amount',
                'El importe del cobro no es valido.'
            );
        }

        if (! $this->validPurchaseId($purchaseId)) {
            throw new CreditTransactionException(
                'invalid_purchase_id',
                'El identificador de compra no es valido.'
            );
        }

        $fingerprint = $this->fingerprint(
            $accountId,
            $userId,
            $amount,
            $type,
            $purchaseContext
        );

        $claim = $this->claimPurchase(
            $purchaseId,
            $accountId,
            $userId,
            $amount,
            $type,
            $fingerprint
        );

        if ($claim['completed']) {
            return $claim['result'];
        }

        $attemptToken = $claim['attempt_token'];

        $recovered = $this->recoverAppliedBridgeDebit(
            $accountId,
            $userId,
            $amount,
            $type,
            $purchaseId,
            $attemptToken,
            $operation
        );

        if ($recovered !== null) {
            return $recovered;
        }

        try {
            $payer = DB::table('users')
                ->where('id', $userId)
                ->first([
                    'id',
                    'username',
                    'credits',
                    'online',
                ]);

            if (! $payer) {
                throw new CreditTransactionException(
                    'payer_not_found',
                    'No se encontro el personaje pagador.'
                );
            }

            $this->assertActiveCharacter(
                $accountId,
                $userId
            );

            $payer->online =
                $this->presence->effectiveOnlineState(
                    $userId,
                    $payer->online
                )
                    ? '1'
                    : '0';

            if (
                (string) $payer->online === '0' &&
                $this->hasPendingBridgeDebit($purchaseId)
            ) {
                $this->markManualReview(
                    $purchaseId,
                    $attemptToken,
                    'bridge_debit_pending_offline'
                );

                throw new CreditTransactionException(
                    'purchase_manual_review',
                    'Existe un cobro pendiente de reconciliar. No se realizara otro cargo.'
                );
            }

            if ((string) $payer->online !== '0') {
                return $this->debitOnline(
                    $accountId,
                    $payer,
                    $amount,
                    $type,
                    $purchaseId,
                    $attemptToken,
                    $operation
                );
            }

            try {
                return $this->debitOffline(
                    $accountId,
                    $userId,
                    $amount,
                    $type,
                    $purchaseId,
                    $attemptToken,
                    $operation
                );
            } catch (CreditTransactionException $exception) {
                if (
                    $exception->reason !==
                    'payer_became_online'
                ) {
                    throw $exception;
                }
            }

            $payer = DB::table('users')
                ->where('id', $userId)
                ->first([
                    'id',
                    'username',
                    'credits',
                    'online',
                ]);

            if (! $payer) {
                throw new CreditTransactionException(
                    'payer_not_found',
                    'No se encontro el personaje pagador.'
                );
            }

            return $this->debitOnline(
                $accountId,
                $payer,
                $amount,
                $type,
                $purchaseId,
                $attemptToken,
                $operation
            );
        } catch (Throwable $exception) {
            $this->markUnfinishedPurchase(
                $purchaseId,
                $attemptToken,
                $exception
            );

            throw $exception;
        }
    }

    private function recoverAppliedBridgeDebit(
        int $accountId,
        int $userId,
        int $amount,
        string $type,
        string $purchaseId,
        string $attemptToken,
        Closure $operation
    ): ?array {
        $bridge = DB::table(
            'credit_bridge_transactions'
        )
            ->where(
                'transaction_id',
                $purchaseId
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
            (string) $bridge->operation !== 'debit'
        ) {
            $this->markManualReview(
                $purchaseId,
                $attemptToken,
                'bridge_transaction_conflict'
            );

            throw new CreditTransactionException(
                'purchase_conflict',
                'El registro de CreditBridge no coincide con esta compra.'
            );
        }

        if ((string) $bridge->status !== 'applied') {
            return null;
        }

        if (
            (
                (int) $bridge->balance_before -
                (int) $bridge->balance_after
            ) !== $amount
        ) {
            $this->markManualReview(
                $purchaseId,
                $attemptToken,
                'bridge_balance_mismatch'
            );

            throw new CreditTransactionException(
                'invalid_bridge_record',
                'El cobro registrado por CreditBridge no es coherente.'
            );
        }

        $payment = [
            'channel' => 'online_credit_bridge',
            'transaction_id' => $purchaseId,
            'balance_before' =>
                (int) $bridge->balance_before,
            'balance_after' =>
                (int) $bridge->balance_after,
        ];

        try {
            return DB::transaction(function () use (
                $accountId,
                $userId,
                $amount,
                $type,
                $purchaseId,
                $attemptToken,
                $operation,
                $payment
            ) {
                $purchase = $this->lockPurchase(
                    $purchaseId
                );

                if ($purchase->status === 'completed') {
                    return $this->decodeResult(
                        $purchase->result_json
                    );
                }

                $this->assertAttempt(
                    $purchase,
                    $attemptToken
                );

                $payer = DB::table('users')
                    ->where('id', $userId)
                    ->lockForUpdate()
                    ->first([
                        'id',
                        'username',
                        'credits',
                        'online',
                    ]);

                if (! $payer) {
                    throw new CreditTransactionException(
                        'payer_not_found',
                        'No se encontro el personaje pagador.'
                    );
                }

                $payload = $this->runOperation(
                    $operation,
                    $payer,
                    $payment
                );

                $this->writeLedger(
                    $accountId,
                    $userId,
                    $type,
                    $amount,
                    $purchaseId,
                    $payment,
                    $payload['metadata']
                );

                $result = [
                    'result' => $payload['result'],
                    'payment' => $payment,
                ];

                $this->completePurchase(
                    $purchaseId,
                    $attemptToken,
                    $result,
                    $payment
                );

                return $result;
            }, 5);
        } catch (Throwable $exception) {
            try {
                $this->compensateOnlineDebit(
                    $userId,
                    $amount,
                    $accountId,
                    $type,
                    $purchaseId,
                    $exception
                );

                $this->markRefunded(
                    $purchaseId,
                    $attemptToken
                );
            } catch (Throwable $refundException) {
                $this->markManualReview(
                    $purchaseId,
                    $attemptToken,
                    'compensation_failed'
                );

                throw $refundException;
            }

            throw $exception;
        }
    }

    private function hasPendingBridgeDebit(
        string $purchaseId
    ): bool {
        return DB::table(
            'credit_bridge_transactions'
        )
            ->where(
                'transaction_id',
                $purchaseId
            )
            ->where('operation', 'debit')
            ->where('status', 'pending')
            ->exists();
    }
    private function debitOffline(
        int $accountId,
        int $userId,
        int $amount,
        string $type,
        string $purchaseId,
        string $attemptToken,
        Closure $operation
    ): array {
        return DB::transaction(function () use (
            $accountId,
            $userId,
            $amount,
            $type,
            $purchaseId,
            $attemptToken,
            $operation
        ) {
            $purchase = $this->lockPurchase(
                $purchaseId
            );

            if ($purchase->status === 'completed') {
                return $this->decodeResult(
                    $purchase->result_json
                );
            }

            $this->assertAttempt(
                $purchase,
                $attemptToken
            );

            $this->assertActiveCharacter(
                $accountId,
                $userId,
                true
            );

            $payer = DB::table('users')
                ->where('id', $userId)
                ->lockForUpdate()
                ->first([
                    'id',
                    'username',
                    'credits',
                    'online',
                ]);

            if (! $payer) {
                throw new CreditTransactionException(
                    'payer_not_found',
                    'No se encontro el personaje pagador.'
                );
            }

            if ((string) $payer->online !== '0') {
                throw new CreditTransactionException(
                    'payer_became_online',
                    'El personaje se conecto durante el cobro.'
                );
            }

            $balanceBefore =
                (int) $payer->credits;

            if ($balanceBefore < $amount) {
                throw new CreditTransactionException(
                    'insufficient_funds',
                    "{$payer->username} necesita {$amount} creditos. Tiene {$balanceBefore}."
                );
            }

            $updated = DB::table('users')
                ->where('id', $userId)
                ->where('credits', '>=', $amount)
                ->decrement('credits', $amount);

            if ($updated !== 1) {
                throw new CreditTransactionException(
                    'debit_failed',
                    'No se pudo realizar el cobro.'
                );
            }

            $payment = [
                'channel' => 'offline_db',
                'transaction_id' => $purchaseId,
                'balance_before' => $balanceBefore,
                'balance_after' =>
                    $balanceBefore - $amount,
            ];

            $payload = $this->runOperation(
                $operation,
                $payer,
                $payment
            );

            $this->writeLedger(
                $accountId,
                $userId,
                $type,
                $amount,
                $purchaseId,
                $payment,
                $payload['metadata']
            );

            $result = [
                'result' => $payload['result'],
                'payment' => $payment,
            ];

            $this->completePurchase(
                $purchaseId,
                $attemptToken,
                $result,
                $payment
            );

            return $result;
        }, 5);
    }

    private function debitOnline(
        int $accountId,
        object $payer,
        int $amount,
        string $type,
        string $purchaseId,
        string $attemptToken,
        Closure $operation
    ): array {
        $this->assertActiveCharacter(
            $accountId,
            (int) $payer->id
        );

        $response = $this->sendDebitSafely(
            (int) $payer->id,
            $amount,
            $purchaseId,
            (string) $payer->username
        );

        $payment = $this->resolveOnlinePayment(
            (int) $payer->id,
            $amount,
            $purchaseId,
            $response
        );

        try {
            return DB::transaction(function () use (
                $accountId,
                $payer,
                $amount,
                $type,
                $purchaseId,
                $attemptToken,
                $operation,
                $payment
            ) {
                $purchase = $this->lockPurchase(
                    $purchaseId
                );

                if ($purchase->status === 'completed') {
                    return $this->decodeResult(
                        $purchase->result_json
                    );
                }

                $this->assertAttempt(
                    $purchase,
                    $attemptToken
                );

                $this->assertActiveCharacter(
                    $accountId,
                    (int) $payer->id,
                    true
                );

                $currentPayer = DB::table('users')
                    ->where('id', $payer->id)
                    ->lockForUpdate()
                    ->first([
                        'id',
                        'username',
                        'credits',
                        'online',
                    ]);

                if (! $currentPayer) {
                    throw new CreditTransactionException(
                        'payer_not_found',
                        'No se encontro el personaje pagador.'
                    );
                }

                if (
                    (int) $currentPayer->credits !==
                    (int) $payment['balance_after']
                ) {
                    throw new CreditTransactionException(
                        'post_debit_balance_mismatch',
                        'El saldo persistido no coincide con el cobro confirmado.'
                    );
                }

                $payload = $this->runOperation(
                    $operation,
                    $currentPayer,
                    $payment
                );

                $this->writeLedger(
                    $accountId,
                    (int) $payer->id,
                    $type,
                    $amount,
                    $purchaseId,
                    $payment,
                    $payload['metadata']
                );

                $result = [
                    'result' => $payload['result'],
                    'payment' => $payment,
                ];

                $this->completePurchase(
                    $purchaseId,
                    $attemptToken,
                    $result,
                    $payment
                );

                return $result;
            }, 5);
        } catch (Throwable $exception) {
            try {
                $this->compensateOnlineDebit(
                    (int) $payer->id,
                    $amount,
                    $accountId,
                    $type,
                    $purchaseId,
                    $exception
                );

                $this->markRefunded(
                    $purchaseId,
                    $attemptToken
                );
            } catch (Throwable $refundException) {
                $this->markManualReview(
                    $purchaseId,
                    $attemptToken,
                    'compensation_failed'
                );

                throw $refundException;
            }

            throw $exception;
        }
    }

    private function sendDebitSafely(
        int $userId,
        int $amount,
        string $purchaseId,
        string $username
    ): array {
        try {
            return $this->bridge->debit(
                $userId,
                $amount,
                $purchaseId
            );
        } catch (CreditBridgeException $firstException) {
            try {
                return $this->bridge->debit(
                    $userId,
                    $amount,
                    $purchaseId
                );
            } catch (CreditBridgeException $secondException) {
                $bridge = DB::table(
                    'credit_bridge_transactions'
                )
                    ->where(
                        'transaction_id',
                        $purchaseId
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
                    (string) $bridge->operation === 'debit' &&
                    (string) $bridge->status === 'applied'
                ) {
                    return [
                        'status' => 0,
                        'message' =>
                            'debited|' .
                            (int) $bridge->balance_before .
                            '|' .
                            (int) $bridge->balance_after,
                    ];
                }

                throw new CreditTransactionException(
                    'online_unavailable',
                    "CreditBridge no esta disponible para {$username}, que sigue conectado al hotel. No se ha realizado ningun cobro; intentalo de nuevo en unos segundos."
                );
            }
        }
    }

    private function resolveOnlinePayment(
        int $userId,
        int $amount,
        string $purchaseId,
        array $response
    ): array {
        $message =
            (string) ($response['message'] ?? '');

        if ((int) ($response['status'] ?? -1) !== 0) {
            if (
                str_starts_with(
                    $message,
                    'insufficient_funds|'
                )
            ) {
                $parts = explode('|', $message);
                $balance =
                    (int) ($parts[1] ?? 0);

                throw new CreditTransactionException(
                    'insufficient_funds',
                    "Se necesitan {$amount} creditos. Saldo disponible: {$balance}."
                );
            }

            throw new CreditTransactionException(
                'bridge_rejected',
                'Morningstar no pudo confirmar el cobro.'
            );
        }

        $parts = explode('|', $message);

        if (
            count($parts) === 3 &&
            $parts[0] === 'debited' &&
            is_numeric($parts[1]) &&
            is_numeric($parts[2]) &&
            ((int) $parts[1] - $amount) ===
                (int) $parts[2]
        ) {
            return [
                'channel' =>
                    'online_credit_bridge',
                'transaction_id' =>
                    $purchaseId,
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
                $purchaseId
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
            (string) $bridge->operation === 'debit' &&
            (string) $bridge->status === 'applied' &&
            (
                (int) $bridge->balance_before -
                (int) $bridge->balance_after
            ) === $amount
        ) {
            return [
                'channel' =>
                    'online_credit_bridge',
                'transaction_id' =>
                    $purchaseId,
                'balance_before' =>
                    (int) $bridge->balance_before,
                'balance_after' =>
                    (int) $bridge->balance_after,
            ];
        }

        throw new CreditTransactionException(
            'invalid_bridge_response',
            'No se pudo verificar de forma segura el cobro.'
        );
    }

    private function claimPurchase(
        string $purchaseId,
        int $accountId,
        int $userId,
        int $amount,
        string $type,
        string $fingerprint
    ): array {
        return DB::transaction(function () use (
            $purchaseId,
            $accountId,
            $userId,
            $amount,
            $type,
            $fingerprint
        ) {
            $attemptToken =
                (string) Str::uuid();

            $lease =
                now()->addSeconds(
                    self::PURCHASE_LEASE_SECONDS
                );

            DB::table('purchase_operations')
                ->insertOrIgnore([
                    'id' => $purchaseId,
                    'account_id' => $accountId,
                    'user_id' => $userId,
                    'type' => $type,
                    'amount' => $amount,
                    'fingerprint' => $fingerprint,
                    'status' => 'processing',
                    'attempt_token' =>
                        $attemptToken,
                    'lease_expires_at' =>
                        $lease,
                    'created_at' => now(),
                    'updated_at' => now(),
                ]);

            $purchase = DB::table(
                'purchase_operations'
            )
                ->where('id', $purchaseId)
                ->lockForUpdate()
                ->first();

            if (! $purchase) {
                throw new CreditTransactionException(
                    'purchase_claim_failed',
                    'No se pudo reservar la compra.'
                );
            }

            $this->assertPurchaseFingerprint(
                $purchase,
                $accountId,
                $userId,
                $amount,
                $type,
                $fingerprint
            );

            if ($purchase->status === 'completed') {
                return [
                    'completed' => true,
                    'result' =>
                        $this->decodeResult(
                            $purchase->result_json
                        ),
                    'attempt_token' => null,
                ];
            }

            if (
                $purchase->status === 'refunded'
            ) {
                throw new CreditTransactionException(
                    'purchase_refunded',
                    'Esta compra ya fue anulada y reembolsada.'
                );
            }

            if (
                $purchase->status === 'manual_review'
            ) {
                throw new CreditTransactionException(
                    'purchase_manual_review',
                    'Esta compra requiere revision administrativa.'
                );
            }

            if ($purchase->status === 'failed') {
                throw new CreditTransactionException(
                    'purchase_failed',
                    'Este intento de compra ya fallo. Recarga la pagina para iniciar uno nuevo.'
                );
            }

            $refund = DB::table(
                'credit_bridge_transactions'
            )
                ->where(
                    'transaction_id',
                    'refund-' . $purchaseId
                )
                ->where('operation', 'credit')
                ->where('status', 'applied')
                ->first();

            if ($refund) {
                DB::table('purchase_operations')
                    ->where('id', $purchaseId)
                    ->update([
                        'status' => 'refunded',
                        'attempt_token' => null,
                        'lease_expires_at' => null,
                        'refunded_at' => now(),
                        'updated_at' => now(),
                    ]);

                throw new CreditTransactionException(
                    'purchase_refunded',
                    'Esta compra ya fue anulada y reembolsada.'
                );
            }

            if (
                (string) $purchase->attempt_token ===
                $attemptToken
            ) {
                return [
                    'completed' => false,
                    'result' => null,
                    'attempt_token' =>
                        $attemptToken,
                ];
            }

            if (
                $purchase->lease_expires_at &&
                strtotime(
                    (string) $purchase->lease_expires_at
                ) > time()
            ) {
                throw new CreditTransactionException(
                    'purchase_in_progress',
                    'La compra ya se esta procesando.'
                );
            }

            DB::table('purchase_operations')
                ->where('id', $purchaseId)
                ->update([
                    'status' => 'processing',
                    'attempt_token' =>
                        $attemptToken,
                    'lease_expires_at' =>
                        $lease,
                    'error_code' => null,
                    'updated_at' => now(),
                ]);

            return [
                'completed' => false,
                'result' => null,
                'attempt_token' =>
                    $attemptToken,
            ];
        }, 5);
    }

    private function completePurchase(
        string $purchaseId,
        string $attemptToken,
        array $result,
        array $payment
    ): void {
        $updated = DB::table(
            'purchase_operations'
        )
            ->where('id', $purchaseId)
            ->where('status', 'processing')
            ->where(
                'attempt_token',
                $attemptToken
            )
            ->update([
                'status' => 'completed',
                'result_json' => json_encode(
                    $result,
                    JSON_THROW_ON_ERROR
                ),
                'payment_json' => json_encode(
                    $payment,
                    JSON_THROW_ON_ERROR
                ),
                'attempt_token' => null,
                'lease_expires_at' => null,
                'error_code' => null,
                'completed_at' => now(),
                'updated_at' => now(),
            ]);

        if ($updated !== 1) {
            throw new CreditTransactionException(
                'purchase_attempt_lost',
                'La compra perdio su bloqueo de procesamiento.'
            );
        }
    }

    private function markRefunded(
        string $purchaseId,
        string $attemptToken
    ): void {
        DB::table('purchase_operations')
            ->where('id', $purchaseId)
            ->where('status', 'processing')
            ->where(
                'attempt_token',
                $attemptToken
            )
            ->update([
                'status' => 'refunded',
                'attempt_token' => null,
                'lease_expires_at' => null,
                'error_code' => null,
                'refunded_at' => now(),
                'updated_at' => now(),
            ]);
    }

    private function markManualReview(
        string $purchaseId,
        string $attemptToken,
        string $errorCode
    ): void {
        DB::table('purchase_operations')
            ->where('id', $purchaseId)
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

    private function markUnfinishedPurchase(
        string $purchaseId,
        string $attemptToken,
        Throwable $exception
    ): void {
        $purchase = DB::table(
            'purchase_operations'
        )
            ->where('id', $purchaseId)
            ->first([
                'status',
                'attempt_token',
            ]);

        if (
            ! $purchase ||
            $purchase->status !== 'processing' ||
            (string) $purchase->attempt_token !==
                $attemptToken
        ) {
            return;
        }

        $refund = DB::table(
            'credit_bridge_transactions'
        )
            ->where(
                'transaction_id',
                'refund-' . $purchaseId
            )
            ->where('operation', 'credit')
            ->where('status', 'applied')
            ->exists();

        if ($refund) {
            $this->markRefunded(
                $purchaseId,
                $attemptToken
            );

            return;
        }

        $debit = DB::table(
            'credit_bridge_transactions'
        )
            ->where(
                'transaction_id',
                $purchaseId
            )
            ->where('operation', 'debit')
            ->whereIn(
                'status',
                ['pending', 'applied']
            )
            ->exists();

        if ($debit) {
            $this->markManualReview(
                $purchaseId,
                $attemptToken,
                $exception instanceof
                    CreditTransactionException
                    ? $exception->reason
                    : 'unexpected_error'
            );

            return;
        }

        DB::table('purchase_operations')
            ->where('id', $purchaseId)
            ->where('status', 'processing')
            ->where(
                'attempt_token',
                $attemptToken
            )
            ->update([
                'status' => 'failed',
                'attempt_token' => null,
                'lease_expires_at' => null,
                'error_code' =>
                    $exception instanceof
                    CreditTransactionException
                        ? $exception->reason
                        : 'unexpected_error',
                'updated_at' => now(),
            ]);
    }

    private function lockPurchase(
        string $purchaseId
    ): object {
        $purchase = DB::table(
            'purchase_operations'
        )
            ->where('id', $purchaseId)
            ->lockForUpdate()
            ->first();

        if (! $purchase) {
            throw new CreditTransactionException(
                'purchase_not_found',
                'No se encontro la operacion de compra.'
            );
        }

        return $purchase;
    }

    private function assertAttempt(
        object $purchase,
        string $attemptToken
    ): void {
        if (
            $purchase->status !== 'processing' ||
            (string) $purchase->attempt_token !==
                $attemptToken
        ) {
            throw new CreditTransactionException(
                'purchase_attempt_lost',
                'La compra ya esta siendo procesada por otro intento.'
            );
        }
    }

    private function assertPurchaseFingerprint(
        object $purchase,
        int $accountId,
        int $userId,
        int $amount,
        string $type,
        string $fingerprint
    ): void {
        if (
            (int) $purchase->account_id !==
                $accountId ||
            (int) $purchase->user_id !==
                $userId ||
            (int) $purchase->amount !==
                $amount ||
            (string) $purchase->type !==
                $type ||
            ! hash_equals(
                (string) $purchase->fingerprint,
                $fingerprint
            )
        ) {
            throw new CreditTransactionException(
                'purchase_conflict',
                'El identificador de compra ya fue usado con otros datos.'
            );
        }
    }

    private function fingerprint(
        int $accountId,
        int $userId,
        int $amount,
        string $type,
        array $context
    ): string {
        $payload = [
            'account_id' => $accountId,
            'user_id' => $userId,
            'amount' => $amount,
            'type' => $type,
            'context' =>
                $this->canonicalize($context),
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

    private function validPurchaseId(
        string $purchaseId
    ): bool {
        return preg_match(
            '/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i',
            $purchaseId
        ) === 1;
    }

    private function decodeResult(
        ?string $json
    ): array {
        if (! $json) {
            throw new CreditTransactionException(
                'purchase_result_missing',
                'La compra completada no tiene resultado almacenado.'
            );
        }

        $decoded = json_decode(
            $json,
            true,
            512,
            JSON_THROW_ON_ERROR
        );

        if (! is_array($decoded)) {
            throw new CreditTransactionException(
                'purchase_result_invalid',
                'El resultado almacenado de la compra no es valido.'
            );
        }

        return $decoded;
    }

    private function compensateOnlineDebit(
        int $userId,
        int $amount,
        int $accountId,
        string $type,
        string $debitTransactionId,
        Throwable $originalException
    ): void {
        $refundTransactionId =
            'refund-' . $debitTransactionId;

        try {
            try {
                $response = $this->bridge->credit(
                    $userId,
                    $amount,
                    $refundTransactionId
                );
            } catch (
                CreditBridgeException $firstException
            ) {
                try {
                    $response =
                        $this->bridge->credit(
                            $userId,
                            $amount,
                            $refundTransactionId
                        );
                } catch (
                    CreditBridgeException $secondException
                ) {
                    $bridgeTransaction =
                        DB::table(
                            'credit_bridge_transactions'
                        )
                            ->where(
                                'transaction_id',
                                $refundTransactionId
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
                        $bridgeTransaction &&
                        (int)
                            $bridgeTransaction->user_id
                            === $userId &&
                        (int)
                            $bridgeTransaction->amount
                            === $amount &&
                        (string)
                            $bridgeTransaction->operation
                            === 'credit' &&
                        (string)
                            $bridgeTransaction->status
                            === 'applied' &&
                        (
                            (int)
                                $bridgeTransaction->balance_after -
                            (int)
                                $bridgeTransaction->balance_before
                        ) === $amount
                    ) {
                        return;
                    }

                    throw $secondException;
                }
            }

            $parts = explode(
                '|',
                (string)
                    ($response['message'] ?? '')
            );

            if (
                (int)
                    ($response['status'] ?? -1)
                    !== 0 ||
                count($parts) !== 3 ||
                $parts[0] !== 'credited' ||
                ! is_numeric($parts[1]) ||
                ! is_numeric($parts[2]) ||
                (
                    (int) $parts[2] -
                    (int) $parts[1]
                ) !== $amount
            ) {
                throw new CreditBridgeException(
                    'Morningstar no confirmo correctamente la compensacion.'
                );
            }
        } catch (Throwable $refundException) {
            Log::critical(
                'CreditBridge compensation failed after online debit.',
                [
                    'account_id' => $accountId,
                    'user_id' => $userId,
                    'amount' => $amount,
                    'type' => $type,
                    'debit_transaction_id' =>
                        $debitTransactionId,
                    'refund_transaction_id' =>
                        $refundTransactionId,
                    'original_error' =>
                        $originalException->getMessage(),
                    'refund_error' =>
                        $refundException->getMessage(),
                ]
            );

            throw new CreditTransactionException(
                'compensation_failed',
                'El cobro se realizo, pero la devolucion automatica no pudo confirmarse. Contacta con un administrador.'
            );
        }
    }

    private function runOperation(
        Closure $operation,
        object $payer,
        array $payment
    ): array {
        $payload =
            $operation($payer, $payment);

        if (! is_array($payload)) {
            $payload = [];
        }

        return [
            'result' =>
                $payload['result'] ?? null,
            'metadata' =>
                is_array(
                    $payload['metadata'] ?? null
                )
                    ? $payload['metadata']
                    : [],
        ];
    }

    private function writeLedger(
        int $accountId,
        int $userId,
        string $type,
        int $amount,
        string $purchaseId,
        array $payment,
        array $metadata
    ): void {
        $metadata['payment_channel'] =
            $payment['channel'];

        $metadata['transaction_id'] =
            $payment['transaction_id'];

        $metadata['purchase_id'] =
            $purchaseId;

        DB::table('credit_transactions')
            ->insert([
                'account_id' => $accountId,
                'user_id' => $userId,
                'purchase_id' => $purchaseId,
                'type' => $type,
                'amount' => -$amount,
                'balance_before' =>
                    $payment['balance_before'],
                'balance_after' =>
                    $payment['balance_after'],
                'metadata' => json_encode(
                    $metadata,
                    JSON_THROW_ON_ERROR
                ),
                'created_at' => now(),
            ]);
    }

    private function assertActiveCharacter(
        int $accountId,
        int $userId,
        bool $lock = false
    ): void {
        $query = DB::table(
            'account_characters'
        )
            ->where(
                'account_id',
                $accountId
            )
            ->where(
                'user_id',
                $userId
            )
            ->whereNull('archived_at');

        if ($lock) {
            $query->lockForUpdate();
        }

        if (! $query->exists()) {
            throw new CreditTransactionException(
                'invalid_payer',
                'El personaje seleccionado no pertenece a tus personajes activos.'
            );
        }
    }
}