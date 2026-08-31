<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\DB;

class CreditReconcileCommand extends Command
{
    protected $signature = 'credits:reconcile
        {--id= : Revisar únicamente una operación concreta}
        {--all : Mostrar también estados no problemáticos}';

    protected $description =
        'Audita compras, reembolsos, ajustes administrativos, ledger y CreditBridge sin modificar datos.';

    private int $issues = 0;

    public function handle(): int
    {
        $id = trim((string) $this->option('id'));
        $showAll = (bool) $this->option('all');

        $this->newLine();
        $this->info('=== RECONCILIACIÓN ECONÓMICA ===');

        if ($id !== '') {
            $this->line("Filtro: {$id}");
        }

        $this->auditPurchases($id, $showAll);
        $this->auditRefunds($id, $showAll);
        $this->auditAdminAdjustments($id, $showAll);
        $this->auditLedger($id, $showAll);
        $this->auditBridge($id, $showAll);

        $this->newLine();
        $this->info('=== RESULTADO ===');

        if ($this->issues === 0) {
            $this->info(
                'OK: no se han encontrado inconsistencias.'
            );

            return self::SUCCESS;
        }

        $this->error(
            "INCIDENCIAS: {$this->issues}"
        );

        return self::FAILURE;
    }

    private function auditPurchases(
        string $filterId,
        bool $showAll
    ): void {
        $this->newLine();
        $this->comment('COMPRAS');

        $query = DB::table('purchase_operations')
            ->orderBy('created_at');

        if ($filterId !== '') {
            $query->where('id', $filterId);
        }

        $rows = $query->get();

        if ($rows->isEmpty()) {
            $this->line(
                'Sin compras coincidentes.'
            );

            return;
        }

        foreach ($rows as $row) {
            $problems = [];

            $ledger = DB::table(
                'credit_transactions'
            )
                ->where(
                    'purchase_id',
                    $row->id
                )
                ->get();

            $bridge = DB::table(
                'credit_bridge_transactions'
            )
                ->where(
                    'transaction_id',
                    $row->id
                )
                ->first();

            $refunds = DB::table(
                'credit_refunds'
            )
                ->where(
                    'original_purchase_id',
                    $row->id
                )
                ->orderBy('created_at')
                ->get();

            if (
                (string) $row->status ===
                'completed'
            ) {
                if ($ledger->count() !== 1) {
                    $problems[] =
                        'completed pero ledger=' .
                        $ledger->count();
                }

                $this->checkPurchasePaymentBridge(
                    $row,
                    $bridge,
                    $problems
                );
            }

            if (
                (string) $row->status ===
                'processing'
            ) {
                $expired =
                    ! $row->lease_expires_at ||
                    Carbon::parse(
                        $row->lease_expires_at
                    )->isPast();

                if ($expired) {
                    $problems[] =
                        'processing con lease caducado';
                }
            }

            if (
                (string) $row->status ===
                'manual_review'
            ) {
                $problems[] =
                    'requiere revisión manual: ' .
                    (
                        $row->error_code ??
                        'sin código'
                    );
            }

            if (
                (string) $row->status ===
                'refunded'
            ) {
                /*
                 * Un reembolso NO borra el débito original.
                 * El ledger de compra es historial contable y
                 * debe conservarse junto al crédito compensatorio.
                 */
                if ($ledger->count() !== 1) {
                    $problems[] =
                        'refunded pero ledger original=' .
                        $ledger->count() .
                        ' (esperado 1)';
                }

                if ($refunds->count() !== 1) {
                    $problems[] =
                        'refunded pero credit_refunds=' .
                        $refunds->count() .
                        ' (esperado 1)';
                } else {
                    $refund = $refunds->first();

                    if (
                        (string) $refund->status !==
                        'completed'
                    ) {
                        $problems[] =
                            'credit_refund status=' .
                            $refund->status;
                    }

                    if (
                        (int) $refund->amount !==
                        (int) $row->amount
                    ) {
                        $problems[] =
                            'importe refund != compra';
                    }

                    if (
                        (int) $refund->account_id !==
                        (int) $row->account_id
                    ) {
                        $problems[] =
                            'refund account_id != compra';
                    }

                    if (
                        (int) $refund->user_id !==
                        (int) $row->user_id
                    ) {
                        $problems[] =
                            'refund user_id != compra';
                    }
                }
            }

            if ($bridge) {
                $this->checkBridgeArithmetic(
                    $bridge,
                    "compra {$row->id}",
                    $problems
                );
            }

            foreach ($ledger as $entry) {
                $this->checkLedgerArithmetic(
                    $entry,
                    $problems
                );

                if (
                    (int) $entry->amount !==
                    -1 * (int) $row->amount
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'tiene importe distinto a la compra';
                }

                if (
                    (int) $entry->account_id !==
                    (int) $row->account_id
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'account_id distinto';
                }

                if (
                    (int) $entry->user_id !==
                    (int) $row->user_id
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'user_id distinto';
                }
            }

            $this->printRow(
                'PURCHASE',
                (string) $row->id,
                (string) $row->status,
                $problems,
                $showAll
            );
        }
    }

