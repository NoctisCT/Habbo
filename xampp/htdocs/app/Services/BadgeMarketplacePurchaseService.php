<?php

namespace App\Services;

use App\Exceptions\CreditBridgeException;
use App\Models\BadgeMarketplaceSale;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;
use RuntimeException;
use Throwable;

class BadgeMarketplacePurchaseService
{
    public function __construct(
        private readonly CreditTransactionService $creditTransactions,
        private readonly CreditRefundService $creditRefunds,
        private readonly CreditBridgeClient $bridge,
        private readonly EmulatorPresenceService $presence,
        private readonly RconService $rcon,
        private readonly AccountNotificationService $notifications,
        private readonly BadgeSellerEligibilityService $sellerEligibility
    ) {
    }

    public function purchase(
        int $buyerAccountId,
        int $listingId,
        int $payerUserId,
        int $expectedPrice,
        string $purchaseId
    ): array {
        $listing =
            $this->purchasableListing(
                $listingId
            );

        if (
            (int) $listing->buyer_price !==
            $expectedPrice
        ) {
            throw ValidationException::withMessages([
                'purchase' =>
                    'El precio de esta placa ha cambiado. Actualiza el mercado antes de confirmar la compra.',
            ]);
        }

        $buyer =
            $this->buyerCharacter(
                $buyerAccountId,
                $payerUserId
            );

        if (
            $this->userHasBadge(
                (int) $buyer->id,
                (string) $listing->badge_code
            )
        ) {
            throw ValidationException::withMessages([
                'payer_user_id' =>
                    $buyer->username .
                    ' ya tiene esta placa. No se ha realizado ningún cobro.',
            ]);
        }

        $pending =
            $this->pendingSaleForBadge(
                (int) $buyer->id,
                (string) $listing->badge_code
            );

        if ($pending) {
            throw ValidationException::withMessages([
                'purchase' =>
                    'Existe una compra anterior pendiente para esta placa. No se realizará otro cobro.',
            ]);
        }

        $existing = DB::table(
            'badge_marketplace_sales'
        )
            ->where(
                'purchase_id',
                $purchaseId
            )
            ->first();

        if ($existing) {
            return $this->resumeSale(
                (int) $existing->id
            );
        }

        $this->creditTransactions
            ->debitAndRun(
                    $buyerAccountId,
                    $payerUserId,
                    (int)
                    $listing->buyer_price,
                    'badge_marketplace_purchase',
                    $purchaseId,
                    [
                        'listing_id' =>
                            (int) $listing->id,
                        'creator_badge_id' =>
                            (int)
                            $listing->creator_badge_id,
                        'badge_code' =>
                            (string)
                            $listing->badge_code,
                        'seller_account_id' =>
                            (int)
                            $listing->seller_account_id,
                        'seller_user_id' =>
                            (int)
                            $listing->creator_user_id,
                        'seller_earnings' =>
                            (int)
                            $listing->seller_earnings,
                        'hotel_commission' =>
                            (int)
                            $listing->hotel_commission,
                        'buyer_price' =>
                            (int)
                            $listing->buyer_price,
                    ],
                    function () use (
                        $buyerAccountId,
                        $listingId,
                        $payerUserId,
                        $expectedPrice,
                        $purchaseId
                    ): array {
                        $locked =
                            $this->purchasableListing(
                                $listingId,
                                true
                            );

                        if (
                            (int) $locked->buyer_price !==
                            $expectedPrice
                        ) {
                            throw ValidationException::withMessages([
                                'purchase' =>
                                    'El precio de esta placa ha cambiado. No se ha realizado ningún cobro.',
                            ]);
                        }

                        $buyer =
                            $this->buyerCharacter(
                                $buyerAccountId,
                                $payerUserId,
                                true
                            );

                        $existingSale =
                            DB::table(
                                'badge_marketplace_sales'
                            )
                                ->where(
                                    'purchase_id',
                                    $purchaseId
                                )
                                ->lockForUpdate()
                                ->first();

                        if ($existingSale) {
                            return [
                                'result' => [
                                    'sale_id' =>
                                        (int)
                                        $existingSale->id,
                                ],
                                'metadata' => [
                                    'sale_id' =>
                                        (int)
                                        $existingSale->id,
                                    'listing_id' =>
                                        (int)
                                        $existingSale->listing_id,
                                ],
                            ];
                        }

                        $alreadyOwned =
                            DB::table(
                                'users_badges'
                            )
                                ->where(
                                    'user_id',
                                    $buyer->id
                                )
                                ->where(
                                    'badge_code',
                                    $locked->badge_code
                                )
                                ->lockForUpdate()
                                ->first([
                                    'id',
                                ]);

                        if ($alreadyOwned) {
                            throw ValidationException::withMessages([
                                'payer_user_id' =>
                                    $buyer->username .
                                    ' ya tiene esta placa. No se ha realizado ningún cobro.',
                            ]);
                        }

                        $otherPending =
                            DB::table(
                                'badge_marketplace_sales'
                            )
                                ->where(
                                    'buyer_user_id',
                                    $buyer->id
                                )
                                ->where(
                                    'badge_code',
                                    $locked->badge_code
                                )
                                ->whereIn(
                                    'status',
                                    [
                                        BadgeMarketplaceSale::STATUS_PAID_PENDING_DELIVERY,
                                        BadgeMarketplaceSale::STATUS_DELIVERED_PENDING_PAYOUT,
                                        BadgeMarketplaceSale::STATUS_MANUAL_REVIEW,
                                    ]
                                )
                                ->lockForUpdate()
                                ->first();

                        if ($otherPending) {
                            throw ValidationException::withMessages([
                                'purchase' =>
                                    'Ya existe una compra pendiente de esta placa. No se ha realizado otro cobro.',
                            ]);
                        }

                        $refundId =
                            (string)
                            Str::uuid();

                        $saleId =
                            DB::table(
                                'badge_marketplace_sales'
                            )
                                ->insertGetId([
                                    'purchase_id' =>
                                        $purchaseId,
                                    'refund_id' =>
                                        $refundId,
                                    'listing_id' =>
                                        (int)
                                        $locked->id,
                                    'creator_badge_id' =>
                                        (int)
                                        $locked->creator_badge_id,
                                    'seller_account_id' =>
                                        (int)
                                        $locked->seller_account_id,
                                    'seller_user_id' =>
                                        (int)
                                        $locked->creator_user_id,
                                    'buyer_account_id' =>
                                        $buyerAccountId,
                                    'buyer_user_id' =>
                                        (int)
                                        $buyer->id,
                                    'badge_code' =>
                                        (string)
                                        $locked->badge_code,
                                    'badge_name' =>
                                        (string)
                                        $locked->badge_name,
                                    'seller_username' =>
                                        (string)
                                        $locked->creator_username,
                                    'buyer_username' =>
                                        (string)
                                        $buyer->username,
                                    'seller_earnings' =>
                                        (int)
                                        $locked->seller_earnings,
                                    'hotel_commission' =>
                                        (int)
                                        $locked->hotel_commission,
                                    'buyer_price' =>
                                        (int)
                                        $locked->buyer_price,
                                    'status' =>
                                        BadgeMarketplaceSale::STATUS_PAID_PENDING_DELIVERY,
                                    'created_at' =>
                                        now(),
                                    'updated_at' =>
                                        now(),
                                ]);

                        return [
                            'result' => [
                                'sale_id' =>
                                    $saleId,
                            ],
                            'metadata' => [
                                'sale_id' =>
                                    $saleId,
                                'listing_id' =>
                                    (int)
                                    $locked->id,
                                'creator_badge_id' =>
                                    (int)
                                    $locked->creator_badge_id,
                                'badge_code' =>
                                    (string)
                                    $locked->badge_code,
                                'seller_account_id' =>
                                    (int)
                                    $locked->seller_account_id,
                                'seller_earnings' =>
                                    (int)
                                    $locked->seller_earnings,
                                'hotel_commission' =>
                                    (int)
                                    $locked->hotel_commission,
                            ],
                        ];
                    }
                );

        return $this->finalizeChargedPurchase(
            $purchaseId,
            $listing,
            $buyer
        );
    }

