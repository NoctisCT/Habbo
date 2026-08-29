<?php

namespace App\Filament\Resources\Audit;

use App\Filament\Resources\Audit\AdminCreditAdjustmentResource\Pages;
use App\Models\AdminCreditAdjustment;
use Filament\Forms\Components\DatePicker;
use Filament\Infolists\Components\Section;
use Filament\Infolists\Components\TextEntry;
use Filament\Infolists\Infolist;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Filters\Filter;
use Filament\Tables\Filters\SelectFilter;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

class AdminCreditAdjustmentResource extends Resource
{
    protected static ?string $model =
        AdminCreditAdjustment::class;

    protected static ?string $navigationIcon =
        'heroicon-o-banknotes';

    protected static ?string $navigationGroup =
        "Auditor\u{00ED}a";

    protected static ?string $navigationLabel =
        "Historial de cr\u{00E9}ditos";

    protected static ?string $modelLabel =
        "ajuste de cr\u{00E9}ditos";

    protected static ?string $pluralModelLabel =
        "historial de cr\u{00E9}ditos";

    protected static ?string $slug =
        'audit/credit-history';

    protected static ?int $navigationSort = 10;

    public static function shouldRegisterNavigation(): bool
    {
        return static::isMaximumRank();
    }

    public static function canViewAny(): bool
    {
        return static::isMaximumRank();
    }

    public static function canView(Model $record): bool
    {
        return static::isMaximumRank();
    }

    public static function canCreate(): bool
    {
        return false;
    }

    public static function canEdit(Model $record): bool
    {
        return false;
    }

    public static function canDelete(Model $record): bool
    {
        return false;
    }

