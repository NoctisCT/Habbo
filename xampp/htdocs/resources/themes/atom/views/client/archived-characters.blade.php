<x-app-layout>
    @push('title', 'Personajes archivados')

    <div class="col-span-12 flex flex-col gap-y-5">
        <div class="rounded-lg border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-gray-900">
            <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div>
                    <h1 class="text-3xl font-bold text-gray-900 dark:text-white">
                        Personajes archivados
                    </h1>

                    <p class="mt-1 text-gray-500 dark:text-gray-400">
                        Recupera personajes eliminados anteriormente de tu cuenta.
                    </p>
                </div>

                <div class="flex flex-wrap items-center gap-2">
                    <span class="rounded-full bg-gray-100 px-4 py-2 text-sm font-semibold text-gray-700 dark:bg-gray-800 dark:text-gray-200">
                        {{ $slotsUsed }} / {{ $slotsTotal }} slots
                    </span>

                    <a
                        data-turbolinks="false"
                        href="{{ route('character-select') }}"
                        class="rounded-lg bg-gray-100 px-5 py-3 font-semibold text-gray-700 transition hover:bg-gray-200 dark:bg-gray-800 dark:text-gray-200 dark:hover:bg-gray-700"
                    >
                        Volver a personajes
                    </a>
                </div>
            </div>
        </div>

        @if (session('success'))
            <div class="rounded-lg border border-green-300 bg-green-50 px-5 py-4 font-medium text-green-800 dark:border-green-900 dark:bg-green-950 dark:text-green-200">
                {{ session('success') }}
            </div>
        @endif

        @if ($errors->any())
            <div class="rounded-lg border border-red-300 bg-red-50 px-5 py-4 text-red-800 dark:border-red-900 dark:bg-red-950 dark:text-red-200">
                @foreach ($errors->all() as $error)
                    <div>{{ $error }}</div>
                @endforeach
            </div>
        @endif

        @if ($archivedCharacters->isEmpty())
            <div class="rounded-lg border border-gray-200 bg-white p-10 text-center dark:border-gray-800 dark:bg-gray-900">
                <h2 class="text-2xl font-bold text-gray-900 dark:text-white">
                    No tienes personajes archivados
                </h2>

                <p class="mt-2 text-gray-500 dark:text-gray-400">
                    Los personajes que elimines aparecer&aacute;n aqu&iacute;.
                </p>
            </div>
        @else
            <div class="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3">
                @foreach ($archivedCharacters as $archived)
                    <div class="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-gray-800 dark:bg-gray-900">
                        <div
                            class="relative flex h-48 items-end justify-center overflow-hidden"
                            style="background: rgba(0,0,0,.25) url({{ setting('cms_me_backdrop') }});"
                        >
                            <div class="absolute left-4 top-4 z-10">
                                <span class="rounded-full bg-gray-700 px-3 py-1 text-xs font-bold text-white">
                                    Archivado
                                </span>
                            </div>

                            <img
                                class="h-44 object-contain drop-shadow-xl"
                                style="image-rendering: auto;"
                                src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw=="
                                data-character-avatar="1"
                                data-avatar-id="{{ $archived->id }}"
                                data-avatar-figure="{{ $archived->look }}"
                                data-avatar-gender="{{ $archived->gender ?: 'M' }}"
                                data-avatar-gesture="sad"
                                alt="{{ $archived->username }}"
                            >
                        </div>

                        <div class="flex flex-col gap-4 p-5">
                            <div>
                                <h2 class="truncate text-2xl font-bold text-gray-900 dark:text-white">
                                    {{ $archived->username }}
                                </h2>

                                <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
                                    Eliminado:
                                    {{ \Carbon\Carbon::parse($archived->archived_at, 'UTC')->setTimezone('Europe/Madrid')->format('d/m/Y H:i') }}
                                </p>
                            </div>

                            <p class="text-sm text-gray-600 dark:text-gray-300">
                                Al restaurarlo recuperar&aacute;s este mismo personaje con su inventario, progreso, placas y dem&aacute;s datos.
                            </p>

                            @if ($slotsUsed < $slotsTotal)
                                <form
                                    action="{{ route('character-restore-paid', $archived->id) }}"
                                    method="POST"
                                    class="flex flex-col gap-3"
                                    onsubmit="this.querySelector('button[type=submit]').disabled=true;"
                                >
                                    @csrf
                      <input
                          type="hidden"
                          name="purchase_id"
                          value="{{ (string) \Illuminate\Support\Str::uuid() }}"
                      >

                                    <label class="text-sm font-semibold text-gray-700 dark:text-gray-300">
                                        Personaje que paga
                                    </label>

                                    <select
                                        name="payer_user_id"
                                        required
                                        class="rounded-lg border border-gray-300 bg-gray-50 px-4 py-3 text-gray-900 dark:border-gray-700 dark:bg-gray-800 dark:text-white"
                                    >
                                        <option value="">Selecciona un personaje</option>

                                        @foreach ($characters as $payer)
                                            <option
                                                value="{{ $payer->id }}"
                                                @selected((string) old('payer_user_id') === (string) $payer->id)
                                            >
                                                {{ $payer->username }} &mdash; {{ number_format((int) $payer->credits, 0, ',', '.') }} cr&eacute;ditos{{ $payer->online ? ' - Conectado' : '' }}
                                            </option>
                                        @endforeach
                                    </select>

                                    <p class="text-xs text-gray-500 dark:text-gray-400">
                                        Puedes pagar con personajes conectados o desconectados.
                                    </p>

                                    <button
                                        type="submit"
                                        class="w-full rounded-lg bg-green-600 px-5 py-3 font-bold text-white transition hover:bg-green-700 disabled:cursor-not-allowed disabled:opacity-60"
                                    >
                                        Restaurar por {{ number_format($restorePrice, 0, ',', '.') }} cr&eacute;ditos
                                    </button>
                                </form>
                            @else
                                <div class="rounded-lg bg-yellow-50 px-4 py-3 text-sm font-semibold text-yellow-800 dark:bg-yellow-950 dark:text-yellow-200">
                                    Necesitas un slot libre para restaurar este personaje.
                                </div>
                            @endif
                        </div>
                    </div>
                @endforeach
            </div>
        @endif
    </div>