    private function finalizeChargedPurchase(
        string $purchaseId,
        object $listing,
        object $buyer
    ): array {
        $sale =
            DB::table(
                'badge_marketplace_sales'
            )
                ->where(
                    'purchase_id',
                    $purchaseId
                )
                ->first();

        if (! $sale) {
            try {
                $this->creditRefunds->refund(
                    (string)
                    Str::uuid(),
                    $purchaseId,
                    (int)
                    $listing->buyer_price,
                    'badge_marketplace_sale_missing_after_charge',
                    [
                        'listing_id' =>
                            (int)
                            $listing->id,
                        'creator_badge_id' =>
                            (int)
                            $listing->creator_badge_id,
                        'badge_code' =>
                            (string)
                            $listing->badge_code,
                        'buyer_user_id' =>
                            (int)
                            $buyer->id,
                    ]
                );
            } catch (Throwable $refundException) {
                report(
                    $refundException
                );

                throw new RuntimeException(
                    'El cobro se confirmó, pero no se pudo crear la venta y el reembolso requiere revisión manual.',
                    0,
                    $refundException
                );
            }

            throw ValidationException::withMessages([
                'purchase' =>
                    'No se pudo completar la compra. Se te han devuelto los ' .
                    (int)
                    $listing->buyer_price .
                    ' créditos.',
            ]);
        }

        try {
            return $this->resumeSale(
                (int)
                $sale->id
            );
        } catch (
            ValidationException $exception
        ) {
            throw $exception;
        } catch (Throwable $exception) {
            report(
                $exception
            );

            $fresh =
                $this->sale(
                    (int)
                    $sale->id
                );

            if (
                $fresh->status ===
                BadgeMarketplaceSale::STATUS_COMPLETED
            ) {
                return $this->result(
                    $fresh,
                    true
                );
            }

            if (
                $fresh->status ===
                BadgeMarketplaceSale::STATUS_REFUNDED
            ) {
                throw ValidationException::withMessages([
                    'purchase' =>
                        'La compra no pudo completarse y el cobro fue reembolsado.',
                ]);
            }

            if (
                ! $this->userHasBadge(
                    (int)
                    $fresh->buyer_user_id,
                    (string)
                    $fresh->badge_code
                )
            ) {
                $this->refundDeliveryFailure(
                    $fresh,
                    $exception
                );

                throw new RuntimeException(
                    'La compra no pudo recuperarse tras el cobro.'
                );
            }

            if (
                $fresh->delivered_at ===
                    null ||
                $fresh->status ===
                    BadgeMarketplaceSale::STATUS_PAID_PENDING_DELIVERY
            ) {
                $fresh =
                    $this->markDelivered(
                        $fresh
                    );
            }

            $fresh =
                $this->markPayoutManualReview(
                    $fresh,
                    'Error inesperado posterior al cobro con la placa ya entregada: ' .
                    $exception->getMessage(),
                    $fresh->payout_transaction_id
                        ?? null
                );

            return $this->result(
                $fresh,
                true
            );
        }
    }
    public function resumeSale(
        int $saleId
    ): array {
        $sale =
            $this->sale(
                $saleId
            );

        if (
            $sale->status ===
            BadgeMarketplaceSale::STATUS_COMPLETED
        ) {
            return $this->result(
                $sale,
                false
            );
        }

        if (
            $sale->status ===
            BadgeMarketplaceSale::STATUS_REFUNDED
        ) {
            throw ValidationException::withMessages([
                'purchase' =>
                    'Esta compra ya fue reembolsada.',
            ]);
        }

        if (
            $sale->status ===
            BadgeMarketplaceSale::STATUS_PAID_PENDING_DELIVERY
        ) {
            $sale =
                $this->deliver(
                    $sale
                );
        }

        if (
            in_array(
                $sale->status,
                [
                    BadgeMarketplaceSale::STATUS_DELIVERED_PENDING_PAYOUT,
                    BadgeMarketplaceSale::STATUS_MANUAL_REVIEW,
                ],
                true
            ) &&
            $this->userHasBadge(
                (int) $sale->buyer_user_id,
                (string) $sale->badge_code
            )
        ) {
            $sale =
                $this->paySeller(
                    $sale
                );
        }

        return $this->result(
            $sale,
            true
        );
    }