    private function auditRefunds(
        string $filterId,
        bool $showAll
    ): void {
        $this->newLine();
        $this->comment('REEMBOLSOS');

        $query = DB::table(
            'credit_refunds'
        )->orderBy('created_at');

        if ($filterId !== '') {
            $query->where(
                function ($query) use (
                    $filterId
                ) {
                    $query
                        ->where(
                            'id',
                            $filterId
                        )
                        ->orWhere(
                            'original_purchase_id',
                            $filterId
                        )
                        ->orWhere(
                            'bridge_transaction_id',
                            $filterId
                        );
                }
            );
        }

        $rows = $query->get();

        if ($rows->isEmpty()) {
            $this->line(
                'Sin reembolsos coincidentes.'
            );

            return;
        }

        foreach ($rows as $row) {
            $problems = [];

            $purchase = DB::table(
                'purchase_operations'
            )
                ->where(
                    'id',
                    $row->original_purchase_id
                )
                ->first();

            $ledgerId =
                'refund-' .
                $row->id;

            $ledger = DB::table(
                'credit_transactions'
            )
                ->where(
                    'purchase_id',
                    $ledgerId
                )
                ->get();

            $bridge = null;

            if (
                $row->bridge_transaction_id
            ) {
                $bridge = DB::table(
                    'credit_bridge_transactions'
                )
                    ->where(
                        'transaction_id',
                        $row->bridge_transaction_id
                    )
                    ->first();
            }

            if (! $purchase) {
                $problems[] =
                    'sin compra original';
            } else {
                if (
                    (string) $purchase->status !==
                    'refunded'
                ) {
                    $problems[] =
                        'compra original status=' .
                        $purchase->status .
                        ' (esperado refunded)';
                }

                if (
                    (int) $purchase->amount !==
                    (int) $row->amount
                ) {
                    $problems[] =
                        'importe != compra original';
                }

                if (
                    (int) $purchase->account_id !==
                    (int) $row->account_id
                ) {
                    $problems[] =
                        'account_id != compra original';
                }

                if (
                    (int) $purchase->user_id !==
                    (int) $row->user_id
                ) {
                    $problems[] =
                        'user_id != compra original';
                }
            }

            if (
                (string) $row->status ===
                'completed'
            ) {
                if (
                    $row->completed_at === null
                ) {
                    $problems[] =
                        'completed_at NULL';
                }

                if (
                    $row->balance_before === null ||
                    $row->balance_after === null
                ) {
                    $problems[] =
                        'balances del refund incompletos';
                } elseif (
                    (int) $row->balance_before +
                    (int) $row->amount !==
                    (int) $row->balance_after
                ) {
                    $problems[] =
                        'refund before + amount != after';
                }

                if ($ledger->count() !== 1) {
                    $problems[] =
                        'refund completed pero ledger=' .
                        $ledger->count();
                }
            }

            if (
                (string) $row->status ===
                'processing'
            ) {
                $expired =
                    ! $row->lease_expires_at ||
                    Carbon::parse(
                        $row->lease_expires_at
                    )->isPast();

                if ($expired) {
                    $problems[] =
                        'processing con lease caducado';
                }
            }

            if (
                (string) $row->status ===
                'manual_review'
            ) {
                $problems[] =
                    'requiere revisión manual: ' .
                    (
                        $row->error_code ??
                        'sin código'
                    );
            }

            foreach ($ledger as $entry) {
                $this->checkLedgerArithmetic(
                    $entry,
                    $problems
                );

                if (
                    (string) $entry->type !==
                    'credit_refund'
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'type != credit_refund';
                }

                if (
                    (int) $entry->amount !==
                    (int) $row->amount
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'importe != refund';
                }

                if (
                    (int) $entry->account_id !==
                    (int) $row->account_id
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'account_id != refund';
                }

                if (
                    (int) $entry->user_id !==
                    (int) $row->user_id
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'user_id != refund';
                }

                if (
                    $row->balance_before !== null &&
                    (int) $entry->balance_before !==
                    (int) $row->balance_before
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'balance_before != refund';
                }

                if (
                    $row->balance_after !== null &&
                    (int) $entry->balance_after !==
                    (int) $row->balance_after
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'balance_after != refund';
                }
            }

            $channel =
                (string)
                ($row->payment_channel ?? '');

            if (
                (string) $row->status ===
                'completed'
            ) {
                if (
                    $channel ===
                    'offline_db'
                ) {
                    if (
                        $row->bridge_transaction_id
                    ) {
                        $problems[] =
                            'offline_db referencia Bridge';
                    }
                } elseif (
                    $channel ===
                    'online_credit_bridge'
                ) {
                    if (
                        ! $row->bridge_transaction_id
                    ) {
                        $problems[] =
                            'online refund sin bridge_transaction_id';
                    } elseif (! $bridge) {
                        $problems[] =
                            'online refund sin journal Bridge';
                    } else {
                        if (
                            (string) $bridge->status !==
                            'applied'
                        ) {
                            $problems[] =
                                'refund Bridge status=' .
                                $bridge->status;
                        }

                        if (
                            (string) $bridge->operation !==
                            'credit'
                        ) {
                            $problems[] =
                                'refund Bridge operation != credit';
                        }

                        if (
                            (int) $bridge->amount !==
                            (int) $row->amount
                        ) {
                            $problems[] =
                                'refund Bridge amount != refund';
                        }

                        if (
                            (int) $bridge->user_id !==
                            (int) $row->user_id
                        ) {
                            $problems[] =
                                'refund Bridge user_id != refund';
                        }

                        if (
                            $row->balance_before !== null &&
                            (int) $bridge->balance_before !==
                            (int) $row->balance_before
                        ) {
                            $problems[] =
                                'refund Bridge balance_before != refund';
                        }

                        if (
                            $row->balance_after !== null &&
                            (int) $bridge->balance_after !==
                            (int) $row->balance_after
                        ) {
                            $problems[] =
                                'refund Bridge balance_after != refund';
                        }
                    }
                } else {
                    $problems[] =
                        'payment_channel de refund desconocido: ' .
                        (
                            $channel !== ''
                                ? $channel
                                : 'vacío'
                        );
                }
            }

            if ($bridge) {
                $this->checkBridgeArithmetic(
                    $bridge,
                    "refund {$row->id}",
                    $problems
                );
            }

            $this->printRow(
                'REFUND',
                (string) $row->id,
                (string) $row->status,
                $problems,
                $showAll
            );
        }
    }

