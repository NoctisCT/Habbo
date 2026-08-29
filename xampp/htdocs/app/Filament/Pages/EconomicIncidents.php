<?php

namespace App\Filament\Pages;

use Filament\Pages\Page;
use Illuminate\Support\Facades\Artisan;
use Illuminate\Support\Facades\DB;

class EconomicIncidents extends Page
{
    protected static ?string $navigationIcon =
        'heroicon-o-exclamation-triangle';

    protected static ?string $navigationGroup =
        "Auditor\u{00ED}a";

    protected static ?string $navigationLabel =
        "Incidencias econ\u{00F3}micas";

    protected static ?string $title =
        "Incidencias econ\u{00F3}micas";

    protected static ?int $navigationSort = 20;

    protected static string $view =
        'filament.pages.economic-incidents';

    public static function shouldRegisterNavigation(): bool
    {
        return static::isMaximumRank();
    }

    public static function canAccess(): bool
    {
        return static::isMaximumRank();
    }

    public function getReconciliation(): array
    {
        $exitCode = Artisan::call(
            'credits:reconcile'
        );

        return [
            'exit_code' => $exitCode,
            'output' => trim(
                Artisan::output()
            ),
            'checked_at' => now(
                'Europe/Madrid'
            )->format(
                'd/m/Y H:i:s'
            ),
        ];
    }

    private static function isMaximumRank(): bool
    {
        $actor = auth()->user();

        if (! $actor) {
            return false;
        }

        $actorLevel = DB::table('permissions')
            ->where('id', $actor->rank)
            ->value('level');

        $maxLevel = DB::table('permissions')
            ->max('level');

        return
            $actorLevel !== null &&
            $maxLevel !== null &&
            (int) $actorLevel ===
                (int) $maxLevel;
    }
}