<style id="archived-avatar-bridge-styles">
    [data-character-avatar="1"] {
        position: absolute;
        left: 50%;
        bottom: 8px;
        transform: translateX(-50%) scale(1.28);
        transform-origin: bottom center;
        image-rendering: auto;
        z-index: 3;
        width: auto !important;
        height: auto !important;
        max-width: none !important;
        pointer-events: none;
        filter: drop-shadow(0 2px 0 rgba(0,0,0,.18));
    }
</style>

<script id="archived-avatar-bridge-script">
document.addEventListener('DOMContentLoaded', function ()
{
    const avatars = Array.from(
        document.querySelectorAll('[data-character-avatar="1"]')
    );

    if(!avatars.length) return;

    avatars.forEach(function (avatar)
    {
        const media = avatar.parentElement;

        if(!media) return;

        media.style.position = 'relative';
        media.style.overflow = 'hidden';
        media.style.backgroundRepeat = 'no-repeat';
        media.style.backgroundSize = 'cover';
        media.style.backgroundPosition = 'center bottom';
    });

    const bridge = document.createElement('iframe');

    bridge.src = '/dist/index.html?avatar-bridge=1';
    bridge.setAttribute('aria-hidden', 'true');
    bridge.setAttribute('tabindex', '-1');

    Object.assign(bridge.style, {
        position: 'absolute',
        width: '2px',
        height: '2px',
        left: '-10000px',
        top: '-10000px',
        border: '0',
        opacity: '0',
        pointerEvents: 'none'
    });

    window.addEventListener('message', function (event)
    {
        if(event.origin !== window.location.origin) return;
        if(!event.data) return;

        if(event.data.type === 'avatar-bridge-ready')
        {
            avatars.forEach(function (avatar)
            {
                if(!bridge.contentWindow) return;

                bridge.contentWindow.postMessage({
                    type: 'avatar-bridge-render',
                    id: avatar.dataset.avatarId,
                    figure: avatar.dataset.avatarFigure || '',
                    gender: avatar.dataset.avatarGender || 'M',
                    direction: 2,
                    gesture: avatar.dataset.avatarGesture || ''
                }, window.location.origin);
            });

            return;
        }

        if(event.data.type !== 'avatar-bridge-result') return;

        const avatar = document.querySelector(
            '[data-character-avatar="1"][data-avatar-id="' +
            CSS.escape(String(event.data.id)) +
            '"]'
        );

        if(!avatar || !event.data.src) return;

        avatar.src = event.data.src;
    });

    document.body.appendChild(bridge);
});
</script>
</x-app-layout>