    public static function canDeleteAny(): bool
    {
        return false;
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                Tables\Columns\TextColumn::make('created_at')
                    ->label('Fecha')
                    ->dateTime('d/m/Y H:i:s', 'Europe/Madrid')
                    ->sortable(),

                Tables\Columns\TextColumn::make(
                    'actor.username'
                )
                    ->label('Administrador')
                    ->searchable()
                    ->sortable(),

                Tables\Columns\TextColumn::make(
                    'target.username'
                )
                    ->label('Personaje')
                    ->searchable()
                    ->sortable(),

                Tables\Columns\TextColumn::make('delta')
                    ->label('Ajuste')
                    ->formatStateUsing(
                        function ($state): string {
                            $value = (int) $state;

                            return
                                ($value > 0 ? '+' : '') .
                                number_format(
                                    $value,
                                    0,
                                    ',',
                                    '.'
                                );
                        }
                    )
                    ->sortable(),

                Tables\Columns\TextColumn::make(
                    'balance_summary'
                )
                    ->label('Saldo')
                    ->getStateUsing(
                        function (
                            AdminCreditAdjustment $record
                        ): string {
                            $before =
                                $record->balance_before;

                            $after =
                                $record->balance_after;

                            if (
                                $before === null ||
                                $after === null
                            ) {
                                return '-';
                            }

                            return
                                number_format(
                                    $before,
                                    0,
                                    ',',
                                    '.'
                                ) .
                                " \u{2192} " .
                                number_format(
                                    $after,
                                    0,
                                    ',',
                                    '.'
                                );
                        }
                    ),

                Tables\Columns\TextColumn::make('reason')
                    ->label('Motivo')
                    ->wrap()
                    ->limit(80)
                    ->tooltip(
                        fn (
                            AdminCreditAdjustment $record
                        ): string => $record->reason
                    ),

                Tables\Columns\TextColumn::make('channel')
                    ->label('Canal')
                    ->getStateUsing(
                        fn (
                            AdminCreditAdjustment $record
                        ): string =>
                            $record->bridge_transaction_id
                                ? 'CreditBridge'
                                : 'Base de datos'
                    ),

                Tables\Columns\TextColumn::make('status')
                    ->label('Estado')
                    ->badge()
                    ->formatStateUsing(
                        fn ($state): string =>
                            match ((string) $state) {
                                'completed' =>
                                    'Completado',
                                'processing' =>
                                    'Procesando',
                                'manual_review' =>
                                    "Revisi\u{00F3}n manual",
                                'failed' =>
                                    'Fallido',
                                default =>
                                    (string) $state,
                            }
                    ),

                Tables\Columns\TextColumn::make('id')
                    ->label('UUID')
                    ->copyable()
                    ->toggleable(
                        isToggledHiddenByDefault: true
                    ),
            ])
            ->defaultSort('created_at', 'desc')
            ->filters([
                SelectFilter::make('actor_user_id')
                    ->label('Administrador')
                    ->relationship(
                        'actor',
                        'username'
                    )
                    ->searchable()
                    ->preload(),

                SelectFilter::make('target_user_id')
                    ->label('Personaje')
                    ->relationship(
                        'target',
                        'username'
                    )
                    ->searchable()
                    ->preload(),

                SelectFilter::make('status')
                    ->label('Estado')
                    ->options([
                        'completed' =>
                            'Completado',
                        'processing' =>
                            'Procesando',
                        'manual_review' =>
                            "Revisi\u{00F3}n manual",
                        'failed' =>
                            'Fallido',
                    ]),

                Filter::make('created_at')
                    ->label('Fecha')
                    ->form([
                        DatePicker::make('from')
                            ->label('Desde'),
                        DatePicker::make('until')
                            ->label('Hasta'),
                    ])
                    ->query(
                        function (
                            Builder $query,
                            array $data
                        ): Builder {
                            return $query
                                ->when(
                                    $data['from'] ?? null,
                                    fn (
                                        Builder $query,
                                        $date
                                    ): Builder =>
                                        $query->whereDate(
                                            'created_at',
                                            '>=',
                                            $date
                                        )
                                )
                                ->when(
                                    $data['until'] ?? null,
                                    fn (
                                        Builder $query,
                                        $date
                                    ): Builder =>
                                        $query->whereDate(
                                            'created_at',
                                            '<=',
                                            $date
                                        )
                                );
                        }
                    ),
            ])
            ->actions([
                Tables\Actions\ViewAction::make()
                    ->label('Ver'),
            ])
            ->bulkActions([]);
    }

    public static function infolist(
        Infolist $infolist
    ): Infolist {
        return $infolist
            ->schema([
                Section::make(
                    "Operaci\u{00F3}n"
                )
                    ->columns(3)
                    ->schema([
                        TextEntry::make('created_at')
                            ->label('Fecha')
                            ->dateTime(
                                'd/m/Y H:i:s',
                                'Europe/Madrid'
                            ),

                        TextEntry::make(
                            'actor.username'
                        )
                            ->label('Administrador'),

                        TextEntry::make(
                            'target.username'
                        )
                            ->label('Personaje'),

                        TextEntry::make('delta')
                            ->label('Ajuste')
                            ->formatStateUsing(
                                function ($state): string {
                                    $value =
                                        (int) $state;

                                    return
                                        ($value > 0
                                            ? '+'
                                            : '') .
                                        number_format(
                                            $value,
                                            0,
                                            ',',
                                            '.'
                                        );
                                }
                            ),

                        TextEntry::make(
                            'balance_before'
                        )
                            ->label('Saldo anterior')
                            ->formatStateUsing(
                                fn ($state): string =>
                                    $state === null
                                        ? '-'
                                        : number_format(
                                            (int) $state,
                                            0,
                                            ',',
                                            '.'
                                        )
                            ),

                        TextEntry::make(
                            'balance_after'
                        )
                            ->label('Saldo posterior')
                            ->formatStateUsing(
                                fn ($state): string =>
                                    $state === null
                                        ? '-'
                                        : number_format(
                                            (int) $state,
                                            0,
                                            ',',
                                            '.'
                                        )
                            ),

                        TextEntry::make('status')
                            ->label('Estado')
                            ->badge()
                            ->formatStateUsing(
                                fn ($state): string =>
                                    match (
                                        (string) $state
                                    ) {
                                        'completed' =>
                                            'Completado',
                                        'processing' =>
                                            'Procesando',
                                        'manual_review' =>
                                            "Revisi\u{00F3}n manual",
                                        'failed' =>
                                            'Fallido',
                                        default =>
                                            (string) $state,
                                    }
                            ),

                        TextEntry::make(
                            'payment_channel'
                        )
                            ->label('Canal')
                            ->getStateUsing(
                                fn (
                                    AdminCreditAdjustment $record
                                ): string =>
                                    $record
                                        ->bridge_transaction_id
                                            ? 'CreditBridge'
                                            : 'Base de datos'
                            ),

                        TextEntry::make(
                            'completed_at'
                        )
                            ->label('Completado')
                            ->dateTime(
                                'd/m/Y H:i:s',
                                'Europe/Madrid'
                            )
                            ->placeholder('-'),
                    ]),

                Section::make(
                    "Auditor\u{00ED}a"
                )
                    ->schema([
                        TextEntry::make('reason')
                            ->label('Motivo'),

                        TextEntry::make('id')
                            ->label('UUID del ajuste')
                            ->copyable(),

                        TextEntry::make(
                            'bridge_transaction_id'
                        )
                            ->label(
                                'UUID de CreditBridge'
                            )
                            ->copyable()
                            ->placeholder(
                                'No utilizado'
                            ),

                        TextEntry::make(
                            'error_code'
                        )
                            ->label(
                                "C\u{00F3}digo de error"
                            )
                            ->placeholder(
                                'Sin errores'
                            ),
                    ]),
            ]);
    }

    public static function getPages(): array
    {
        return [
            'index' =>
                Pages\ListAdminCreditAdjustments::route(
                    '/'
                ),

            'view' =>
                Pages\ViewAdminCreditAdjustment::route(
                    '/{record}'
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
            (int) $actorLevel === (int) $maxLevel;
    }
}