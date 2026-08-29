<x-app-layout>
    @push('title', 'Personajes')

    <div class="col-span-12 flex flex-col gap-y-5">
        <div class="rounded-lg border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-gray-900">
            <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div>
                    <h1 class="text-3xl font-bold text-gray-900 dark:text-white">
                        Tus personajes
                    </h1>

                    <p class="mt-1 text-gray-500 dark:text-gray-400">
                        Elige con quién quieres jugar y gestiona los personajes de tu cuenta.
                    </p>
                </div>

                <div class="flex flex-wrap gap-2">
                    <span class="rounded-full bg-gray-100 px-4 py-2 text-sm font-semibold text-gray-700 dark:bg-gray-800 dark:text-gray-200">
                        {{ $slotsUsed }} / {{ $slotsTotal }} slots
                    </span>

                    @if ($archivedCount > 0)
                        <a
                            data-turbolinks="false"
                            href="{{ route('character-archived-index') }}"
                            class="rounded-full bg-gray-100 px-4 py-2 text-sm font-semibold text-gray-600 transition hover:bg-gray-200 hover:text-gray-900 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700 dark:hover:text-white"
                        >
                            {{ $archivedCount }} archivado{{ $archivedCount === 1 ? '' : 's' }}
                        </a>
                    @endif
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

        <div class="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3">
            @foreach ($characters as $character)
                <div class="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-gray-800 dark:bg-gray-900">
                    <div
                        class="relative flex h-48 items-end justify-center overflow-hidden"
                        style="background: rgba(0,0,0,.25) url({{ setting('cms_me_backdrop') }});"
                    >
                        <div class="absolute left-4 top-4 flex flex-wrap gap-2">
                            @if ((int) $character->is_primary === 1)
                                <span class="rounded-full bg-yellow-400 px-3 py-1 text-xs font-bold text-black">
                                    ★ Principal
                                </span>
                            @endif

                            @if ((bool) $character->online)
                                <span class="rounded-full bg-green-500 px-3 py-1 text-xs font-bold text-white">
                                    ● Online
                                </span>
                            @else
                                <span class="rounded-full bg-gray-700 px-3 py-1 text-xs font-bold text-white">
                                    Offline
                                </span>
                            @endif
                        </div>

                        <div class="absolute right-4 top-4 rounded-full bg-black/60 px-3 py-1 text-xs font-semibold text-white">
                            Slot {{ $character->slot }}
                        </div>

                        <img
                            class="h-44 object-contain drop-shadow-xl"
                            style="image-rendering: auto;"
                            src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw=="
                            data-character-avatar="1"
                            data-avatar-id="{{ $character->id }}"
                            data-avatar-figure="{{ $character->look }}"
                            data-avatar-gender="{{ $character->gender ?: 'M' }}"
                            alt="{{ $character->username }}"
                        >
                    </div>

                    <div class="flex flex-col gap-4 p-5">
                        <div>
                            <div class="flex items-center justify-between gap-3">
                                <h2 class="truncate text-2xl font-bold text-gray-900 dark:text-white">
                                    {{ $character->username }}
                                </h2>

    
                            </div>

                            <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
                                @if ($character->last_online)
                                    Última conexión:
                                    {{ \Carbon\Carbon::createFromTimestamp((int) $character->last_online, 'UTC')->setTimezone('Europe/Madrid')->format('d/m/Y H:i') }}
                                @else
                                    Todavía no ha entrado al hotel
                                @endif
                            </p>
                        </div>

                        <form
                            action="{{ route('character-motto', $character->id) }}"
                            method="POST"
                            class="flex flex-col gap-2"
                        >
                            @csrf
                            @method('PATCH')

                            <label class="text-xs font-bold uppercase tracking-wide text-gray-500 dark:text-gray-400">
                                Misión
                            </label>

                            <div class="flex gap-2">
                                <input
                                    type="text"
                                    name="motto"
                                    maxlength="127"
                                    value="{{ $character->motto }}"
                                    class="min-w-0 flex-1 rounded-lg border border-gray-300 bg-gray-50 px-3 py-2 text-sm text-gray-900 dark:border-gray-700 dark:bg-gray-800 dark:text-white"
                                >

                                <button
                                    type="submit"
                                    class="rounded-lg bg-gray-800 px-4 py-2 text-sm font-semibold text-white transition hover:bg-black dark:bg-gray-700 dark:hover:bg-gray-600"
                                >
                                    Guardar
                                </button>
                            </div>
                        </form>

                        <div class="grid grid-cols-2 gap-2">
                            <a
                                data-turbolinks="false"
                                href="{{ route('nitro-character', $character->id) }}"
                                target="_blank"
                                class="flex items-center justify-center rounded-lg bg-green-600 px-4 py-3 text-center font-bold text-white transition hover:bg-green-700"
                            >
                                Entrar
                            </a>

                            <a
                                href="{{ route('profile.show', $character->username) }}"
                                class="flex items-center justify-center rounded-lg bg-gray-100 px-4 py-3 text-center font-semibold text-gray-800 transition hover:bg-gray-200 dark:bg-gray-800 dark:text-gray-200 dark:hover:bg-gray-700"
                            >
                                Perfil
                            </a>
                        </div>

                        @if ((int) $character->is_primary !== 1)
                            <form
                                action="{{ route('character-primary', $character->id) }}"
                                method="POST"
                            >
                                @csrf
                                @method('PATCH')

                                <button
                                    type="submit"
                                    class="w-full rounded-lg border border-yellow-400 bg-yellow-50 px-4 py-3 font-bold text-yellow-800 transition duration-200 hover:border-yellow-500 hover:bg-yellow-400 hover:text-black dark:bg-yellow-950 dark:text-yellow-200 dark:hover:bg-yellow-400 dark:hover:text-black"
                                >
                                    ★ Hacer personaje principal
                                </button>
                            </form>

                            <form
                                action="{{ route('character-archive', $character->id) }}"
                                method="POST"
                                onsubmit="return confirm('¿Seguro que quieres eliminar a {{ addslashes($character->username) }}? El personaje dejará de estar disponible y su restauración no será gratuita.');"
                            >
                                @csrf
                                @method('DELETE')

                                <button
                                    type="submit"
                                    class="w-full rounded-lg border border-red-300 px-4 py-3 font-semibold text-red-600 transition duration-200 hover:border-red-600 hover:bg-red-600 hover:text-white dark:border-red-900 dark:text-red-400 dark:hover:border-red-600 dark:hover:bg-red-600 dark:hover:text-white"
                                >
                                    Eliminar personaje
                                </button>
                            </form>
                        @else
                            <div class="rounded-lg bg-yellow-50 px-4 py-3 text-center text-sm text-yellow-800 dark:bg-yellow-950 dark:text-yellow-200">
                                Para eliminar este personaje primero debes elegir otro como principal.
                            </div>
                        @endif
                    </div>
                </div>
            @endforeach

            @if ($slotsUsed < $slotsTotal)
                <div class="flex min-h-[520px] flex-col items-center justify-center rounded-xl border-2 border-dashed border-gray-300 bg-white p-8 text-center dark:border-gray-700 dark:bg-gray-900">
                    <div class="mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-gray-100 text-5xl font-light text-gray-400 dark:bg-gray-800">
                        +
                    </div>

                    <h2 class="text-2xl font-bold text-gray-900 dark:text-white">
                        Crear personaje
                    </h2>

                    <p class="mt-2 max-w-xs text-sm text-gray-500 dark:text-gray-400">
                        Tienes {{ $slotsTotal - $slotsUsed }} slot{{ ($slotsTotal - $slotsUsed) === 1 ? '' : 's' }} libre{{ ($slotsTotal - $slotsUsed) === 1 ? '' : 's' }}.
                    </p>

                    <form
                        action="{{ route('character-create') }}"
                        method="POST"
                        class="mt-6 flex w-full max-w-sm flex-col gap-3"
                    >
                        @csrf

                        <input
                            type="text"
                            name="username"
                            maxlength="25"
                            required
                            placeholder="Nombre del personaje"
                            class="rounded-lg border border-gray-300 bg-gray-50 px-4 py-3 text-center text-gray-900 dark:border-gray-700 dark:bg-gray-800 dark:text-white"
                        >

                        <button
                            type="submit"
                            class="rounded-lg bg-green-600 px-5 py-3 font-bold text-white transition hover:bg-green-700"
                        >
                            Crear nuevo personaje
                        </button>
                    </form>
                </div>
            @endif

            <div class="flex min-h-[520px] flex-col items-center justify-center rounded-xl border-2 border-dashed border-gray-300 bg-white p-8 text-center dark:border-gray-700 dark:bg-gray-900">
                <div class="mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-gray-100 text-5xl font-light text-gray-400 dark:bg-gray-800">
                    +
                </div>

                <h2 class="text-2xl font-bold text-gray-900 dark:text-white">
                    Comprar slot adicional
                </h2>

                <p class="mt-2 max-w-xs text-sm text-gray-500 dark:text-gray-400">
                    A&ntilde;ade permanentemente un nuevo slot a tu cuenta.
                </p>

                <div class="mt-4 rounded-lg bg-gray-100 px-4 py-2 text-sm font-bold text-gray-700 dark:bg-gray-800 dark:text-gray-200">
                    {{ number_format($slotPrice, 0, ',', '.') }} cr&eacute;ditos
                </div>

                <form
                    action="{{ route('character-slot-purchase') }}"
                    method="POST"
                    class="mt-6 flex w-full max-w-sm flex-col gap-3"
                    onsubmit="this.querySelector('button[type=submit]').disabled=true;"
                >
                    @csrf
                      <input
                          type="hidden"
                          name="purchase_id"
                          value="{{ (string) \Illuminate\Support\Str::uuid() }}"
                      >

                    <label class="text-left text-sm font-semibold text-gray-700 dark:text-gray-300">
                        Personaje que paga
                    </label>

                    <select
                        name="payer_user_id"
                        required
                        class="rounded-lg border border-gray-300 bg-gray-50 px-4 py-3 text-gray-900 dark:border-gray-700 dark:bg-gray-800 dark:text-white"
                    >
                        <option value="">Selecciona un personaje</option>

                        @foreach ($characters as $character)
                            <option
                                value="{{ $character->id }}"
                                @selected((string) old('payer_user_id') === (string) $character->id)
                            >
                                {{ $character->username }} &mdash; {{ number_format((int) $character->credits, 0, ',', '.') }} cr&eacute;ditos{{ $character->online ? ' - Conectado' : '' }}
                            </option>
                        @endforeach
                    </select>

                    @error('payer_user_id')
                        <p class="text-sm font-semibold text-red-600">
                            {{ $message }}
                        </p>
                    @enderror

                    <p class="text-xs text-gray-500 dark:text-gray-400">
                        Puedes pagar con personajes conectados o desconectados.
                    </p>

                    <button
                        type="submit"
                        class="w-full rounded-lg bg-green-600 px-5 py-3 font-bold text-white transition hover:bg-green-700 disabled:cursor-not-allowed disabled:opacity-60"
                    >
                        Pagar {{ number_format($slotPrice, 0, ',', '.') }} cr&eacute;ditos y comprar slot
                    </button>
                </form>
            </div>
        </div>
    </div>