    private function auditAdminAdjustments(
        string $filterId,
        bool $showAll
    ): void {
        $this->newLine();
        $this->comment(
            'AJUSTES ADMINISTRATIVOS'
        );

        $query = DB::table(
            'admin_credit_adjustments'
        )->orderBy('created_at');

        if ($filterId !== '') {
            $query->where('id', $filterId);
        }

        $rows = $query->get();

        if ($rows->isEmpty()) {
            $this->line(
                'Sin ajustes coincidentes.'
            );

            return;
        }

        foreach ($rows as $row) {
            $problems = [];

            $ledger = DB::table(
                'credit_transactions'
            )
                ->where(
                    'purchase_id',
                    $row->id
                )
                ->get();

            $bridge = DB::table(
                'credit_bridge_transactions'
            )
                ->where(
                    'transaction_id',
                    $row->id
                )
                ->first();

            if (
                (string) $row->status ===
                'completed'
            ) {
                if ($ledger->count() !== 1) {
                    $problems[] =
                        'completed pero ledger=' .
                        $ledger->count();
                }

                if (
                    $row->balance_before === null
                ) {
                    $problems[] =
                        'balance_before NULL';
                }

                if (
                    $row->balance_after === null
                ) {
                    $problems[] =
                        'balance_after NULL';
                }

                if (
                    $row->balance_before !== null &&
                    $row->balance_after !== null &&
                    (int) $row->balance_before +
                    (int) $row->delta !==
                    (int) $row->balance_after
                ) {
                    $problems[] =
                        'before + delta != after';
                }

                if (
                    $row->bridge_transaction_id
                ) {
                    $bridge = DB::table(
                        'credit_bridge_transactions'
                    )
                        ->where(
                            'transaction_id',
                            $row->bridge_transaction_id
                        )
                        ->first();

                    if (! $bridge) {
                        $problems[] =
                            'bridge_transaction_id sin journal Bridge';
                    } elseif (
                        (string) $bridge->status !==
                        'applied'
                    ) {
                        $problems[] =
                            'Bridge status=' .
                            $bridge->status;
                    }
                } elseif ($bridge) {
                    $problems[] =
                        'Bridge existe pero journal admin no lo referencia';
                }
            }

            if (
                (string) $row->status ===
                'processing'
            ) {
                $expired =
                    ! $row->lease_expires_at ||
                    Carbon::parse(
                        $row->lease_expires_at
                    )->isPast();

                if ($expired) {
                    $problems[] =
                        'processing con lease caducado';
                }
            }

            if (
                (string) $row->status ===
                'manual_review'
            ) {
                $problems[] =
                    'requiere revisión manual: ' .
                    (
                        $row->error_code ??
                        'sin código'
                    );
            }

            foreach ($ledger as $entry) {
                $this->checkLedgerArithmetic(
                    $entry,
                    $problems
                );

                if (
                    (int) $entry->amount !==
                    (int) $row->delta
                ) {
                    $problems[] =
                        "ledger #{$entry->id} " .
                        'tiene importe distinto';
                }
            }

            if ($bridge) {
                $this->checkBridgeArithmetic(
                    $bridge,
                    "ajuste {$row->id}",
                    $problems
                );
            }

            $this->printRow(
                'ADMIN',
                (string) $row->id,
                (string) $row->status,
                $problems,
                $showAll
            );
        }
    }