    private function deliver(
        object $sale
    ): object {
        if (
            $this->userHasBadge(
                (int) $sale->buyer_user_id,
                (string) $sale->badge_code
            )
        ) {
            return $this->markDelivered(
                $sale
            );
        }

        $buyer =
            DB::table('users')
                ->where(
                    'id',
                    $sale->buyer_user_id
                )
                ->first([
                    'id',
                    'username',
                    'online',
                ]);

        if (! $buyer) {
            return $this->refundDeliveryFailure(
                $sale,
                new RuntimeException(
                    'El personaje comprador ya no existe.'
                )
            );
        }

        try {
            $effectiveOnline =
                $this->presence
                    ->effectiveOnlineState(
                        (int) $buyer->id,
                        $buyer->online
                    );

            $granted = false;

            if (! $effectiveOnline) {
                $granted =
                    $this->grantBadgeOfflineIfStillOffline(
                        (int) $buyer->id,
                        (string) $sale->badge_code
                    );
            }

            if (! $granted) {
                $buyer =
                    DB::table('users')
                        ->where(
                            'id',
                            $sale->buyer_user_id
                        )
                        ->first([
                            'id',
                            'username',
                            'online',
                        ]);

                if (! $buyer) {
                    throw new RuntimeException(
                        'El personaje comprador ya no existe.'
                    );
                }

                $this->rcon->giveBadge(
                    $buyer,
                    (string)
                    $sale->badge_code
                );
            }

            if (
                ! $this->userHasBadge(
                    (int) $sale->buyer_user_id,
                    (string) $sale->badge_code
                )
            ) {
                throw new RuntimeException(
                    'No se confirmó la placa comprada en users_badges.'
                );
            }
        } catch (Throwable $deliveryException) {
            if (
                $this->userHasBadge(
                    (int) $sale->buyer_user_id,
                    (string) $sale->badge_code
                )
            ) {
                return $this->markDelivered(
                    $sale
                );
            }

            return $this->refundDeliveryFailure(
                $sale,
                $deliveryException
            );
        }

        return $this->markDelivered(
            $sale
        );
    }

