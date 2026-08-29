<x-app-layout>
    @push('title', 'Personajes archivados - Administracion')

    <div class="col-span-12 flex flex-col gap-y-5">
        <div class="rounded-lg border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-gray-900">
            <div>
                <h1 class="text-3xl font-bold text-gray-900 dark:text-white">
                    Personajes archivados
                </h1>

                <p class="mt-1 text-gray-500 dark:text-gray-400">
                    Restauraci&oacute;n administrativa de personajes eliminados.
                </p>
            </div>

            <form id="archived-character-search" method="GET" action="{{ route('admin.archived-characters') }}" class="mt-6 flex gap-3">
                <input
                    type="text"
                    id="archived-character-query"
                    name="q"
                    autocomplete="off"
                    value="{{ $search }}"
                    placeholder="Nombre, email o ID del personaje"
                    class="w-full rounded-lg border border-gray-300 bg-gray-50 px-4 py-3 text-gray-900 dark:border-gray-700 dark:bg-gray-800 dark:text-white"
                >

                <button
                    type="submit"
                    class="rounded-lg bg-gray-800 px-6 py-3 font-bold text-white transition hover:bg-gray-700"
                >
                    Buscar
                </button>
            </form>
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

        <div id="archived-character-results">
        @if ($archivedCharacters->isEmpty())
            <div class="rounded-lg border border-gray-200 bg-white p-10 text-center dark:border-gray-800 dark:bg-gray-900">
                No hay personajes archivados que coincidan con la b&uacute;squeda.
            </div>
        @else
            <div class="overflow-hidden rounded-lg border border-gray-200 bg-white dark:border-gray-800 dark:bg-gray-900">
                <div class="overflow-x-auto">
                    <table class="w-full text-left">
                        <thead class="bg-gray-50 text-sm text-gray-500 dark:bg-gray-800 dark:text-gray-400">
                            <tr>
                                <th class="px-5 py-4">Personaje</th>
                                <th class="px-5 py-4">Cuenta</th>
                                <th class="px-5 py-4">Archivado</th>
                                <th class="px-5 py-4">Rango</th>
                                <th class="px-5 py-4 text-right">Acci&oacute;n</th>
                            </tr>
                        </thead>

                        <tbody class="divide-y divide-gray-200 dark:divide-gray-800">
                            @foreach ($archivedCharacters as $archived)
                                <tr>
                                    <td class="px-5 py-4">
                                        <div class="font-bold text-gray-900 dark:text-white">
                                            {{ $archived->username }}
                                        </div>

                                        <div class="text-xs text-gray-500">
                                            ID {{ $archived->user_id }}
                                        </div>
                                    </td>

                                    <td class="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">
                                        {{ $archived->account_email }}
                                    </td>

                                    <td class="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">
                                        {{ \Carbon\Carbon::parse($archived->archived_at, 'UTC')->setTimezone('Europe/Madrid')->format('d/m/Y H:i') }}
                                    </td>

                                    <td class="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">
                                        {{ $archived->rank }}
                                    </td>

                                    <td class="px-5 py-4 text-right">
                                        <form
                                            method="POST"
                                            action="{{ route('admin.character-restore', $archived->user_id) }}"
                                            onsubmit="this.querySelector('button[type=submit]').disabled=true;"
                                        >
                                            @csrf

                                            <button
                                                type="submit"
                                                class="rounded-lg bg-green-600 px-4 py-2 font-bold text-white transition hover:bg-green-700 disabled:opacity-60"
                                            >
                                                Restaurar
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            @endforeach
                        </tbody>
                    </table>
                </div>

                <div class="border-t border-gray-200 p-4 dark:border-gray-800">
                    {{ $archivedCharacters->links() }}
                </div>
            </div>
        @endif
        </div>
    </div>

<script id="archived-character-live-search-script">
document.addEventListener('DOMContentLoaded', function ()
{
    const form = document.getElementById('archived-character-search');
    const input = document.getElementById('archived-character-query');
    const results = document.getElementById('archived-character-results');

    if(!form || !input || !results) return;

    let timer = null;
    let activeRequest = null;
    let lastValue = input.value.trim();

    async function loadResults(url)
    {
        if(activeRequest)
        {
            activeRequest.abort();
        }

        activeRequest = new AbortController();

        results.style.opacity = '0.55';
        results.style.transition = 'opacity .12s ease';
        results.setAttribute('aria-busy', 'true');

        try
        {
            const response = await fetch(url, {
                method: 'GET',
                credentials: 'same-origin',
                headers: {
                    'Accept': 'text/html',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                signal: activeRequest.signal
            });

            if(!response.ok)
            {
                throw new Error('HTTP ' + response.status);
            }

            const html = await response.text();
            const parsed = new DOMParser().parseFromString(html, 'text/html');
            const incoming = parsed.getElementById('archived-character-results');

            if(!incoming)
            {
                throw new Error('La respuesta no contiene archived-character-results.');
            }

            results.innerHTML = incoming.innerHTML;

            const target = new URL(url, window.location.origin);

            window.history.replaceState(
                {},
                '',
                target.pathname + target.search
            );
        }
        catch(error)
        {
            if(error.name !== 'AbortError')
            {
                console.error('Error en busqueda AJAX:', error);
            }
        }
        finally
        {
            results.style.opacity = '1';
            results.removeAttribute('aria-busy');
        }
    }

    function buildSearchUrl()
    {
        const url = new URL(form.action, window.location.origin);
        const value = input.value.trim();

        if(value !== '')
        {
            url.searchParams.set('q', value);
        }

        return url.toString();
    }

    form.addEventListener('submit', function (event)
    {
        event.preventDefault();

        lastValue = input.value.trim();
        loadResults(buildSearchUrl());
    });

    input.addEventListener('input', function ()
    {
        window.clearTimeout(timer);

        timer = window.setTimeout(function ()
        {
            const value = input.value.trim();

            if(value === lastValue) return;

            lastValue = value;
            loadResults(buildSearchUrl());
        }, 300);
    });

    results.addEventListener('click', function (event)
    {
        const link = event.target.closest('a[href]');

        if(!link) return;

        const url = new URL(link.href, window.location.origin);

        if(url.origin !== window.location.origin) return;

        event.preventDefault();
        loadResults(url.toString());
    });
});
</script>
</x-app-layout>