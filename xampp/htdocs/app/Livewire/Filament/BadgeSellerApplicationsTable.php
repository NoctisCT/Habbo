<?php

namespace App\Livewire\Filament;

use App\Models\BadgeSellerLicense;
use App\Services\BadgeSellerEligibilityService;
use Carbon\Carbon;
use Filament\Forms\Components\Textarea;
use Filament\Forms\Concerns\InteractsWithForms;
use Filament\Forms\Contracts\HasForms;
use Filament\Notifications\Notification;
use Filament\Tables;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Concerns\InteractsWithTable;
use Filament\Tables\Contracts\HasTable;
use Filament\Tables\Filters\SelectFilter;
use Filament\Tables\Table;
use Illuminate\Support\Facades\DB;
use Livewire\Component;
use Throwable;

class BadgeSellerApplicationsTable extends Component implements HasForms, HasTable
{
    use InteractsWithForms;
    use InteractsWithTable;

    public function mount(): void
    {
        abort_unless(
            hasHousekeepingPermission(
                'manage_badge_marketplace'
            ),
            403
        );
    }

    public function table(
        Table $table
    ): Table {
        return $table
            ->query(
                BadgeSellerLicense::query()
            )
            ->defaultSort(
                'applied_at',
                'desc'
            )
            ->columns([
                TextColumn::make('account_id')
                    ->label('Cuenta')
                    ->formatStateUsing(
                        fn ($state): string =>
                            '#' . (int) $state
                    )
                    ->description(
                        fn (
                            BadgeSellerLicense $record
                        ): string =>
                            self::accountCharacters(
                                (int)
                                $record->account_id
                            )
                    )
                    ->searchable(),

                TextColumn::make(
                    'approved_badges'
                )
                    ->label(
                        'Placas aprobadas'
                    )
                    ->getStateUsing(
                        fn (
                            BadgeSellerLicense $record
                        ): int =>
                            app(
                                BadgeSellerEligibilityService::class
                            )
                                ->approvedBadgeCount(
                                    (int)
                                    $record->account_id
                                )
                    )
                    ->badge()
                    ->color(
                        fn ($state): string =>
                            (int) $state >=
                            app(
                                BadgeSellerEligibilityService::class
                            )
                                ->minimumApprovedBadges()
                                    ? 'success'
                                    : 'danger'
                    ),

                TextColumn::make('status')
                    ->label('Estado')
                    ->badge()
                    ->formatStateUsing(
                        fn (
                            string $state
                        ): string => match (
                            $state
                        ) {
                            BadgeSellerLicense::STATUS_PENDING =>
                                'Pendiente',
                            BadgeSellerLicense::STATUS_WAITLISTED =>
                                'Lista de espera',
                            BadgeSellerLicense::STATUS_ACTIVE =>
                                'Activa',
                            BadgeSellerLicense::STATUS_REVOKED =>
                                'Retirada',
                            default =>
                                $state,
                        }
                    )
                    ->color(
                        fn (
                            string $state
                        ): string => match (
                            $state
                        ) {
                            BadgeSellerLicense::STATUS_PENDING =>
                                'warning',
                            BadgeSellerLicense::STATUS_WAITLISTED =>
                                'info',
                            BadgeSellerLicense::STATUS_ACTIVE =>
                                'success',
                            BadgeSellerLicense::STATUS_REVOKED =>
                                'gray',
                            default =>
                                'gray',
                        }
                    ),

                TextColumn::make(
                    'community_slot'
                )
                    ->label('Plaza')
                    ->formatStateUsing(
                        fn ($state): string =>
                            $state === null
                                ? '—'
                                : (
                                    '#' .
                                    (int) $state
                                )
                    ),

                TextColumn::make('applied_at')
                    ->label('Solicitada')
                    ->formatStateUsing(
                        fn ($state): string =>
                            self::dateTime(
                                $state
                            )
                    )
                    ->sortable(),

                TextColumn::make(
                    'last_activity_at'
                )
                    ->label(
                        'Última actividad'
                    )
                    ->formatStateUsing(
                        fn ($state): string =>
                            self::dateTime(
                                $state
                            )
                    )
                    ->toggleable(),

                TextColumn::make(
                    'revocation_reason'
                )
                    ->label('Motivo')
                    ->placeholder('—')
                    ->wrap()
                    ->toggleable(
                        isToggledHiddenByDefault:
                            true
                    ),
            ])
            ->filters([
                SelectFilter::make('status')
                    ->label('Estado')
                    ->options([
                        BadgeSellerLicense::STATUS_PENDING =>
                            'Pendiente',
                        BadgeSellerLicense::STATUS_WAITLISTED =>
                            'Lista de espera',
                        BadgeSellerLicense::STATUS_ACTIVE =>
                            'Activa',
                        BadgeSellerLicense::STATUS_REVOKED =>
                            'Retirada',
                    ]),
            ])
            ->actions([
                Tables\Actions\Action::make(
                    'activate'
                )
                    ->label(
                        'Aprobar licencia'
                    )
                    ->icon(
                        'heroicon-o-check-circle'
                    )
                    ->color('success')
                    ->requiresConfirmation()
                    ->modalHeading(
                        'Aprobar vendedor de placas'
                    )
                    ->modalDescription(
                        'Si queda una plaza comunitaria libre, la licencia se activará. Si no queda ninguna, continuará en lista de espera.'
                    )
                    ->modalSubmitActionLabel(
                        'Aprobar licencia'
                    )
                    ->modalCancelActionLabel(
                        'Cancelar'
                    )
                    ->modalFooterActions(
                        fn (
                            Tables\Actions\Action $action
                        ): array => [
                            $action->getModalCancelAction(),
                            $action->getModalSubmitAction(),
                        ]
                    )
                    ->visible(
                        fn (
                            BadgeSellerLicense $record
                        ): bool =>
                            in_array(
                                $record->status,
                                [
                                    BadgeSellerLicense::STATUS_PENDING,
                                    BadgeSellerLicense::STATUS_WAITLISTED,
                                ],
                                true
                            )
                    )
                    ->action(
                        function (
                            BadgeSellerLicense $record
                        ): void {
                            try {
                                $result = app(
                                    BadgeSellerEligibilityService::class
                                )
                                    ->activateCommunityLicense(
                                        (int)
                                        $record->id,
                                        (int)
                                        auth()->id()
                                    );

                                if (
                                    $result->status ===
                                    BadgeSellerLicense::STATUS_ACTIVE
                                ) {
                                    Notification::make()
                                        ->title(
                                            'Licencia activada'
                                        )
                                        ->body(
                                            'Plaza comunitaria #' .
                                            (int)
                                            $result->community_slot .
                                            '.'
                                        )
                                        ->success()
                                        ->send();

                                    return;
                                }

                                Notification::make()
                                    ->title(
                                        'Sigue en lista de espera'
                                    )
                                    ->body(
                                        'No hay plazas comunitarias libres en este momento.'
                                    )
                                    ->warning()
                                    ->send();
                            } catch (
                                Throwable $exception
                            ) {
                                report(
                                    $exception
                                );

                                Notification::make()
                                    ->title(
                                        'No se pudo activar la licencia'
                                    )
                                    ->body(
                                        $exception
                                            ->getMessage()
                                    )
                                    ->danger()
                                    ->persistent()
                                    ->send();
                            }
                        }
                    ),

                Tables\Actions\Action::make(
                    'revoke'
                )
                    ->label(
                        fn (
                            BadgeSellerLicense $record
                        ): string =>
                            $record->status ===
                            BadgeSellerLicense::STATUS_ACTIVE
                                ? 'Retirar licencia'
                                : 'Rechazar solicitud'
                    )
                    ->icon(
                        'heroicon-o-x-circle'
                    )
                    ->color('danger')
                    ->visible(
                        fn (
                            BadgeSellerLicense $record
                        ): bool =>
                            $record->status !==
                            BadgeSellerLicense::STATUS_REVOKED
                    )
                    ->form([
                        Textarea::make(
                            'reason'
                        )
                            ->label('Motivo')
                            ->required()
                            ->minLength(3)
                            ->maxLength(255)
                            ->rows(4),
                    ])
                    ->modalHeading(
                        fn (
                            BadgeSellerLicense $record
                        ): string =>
                            $record->status ===
                            BadgeSellerLicense::STATUS_ACTIVE
                                ? 'Retirar licencia de vendedor'
                                : 'Rechazar solicitud de vendedor'
                    )
                    ->modalSubmitActionLabel(
                        'Confirmar'
                    )
                    ->modalCancelActionLabel(
                        'Cancelar'
                    )
                    ->action(
                        function (
                            BadgeSellerLicense $record,
                            array $data
                        ): void {
                            try {
                                app(
                                    BadgeSellerEligibilityService::class
                                )
                                    ->revokeCommunityLicense(
                                        (int)
                                        $record->id,
                                        (int)
                                        auth()->id(),
                                        (string)
                                        $data['reason']
                                    );

                                Notification::make()
                                    ->title(
                                        'Solicitud actualizada'
                                    )
                                    ->body(
                                        'La licencia queda retirada y, si ocupaba una plaza comunitaria, esa plaza vuelve a estar disponible.'
                                    )
                                    ->success()
                                    ->send();
                            } catch (
                                Throwable $exception
                            ) {
                                report(
                                    $exception
                                );

                                Notification::make()
                                    ->title(
                                        'No se pudo retirar la licencia'
                                    )
                                    ->body(
                                        $exception
                                            ->getMessage()
                                    )
                                    ->danger()
                                    ->persistent()
                                    ->send();
                            }
                        }
                    ),
            ])
            ->bulkActions([]);
    }

    public function render()
    {
        return view(
            'livewire.filament.badge-seller-applications-table'
        );
    }

    private static function accountCharacters(
        int $accountId
    ): string {
        $names = DB::table(
            'account_characters as ac'
        )
            ->join(
                'users as u',
                'u.id',
                '=',
                'ac.user_id'
            )
            ->where(
                'ac.account_id',
                $accountId
            )
            ->whereNull(
                'ac.archived_at'
            )
            ->orderByDesc(
                'ac.is_primary'
            )
            ->orderBy(
                'ac.slot'
            )
            ->pluck(
                'u.username'
            )
            ->all();

        if ($names === []) {
            return 'Sin personajes activos';
        }

        return implode(
            ', ',
            $names
        );
    }

    private static function dateTime(
        mixed $state
    ): string {
        if (empty($state)) {
            return '—';
        }

        return Carbon::parse(
            (string) $state,
            'UTC'
        )
            ->setTimezone(
                'Europe/Madrid'
            )
            ->format(
                'd/m/Y H:i'
            );
    }
}