<style id="character-avatar-bridge-styles">
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

    .character-primary-action,
    .character-delete-action {
        transition:
            background-color .18s ease,
            border-color .18s ease,
            color .18s ease,
            transform .18s ease,
            box-shadow .18s ease !important;
    }

    .character-primary-action:hover {
        background: #facc15 !important;
        border-color: #eab308 !important;
        color: #111827 !important;
        transform: translateY(-2px);
        box-shadow: 0 8px 18px rgba(234, 179, 8, .28);
    }

    .character-delete-action:hover {
        background: #dc2626 !important;
        border-color: #b91c1c !important;
        color: #ffffff !important;
        transform: translateY(-2px);
        box-shadow: 0 8px 18px rgba(220, 38, 38, .28);
    }
</style>

<script id="character-avatar-bridge-script">
document.addEventListener('DOMContentLoaded', function ()
{
    const avatars = Array.from(document.querySelectorAll('[data-character-avatar="1"]'));

    document.querySelectorAll('form').forEach(function (form)
    {
        const button = form.querySelector('button');

        if(!button) return;

        if(form.action && form.action.endsWith('/primary'))
        {
            button.classList.add('character-primary-action');
        }

        const method = form.querySelector('input[name="_method"]');

        if(method && String(method.value).toUpperCase() === 'DELETE')
        {
            button.classList.add('character-delete-action');
        }
    });

    function findBackgroundContainer(element)
    {
        let node = element ? element.parentElement : null;

        while(node)
        {
            const style = window.getComputedStyle(node);
            const hasBackground = style.backgroundImage && style.backgroundImage !== 'none';

            if(hasBackground) return node;

            node = node.parentElement;
        }

        return null;
    }

    function tuneAvatarCard(avatar)
    {
        const media = findBackgroundContainer(avatar);

        if(media)
        {
            media.style.position = 'relative';
            media.style.overflow = 'hidden';
            media.style.backgroundRepeat = 'no-repeat';
            media.style.backgroundSize = 'cover';
            media.style.backgroundPosition = 'center bottom';
        }

        const parent = avatar.parentElement;

        if(parent)
        {
            parent.style.position = 'relative';
            parent.style.overflow = 'hidden';
        }
    }

    avatars.forEach(tuneAvatarCard);

    if(!avatars.length) return;

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
                    direction: 2
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
        tuneAvatarCard(avatar);
    });

    document.body.appendChild(bridge);
});
</script>
</x-app-layout>