    private function markDelivered(
        object $sale
    ): object {
        DB::table(
            'badge_marketplace_sales'
        )
            ->where(
                'id',
                $sale->id
            )
            ->whereIn(
                'status',
                [
                    BadgeMarketplaceSale::STATUS_PAID_PENDING_DELIVERY,
                    BadgeMarketplaceSale::STATUS_MANUAL_REVIEW,
                ]
            )
            ->update([
                'status' =>
                    BadgeMarketplaceSale::STATUS_DELIVERED_PENDING_PAYOUT,
                'delivered_at' =>
                    $sale->delivered_at
                        ?? now(),
                'error_message' =>
                    null,
                'updated_at' =>
                    now(),
            ]);

        return $this->sale(
            (int) $sale->id
        );
    }

    private function refundDeliveryFailure(
        object $sale,
        Throwable $deliveryException
    ): object {
        try {
            $this->creditRefunds->refund(
                (string)
                $sale->refund_id,
                (string)
                $sale->purchase_id,
                (int)
                $sale->buyer_price,
                'badge_marketplace_delivery_failed',
                [
                    'badge_marketplace_sale_id' =>
                        (int)
                        $sale->id,
                    'listing_id' =>
                        (int)
                        $sale->listing_id,
                    'creator_badge_id' =>
                        (int)
                        $sale->creator_badge_id,
                    'badge_code' =>
                        (string)
                        $sale->badge_code,
                    'buyer_user_id' =>
                        (int)
                        $sale->buyer_user_id,
                    'delivery_error' =>
                        mb_substr(
                            $deliveryException
                                ->getMessage(),
                            0,
                            500
                        ),
                ]
            );
        } catch (Throwable $refundException) {
            DB::table(
                'badge_marketplace_sales'
            )
                ->where(
                    'id',
                    $sale->id
                )
                ->update([
                    'status' =>
                        BadgeMarketplaceSale::STATUS_MANUAL_REVIEW,
                    'error_message' =>
                        mb_substr(
                            'Entrega: ' .
                            $deliveryException
                                ->getMessage() .
                            ' | Reembolso: ' .
                            $refundException
                                ->getMessage(),
                            0,
                            2000
                        ),
                    'updated_at' =>
                        now(),
                ]);

            throw new RuntimeException(
                'La entrega falló y el reembolso requiere revisión manual.',
                0,
                $refundException
            );
        }

        DB::table(
            'badge_marketplace_sales'
        )
            ->where(
                'id',
                $sale->id
            )
            ->update([
                'status' =>
                    BadgeMarketplaceSale::STATUS_REFUNDED,
                'error_message' =>
                    mb_substr(
                        $deliveryException
                            ->getMessage(),
                        0,
                        2000
                    ),
                'refunded_at' =>
                    now(),
                'updated_at' =>
                    now(),
            ]);

        throw ValidationException::withMessages([
            'purchase' =>
                'No se pudo entregar la placa. Se te han devuelto los ' .
                (int) $sale->buyer_price .
                ' créditos.',
        ]);
    }

    private function paySeller(
        object $sale
    ): object {
        if (
            $sale->paid_out_at !== null ||
            $sale->status ===
            BadgeMarketplaceSale::STATUS_COMPLETED
        ) {
            return $sale;
        }

        $amount =
            (int)
            $sale->seller_earnings;

        if ($amount === 0) {
            return $this->finishSale(
                $sale,
                'none',
                null,
                null,
                null
            );
        }

        $transactionId =
            'badge-marketplace-sale-' .
            (int) $sale->id .
            '-payout';

        $recovered =
            $this->recoverBridgePayout(
                $sale,
                $transactionId
            );

        if ($recovered !== null) {
            return $this->finishSale(
                $sale,
                'online_credit_bridge',
                $transactionId,
                $recovered['balance_before'],
                $recovered['balance_after']
            );
        }

        $seller =
            DB::table('users')
                ->where(
                    'id',
                    $sale->seller_user_id
                )
                ->first([
                    'id',
                    'credits',
                    'online',
                ]);

        if (! $seller) {
            return $this->markPayoutManualReview(
                $sale,
                'El personaje vendedor ya no existe.'
            );
        }

        $effectiveOnline =
            $this->presence
                ->effectiveOnlineState(
                    (int) $seller->id,
                    $seller->online
                );

        if (! $effectiveOnline) {
            $offline =
                $this->creditSellerOfflineIfStillOffline(
                    $sale,
                    $transactionId
                );

            if ($offline !== null) {
                $this->notifySeller(
                    $offline
                );

                return $offline;
            }
        }

        try {
            try {
                $response =
                    $this->bridge->credit(
                        (int)
                        $sale->seller_user_id,
                        $amount,
                        $transactionId
                    );
            } catch (
                CreditBridgeException
            ) {
                $response =
                    $this->bridge->credit(
                        (int)
                        $sale->seller_user_id,
                        $amount,
                        $transactionId
                    );
            }

            $payment =
                $this->resolveOnlinePayout(
                    (int)
                    $sale->seller_user_id,
                    $amount,
                    $transactionId,
                    $response
                );

            return $this->finishSale(
                $sale,
                'online_credit_bridge',
                $transactionId,
                $payment['balance_before'],
                $payment['balance_after']
            );
        } catch (Throwable $exception) {
            $recovered =
                $this->recoverBridgePayout(
                    $sale,
                    $transactionId
                );

            if ($recovered !== null) {
                return $this->finishSale(
                    $sale,
                    'online_credit_bridge',
                    $transactionId,
                    $recovered['balance_before'],
                    $recovered['balance_after']
                );
            }

            report($exception);

            return $this->markPayoutManualReview(
                $sale,
                $exception->getMessage(),
                $transactionId
            );
        }
    }

