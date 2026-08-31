<x-app-layout>
    @push('title', 'Notificaciones')

    <div class="col-span-12 flex flex-col gap-y-5">
        <div class="rounded-lg border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-gray-900">
            <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                <div>
                    <h1 class="text-3xl font-bold text-gray-900 dark:text-white">
                        Notificaciones
                    </h1>

                    <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
                        Aquí encontrarás avisos de tu cuenta, placas, marketplace y otros sistemas del hotel.
                    </p>
                </div>

                @if ($unreadCount > 0)
                    <form method="POST" action="{{ route('notifications.mark-all-read') }}">
                        @csrf

                        <button
                            type="submit"
                            class="rounded-lg bg-gray-900 px-4 py-2.5 text-sm font-bold text-white transition hover:bg-black dark:bg-gray-700 dark:hover:bg-gray-600"
                        >
                            Marcar todas como leídas
                        </button>
                    </form>
                @endif
            </div>
        </div>

        <div class="overflow-hidden rounded-lg border border-gray-200 bg-white dark:border-gray-800 dark:bg-gray-900">
            @forelse ($notifications as $notification)
                <form
                    method="POST"
                    action="{{ route('notifications.open', $notification) }}"
                    class="border-b border-gray-100 last:border-b-0 dark:border-gray-800"
                >
                    @csrf

                    <button
                        type="submit"
                        class="relative flex w-full gap-4 px-5 py-4 text-left transition hover:bg-gray-50 dark:hover:bg-gray-800 {{ $notification->read_at === null ? 'bg-red-50/50 dark:bg-red-950/10' : '' }}"
                    >
                        <span class="mt-1.5 flex h-3 w-3 shrink-0 items-center justify-center">
                            @if ($notification->read_at === null)
                                <span class="h-2.5 w-2.5 rounded-full bg-red-600"></span>
                            @else
                                <span class="h-2.5 w-2.5 rounded-full bg-gray-300 dark:bg-gray-700"></span>
                            @endif
                        </span>

                        <span class="min-w-0 flex-1">
                            <span class="flex flex-wrap items-center justify-between gap-2">
                                <span class="font-bold text-gray-900 dark:text-white">
                                    {{ $notification->title }}
                                </span>

                                <span class="text-xs text-gray-400">
                                    {{ $notification->created_at?->setTimezone('Europe/Madrid')->format('d/m/Y H:i') }}
                                </span>
                            </span>

                            <span class="mt-1 block text-sm leading-6 text-gray-600 dark:text-gray-300">
                                {{ $notification->message }}
                            </span>
                        </span>
                    </button>
                </form>
            @empty
                <div class="px-6 py-12 text-center">
                    <div class="text-lg font-bold text-gray-700 dark:text-gray-200">
                        Todavía no tienes notificaciones
                    </div>

                    <p class="mt-2 text-sm text-gray-500 dark:text-gray-400">
                        Cuando haya algo nuevo aparecerá aquí.
                    </p>
                </div>
            @endforelse
        </div>

        @if ($notifications->hasPages())
            <div>
                {{ $notifications->links() }}
            </div>
        @endif
    </div>
</x-app-layout>
