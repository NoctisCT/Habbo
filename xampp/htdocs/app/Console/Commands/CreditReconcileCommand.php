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
        'Audita compras, ajustes administrativos, ledger y CreditBridge sin modificar datos.';

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
        $this->auditAdminAdjustments($id, $showAll);
        $this->auditLedger($id, $showAll);
        $this->auditBridge($id, $showAll);

        $this->newLine();
        $this->info('=== RESULTADO ===');

        if ($this->issues === 0) {
            $this->info('OK: no se han encontrado inconsistencias.');

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
            $this->line('Sin compras coincidentes.');

            return;
        }

        foreach ($rows as $row) {
            $problems = [];

            $ledger = DB::table('credit_transactions')
                ->where('purchase_id', $row->id)
                ->get();

            $bridge = DB::table(
                'credit_bridge_transactions'
            )
                ->where('transaction_id', $row->id)
                ->first();

            $refund = DB::table(
                'credit_bridge_transactions'
            )
                ->where(
                    'transaction_id',
                    'refund-' . $row->id
                )
                ->first();

            if ((string) $row->status === 'completed') {
                if ($ledger->count() !== 1) {
                    $problems[] =
                        'completed pero ledger=' .
                        $ledger->count();
                }

                if ($row->payment_json) {
                    $payment = json_decode(
                        $row->payment_json,
                        true
                    );

                    if (! is_array($payment)) {
                        $problems[] =
                            'payment_json inválido';
                    } else {
                        $channel =
                            $payment['channel'] ??
                            $payment['payment_channel'] ??
                            null;

                        if (
                            $channel ===
                            'online_credit_bridge'
                        ) {
                            if (! $bridge) {
                                $problems[] =
                                    'pago online sin journal Bridge';
                            } elseif (
                                (string) $bridge->status !==
                                'applied'
                            ) {
                                $problems[] =
                                    'Bridge online status=' .
                                    $bridge->status;
                            }
                        }
                    }
                }
            }

            if ((string) $row->status === 'processing') {
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
                    ($row->error_code ?? 'sin código');
            }

            if ((string) $row->status === 'refunded') {
                if (
                    $bridge &&
                    (string) $bridge->status === 'applied'
                ) {
                    if (! $refund) {
                        $problems[] =
                            'refunded con debit aplicado pero sin refund Bridge';
                    } elseif (
                        (string) $refund->status !==
                        'applied'
                    ) {
                        $problems[] =
                            'refund Bridge no aplicado';
                    }
                }

                if ($ledger->count() !== 0) {
                    $problems[] =
                        'refunded conserva ledger de compra';
                }
            }

            if ($bridge) {
                $this->checkBridgeArithmetic(
                    $bridge,
                    "compra {$row->id}",
                    $problems
                );
            }

            if ($refund) {
                $this->checkBridgeArithmetic(
                    $refund,
                    "refund {$row->id}",
                    $problems
                );
            }

            foreach ($ledger as $entry) {
                if (
                    (int) $entry->balance_before +
                    (int) $entry->amount !==
                    (int) $entry->balance_after
                ) {
                    $problems[] =
                        "ledger #{$entry->id} no cuadra matemáticamente";
                }
            }

            $this->printRow(
                'PURCHASE',
                $row->id,
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
        $this->comment('AJUSTES ADMINISTRATIVOS');

        $query = DB::table(
            'admin_credit_adjustments'
        )->orderBy('created_at');

        if ($filterId !== '') {
            $query->where('id', $filterId);
        }

        $rows = $query->get();

        if ($rows->isEmpty()) {
            $this->line('Sin ajustes coincidentes.');

            return;
        }

        foreach ($rows as $row) {
            $problems = [];

            $ledger = DB::table('credit_transactions')
                ->where('purchase_id', $row->id)
                ->get();

            $bridge = DB::table(
                'credit_bridge_transactions'
            )
                ->where('transaction_id', $row->id)
                ->first();

            if ((string) $row->status === 'completed') {
                if ($ledger->count() !== 1) {
                    $problems[] =
                        'completed pero ledger=' .
                        $ledger->count();
                }

                if ($row->balance_before === null) {
                    $problems[] =
                        'balance_before NULL';
                }

                if ($row->balance_after === null) {
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

                if ($row->bridge_transaction_id) {
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

            if ((string) $row->status === 'processing') {
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
                    ($row->error_code ?? 'sin código');
            }

            foreach ($ledger as $entry) {
                if (
                    (int) $entry->balance_before +
                    (int) $entry->amount !==
                    (int) $entry->balance_after
                ) {
                    $problems[] =
                        "ledger #{$entry->id} no cuadra matemáticamente";
                }

                if (
                    (int) $entry->amount !==
                    (int) $row->delta
                ) {
                    $problems[] =
                        "ledger #{$entry->id} tiene importe distinto";
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
                $row->id,
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

        $query = DB::table('credit_transactions')
            ->whereNotNull('purchase_id')
            ->orderBy('id');

        if ($filterId !== '') {
            $query->where(
                'purchase_id',
                $filterId
            );
        }

        $rows = $query->get();

        if ($rows->isEmpty()) {
            $this->line('Sin ledger coincidente.');

            return;
        }

        foreach ($rows as $row) {
            $problems = [];

            $purchaseExists = DB::table(
                'purchase_operations'
            )
                ->where('id', $row->purchase_id)
                ->exists();

            $adminExists = DB::table(
                'admin_credit_adjustments'
            )
                ->where('id', $row->purchase_id)
                ->exists();

            if (! $purchaseExists && ! $adminExists) {
                $problems[] =
                    'sin operación de origen';
            }

            if (
                (int) $row->balance_before +
                (int) $row->amount !==
                (int) $row->balance_after
            ) {
                $problems[] =
                    'before + amount != after';
            }

            $this->printRow(
                'LEDGER',
                (string) $row->purchase_id,
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
            $query->where(function ($query) use (
                $filterId
            ) {
                $query
                    ->where(
                        'transaction_id',
                        $filterId
                    )
                    ->orWhere(
                        'transaction_id',
                        'refund-' . $filterId
                    );
            });
        }

        $rows = $query->get();

        if ($rows->isEmpty()) {
            $this->line('Sin operaciones Bridge coincidentes.');

            return;
        }

        foreach ($rows as $row) {
            $problems = [];

            $transactionId =
                (string) $row->transaction_id;

            $baseId = str_starts_with(
                $transactionId,
                'refund-'
            )
                ? substr(
                    $transactionId,
                    strlen('refund-')
                )
                : $transactionId;

            $purchaseExists = DB::table(
                'purchase_operations'
            )
                ->where('id', $baseId)
                ->exists();

            $adminExists = DB::table(
                'admin_credit_adjustments'
            )
                ->where('id', $baseId)
                ->exists();

            if (! $purchaseExists && ! $adminExists) {
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

    private function checkBridgeArithmetic(
        object $row,
        string $label,
        array &$problems
    ): void {
        if ((string) $row->status !== 'applied') {
            return;
        }

        $before = (int) $row->balance_before;
        $after = (int) $row->balance_after;
        $amount = (int) $row->amount;

        if ((string) $row->operation === 'debit') {
            if ($before - $amount !== $after) {
                $problems[] =
                    "{$label}: debit no cuadra";
            }

            return;
        }

        if ((string) $row->operation === 'credit') {
            if ($before + $amount !== $after) {
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

        $this->issues += count($problems);

        $this->error(
            "[!] {$kind} {$id} | {$status}"
        );

        foreach ($problems as $problem) {
            $this->line("    - {$problem}");
        }
    }
}