    private function creditSellerOfflineIfStillOffline(
        object $sale,
        string $transactionId
    ): ?object {
        return DB::transaction(
            function () use (
                $sale,
                $transactionId
            ): ?object {
                $locked =
                    DB::table(
                        'badge_marketplace_sales'
                    )
                        ->where(
                            'id',
                            $sale->id
                        )
                        ->lockForUpdate()
                        ->first();

                if (! $locked) {
                    throw new RuntimeException(
                        'La venta ya no existe.'
                    );
                }

                if (
                    $locked->status ===
                    BadgeMarketplaceSale::STATUS_COMPLETED
                ) {
                    return $locked;
                }

                $seller =
                    DB::table('users')
                        ->where(
                            'id',
                            $locked->seller_user_id
                        )
                        ->lockForUpdate()
                        ->first([
                            'id',
                            'credits',
                            'online',
                        ]);

                if (! $seller) {
                    throw new RuntimeException(
                        'El personaje vendedor ya no existe.'
                    );
                }

                if (
                    (string) $seller->online !==
                    '0'
                ) {
                    return null;
                }

                $balanceBefore =
                    (int)
                    $seller->credits;

                $amount =
                    (int)
                    $locked->seller_earnings;

                $updated =
                    DB::table('users')
                        ->where(
                            'id',
                            $seller->id
                        )
                        ->increment(
                            'credits',
                            $amount
                        );

                if ($updated !== 1) {
                    throw new RuntimeException(
                        'No se pudo acreditar la ganancia al vendedor.'
                    );
                }

                $balanceAfter =
                    $balanceBefore +
                    $amount;

                $this->recordSellerPayoutLedger(
                    $locked,
                    $transactionId,
                    'offline_db',
                    $balanceBefore,
                    $balanceAfter
                );

                DB::table(
                    'badge_marketplace_sales'
                )
                    ->where(
                        'id',
                        $locked->id
                    )
                    ->update([
                        'status' =>
                            BadgeMarketplaceSale::STATUS_COMPLETED,
                        'payout_channel' =>
                            'offline_db',
                        'payout_transaction_id' =>
                            $transactionId,
                        'seller_balance_before' =>
                            $balanceBefore,
                        'seller_balance_after' =>
                            $balanceAfter,
                        'paid_out_at' =>
                            now(),
                        'error_message' =>
                            null,
                        'updated_at' =>
                            now(),
                    ]);

                return $this->sale(
                    (int) $locked->id
                );
            },
            5
        );
    }

    private function resolveOnlinePayout(
        int $userId,
        int $amount,
        string $transactionId,
        array $response
    ): array {
        $message =
            (string)
            (
                $response['message']
                ?? ''
            );

        $parts =
            explode(
                '|',
                $message
            );

        if (
            (int)
            (
                $response['status']
                ?? -1
            ) === 0 &&
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
                'balance_before' =>
                    (int)
                    $parts[1],
                'balance_after' =>
                    (int)
                    $parts[2],
            ];
        }

        $bridge =
            DB::table(
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
            (int) $bridge->user_id ===
                $userId &&
            (int) $bridge->amount ===
                $amount &&
            (string) $bridge->operation ===
                'credit' &&
            (string) $bridge->status ===
                'applied' &&
            (
                (int)
                $bridge->balance_after -
                (int)
                $bridge->balance_before
            ) === $amount
        ) {
            return [
                'balance_before' =>
                    (int)
                    $bridge->balance_before,
                'balance_after' =>
                    (int)
                    $bridge->balance_after,
            ];
        }

