<x-app-layout>
    @push('title', 'Marketplace - Placas')

    <div class="col-span-12" x-data="badgeCreator()">
        <x-content.content-card icon="hotel-icon" classes="border dark:border-gray-900">
            <x-slot:title>
                Marketplace · Placas
            </x-slot:title>

            <x-slot:under-title>
                Descubre placas de la comunidad, crea las tuyas y gestiona tus publicaciones.
            </x-slot:under-title>

            @if (session('success'))
                <div class="mx-2 mt-3 rounded border border-emerald-500/40 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-700 dark:text-emerald-300">
                    {{ session('success') }}
                </div>
            @endif

            @if ($errors->any())
                <div class="mx-2 mt-3 rounded border border-red-500/40 bg-red-500/10 px-4 py-3 text-sm text-red-700 dark:text-red-300">
                    <div class="font-semibold">Revisa estos datos:</div>
                    <ul class="mt-1 list-disc pl-5">
                        @foreach ($errors->all() as $error)
                            <li>{{ $error }}</li>
                        @endforeach
                    </ul>
                </div>
            @endif

            <div class="px-2 py-4 text-sm dark:text-gray-200">
                <div class="mb-5 flex flex-wrap gap-2">
                    <button
                        type="button"
                        @click="setTab('market')"
                        :class="tab === 'market' ? 'ring-2 ring-inset ring-yellow-400' : ''"
                        class="rounded border-2 px-4 py-2 font-semibold transition"
                        style="background:#168eea;color:#fff;border-color:#51b4ff;"
                    >
                        Mercado
                    </button>

                    <button
                        type="button"
                        @click="setTab('create')"
                        :class="tab === 'create' ? 'ring-2 ring-inset ring-yellow-400' : ''"
                        class="rounded border-2 px-4 py-2 font-semibold transition"
                        style="background:#eeb425;color:#fff;border-color:#e6a914;"
                    >
                        Crear placa
                    </button>

                    <button
                        type="button"
                        @click="setTab('mine')"
                        :class="tab === 'mine' ? 'ring-2 ring-inset ring-yellow-400' : ''"
                        class="rounded border-2 px-4 py-2 font-semibold transition"
                        style="background:#22a866;color:#fff;border-color:#4fd68c;"
                    >
                        Mis placas
                    </button>
                    <button
                        type="button"
                        @click="setTab('seller')"
                        :class="tab === 'seller' ? 'ring-2 ring-inset ring-yellow-400' : ''"
                        class="rounded border-2 px-4 py-2 font-semibold transition"
                        style="background:#a34fb5;color:#fff;border-color:#cf74df;"
                    >
                        Vender placas
                    </button>
                </div>

                <section x-show="tab === 'create'" x-cloak>
                    <div class="grid grid-cols-1 gap-5 lg:grid-cols-2">
                        <div class="rounded border border-gray-300 p-4 dark:border-gray-700">
                            <h3 class="text-base font-bold">1. Sube tu placa</h3>
                            <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                                Sube una placa que ya hayas diseñado. El sistema conserva el pixelado,
                                la proporción y la transparencia, y prepara el GIF final de 40×40.
                            </p>

                            <div class="mt-4 rounded border border-amber-500/30 bg-amber-500/10 p-3 text-xs text-amber-700 dark:text-amber-300">
                                <strong>Requisito de diseño:</strong> usa fondo transparente cuando el diseño no ocupe todo el lienzo. Si la placa ocupa los 40×40 completos, puede ser opaca. El resultado será revisado por moderación.
                            </div>

                            <div class="mt-4">
                                <label class="font-semibold">Archivo de la placa</label>
                                <input
                                    x-ref="image"
                                    @change="resetPreview()"
                                    type="file"
                                    accept=".png,.gif,image/png,image/gif"
                                    class="mt-2 block w-full rounded border border-gray-300 p-2 dark:border-gray-700 dark:bg-gray-800"
                                >
                                <div class="mt-1 text-xs text-gray-500">
                                    Máximo 5 MB. PNG y GIF únicamente. Si supera 40×40, se reduce sin suavizado y el resultado final se guarda como GIF de 40×40.
                                </div>
                            </div>

                            <button
                                type="button"
                                @click="generatePreview()"
                                :disabled="processing"
                                class="mt-4 w-full rounded border-2 p-2 font-semibold transition disabled:cursor-not-allowed disabled:opacity-50"
                                style="background:#16a34a;color:#fff;border-color:#22c55e;"
                            >
                                <span x-show="!processing">Generar vista previa</span>
                                <span x-show="processing">Procesando...</span>
                            </button>

                            <div
                                x-show="previewError"
                                x-text="previewError"
                                class="mt-3 rounded bg-red-500/10 p-3 text-xs text-red-600 dark:text-red-300"
                            ></div>
                        </div>

                        <div class="rounded border border-gray-300 p-4 dark:border-gray-700">
                            <h3 class="text-base font-bold">2. Vista previa final</h3>
                            <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                                Ambas vistas muestran exactamente el mismo GIF que se enviará a moderación.
                            </p>

                            <div class="mt-5 grid grid-cols-1 gap-4 sm:grid-cols-2">
                                <div class="rounded border border-gray-300 p-4 text-center dark:border-gray-700">
                                    <div class="mb-3 text-xs font-semibold uppercase tracking-wide text-gray-500">
                                        Tamaño real
                                    </div>

                                    <div
                                        class="mx-auto flex h-24 w-24 items-center justify-center rounded border border-dashed border-gray-300 dark:border-gray-700"
                                        style="background-color:#d1d5db;background-image:linear-gradient(45deg,#fff 25%,transparent 25%),linear-gradient(-45deg,#fff 25%,transparent 25%),linear-gradient(45deg,transparent 75%,#fff 75%),linear-gradient(-45deg,transparent 75%,#fff 75%);background-size:12px 12px;background-position:0 0,0 6px,6px -6px,-6px 0;"
                                    >
                                        <template x-if="previewUrl">
                                            <img
                                                :src="previewUrl"
                                                alt="Placa a tamaño real"
                                                width="40"
                                                height="40"
                                                style="width:40px;height:40px;image-rendering:pixelated;"
                                            >
                                        </template>

                                        <div x-show="!previewUrl" class="px-2 text-xs text-gray-500">
                                            40×40
                                        </div>
                                    </div>

                                    <div class="mt-2 text-xs text-gray-500">
                                        Así se verá a tamaño real.
                                    </div>
                                </div>

                                <div class="rounded border border-gray-300 p-4 text-center dark:border-gray-700">
                                    <div class="mb-3 text-xs font-semibold uppercase tracking-wide text-gray-500">
                                        Inspección ampliada
                                    </div>

                                    <div
                                        class="mx-auto flex h-48 w-48 items-center justify-center rounded border border-dashed border-gray-300 dark:border-gray-700"
                                        style="background-color:#d1d5db;background-image:linear-gradient(45deg,#fff 25%,transparent 25%),linear-gradient(-45deg,#fff 25%,transparent 25%),linear-gradient(45deg,transparent 75%,#fff 75%),linear-gradient(-45deg,transparent 75%,#fff 75%);background-size:16px 16px;background-position:0 0,0 8px,8px -8px,-8px 0;"
                                    >
                                        <template x-if="previewUrl">
                                            <img
                                                :src="previewUrl"
                                                alt="Placa ampliada"
                                                width="160"
                                                height="160"
                                                style="width:160px;height:160px;image-rendering:pixelated;"
                                            >
                                        </template>

                                        <div x-show="!previewUrl" class="px-4 text-center text-gray-500">
                                            Genera una vista previa para inspeccionar los píxeles.
                                        </div>
                                    </div>

                                    <div class="mt-2 text-xs text-gray-500">
                                        Ampliada 4×, sin suavizado.
                                    </div>
                                </div>
                            </div>

                            <div
                                x-show="previewUrl"
                                class="mt-4 rounded bg-emerald-500/10 p-3 text-xs text-emerald-700 dark:text-emerald-300"
                            >
                                <span>Salida final: GIF · 40×40 px · placa preparada para moderación</span>
                            </div>
                        </div>
                    </div>

                    <form
                        method="POST"
                        action="{{ route('marketplace.badges.store') }}"
                        class="mt-5 rounded border border-gray-300 p-4 dark:border-gray-700"
                        @submit="submitting = true"
                    >
                        @csrf

                        <input type="hidden" name="preview_token" :value="previewToken">
                        <input type="hidden" name="purchase_id" :value="purchaseId">

                        <h3 class="text-base font-bold">3. Datos y envío</h3>

                        <div class="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
                            <div>
                                <label class="font-semibold">Personaje creador</label>
                                <select
                                    name="creator_user_id"
                                    required
                                    class="mt-2 h-12 w-full rounded border-4 border-gray-200 px-3 dark:border-gray-700 dark:bg-gray-800 focus:border-[#eeb425] focus:ring-0"
                                >
                                    @foreach ($characters as $character)
                                        <option value="{{ $character->id }}">
                                            {{ $character->username }}
                                            @if ($character->is_primary) · principal @endif
                                        </option>
                                    @endforeach
                                </select>
                                <p class="mt-1 text-xs text-gray-500">
                                    La autoría y el futuro derecho de venta pertenecerán a este personaje.
                                </p>
                            </div>

                            <div>
                                <label class="font-semibold">Personaje que paga</label>
                                <select
                                    name="payer_user_id"
                                    required
                                    class="mt-2 h-12 w-full rounded border-4 border-gray-200 px-3 dark:border-gray-700 dark:bg-gray-800 focus:border-[#eeb425] focus:ring-0"
                                >
                                    @foreach ($characters as $character)
                                        <option value="{{ $character->id }}">
                                            {{ $character->username }} · {{ number_format($character->credits, 0, ',', '.') }} créditos
                                        </option>
                                    @endforeach
                                </select>
                            </div>

                            <div>
                                <label class="font-semibold">Nombre de la placa</label>
                                <input
                                    name="badge_name"
                                    value="{{ old('badge_name') }}"
                                    maxlength="60"
                                    required
                                    class="mt-2 h-12 w-full rounded border-4 border-gray-200 px-3 dark:border-gray-700 dark:bg-gray-800 focus:border-[#eeb425] focus:ring-0"
                                    placeholder="Ej. Gato espacial"
                                >
                            </div>

                            <div>
                                <label class="font-semibold">Descripción</label>
                                <input
                                    name="badge_description"
                                    value="{{ old('badge_description') }}"
                                    maxlength="255"
                                    required
                                    class="mt-2 h-12 w-full rounded border-4 border-gray-200 px-3 dark:border-gray-700 dark:bg-gray-800 focus:border-[#eeb425] focus:ring-0"
                                    placeholder="Descripción que tendrá la placa"
                                >
                            </div>
                        </div>

                        <div class="mt-5 rounded bg-yellow-500/10 p-3 text-sm">
                            <strong>Precio: {{ $badgePrice }} créditos.</strong>
                            Solo se cobran cuando confirmas este formulario.
                            Si el staff rechaza la placa, los {{ $badgePrice }} créditos se devolverán automáticamente.
                            Máximo: {{ $maxPending }} solicitudes pendientes por cuenta.
                        </div>

                        <button
                            type="submit"
                            :disabled="!previewToken || submitting"
                            class="mt-4 w-full rounded border-2 p-3 font-bold transition disabled:cursor-not-allowed disabled:opacity-50"
                            style="background:#eeb425;color:#fff;border-color:#facc15;"
                        >
                            <span x-show="!submitting">Confirmar y enviar por {{ $badgePrice }} créditos</span>
                            <span x-show="submitting">Enviando...</span>
                        </button>
                    </form>
                </section>

                <section x-show="tab === 'mine'" x-cloak>
                    <h3 class="text-base font-bold">Mis placas creadas</h3>
                    <p class="mt-1 text-xs text-gray-500">
                        Aquí solo aparecen placas creadas por tus personajes y aceptadas por el staff.
                        No se muestran placas obtenidas en juegos, eventos o compradas a otros creadores.
                    </p>

                    @if ($creatorBadges->isEmpty())
                        <div class="mt-4 rounded border border-dashed border-gray-300 p-8 text-center text-gray-500 dark:border-gray-700">
                            Todavía no tienes placas de creador aprobadas.
                        </div>
                    @else
                        <div class="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
                            @foreach ($creatorBadges as $badge)
                                <div class="rounded border border-gray-300 p-4 dark:border-gray-700">
                                    <div class="flex gap-3">
                                        <img
                                            src="{{ asset(rtrim(setting('badges_path', '/gamedata/c_images/album1584/'), '/\\') . '/' . $badge->badge_code . '.gif') }}"
                                            alt="{{ $badge->badge_name }}"
                                            width="40"
                                            height="40"
                                            class="shrink-0"
                                            style="width:40px;height:40px;min-width:40px;max-width:40px;object-fit:contain;image-rendering:pixelated;"
                                        >
                                        <div>
                                            <div class="font-bold">{{ $badge->badge_name }}</div>
                                            <div class="text-xs text-gray-500">{{ $badge->badge_code }}</div>
                                            <div class="mt-1 text-xs">
                                                Creador:
                                                {{ $badge->creator_username ?? ('#' . $badge->creator_user_id) }}
                                            </div>
                                        </div>
                                    </div>

                                    @if ($badge->creator_archived_at)
                                        <div class="mt-3 rounded bg-gray-500/10 p-2 text-xs text-gray-500">
                                            Personaje archivado · derechos de venta pausados.
                                        </div>
                                    @else
                                        <div class="mt-3 rounded bg-emerald-500/10 p-2 text-xs text-emerald-600 dark:text-emerald-300">
                                            Autoría activa
                                        </div>
                                        <details class="mt-3 rounded border border-gray-300 bg-gray-50 dark:border-gray-700 dark:bg-gray-800/60">
                                            <summary class="cursor-pointer select-none px-3 py-2.5 font-semibold text-gray-800 dark:text-gray-100">
                                                Regalar · 3 créditos
                                            </summary>

                                            <form
                                                method="POST"
                                                action="{{ route('marketplace.badges.gift', ['creatorBadge' => $badge->id]) }}"
                                                class="border-t border-gray-200 p-3 dark:border-gray-700"
                                            >
                                                @csrf

                                                <input
                                                    type="hidden"
                                                    name="purchase_id"
                                                    value="{{ (string) \Illuminate\Support\Str::uuid() }}"
                                                >

                                                <div>
                                                    <label class="text-xs font-semibold">
                                                        Personaje destinatario
                                                    </label>

                                                    <input
                                                        type="text"
                                                        name="recipient_username"
                                                        maxlength="100"
                                                        required
                                                        autocomplete="off"
                                                        placeholder="Nombre del personaje"
                                                        class="mt-1 h-10 w-full rounded border border-gray-300 px-3 dark:border-gray-700 dark:bg-gray-900"
                                                    >
                                                </div>

                                                <div class="mt-3">
                                                    <label class="text-xs font-semibold">
                                                        Personaje que paga
                                                    </label>

                                                    <select
                                                        name="payer_user_id"
                                                        required
                                                        class="mt-1 h-10 w-full rounded border border-gray-300 px-3 dark:border-gray-700 dark:bg-gray-900"
                                                    >
                                                        @foreach ($characters as $character)
                                                            <option value="{{ $character->id }}">
                                                                {{ $character->username }} · {{ number_format($character->credits, 0, ',', '.') }} créditos
                                                            </option>
                                                        @endforeach
                                                    </select>
                                                </div>

                                                <div class="mt-3 rounded bg-amber-500/10 p-2.5 text-xs leading-5 text-amber-800 dark:text-amber-200">
                                                    El destinatario recibirá una copia de esta placa.
                                                    La autoría seguirá siendo tuya y el regalo no concede derecho de reventa ni publica la placa en el marketplace.
                                                    Si ya tiene la placa, no se cobrará nada.
                                                </div>

                                                <button
                                                    type="submit"
                                                    class="mt-3 w-full rounded border-2 px-3 py-2.5 font-bold transition"
                                                    style="background:#eeb425;color:#fff;border-color:#facc15;"
                                                >
                                                    Confirmar regalo por 3 créditos
                                                </button>
                                            </form>
                                        </details>
                                    @endif

                                    @if ($badge->hotel_distribution_granted_at)
                                        <div class="mt-3 rounded border border-gray-300 bg-gray-50 p-3 text-xs leading-5 dark:border-gray-700 dark:bg-gray-800/60">
                                            <div class="font-semibold">
                                                Derecho de distribución concedido
                                            </div>
                                            <p class="mt-1 text-gray-600 dark:text-gray-300">
                                                Biribiri puede distribuir esta placa de forma permanente y no exclusiva mediante canales oficiales.
                                                La autoría sigue siendo tuya y puedes continuar usándola, regalándola y vendiéndola.
                                            </p>
                                            <div class="mt-1 text-gray-500 dark:text-gray-400">
                                                Concedido el
                                                {{ \Carbon\Carbon::parse((string) $badge->hotel_distribution_granted_at, 'UTC')->setTimezone('Europe/Madrid')->format('d/m/Y H:i') }}
                                            </div>
                                        </div>
                                    @else
                                        <details class="mt-3 rounded border border-gray-300 bg-gray-50 dark:border-gray-700 dark:bg-gray-800/60">
                                            <summary class="cursor-pointer select-none px-3 py-2.5 font-semibold text-gray-800 dark:text-gray-100">
                                                Distribución oficial de Biribiri
                                            </summary>

                                            <form
                                                method="POST"
                                                action="{{ route('marketplace.badges.hotel-distribution.grant', ['creatorBadge' => $badge->id]) }}"
                                                class="border-t border-gray-200 p-3 dark:border-gray-700"
                                                onsubmit="return confirm('Esta autorización es permanente e irreversible. ¿Confirmas que quieres conceder a Biribiri el derecho de distribución de esta placa?');"
                                            >
                                                @csrf

                                                <p class="text-xs leading-5 text-gray-700 dark:text-gray-300">
                                                    Puedes autorizar a Biribiri a distribuir esta placa permanentemente mediante eventos, premios, campañas, tienda oficial u otros canales del hotel.
                                                    El derecho es no exclusivo: la autoría no cambia y seguirás pudiendo usar, regalar y vender la placa.
                                                </p>

                                                <label class="mt-3 flex items-start gap-2 text-xs leading-5">
                                                    <input
                                                        type="checkbox"
                                                        name="confirm_permanent"
                                                        value="1"
                                                        required
                                                        class="mt-1 shrink-0 rounded border-gray-300"
                                                    >
                                                    <span>
                                                        Entiendo que esta autorización es permanente, no exclusiva e irreversible y que no podré retirarla después.
                                                    </span>
                                                </label>

                                                <button
                                                    type="submit"
                                                    class="mt-3 w-full rounded border border-gray-400 px-3 py-2.5 font-semibold dark:border-gray-600"
                                                >
                                                    Conceder derecho permanente
                                                </button>
                                            </form>
                                        </details>
                                    @endif
                                </div>
                            @endforeach
                        </div>
                    @endif

                    <h3 class="mt-8 text-base font-bold">Solicitudes recientes</h3>

                    @if ($submissions->isEmpty())
                        <div class="mt-4 text-gray-500">No has enviado ninguna placa.</div>
                    @else
                        <div class="mt-4 space-y-2">
                            @foreach ($submissions as $submission)
                                <div class="flex flex-col justify-between gap-2 rounded border border-gray-300 p-3 dark:border-gray-700 sm:flex-row sm:items-center">
                                    <div class="flex items-center gap-3">
                                        <div
                                            class="flex h-12 w-12 shrink-0 items-center justify-center overflow-hidden rounded border border-gray-300 dark:border-gray-700"
                                            style="background-color:#d1d5db;background-image:linear-gradient(45deg,#f3f4f6 25%,transparent 25%),linear-gradient(-45deg,#f3f4f6 25%,transparent 25%),linear-gradient(45deg,transparent 75%,#f3f4f6 75%),linear-gradient(-45deg,transparent 75%,#f3f4f6 75%);background-size:12px 12px;background-position:0 0,0 6px,6px -6px,-6px 0;"
                                        >
                                            @if ($submission->preview_data_uri)
                                                <img
                                                    src="{{ $submission->preview_data_uri }}"
                                                    alt="Placa {{ $submission->badge_name }}"
                                                    width="40"
                                                    height="40"
                                                    class="h-10 w-10 object-contain"
                                                    style="image-rendering:pixelated;"
                                                >
                                            @else
                                                <span class="text-xs text-gray-500">Sin preview</span>
                                            @endif
                                        </div>

                                        <div>
                                            <div class="font-semibold">{{ $submission->badge_name }}</div>
                                            <div class="text-xs text-gray-500">
                                                {{ $submission->creator_username ?? ('#' . $submission->creator_user_id) }}
                                                · {{ $submission->created_at_display }}
                                                · {{ $submission->source_mode === 'auto' ? 'conversión automática (legacy)' : 'placa subida' }}
                                            </div>
                                            @if ($submission->moderation_reason)
                                                <div class="mt-1 text-xs text-red-500">
                                                    {{ $submission->moderation_reason }}
                                                </div>
                                            @endif
                                        </div>
                                    </div>

                                    <div>
                                        @if ($submission->status === 'pending')
                                            <span class="rounded bg-yellow-500/15 px-2 py-1 text-xs font-semibold text-yellow-700 dark:text-yellow-300">Pendiente</span>
                                        @elseif ($submission->status === 'approved')
                                            <span class="rounded bg-emerald-500/15 px-2 py-1 text-xs font-semibold text-emerald-700 dark:text-emerald-300">Aceptada</span>
                                        @elseif ($submission->status === 'rejected')
                                            <span class="rounded bg-red-500/15 px-2 py-1 text-xs font-semibold text-red-700 dark:text-red-300">Rechazada · reembolsada</span>
                                        @else
                                            <span class="rounded bg-gray-500/15 px-2 py-1 text-xs font-semibold">{{ $submission->status }}</span>
                                        @endif
                                    </div>
                                </div>
                            @endforeach
                        </div>
                    @endif
                </section>

                <section x-show="tab === 'market'" x-cloak>
                    @php
                        $marketClientItems = $marketListings
                            ->map(
                                static fn (object $listing): array => [
                                    'id' => (int) $listing->id,
                                    'creatorId' => (int) $listing->creator_user_id,
                                    'name' => (string) $listing->badge_name,
                                    'creator' => (string) ($listing->creator_username ?? ''),
                                    'sales' => (int) $listing->sales_count,
                                    'price' => (int) $listing->buyer_price,
                                    'isNew' => (bool) $listing->is_new,
                                    'activatedAt' => $listing->activated_at !== null
                                        ? strtotime((string) $listing->activated_at)
                                        : 0,
                                ]
                            )
                            ->values();
                    @endphp

                    <div class="flex flex-wrap items-end justify-between gap-3">
                        <div>
                            <h3 class="text-base font-bold">Mercado de placas</h3>
                            <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                                Descubre placas creadas por la comunidad.
                            </p>
                        </div>

                        <div class="text-xs font-semibold text-gray-500 dark:text-gray-400">
                            <span x-text="marketFilteredItems.length">{{ $marketListings->count() }}</span>
                            <span x-text="marketFilteredItems.length === 1 ? 'placa' : 'placas'">placas</span>
                        </div>
                    </div>

                    <div class="mt-4">
                        <input
                            type="search"
                            x-model="marketSearch"
                            @input.debounce.150ms="$nextTick(() => marketStateChanged())"
                            maxlength="60"
                            autocomplete="off"
                            placeholder="Buscar por nombre de placa o creador..."
                            class="h-11 w-full rounded border-4 border-gray-200 px-3 dark:border-gray-700 dark:bg-gray-800 focus:border-[#eeb425] focus:ring-0"
                        >
                    </div>

                    <div class="mt-4 flex flex-wrap items-center justify-between gap-3">
                        <div class="flex flex-wrap gap-2">
                            <button
                                type="button"
                                @click="marketSetExplore('all')"
                                :class="marketExplore === 'all'
                                    ? 'border-gray-400 bg-gray-100 dark:border-gray-600 dark:bg-gray-700'
                                    : 'border-gray-300 dark:border-gray-700'"
                                class="rounded border px-3 py-2 text-sm font-semibold transition"
                            >
                                Todas
                            </button>

                            <button
                                type="button"
                                @click="marketSetExplore('popular')"
                                :class="marketExplore === 'popular'
                                    ? 'border-gray-400 bg-gray-100 dark:border-gray-600 dark:bg-gray-700'
                                    : 'border-gray-300 dark:border-gray-700'"
                                class="rounded border px-3 py-2 text-sm font-semibold transition"
                            >
                                Populares
                            </button>

                            <button
                                type="button"
                                @click="marketSetExplore('new')"
                                :class="marketExplore === 'new'
                                    ? 'border-gray-400 bg-gray-100 dark:border-gray-600 dark:bg-gray-700'
                                    : 'border-gray-300 dark:border-gray-700'"
                                class="rounded border px-3 py-2 text-sm font-semibold transition"
                            >
                                Nuevas
                            </button>
                        </div>

                        <div class="flex items-center gap-2">
                            <label
                                for="badge-market-sort"
                                class="text-xs font-semibold text-gray-500 dark:text-gray-400"
                            >
                                Ordenar
                            </label>

                            <select
                                id="badge-market-sort"
                                x-model="marketSort"
                                @change="$nextTick(() => marketStateChanged())"
                                class="h-10 rounded border border-gray-300 bg-white px-3 text-sm dark:border-gray-700 dark:bg-gray-800"
                            >
                                <option value="relevance">Relevancia</option>
                                <option value="sales_desc">Más vendidas</option>
                                <option value="price_asc">Más baratas</option>
                                <option value="price_desc">Más caras</option>
                                <option value="newest">Más nuevas</option>
                                <option value="oldest">Más antiguas</option>
                            </select>
                        </div>
                    </div>

                    <div
                        x-show="marketCreator !== null"
                        x-cloak
                        class="mt-4 rounded border border-gray-300 p-3 dark:border-gray-700"
                    >
                        <div class="flex flex-wrap items-center justify-between gap-2">
                            <div class="text-sm">
                                Catálogo de
                                <strong x-text="marketCreatorName"></strong>
                            </div>

                            <button
                                type="button"
                                @click="marketClearCreator()"
                                class="text-sm font-semibold text-gray-600 hover:underline dark:text-gray-300"
                            >
                                Ver todo el mercado
                            </button>
                        </div>
                    </div>

                    @if ($marketListings->isEmpty())
                        <div class="mt-7 rounded border border-dashed border-gray-300 p-8 text-center text-gray-500 dark:border-gray-700">
                            Todavía no hay placas publicadas en el mercado.
                        </div>
                    @else
                        <div
                            x-show="marketFilteredItems.length === 0"
                            x-cloak
                            class="mt-7 rounded border border-dashed border-gray-300 p-8 text-center text-gray-500 dark:border-gray-700"
                        >
                            No hemos encontrado placas con esos filtros.
                        </div>

                        <div
                            x-show="marketFilteredItems.length > 0"
                            x-cloak
                            class="mt-7 grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3"
                        >
                            @foreach ($marketListings as $listing)
                                @php
                                    $canAfford = $characters->contains(
                                        static fn ($character): bool =>
                                            (int) $character->credits >=
                                            (int) $listing->buyer_price
                                    );

                                    $defaultPayer = $characters->first(
                                        static fn ($character): bool =>
                                            (bool) $character->is_primary &&
                                            (int) $character->credits >=
                                            (int) $listing->buyer_price
                                    ) ?? $characters->first(
                                        static fn ($character): bool =>
                                            (int) $character->credits >=
                                            (int) $listing->buyer_price
                                    );

                                    $purchaseDialogId =
                                        'badge-market-purchase-' .
                                        $listing->id;
                                @endphp

                                <div
                                    x-show="marketPageIds.includes({{ (int) $listing->id }})"
                                    class="rounded border border-gray-300 p-4 dark:border-gray-700"
                                >
                                    <div class="flex gap-3">
                                        <div class="flex h-14 w-14 shrink-0 items-center justify-center rounded border border-gray-300 bg-gray-100 dark:border-gray-700 dark:bg-gray-800">
                                            <img
                                                src="{{ asset(rtrim(setting('badges_path', '/gamedata/c_images/album1584/'), '/\\') . '/' . $listing->badge_code . '.gif') }}"
                                                alt="{{ $listing->badge_name }}"
                                                width="40"
                                                height="40"
                                                style="width:40px;height:40px;min-width:40px;max-width:40px;object-fit:contain;image-rendering:pixelated;"
                                            >
                                        </div>

                                        <div class="min-w-0 flex-1">
                                            <div class="flex items-start justify-between gap-2">
                                                <div class="min-w-0">
                                                    <div class="truncate font-bold">
                                                        {{ $listing->badge_name }}
                                                    </div>

                                                    <p
                                                        class="mt-1 text-xs leading-5 text-gray-600 dark:text-gray-300"
                                                        style="display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;"
                                                        title="{{ $listing->badge_description }}"
                                                    >
                                                        {{ $listing->badge_description }}
                                                    </p>

                                                    <button
                                                        type="button"
                                                        @click="marketSetCreator({{ (int) $listing->creator_user_id }})"
                                                        class="mt-1 text-left text-xs text-gray-500 hover:underline dark:text-gray-400"
                                                    >
                                                        por {{ $listing->creator_username ?? ('#' . $listing->creator_user_id) }}
                                                    </button>
                                                </div>

                                                @if ($listing->is_new)
                                                    <span class="shrink-0 rounded bg-gray-100 px-2 py-1 text-xs font-semibold text-gray-600 dark:bg-gray-800 dark:text-gray-300">
                                                        Nueva
                                                    </span>
                                                @endif
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mt-4 flex items-end justify-between gap-3 border-t border-gray-200 pt-3 dark:border-gray-700">
                                        <div>
                                            <div class="font-bold">
                                                {{ $listing->buyer_price }} créditos
                                            </div>
                                            <div class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                                                {{ $listing->sales_count }}
                                                {{ $listing->sales_count === 1 ? 'venta' : 'ventas' }}
                                            </div>
                                        </div>

                                        <button
                                            type="button"
                                            @disabled(!$canAfford)
                                            onclick="document.getElementById('{{ $purchaseDialogId }}').showModal()"
                                            class="rounded border-2 px-4 py-2 text-sm font-bold transition disabled:cursor-not-allowed disabled:opacity-50"
                                            style="background:#eeb425;color:#fff;border-color:#facc15;"
                                        >
                                            {{ $canAfford ? 'Comprar' : 'Sin saldo' }}
                                        </button>
                                    </div>
                                </div>

                                <dialog
                                    id="{{ $purchaseDialogId }}"
                                    class="w-[calc(100%-2rem)] max-w-lg rounded border border-gray-300 bg-white p-0 shadow-lg backdrop:bg-black/50 dark:border-gray-700 dark:bg-gray-900"
                                >
                                    <div class="p-5">
                                        <div class="flex items-start justify-between gap-4">
                                            <div>
                                                <h3 class="text-base font-bold">
                                                    Comprar placa
                                                </h3>
                                                <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                                                    El personaje que paga será también quien reciba la placa.
                                                </p>
                                            </div>

                                            <button
                                                type="button"
                                                onclick="document.getElementById('{{ $purchaseDialogId }}').close()"
                                                class="text-xl leading-none text-gray-500"
                                                aria-label="Cerrar"
                                            >
                                                ×
                                            </button>
                                        </div>

                                        <div class="mt-4 rounded border border-gray-300 p-4 dark:border-gray-700">
                                            <div class="flex gap-3">
                                                <div class="flex h-14 w-14 shrink-0 items-center justify-center rounded border border-gray-300 bg-gray-100 dark:border-gray-700 dark:bg-gray-800">
                                                    <img
                                                        src="{{ asset(rtrim(setting('badges_path', '/gamedata/c_images/album1584/'), '/\\') . '/' . $listing->badge_code . '.gif') }}"
                                                        alt="{{ $listing->badge_name }}"
                                                        width="40"
                                                        height="40"
                                                        style="width:40px;height:40px;object-fit:contain;image-rendering:pixelated;"
                                                    >
                                                </div>

                                                <div class="min-w-0">
                                                    <div class="font-bold">
                                                        {{ $listing->badge_name }}
                                                    </div>
                                                    <p class="mt-1 text-xs leading-5 text-gray-600 dark:text-gray-300">
                                                        {{ $listing->badge_description }}
                                                    </p>
                                                    <div class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                                                        por {{ $listing->creator_username ?? ('#' . $listing->creator_user_id) }}
                                                    </div>
                                                    <div class="mt-2 font-bold">
                                                        {{ $listing->buyer_price }} créditos
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <form
                                            method="POST"
                                            action="{{ route('marketplace.badges.purchase', ['listing' => $listing->id]) }}"
                                            class="mt-4"
                                        >
                                            @csrf

                                            <input
                                                type="hidden"
                                                name="expected_price"
                                                value="{{ $listing->buyer_price }}"
                                            >

                                            <input
                                                type="hidden"
                                                name="purchase_id"
                                                value="{{ (string) \Illuminate\Support\Str::uuid() }}"
                                            >

                                            <label
                                                for="payer-user-{{ $listing->id }}"
                                                class="font-semibold"
                                            >
                                                Personaje que paga
                                            </label>

                                            <select
                                                id="payer-user-{{ $listing->id }}"
                                                name="payer_user_id"
                                                required
                                                class="mt-2 h-12 w-full rounded border-4 border-gray-200 px-3 dark:border-gray-700 dark:bg-gray-800 focus:border-[#eeb425] focus:ring-0"
                                            >
                                                @foreach ($characters as $character)
                                                    <option
                                                        value="{{ $character->id }}"
                                                        @disabled((int) $character->credits < (int) $listing->buyer_price)
                                                        @selected($defaultPayer && (int) $defaultPayer->id === (int) $character->id)
                                                    >
                                                        {{ $character->username }} · {{ $character->credits }} créditos{{ (int) $character->credits < (int) $listing->buyer_price ? ' · saldo insuficiente' : '' }}
                                                    </option>
                                                @endforeach
                                            </select>

                                            <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                                                La compra entrega una copia. La autoría continúa perteneciendo a {{ $listing->creator_username }}.
                                            </p>

                                            <div class="mt-4 flex flex-wrap justify-end gap-2">
                                                <button
                                                    type="button"
                                                    onclick="document.getElementById('{{ $purchaseDialogId }}').close()"
                                                    class="rounded border border-gray-300 px-4 py-2 font-semibold dark:border-gray-700"
                                                >
                                                    Cancelar
                                                </button>

                                                <button
                                                    type="submit"
                                                    class="rounded border-2 px-4 py-2 font-bold transition"
                                                    style="background:#eeb425;color:#fff;border-color:#facc15;"
                                                >
                                                    Confirmar por {{ $listing->buyer_price }} créditos
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </dialog>
                            @endforeach
                        </div>

                        <div
                            x-show="marketPageCount > 1 && marketFilteredItems.length > 0"
                            x-cloak
                            class="mt-6 flex flex-wrap items-center justify-center gap-2 border-t border-gray-200 pt-4 dark:border-gray-700"
                        >
                            <button
                                type="button"
                                @click="marketGoPage(marketSafePage - 1)"
                                :disabled="marketSafePage <= 1"
                                class="rounded border border-gray-300 px-3 py-2 text-sm font-semibold disabled:cursor-default disabled:text-gray-400 dark:border-gray-700"
                            >
                                Anterior
                            </button>

                            <template x-for="entry in marketPageWindow()" :key="String(entry)">
                                <button
                                    type="button"
                                    @click="typeof entry === 'number' && marketGoPage(entry)"
                                    :disabled="typeof entry !== 'number'"
                                    :style="typeof entry === 'number' && entry === marketSafePage
                                        ? 'background:#eeb425;color:#fff;border-color:#facc15;'
                                        : ''"
                                    :class="typeof entry === 'number'
                                        ? 'border-gray-300 dark:border-gray-700'
                                        : 'cursor-default border-transparent text-gray-400'"
                                    class="rounded border px-3 py-2 text-sm font-semibold"
                                    x-text="typeof entry === 'number' ? entry : '…'"
                                ></button>
                            </template>

                            <button
                                type="button"
                                @click="marketGoPage(marketSafePage + 1)"
                                :disabled="marketSafePage >= marketPageCount"
                                class="rounded border border-gray-300 px-3 py-2 text-sm font-semibold disabled:cursor-default disabled:text-gray-400 dark:border-gray-700"
                            >
                                Siguiente
                            </button>
                        </div>
                    @endif
                </section>

                <section x-show="tab === 'seller'" x-cloak>
                    <h3 class="text-base font-bold">Vender placas</h3>
                    <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                        La licencia de vendedor es gratuita, pertenece a tu cuenta y requiere al menos
                        {{ $sellerMinimumBadges }} placas creadas y aprobadas.
                    </p>

                    <div class="mt-4 rounded border border-gray-300 p-4 dark:border-gray-700">
                        <div class="flex flex-wrap items-center justify-between gap-3">
                            <div>
                                <div class="font-semibold">Progreso de creador</div>
                                <div class="mt-1 text-sm">
                                    {{ $approvedBadgeCount }}/{{ $sellerMinimumBadges }} placas aprobadas
                                </div>
                            </div>

                            @if ($approvedBadgeCount >= $sellerMinimumBadges)
                                <span class="rounded bg-emerald-500/15 px-3 py-1 text-xs font-semibold text-emerald-700 dark:text-emerald-300">
                                    Requisito cumplido
                                </span>
                            @else
                                <span class="rounded bg-amber-500/15 px-3 py-1 text-xs font-semibold text-amber-700 dark:text-amber-300">
                                    Te faltan {{ $sellerMinimumBadges - $approvedBadgeCount }}
                                </span>
                            @endif
                        </div>
                    </div>

                    @if ($isBadgeDesigner)
                        <div class="mt-4 rounded border border-emerald-500/40 bg-emerald-500/10 p-4 text-emerald-800 dark:text-emerald-200">
                            <div class="font-bold">Diseñador de Placas</div>
                            <p class="mt-1 text-sm">
                                Tienes autorización automática para vender placas y no consumes una de las plazas comunitarias.
                            </p>
                        </div>
                    @elseif ($sellerLicense && $sellerLicense->status === 'active')
                        <div class="mt-4 rounded border border-emerald-500/40 bg-emerald-500/10 p-4 text-emerald-800 dark:text-emerald-200">
                            <div class="font-bold">Licencia de vendedor activa</div>
                            <p class="mt-1 text-sm">
                                Ocupas la plaza comunitaria #{{ $sellerLicense->community_slot }}.
                                Cuando habilitemos los anuncios podrás publicar tus placas desde aquí.
                            </p>
                        </div>
                    @elseif ($sellerLicense && $sellerLicense->status === 'pending')
                        <div class="mt-4 rounded border border-amber-500/40 bg-amber-500/10 p-4 text-amber-800 dark:text-amber-200">
                            <div class="font-bold">Solicitud pendiente</div>
                            <p class="mt-1 text-sm">
                                El staff todavía tiene que revisar tu solicitud.
                            </p>
                        </div>
                    @elseif ($sellerLicense && $sellerLicense->status === 'waitlisted')
                        <div class="mt-4 rounded border border-blue-500/40 bg-blue-500/10 p-4 text-blue-800 dark:text-blue-200">
                            <div class="font-bold">Lista de espera</div>
                            <p class="mt-1 text-sm">
                                Cumples los requisitos, pero las {{ config('badge_marketplace.community_license_cap', 3) }}
                                plazas comunitarias están ocupadas. Tu solicitud permanece guardada.
                            </p>
                        </div>
                    @elseif ($sellerLicense && $sellerLicense->status === 'revoked')
                        <div class="mt-4 rounded border border-red-500/40 bg-red-500/10 p-4 text-red-800 dark:text-red-200">
                            <div class="font-bold">Licencia retirada</div>
                            <p class="mt-1 text-sm">
                                No puedes presentar una nueva solicitud automáticamente. El staff debe revisar tu caso.
                            </p>

                            @if ($sellerLicense->revocation_reason)
                                <p class="mt-2 text-xs">
                                    Motivo: {{ $sellerLicense->revocation_reason }}
                                </p>
                            @endif
                        </div>
                    @elseif ($approvedBadgeCount < $sellerMinimumBadges)
                        <div class="mt-4 rounded border border-gray-300 p-4 text-sm dark:border-gray-700">
                            Para solicitar una licencia de vendedor necesitas
                            <strong>{{ $sellerMinimumBadges }} placas aprobadas</strong>.
                            Las placas pendientes o rechazadas no cuentan.
                        </div>
                    @else
                        <div class="mt-4 rounded border border-gray-300 p-4 dark:border-gray-700">
                            <div class="font-bold">Solicitar licencia de vendedor</div>
                            <p class="mt-1 text-sm text-gray-600 dark:text-gray-300">
                                La solicitud es gratuita. Hay
                                <strong>{{ $communitySlotsAvailable }}</strong>
                                plazas comunitarias libres en este momento.
                                La aprobación final siempre la realiza el staff.
                            </p>

                            <form
                                method="POST"
                                action="{{ route('marketplace.badges.seller-license.apply') }}"
                                class="mt-4"
                            >
                                @csrf

                                <button
                                    type="submit"
                                    class="rounded border-2 px-4 py-2 font-bold transition"
                                    style="background:#eeb425;color:#fff;border-color:#facc15;"
                                >
                                    Presentar solicitud
                                </button>
                            </form>
                        </div>
                    @endif

                    @if ($canSellBadges)
                        <div class="mt-6 border-t border-gray-200 pt-5 dark:border-gray-700">
                            <div class="flex flex-wrap items-end justify-between gap-3">
                                <div>
                                    <h3 class="text-base font-bold">Tus anuncios</h3>
                                    <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                                        Puedes tener
                                        <strong>{{ $sellerListingLimit }}</strong>
                                        anuncios activos
                                        @if ($sellerHasHabboClub)
                                            gracias a HC
                                        @else
                                            por cuenta
                                        @endif
                                        .
                                    </p>
                                </div>

                                <div class="flex flex-wrap justify-end gap-2">
                                    <div class="rounded-lg bg-gray-100 px-3 py-2 text-center dark:bg-gray-800">
                                        <div class="text-base font-black">
                                            {{ $sellerActiveListingCount }}/{{ $sellerListingLimit }}
                                        </div>
                                        <div class="text-[11px] text-gray-500 dark:text-gray-400">
                                            anuncios activos
                                        </div>
                                    </div>

                                    <div class="rounded-lg bg-emerald-500/10 px-3 py-2 text-center">
                                        <div class="text-base font-black text-emerald-700 dark:text-emerald-300">
                                            {{ $sellerSalesCount }}
                                        </div>
                                        <div class="text-[11px] text-gray-500 dark:text-gray-400">
                                            ventas
                                        </div>
                                    </div>

                                    <div class="rounded-lg bg-yellow-500/10 px-3 py-2 text-center">
                                        <div class="text-base font-black text-yellow-700 dark:text-yellow-300">
                                            {{ $sellerCreditsEarned }}
                                        </div>
                                        <div class="text-[11px] text-gray-500 dark:text-gray-400">
                                            créditos ganados
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <p class="mt-3 text-xs leading-5 text-gray-700 dark:text-gray-300">
                                Tú eliges cuánto recibes: entre {{ $sellerEarningsMin }} y {{ $sellerEarningsMax }} créditos.
                                El hotel añade {{ $hotelCommission }} créditos de comisión.
                                Por tanto, el comprador pagará entre
                                {{ $sellerEarningsMin + $hotelCommission }} y
                                {{ $sellerEarningsMax + $hotelCommission }} créditos.
                            </p>

                            @if ($sellerCatalog->isEmpty())
                                <div class="mt-4 rounded border border-dashed border-gray-300 p-8 text-center text-gray-500 dark:border-gray-700">
                                    No tienes placas aprobadas disponibles para publicar.
                                </div>
                            @else
                                <div class="mt-5 grid grid-cols-1 gap-3 lg:grid-cols-2">
                                    @foreach ($sellerCatalog as $badge)
                                        <div class="rounded border border-gray-300 p-4 dark:border-gray-700">
                                            <div class="flex items-start justify-between gap-5">
                                                <div class="flex min-w-0 items-start gap-3">
                                                    <div class="flex h-14 w-14 shrink-0 items-center justify-center rounded border border-gray-300 bg-gray-100 dark:border-gray-700 dark:bg-gray-800">
                                                        <img
                                                            src="{{ asset(rtrim(setting('badges_path', '/gamedata/c_images/album1584/'), '/\\') . '/' . $badge->badge_code . '.gif') }}"
                                                            alt="{{ $badge->badge_name }}"
                                                            width="40"
                                                            height="40"
                                                            style="width:40px;height:40px;min-width:40px;max-width:40px;object-fit:contain;image-rendering:pixelated;"
                                                        >
                                                    </div>

                                                    <div class="min-w-0">
                                                        <div class="truncate font-bold">
                                                            {{ $badge->badge_name }}
                                                        </div>

                                                        <p
                                                            class="mt-1 text-xs leading-5 text-gray-600 dark:text-gray-300"
                                                            style="display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;"
                                                            title="{{ $badge->badge_description }}"
                                                        >
                                                            {{ $badge->badge_description }}
                                                        </p>

                                                        <div class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                                                            por {{ $badge->creator_username ?? ('#' . $badge->creator_user_id) }}
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="flex shrink-0 gap-5 text-right">
                                                    <div>
                                                        <div class="font-bold">
                                                            {{ $badge->sales_count }}
                                                        </div>
                                                        <div class="text-xs text-gray-500 dark:text-gray-400">
                                                            {{ $badge->sales_count === 1 ? 'venta' : 'ventas' }}
                                                        </div>
                                                    </div>

                                                    <div>
                                                        <div class="font-bold">
                                                            {{ $badge->credits_earned }}
                                                        </div>
                                                        <div class="whitespace-nowrap text-xs text-gray-500 dark:text-gray-400">
                                                            créditos ganados
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="mt-4 border-t border-gray-200 pt-4 dark:border-gray-700">
                                                @if ($badge->creator_archived_at)
                                                    <div class="font-semibold text-amber-700 dark:text-amber-300">
                                                        Autor archivado · publicación pausada
                                                    </div>
                                                @elseif ($badge->listing_status === 'active')
                                                    <div class="font-semibold">
                                                        Anuncio activo · {{ $badge->buyer_price }} créditos
                                                    </div>
                                                @elseif ($badge->listing_id)
                                                    <div class="font-semibold">
                                                        Anuncio retirado · último precio {{ $badge->buyer_price }} créditos
                                                    </div>
                                                @else
                                                    <div class="font-semibold">
                                                        Sin publicar
                                                    </div>
                                                @endif

                                                @if (!$badge->creator_archived_at && $badge->listing_status === 'active')
                                                    @php
                                                        $sellerPriceFormId =
                                                            'seller-price-' .
                                                            $badge->id;
                                                    @endphp

                                                    <label
                                                        for="seller-price-select-{{ $badge->id }}"
                                                        class="mt-4 block font-semibold"
                                                    >
                                                        Precio de venta
                                                    </label>

                                                    <form
                                                        id="{{ $sellerPriceFormId }}"
                                                        method="POST"
                                                        action="{{ route('marketplace.badges.listing.store', ['creatorBadge' => $badge->id]) }}"
                                                        class="mt-2"
                                                    >
                                                        @csrf

                                                        <select
                                                            id="seller-price-select-{{ $badge->id }}"
                                                            name="seller_earnings"
                                                            class="w-full rounded border border-gray-300 bg-white px-3 py-2 text-sm dark:border-gray-700 dark:bg-gray-800"
                                                        >
                                                            @for ($earning = $sellerEarningsMin; $earning <= $sellerEarningsMax; $earning++)
                                                                <option
                                                                    value="{{ $earning }}"
                                                                    @selected((int) $badge->seller_earnings === $earning)
                                                                >
                                                                    {{ $earning + $hotelCommission }} créditos
                                                                </option>
                                                            @endfor
                                                        </select>
                                                    </form>

                                                    <div class="mt-2 grid grid-cols-2 gap-2">
                                                        <button
                                                            type="submit"
                                                            form="{{ $sellerPriceFormId }}"
                                                            class="rounded border-2 px-3 py-2 text-sm font-bold"
                                                            style="background:#eeb425;color:#fff;border-color:#facc15;"
                                                        >
                                                            Guardar
                                                        </button>

                                                        <form
                                                            method="POST"
                                                            action="{{ route('marketplace.badges.listing.deactivate', ['creatorBadge' => $badge->id]) }}"
                                                            onsubmit="return confirm('¿Retirar esta placa del mercado?');"
                                                        >
                                                            @csrf
                                                            @method('DELETE')

                                                            <button
                                                                type="submit"
                                                                class="w-full rounded border border-red-500/50 px-3 py-2 text-sm font-semibold text-red-600 dark:text-red-300"
                                                            >
                                                                Retirar anuncio
                                                            </button>
                                                        </form>
                                                    </div>
                                                @elseif (!$badge->creator_archived_at && $badge->listing_id)
                                                    <form
                                                        method="POST"
                                                        action="{{ route('marketplace.badges.listing.reactivate', ['creatorBadge' => $badge->id]) }}"
                                                        class="mt-4"
                                                    >
                                                        @csrf

                                                        <button
                                                            type="submit"
                                                            class="rounded border-2 px-4 py-2 text-sm font-bold"
                                                            style="background:#eeb425;color:#fff;border-color:#facc15;"
                                                        >
                                                            Reactivar anuncio · {{ $badge->buyer_price }} créditos
                                                        </button>
                                                    </form>
                                                @elseif (!$badge->creator_archived_at)
                                                    <label
                                                        for="seller-new-price-{{ $badge->id }}"
                                                        class="mt-4 block font-semibold"
                                                    >
                                                        Precio de venta
                                                    </label>

                                                    <form
                                                        method="POST"
                                                        action="{{ route('marketplace.badges.listing.store', ['creatorBadge' => $badge->id]) }}"
                                                        class="mt-2"
                                                    >
                                                        @csrf

                                                        <div class="flex gap-2">
                                                            <select
                                                                id="seller-new-price-{{ $badge->id }}"
                                                                name="seller_earnings"
                                                                class="min-w-0 flex-1 rounded border border-gray-300 bg-white px-3 py-2 text-sm dark:border-gray-700 dark:bg-gray-800"
                                                            >
                                                                @for ($earning = $sellerEarningsMin; $earning <= $sellerEarningsMax; $earning++)
                                                                    <option value="{{ $earning }}">
                                                                        {{ $earning + $hotelCommission }} créditos
                                                                    </option>
                                                                @endfor
                                                            </select>

                                                            <button
                                                                type="submit"
                                                                @disabled($sellerActiveListingCount >= $sellerListingLimit)
                                                                class="rounded border-2 px-4 py-2 text-sm font-bold disabled:cursor-not-allowed disabled:opacity-50"
                                                                style="background:#eeb425;color:#fff;border-color:#facc15;"
                                                            >
                                                                Publicar
                                                            </button>
                                                        </div>
                                                    </form>
                                                @elseif ($badge->listing_status === 'active')
                                                    <form
                                                        method="POST"
                                                        action="{{ route('marketplace.badges.listing.deactivate', ['creatorBadge' => $badge->id]) }}"
                                                        class="mt-4"
                                                    >
                                                        @csrf
                                                        @method('DELETE')

                                                        <button
                                                            type="submit"
                                                            class="rounded border border-red-500/50 px-3 py-2 text-sm font-semibold text-red-600 dark:text-red-300"
                                                        >
                                                            Retirar anuncio pausado
                                                        </button>
                                                    </form>
                                                @endif
                                            </div>
                                        </div>
                                    @endforeach
                                </div>
                            @endif
                        </div>
                    @endif
                </section>
            </div>
        </x-content.content-card>
    </div>

    <script>
        function badgeCreator() {
            return {
                tab: (() => {
                    const requestedTab =
                        new URLSearchParams(
                            window.location.search
                        ).get('tab');

                    return ['market', 'create', 'mine', 'seller'].includes(
                        requestedTab
                    )
                        ? requestedTab
                        : 'market';
                })(),
                previewToken: '',
                previewUrl: '',
                previewError: '',
                processing: false,
                submitting: false,
                purchaseId: crypto.randomUUID(),

                marketItems: @js($marketClientItems),
                marketSearch: @js($marketFilters['q'] ?? ''),
                marketExplore: @js($marketFilters['explore'] ?? 'all'),
                marketSort: @js($marketFilters['sort'] ?? 'relevance'),
                marketCreator: @js($marketFilters['creator'] ?? null),
                marketPage: (() => {
                    const value = Number.parseInt(
                        new URLSearchParams(
                            window.location.search
                        ).get('page') || '1',
                        10
                    );

                    return Number.isFinite(value) && value > 0
                        ? value
                        : 1;
                })(),
                marketPerPage: 12,

                init() {
                    if (! ['all', 'popular', 'new'].includes(this.marketExplore)) {
                        this.marketExplore = 'all';
                    }

                    if (! [
                        'relevance',
                        'sales_desc',
                        'price_asc',
                        'price_desc',
                        'newest',
                        'oldest'
                    ].includes(this.marketSort)) {
                        this.marketSort = 'relevance';
                    }

                    if (
                        this.marketCreator !== null &&
                        Number(this.marketCreator) <= 0
                    ) {
                        this.marketCreator = null;
                    }

                    this.marketPage = this.marketSafePage;
                },

                setTab(nextTab) {
                    if (! ['market', 'create', 'mine', 'seller'].includes(nextTab)) {
                        return;
                    }

                    this.tab = nextTab;

                    if (nextTab === 'market') {
                        this.marketPage = this.marketSafePage;
                        this.syncMarketUrl();

                        return;
                    }

                    const url = new URL(
                        window.location.href
                    );

                    url.searchParams.set(
                        'tab',
                        nextTab
                    );

                    [
                        'q',
                        'explore',
                        'sort',
                        'creator',
                        'page'
                    ].forEach(
                        (key) => url.searchParams.delete(key)
                    );

                    window.history.replaceState(
                        {
                            tab: nextTab
                        },
                        '',
                        url
                    );
                },

                marketNormalize(value) {
                    return String(value ?? '')
                        .normalize('NFD')
                        .replace(
                            /[\u0300-\u036f]/g,
                            ''
                        )
                        .toLocaleLowerCase('es');
                },

                marketRelevance(item, needle) {
                    if (! needle) {
                        return 0;
                    }

                    const name =
                        this.marketNormalize(
                            item.name
                        );

                    const creator =
                        this.marketNormalize(
                            item.creator
                        );

                    if (name === needle) {
                        return 0;
                    }

                    if (name.startsWith(needle)) {
                        return 1;
                    }

                    if (creator === needle) {
                        return 2;
                    }

                    if (creator.startsWith(needle)) {
                        return 3;
                    }

                    return 4;
                },

                get marketFilteredItems() {
                    const needle =
                        this.marketNormalize(
                            this.marketSearch.trim()
                        );

                    const items =
                        this.marketItems.filter(
                            (item) => {
                                if (
                                    this.marketCreator !== null &&
                                    Number(item.creatorId) !== Number(this.marketCreator)
                                ) {
                                    return false;
                                }

                                if (
                                    this.marketExplore === 'new' &&
                                    ! item.isNew
                                ) {
                                    return false;
                                }

                                if (! needle) {
                                    return true;
                                }

                                return (
                                    this.marketNormalize(item.name).includes(needle) ||
                                    this.marketNormalize(item.creator).includes(needle)
                                );
                            }
                        );

                    let sort = this.marketSort;

                    if (
                        sort === 'relevance' &&
                        this.marketExplore === 'popular'
                    ) {
                        sort = 'sales_desc';
                    }

                    if (
                        sort === 'relevance' &&
                        this.marketExplore === 'new'
                    ) {
                        sort = 'newest';
                    }

                    return [...items].sort(
                        (a, b) => {
                            if (sort === 'sales_desc') {
                                return (
                                    Number(b.sales) - Number(a.sales) ||
                                    Number(b.activatedAt) - Number(a.activatedAt) ||
                                    Number(b.id) - Number(a.id)
                                );
                            }

                            if (sort === 'price_asc') {
                                return (
                                    Number(a.price) - Number(b.price) ||
                                    Number(b.sales) - Number(a.sales) ||
                                    Number(b.id) - Number(a.id)
                                );
                            }

                            if (sort === 'price_desc') {
                                return (
                                    Number(b.price) - Number(a.price) ||
                                    Number(b.sales) - Number(a.sales) ||
                                    Number(b.id) - Number(a.id)
                                );
                            }

                            if (sort === 'newest') {
                                return (
                                    Number(b.activatedAt) - Number(a.activatedAt) ||
                                    Number(b.id) - Number(a.id)
                                );
                            }

                            if (sort === 'oldest') {
                                return (
                                    Number(a.activatedAt) - Number(b.activatedAt) ||
                                    Number(a.id) - Number(b.id)
                                );
                            }

                            return (
                                this.marketRelevance(a, needle) -
                                    this.marketRelevance(b, needle) ||
                                Number(b.sales) - Number(a.sales) ||
                                Number(b.activatedAt) - Number(a.activatedAt) ||
                                Number(b.id) - Number(a.id)
                            );
                        }
                    );
                },

                get marketPageCount() {
                    return Math.max(
                        1,
                        Math.ceil(
                            this.marketFilteredItems.length /
                            this.marketPerPage
                        )
                    );
                },

                get marketSafePage() {
                    return Math.max(
                        1,
                        Math.min(
                            Number(this.marketPage) || 1,
                            this.marketPageCount
                        )
                    );
                },

                get marketPageItems() {
                    const start =
                        (this.marketSafePage - 1) *
                        this.marketPerPage;

                    return this.marketFilteredItems.slice(
                        start,
                        start + this.marketPerPage
                    );
                },

                get marketPageIds() {
                    return this.marketPageItems.map(
                        (item) => Number(item.id)
                    );
                },

                get marketCreatorName() {
                    if (this.marketCreator === null) {
                        return '';
                    }

                    const item =
                        this.marketItems.find(
                            (candidate) =>
                                Number(candidate.creatorId) ===
                                Number(this.marketCreator)
                        );

                    return item?.creator ||
                        ('#' + this.marketCreator);
                },

                marketStateChanged() {
                    this.marketPage = 1;
                    this.syncMarketUrl();
                },

                marketSetExplore(explore) {
                    if (! ['all', 'popular', 'new'].includes(explore)) {
                        return;
                    }

                    this.marketExplore = explore;
                    this.marketSort = 'relevance';
                    this.marketPage = 1;
                    this.syncMarketUrl();
                },

                marketSetCreator(creatorId) {
                    this.marketCreator =
                        Number(creatorId);

                    this.marketPage = 1;
                    this.syncMarketUrl();
                },

                marketClearCreator() {
                    this.marketCreator = null;
                    this.marketPage = 1;
                    this.syncMarketUrl();
                },

                marketGoPage(page) {
                    const target =
                        Math.max(
                            1,
                            Math.min(
                                Number(page) || 1,
                                this.marketPageCount
                            )
                        );

                    this.marketPage = target;
                    this.syncMarketUrl();
                },

                marketPageWindow() {
                    const last =
                        this.marketPageCount;

                    const current =
                        this.marketSafePage;

                    if (last <= 7) {
                        return Array.from(
                            {
                                length: last
                            },
                            (_, index) => index + 1
                        );
                    }

                    const result = [1];
                    const start =
                        Math.max(
                            2,
                            current - 2
                        );
                    const end =
                        Math.min(
                            last - 1,
                            current + 2
                        );

                    if (start > 2) {
                        result.push('left-gap');
                    }

                    for (
                        let page = start;
                        page <= end;
                        page++
                    ) {
                        result.push(page);
                    }

                    if (end < last - 1) {
                        result.push('right-gap');
                    }

                    result.push(last);

                    return result;
                },

                syncMarketUrl() {
                    const url = new URL(
                        window.location.href
                    );

                    url.searchParams.set(
                        'tab',
                        'market'
                    );

                    const search =
                        this.marketSearch.trim();

                    if (search) {
                        url.searchParams.set(
                            'q',
                            search
                        );
                    } else {
                        url.searchParams.delete(
                            'q'
                        );
                    }

                    if (this.marketExplore !== 'all') {
                        url.searchParams.set(
                            'explore',
                            this.marketExplore
                        );
                    } else {
                        url.searchParams.delete(
                            'explore'
                        );
                    }

                    if (this.marketSort !== 'relevance') {
                        url.searchParams.set(
                            'sort',
                            this.marketSort
                        );
                    } else {
                        url.searchParams.delete(
                            'sort'
                        );
                    }

                    if (this.marketCreator !== null) {
                        url.searchParams.set(
                            'creator',
                            String(this.marketCreator)
                        );
                    } else {
                        url.searchParams.delete(
                            'creator'
                        );
                    }

                    if (this.marketSafePage > 1) {
                        url.searchParams.set(
                            'page',
                            String(this.marketSafePage)
                        );
                    } else {
                        url.searchParams.delete(
                            'page'
                        );
                    }

                    window.history.replaceState(
                        {
                            tab: 'market'
                        },
                        '',
                        url
                    );
                },

                resetPreview() {
                    this.previewToken = '';
                    this.previewUrl = '';
                    this.previewError = '';
                    this.purchaseId = crypto.randomUUID();
                },

                async generatePreview() {
                    this.previewError = '';

                    const file = this.$refs.image.files[0];

                    if (! file) {
                        this.previewError = 'Selecciona una imagen.';
                        return;
                    }

                    const data = new FormData();
                    data.append('image', file);

                    this.processing = true;

                    try {
                        const response = await fetch(
                            @js(route('marketplace.badges.preview')),
                            {
                                method: 'POST',
                                headers: {
                                    'X-CSRF-TOKEN': @js(csrf_token()),
                                    'Accept': 'application/json'
                                },
                                body: data
                            }
                        );

                        const payload = await response.json();

                        if (! response.ok || ! payload.ok) {
                            const errors = payload.errors || {};
                            const first = Object.values(errors)[0];

                            throw new Error(
                                Array.isArray(first)
                                    ? first[0]
                                    : (payload.message || 'No se pudo generar la vista previa.')
                            );
                        }

                        if (
                            payload.width !== 40 ||
                            payload.height !== 40 ||
                            payload.mime !== 'image/gif'
                        ) {
                            throw new Error(
                                'El servidor no devolvió una placa GIF 40×40 válida.'
                            );
                        }

                        this.previewToken = payload.token;
                        this.previewUrl = payload.preview;
                        this.purchaseId = crypto.randomUUID();
                    } catch (error) {
                        this.previewToken = '';
                        this.previewUrl = '';
                        this.previewError =
                            error.message || 'No se pudo generar la vista previa.';
                    } finally {
                        this.processing = false;
                    }
                }
            }
        }
    </script>
</x-app-layout>
