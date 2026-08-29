<x-filament-panels::page>
    @php
        $reconciliation = $this->getReconciliation();
        $clean = $reconciliation['exit_code'] === 0;
    @endphp

    <div class="space-y-6">
        <div
            @class([
                'rounded-xl border p-6',
                'border-success-500/30 bg-success-500/5' => $clean,
                'border-danger-500/30 bg-danger-500/5' => ! $clean,
            ])
        >
            <div class="flex items-start justify-between gap-4">
                <div>
                    <h2
                        @class([
                            'text-lg font-semibold',
                            'text-success-600 dark:text-success-400' => $clean,
                            'text-danger-600 dark:text-danger-400' => ! $clean,
                        ])
                    >
                        @if ($clean)
                            Sin incidencias econ&oacute;micas
                        @else
                            Se han detectado incidencias econ&oacute;micas
                        @endif
                    </h2>

                    <p class="mt-2 text-sm text-gray-500 dark:text-gray-400">
                        @if ($clean)
                            Compras, ajustes administrativos, ledger y CreditBridge son coherentes.
                        @else
                            Revisa el informe antes de modificar cualquier saldo o transacci&oacute;n.
                        @endif
                    </p>
                </div>

                <div class="text-right text-xs text-gray-500 dark:text-gray-400">
                    &Uacute;ltima comprobaci&oacute;n
                    <br>
                    {{ $reconciliation['checked_at'] }}
                </div>
            </div>
        </div>

        <div class="rounded-xl border border-gray-200 bg-white shadow-sm dark:border-white/10 dark:bg-gray-900">
            <div class="border-b border-gray-200 px-6 py-4 dark:border-white/10">
                <h3 class="font-semibold text-gray-950 dark:text-white">
                    Informe de reconciliaci&oacute;n
                </h3>

                <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
                    Esta pantalla es de solo lectura y no realiza correcciones autom&aacute;ticas.
                </p>
            </div>

            <div class="p-6">
                <pre
    class="overflow-x-auto whitespace-pre-wrap break-words rounded-lg p-4 text-sm leading-6"
    style="background-color: #ffffff; color: #111827; border: 1px solid #d1d5db;"
>{{ $reconciliation['output'] }}</pre>
            </div>
        </div>
    </div>
</x-filament-panels::page>