    private function auditLedger(
        string $filterId,
        bool $showAll
    ): void {
        $this->newLine();
        $this->comment('LEDGER');

        $query = DB::table(
            'credit_transactions'
        )
            ->whereNotNull(
                'purchase_id'
            )
            ->orderBy('id');

        if ($filterId !== '') {
            $refundIds = DB::table(
                'credit_refunds'
            )
                ->where(
                    'original_purchase_id',
                    $filterId
                )
                ->orWhere(
                    'id',
                    $filterId
                )
                ->pluck('id')
                ->map(
                    static fn ($id) =>
                        'refund-' .
                        $id
                )
                ->all();

            $query->where(
                function ($query) use (
                    $filterId,
                    $refundIds
                ) {
                    $query->where(
                        'purchase_id',
                        $filterId
                    );

                    foreach (
                        $refundIds
                        as $refundLedgerId
                    ) {
                        $query->orWhere(
                            'purchase_id',
                            $refundLedgerId
                        );
                    }
                }
            );
        }

        $rows = $query->get();

        if ($rows->isEmpty()) {
            $this->line(
                'Sin ledger coincidente.'
            );

            return;
        }

        foreach ($rows as $row) {
            $problems = [];

            $purchaseId =
                (string)
                $row->purchase_id;

            $purchaseExists = DB::table(
                'purchase_operations'
            )
                ->where(
                    'id',
                    $purchaseId
                )
                ->exists();

            $adminExists = DB::table(
                'admin_credit_adjustments'
            )
                ->where(
                    'id',
                    $purchaseId
                )
                ->exists();

            $refundExists = false;

            if (
                str_starts_with(
                    $purchaseId,
                    'refund-'
                )
            ) {
                $refundId = substr(
                    $purchaseId,
                    strlen('refund-')
                );

                $refundExists = DB::table(
                    'credit_refunds'
                )
                    ->where(
                        'id',
                        $refundId
                    )
                    ->exists();
            }

            if (
                ! $purchaseExists &&
                ! $adminExists &&
                ! $refundExists
            ) {
                $problems[] =
                    'sin operación de origen';
            }

            $this->checkLedgerArithmetic(
                $row,
                $problems
            );

            if (
                (string) $row->type ===
                'credit_refund' &&
                ! $refundExists
            ) {
                $problems[] =
                    'credit_refund sin refund de origen';
            }

            $this->printRow(
                'LEDGER',
                $purchaseId,
                (string) $row->type,
                $problems,
                $showAll
            );
        }
    }

