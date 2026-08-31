<x-filament-panels::page>
    <div class="space-y-4">
        <x-filament::tabs>
            @if ($this->showBadgeTab())
                <x-filament::tabs.item
                    :active="$activeTab === 'badges'"
                    wire:click="selectTab('badges')"
                    icon="heroicon-o-photo"
                >
                    Placas ({{ $this->badgePendingCount() }})
                </x-filament::tabs.item>
            @endif

            @if ($this->showSellerTab())
                <x-filament::tabs.item
                    :active="$activeTab === 'sellers'"
                    wire:click="selectTab('sellers')"
                    icon="heroicon-o-shopping-bag"
                >
                    Vendedor de placas ({{ $this->sellerPendingCount() }})
                </x-filament::tabs.item>
            @endif
        </x-filament::tabs>

        @if ($activeTab === 'badges')
            <livewire:filament.badge-applications-table
                :key="'badge-applications-table'"
            />
        @elseif ($activeTab === 'sellers')
            @php($capacity = $this->sellerCapacity())

            <x-filament::section>
                <div class="text-sm">
                    <strong>Licencias comunitarias:</strong>
                    {{ $capacity['used'] }}/{{ $capacity['cap'] }} ocupadas
                    · {{ $capacity['available'] }} disponibles.
                    Los Diseñadores de Placas no consumen estos huecos.
                </div>
            </x-filament::section>

            <livewire:filament.badge-seller-applications-table
                :key="'badge-seller-applications-table'"
            />
        @endif
    </div>
</x-filament-panels::page>