        throw new RuntimeException(
            'Morningstar no pudo confirmar el pago al vendedor.'
        );
    }

    private function recoverBridgePayout(
        object $sale,
        string $transactionId
    ): ?array {
        $bridge =
            DB::table(
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

        if (! $bridge) {
            return null;
        }

        if (
            (int) $bridge->user_id !==
                (int)
                $sale->seller_user_id ||
            (int) $bridge->amount !==
                (int)
                $sale->seller_earnings ||
            (string) $bridge->operation !==
                'credit'
        ) {
            throw new RuntimeException(
                'La transacción de pago al vendedor entra en conflicto con esta venta.'
            );
        }

        if (
            (string) $bridge->status ===
            'pending'
        ) {
            throw new RuntimeException(
                'El pago al vendedor está pendiente de confirmar.'
            );
        }

        if (
            (string) $bridge->status !==
            'applied'
        ) {
            return null;
        }

        if (
            (
                (int)
                $bridge->balance_after -
                (int)
                $bridge->balance_before
            ) !==
            (int)
            $sale->seller_earnings
        ) {
            throw new RuntimeException(
                'El saldo registrado del pago al vendedor no es coherente.'
            );
        }

        return [
            'balance_before' =>
                (int)
                $bridge->balance_before,
            'balance_after' =>
                (int)
                $bridge->balance_after,
        ];
    }

    private function finishSale(
        object $sale,
        string $channel,
        ?string $transactionId,
        ?int $balanceBefore,
        ?int $balanceAfter
    ): object {
        DB::transaction(
            function () use (
                $sale,
                $channel,
                $transactionId,
                $balanceBefore,
                $balanceAfter
            ): void {
                $locked =
                    DB::table(
                        'badge_marketplace_sales'
                    )
                        ->where(
                            'id',
                            $sale->id
                        )
                        ->lockForUpdate()
                        ->first();

                if (! $locked) {
                    throw new RuntimeException(
                        'La venta ya no existe.'
                    );
                }

                if (
                    $locked->status ===
                    BadgeMarketplaceSale::STATUS_COMPLETED
                ) {
                    return;
                }

                if (
                    (int)
                    $locked->seller_earnings > 0
                ) {
                    if (
                        $transactionId === null ||
                        $balanceBefore === null ||
                        $balanceAfter === null
                    ) {
                        throw new RuntimeException(
                            'Faltan datos para auditar el pago al vendedor.'
                        );
                    }

                    $this->recordSellerPayoutLedger(
                        $locked,
                        $transactionId,
                        $channel,
                        $balanceBefore,
                        $balanceAfter
                    );
                }

                DB::table(
                    'badge_marketplace_sales'
                )
                    ->where(
                        'id',
                        $locked->id
                    )
                    ->update([
                        'status' =>
                            BadgeMarketplaceSale::STATUS_COMPLETED,
                        'payout_channel' =>
                            $channel,
                        'payout_transaction_id' =>
                            $transactionId,
                        'seller_balance_before' =>
                            $balanceBefore,
                        'seller_balance_after' =>
                            $balanceAfter,
                        'paid_out_at' =>
                            now(),
                        'error_message' =>
                            null,
                        'updated_at' =>
                            now(),
                    ]);

                DB::table(
                    'badge_seller_licenses'
                )
                    ->where(
                        'account_id',
                        $locked->seller_account_id
                    )
                    ->where(
                        'status',
                        'active'
                    )
                    ->whereNull(
                        'revoked_at'
                    )
                    ->update([
                        'last_activity_at' =>
                            now(),
                        'updated_at' =>
                            now(),
                    ]);
            },
            5
        );

        $fresh =
            $this->sale(
                (int) $sale->id
            );

        $this->notifySeller(
            $fresh
        );

        return $fresh;
    }

    private function recordSellerPayoutLedger(
        object $sale,
        string $transactionId,
        string $channel,
        int $balanceBefore,
        int $balanceAfter
    ): void {
        $amount =
            (int)
            $sale->seller_earnings;

        if ($amount <= 0) {
            return;
        }

        if (
            $balanceAfter -
            $balanceBefore !==
            $amount
        ) {
            throw new RuntimeException(
                'El saldo del payout no coincide con la ganancia del vendedor.'
            );
        }

        $existing =
            DB::table(
                'credit_transactions'
            )
                ->where(
                    'purchase_id',
                    $transactionId
                )
                ->where(
                    'type',
                    'badge_marketplace_payout'
                )
                ->first();

        if ($existing) {
            if (
                (int)
                $existing->account_id !==
                    (int)
                    $sale->seller_account_id ||
                (int)
                $existing->user_id !==
                    (int)
                    $sale->seller_user_id ||
                (int)
                $existing->amount !==
                    $amount ||
                (int)
                $existing->balance_before !==
                    $balanceBefore ||
                (int)
                $existing->balance_after !==
                    $balanceAfter
            ) {
                throw new RuntimeException(
                    'El ledger existente del payout entra en conflicto con la venta.'
                );
            }

            return;
        }

        DB::table(
            'credit_transactions'
        )->insert([
            'account_id' =>
                (int)
                $sale->seller_account_id,
            'user_id' =>
                (int)
                $sale->seller_user_id,
            'purchase_id' =>
                $transactionId,
            'type' =>
                'badge_marketplace_payout',
            'amount' =>
                $amount,
            'balance_before' =>
                $balanceBefore,
            'balance_after' =>
                $balanceAfter,
            'metadata' =>
                json_encode(
                    [
                        'source' =>
                            'badge_marketplace_sale',
                        'sale_id' =>
                            (int)
                            $sale->id,
                        'source_purchase_id' =>
                            (string)
                            $sale->purchase_id,
                        'listing_id' =>
                            (int)
                            $sale->listing_id,
                        'creator_badge_id' =>
                            (int)
                            $sale->creator_badge_id,
                        'badge_code' =>
                            (string)
                            $sale->badge_code,
                        'seller_account_id' =>
                            (int)
                            $sale->seller_account_id,
                        'seller_user_id' =>
                            (int)
                            $sale->seller_user_id,
                        'seller_earnings' =>
                            $amount,
                        'hotel_commission' =>
                            (int)
                            $sale->hotel_commission,
                        'payout_channel' =>
                            $channel,
                        'payout_transaction_id' =>
                            $transactionId,
                    ],
                    JSON_THROW_ON_ERROR
                ),
            'created_at' =>
                now(),
        ]);
    }
    private function notifySeller(
        object $sale
    ): void {
        try {
            $this->notifications->send(
                (int)
                $sale->seller_account_id,
                'badge.marketplace_sale',
                'Has vendido una placa',
                $sale->buyer_username .
                    ' ha comprado tu placa "' .
                    $sale->badge_name .
                    '". Has recibido ' .
                    (int)
                    $sale->seller_earnings .
                    ' créditos.',
                route(
                    'marketplace.badges.index',
                    [
                        'tab' =>
                            'seller',
                    ],
                    false
                ),
                [
                    'badge_marketplace_sale_id' =>
                        (int)
                        $sale->id,
                    'listing_id' =>
                        (int)
                        $sale->listing_id,
                    'creator_badge_id' =>
                        (int)
                        $sale->creator_badge_id,
                    'badge_code' =>
                        (string)
                        $sale->badge_code,
                    'buyer_account_id' =>
                        (int)
                        $sale->buyer_account_id,
                    'buyer_user_id' =>
                        (int)
                        $sale->buyer_user_id,
                    'seller_earnings' =>
                        (int)
                        $sale->seller_earnings,
                ],
                'badge-marketplace-sale:' .
                    (int)
                    $sale->id .
                    ':seller'
            );
        } catch (Throwable $exception) {
            report($exception);
        }
    }

    private function markPayoutManualReview(
        object $sale,
        string $message,
        ?string $transactionId = null
    ): object {
        DB::table(
            'badge_marketplace_sales'
        )
            ->where(
                'id',
                $sale->id
            )
            ->update([
                'status' =>
                    BadgeMarketplaceSale::STATUS_MANUAL_REVIEW,
                'payout_transaction_id' =>
                    $transactionId,
                'error_message' =>
                    mb_substr(
                        $message,
                        0,
                        2000
                    ),
                'updated_at' =>
                    now(),
            ]);

        return $this->sale(
            (int) $sale->id
        );
    }

    private function grantBadgeOfflineIfStillOffline(
        int $userId,
        string $badgeCode
    ): bool {
        return DB::transaction(
            function () use (
                $userId,
                $badgeCode
            ): bool {
                $user =
                    DB::table('users')
                        ->where(
                            'id',
                            $userId
                        )
                        ->lockForUpdate()
                        ->first([
                            'id',
                            'online',
                        ]);

                if (! $user) {
                    throw new RuntimeException(
                        'El personaje comprador ya no existe.'
                    );
                }

                if (
                    (string) $user->online !==
                    '0'
                ) {
                    return false;
                }

                $alreadyOwned =
                    DB::table(
                        'users_badges'
                    )
                        ->where(
                            'user_id',
                            $userId
                        )
                        ->where(
                            'badge_code',
                            $badgeCode
                        )
                        ->exists();

                if (! $alreadyOwned) {
                    DB::table(
                        'users_badges'
                    )
                        ->insert([
                            'user_id' =>
                                $userId,
                            'slot_id' =>
                                0,
                            'badge_code' =>
                                $badgeCode,
                        ]);
                }

                return true;
            },
            5
        );
    }

    private function purchasableListing(
        int $listingId,
        bool $lock = false
    ): object {
        $query =
            DB::table(
                'badge_marketplace_listings as listings'
            )
                ->join(
                    'creator_badges as badges',
                    'badges.id',
                    '=',
                    'listings.creator_badge_id'
                )
                ->join(
                    'badge_submissions as submissions',
                    'submissions.id',
                    '=',
                    'badges.badge_submission_id'
                )
                ->join(
                    'users as creator',
                    'creator.id',
                    '=',
                    'badges.creator_user_id'
                )
                ->join(
                    'account_characters as creator_link',
                    function ($join): void {
                        $join->on(
                            'creator_link.user_id',
                            '=',
                            'badges.creator_user_id'
                        )
                            ->on(
                                'creator_link.account_id',
                                '=',
                                'badges.account_id'
                            );
                    }
                )
                ->where(
                    'listings.id',
                    $listingId
                )
                ->where(
                    'listings.status',
                    'active'
                )
                ->where(
                    'badges.marketplace_enabled',
                    1
                )
                ->where(
                    'submissions.status',
                    'approved'
                )
                ->whereNull(
                    'creator_link.archived_at'
                );

        if ($lock) {
            $query->lockForUpdate();
        }

        $listing =
            $query->first([
                'listings.id',
                'listings.creator_badge_id',
                'listings.seller_account_id',
                'listings.seller_earnings',
                'listings.hotel_commission',
                'listings.buyer_price',
                'badges.account_id as badge_account_id',
                'badges.creator_user_id',
                'badges.badge_code',
                'badges.badge_name',
                'badges.badge_description',
                'creator.username as creator_username',
            ]);

        if (! $listing) {
            throw ValidationException::withMessages([
                'purchase' =>
                    'Esta placa ya no está disponible en el mercado.',
            ]);
        }

        if (
            (int) $listing->seller_account_id !==
            (int) $listing->badge_account_id
        ) {
            throw new RuntimeException(
                'El vendedor del anuncio no coincide con la cuenta autora de la placa.'
            );
        }

        if (
            ! $this->sellerEligibility
                ->canSell(
                    (int)
                    $listing->seller_account_id
                )
        ) {
            throw ValidationException::withMessages([
                'purchase' =>
                    'El vendedor ya no tiene autorización activa para vender esta placa.',
            ]);
        }

        return $listing;
    }

    private function buyerCharacter(
        int $accountId,
        int $userId,
        bool $lock = false
    ): object {
        $query =
            DB::table('users')
                ->join(
                    'account_characters as ac',
                    'ac.user_id',
                    '=',
                    'users.id'
                )
                ->where(
                    'users.id',
                    $userId
                )
                ->where(
                    'ac.account_id',
                    $accountId
                )
                ->whereNull(
                    'ac.archived_at'
                );

        if ($lock) {
            $query->lockForUpdate();
        }

        $user =
            $query->first([
                'users.id',
                'users.username',
                'users.credits',
                'users.online',
            ]);

        if (! $user) {
            throw ValidationException::withMessages([
                'payer_user_id' =>
                    'El personaje pagador no pertenece a tu cuenta o está archivado.',
            ]);
        }

        return $user;
    }

    private function pendingSaleForBadge(
        int $userId,
        string $badgeCode
    ): ?object {
        return DB::table(
            'badge_marketplace_sales'
        )
            ->where(
                'buyer_user_id',
                $userId
            )
            ->where(
                'badge_code',
                $badgeCode
            )
            ->whereIn(
                'status',
                [
                    BadgeMarketplaceSale::STATUS_PAID_PENDING_DELIVERY,
                    BadgeMarketplaceSale::STATUS_DELIVERED_PENDING_PAYOUT,
                    BadgeMarketplaceSale::STATUS_MANUAL_REVIEW,
                ]
            )
            ->orderByDesc(
                'id'
            )
            ->first();
    }

    private function userHasBadge(
        int $userId,
        string $badgeCode
    ): bool {
        return DB::table(
            'users_badges'
        )
            ->where(
                'user_id',
                $userId
            )
            ->where(
                'badge_code',
                $badgeCode
            )
            ->exists();
    }

    private function sale(
        int $saleId
    ): object {
        $sale =
            DB::table(
                'badge_marketplace_sales'
            )
                ->where(
                    'id',
                    $saleId
                )
                ->first();

        if (! $sale) {
            throw new RuntimeException(
                'La venta no existe.'
            );
        }

        return $sale;
    }

    private function result(
        object $sale,
        bool $resumed
    ): array {
        return [
            'sale_id' =>
                (int)
                $sale->id,
            'status' =>
                (string)
                $sale->status,
            'badge_name' =>
                (string)
                $sale->badge_name,
            'buyer_username' =>
                (string)
                $sale->buyer_username,
            'buyer_price' =>
                (int)
                $sale->buyer_price,
            'seller_earnings' =>
                (int)
                $sale->seller_earnings,
            'delivered' =>
                $sale->delivered_at !==
                null,
            'seller_paid' =>
                $sale->paid_out_at !==
                null,
            'resumed' =>
                $resumed,
        ];
    }
}