    private function auditBridge(
        string $filterId,
        bool $showAll
    ): void {
        $this->newLine();
        $this->comment('CREDITBRIDGE');

        $query = DB::table(
            'credit_bridge_transactions'
        )->orderBy('created_at');

        if ($filterId !== '') {
            $refundBridgeIds = DB::table(
                'credit_refunds'
            )
                ->where(
                    'id',
                    $filterId
                )
                ->orWhere(
                    'original_purchase_id',
                    $filterId
                )
                ->whereNotNull(
                    'bridge_transaction_id'
                )
                ->pluck(
                    'bridge_transaction_id'
                )
                ->all();

            $query->where(
                function ($query) use (
                    $filterId,
                    $refundBridgeIds
                ) {
                    $query->where(
                        'transaction_id',
                        $filterId
                    );

                    foreach (
                        $refundBridgeIds
                        as $bridgeId
                    ) {
                        $query->orWhere(
                            'transaction_id',
                            $bridgeId
                        );
                    }
                }
            );
        }

        $rows = $query->get();

        if ($rows->isEmpty()) {
            $this->line(
                'Sin operaciones Bridge coincidentes.'
            );

            return;
        }

        foreach ($rows as $row) {
            $problems = [];

            $transactionId =
                (string)
                $row->transaction_id;

            $purchaseExists = DB::table(
                'purchase_operations'
            )
                ->where(
                    'id',
                    $transactionId
                )
                ->exists();

            $adminExists = DB::table(
                'admin_credit_adjustments'
            )
                ->where(
                    'id',
                    $transactionId
                )
                ->orWhere(
                    'bridge_transaction_id',
                    $transactionId
                )
                ->exists();

            $refundExists = DB::table(
                'credit_refunds'
            )
                ->where(
                    'bridge_transaction_id',
                    $transactionId
                )
                ->exists();

            /*
             * Compatibilidad con journals antiguos refund-<purchase>.
             */
            $legacyRefundExists = false;

            if (
                str_starts_with(
                    $transactionId,
                    'refund-'
                )
            ) {
                $legacyBaseId = substr(
                    $transactionId,
                    strlen('refund-')
                );

                $legacyRefundExists =
                    DB::table(
                        'purchase_operations'
                    )
                        ->where(
                            'id',
                            $legacyBaseId
                        )
                        ->exists();
            }

            if (
                ! $purchaseExists &&
                ! $adminExists &&
                ! $refundExists &&
                ! $legacyRefundExists
            ) {
                $problems[] =
                    'journal Bridge huérfano';
            }

            if (
                (string) $row->status ===
                'pending'
            ) {
                $problems[] =
                    'Bridge permanece pending';
            }

            $this->checkBridgeArithmetic(
                $row,
                $transactionId,
                $problems
            );

            $this->printRow(
                'BRIDGE',
                $transactionId,
                (string) $row->status,
                $problems,
                $showAll
            );
        }
    }

    private function checkPurchasePaymentBridge(
        object $row,
        ?object $bridge,
        array &$problems
    ): void {
        if (! $row->payment_json) {
            return;
        }

        $payment = json_decode(
            $row->payment_json,
            true
        );

        if (! is_array($payment)) {
            $problems[] =
                'payment_json inválido';

            return;
        }

        $channel =
            $payment['channel'] ??
            $payment['payment_channel'] ??
            null;

        if (
            $channel !==
            'online_credit_bridge'
        ) {
            return;
        }

        if (! $bridge) {
            $problems[] =
                'pago online sin journal Bridge';

            return;
        }

        if (
            (string) $bridge->status !==
            'applied'
        ) {
            $problems[] =
                'Bridge online status=' .
                $bridge->status;
        }
    }

    private function checkLedgerArithmetic(
        object $row,
        array &$problems
    ): void {
        if (
            (int) $row->balance_before +
            (int) $row->amount !==
            (int) $row->balance_after
        ) {
            $problems[] =
                "ledger #{$row->id} " .
                'no cuadra matemáticamente';
        }
    }

    private function checkBridgeArithmetic(
        object $row,
        string $label,
        array &$problems
    ): void {
        if (
            (string) $row->status !==
            'applied'
        ) {
            return;
        }

        $before =
            (int) $row->balance_before;

        $after =
            (int) $row->balance_after;

        $amount =
            (int) $row->amount;

        if (
            (string) $row->operation ===
            'debit'
        ) {
            if (
                $before - $amount !==
                $after
            ) {
                $problems[] =
                    "{$label}: debit no cuadra";
            }

            return;
        }

        if (
            (string) $row->operation ===
            'credit'
        ) {
            if (
                $before + $amount !==
                $after
            ) {
                $problems[] =
                    "{$label}: credit no cuadra";
            }

            return;
        }

        $problems[] =
            "{$label}: operación Bridge desconocida";
    }

    private function printRow(
        string $kind,
        string $id,
        string $status,
        array $problems,
        bool $showAll
    ): void {
        if ($problems === []) {
            if ($showAll) {
                $this->line(
                    "[OK] {$kind} {$id} | {$status}"
                );
            }

            return;
        }

        $this->issues +=
            count($problems);

        $this->error(
            "[!] {$kind} {$id} | {$status}"
        );

        foreach (
            $problems
            as $problem
        ) {
            $this->line(
                "    - {$problem}"
            );
        }
    }
}
