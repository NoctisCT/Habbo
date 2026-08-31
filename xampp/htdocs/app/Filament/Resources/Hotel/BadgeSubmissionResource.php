<?php

namespace App\Filament\Resources\Hotel;

use App\Filament\Resources\Hotel\BadgeSubmissionResource\Pages;
use App\Models\BadgeSubmission;
use App\Models\User;
use App\Services\BadgeSubmissionModerationService;
use Carbon\Carbon;
use Filament\Forms;
use Filament\Forms\Components\Textarea;
use Filament\Notifications\Notification;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Filters\SelectFilter;
use Filament\Tables\Table;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\HtmlString;
use Throwable;

class BadgeSubmissionResource extends Resource
{
    protected static ?string $model = BadgeSubmission::class;

    protected static bool $shouldRegisterNavigation = false;

    protected static ?string $navigationGroup = 'Hotel';
    protected static ?string $navigationIcon = 'heroicon-o-photo';
    protected static ?string $navigationLabel = 'Placas de usuarios';
    protected static ?string $modelLabel = 'Solicitud de placa';
    protected static ?string $pluralModelLabel = 'Solicitudes de placas';
    protected static ?string $slug = 'hotel/badge-submissions';
    protected static ?int $navigationSort = 30;

    public static function canViewAny(): bool
    {
        return hasHousekeepingPermission(
            'manage_badge_submissions'
        );
    }

    public static function canCreate(): bool
    {
        return false;
    }

    public static function canEdit($record): bool
    {
        return false;
    }

    public static function canDelete($record): bool
    {
        return false;
    }

    public static function form(
        Forms\Form $form
    ): Forms\Form {
        return $form->schema([]);
    }

    public static function table(
        Table $table
    ): Table {
        return $table
            ->defaultSort(
                'created_at',
                'desc'
            )
            ->columns([
                TextColumn::make('preview')
                    ->label('Placa')
                    ->getStateUsing(
                        fn (
                            BadgeSubmission $record
                        ) =>
                            self::previewHtml(
                                $record
                            )
                    )
                    ->html(),

                TextColumn::make('badge_name')
                    ->label('Nombre')
                    ->searchable()
                    ->sortable()
                    ->description(
                        fn (
                            BadgeSubmission $record
                        ): string =>
                            (string)
                            $record->badge_description
                    ),

                TextColumn::make('creator_user_id')
                    ->label('Creador')
                    ->formatStateUsing(
                        fn ($state): string =>
                            self::username(
                                (int) $state
                            )
                    ),

                TextColumn::make('payer_user_id')
                    ->label('Pagador')
                    ->formatStateUsing(
                        fn ($state): string =>
                            self::username(
                                (int) $state
                            )
                    )
                    ->toggleable(),

                TextColumn::make('status')
                    ->label('Estado')
                    ->badge()
                    ->formatStateUsing(
                        fn (
                            string $state
                        ): string => match (
                            $state
                        ) {
                            'pending' =>
                                'Pendiente',
                            'rejecting' =>
                                'Reembolso pendiente',
                            'approved' =>
                                'Aprobada',
                            'rejected' =>
                                'Rechazada',
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
                            'pending' =>
                                'warning',
                            'rejecting' =>
                                'danger',
                            'approved' =>
                                'success',
                            'rejected' =>
                                'gray',
                            default =>
                                'gray',
                        }
                    ),

                TextColumn::make('created_at')
                    ->label('Enviada')
                    ->formatStateUsing(
                        fn ($state): string =>
                            Carbon::parse(
                                (string) $state,
                                'UTC'
                            )
                                ->setTimezone(
                                    'Europe/Madrid'
                                )
                                ->format(
                                    'd/m/Y H:i'
                                )
                    )
                    ->sortable(),

                TextColumn::make('badge_code')
                    ->label('Código')
                    ->placeholder('—')
                    ->toggleable(
                        isToggledHiddenByDefault:
                            true
                    ),

                TextColumn::make(
                    'moderation_reason'
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
                        'pending' =>
                            'Pendiente',
                        'rejecting' =>
                            'Reembolso pendiente',
                        'approved' =>
                            'Aprobada',
                        'rejected' =>
                            'Rechazada',
                    ]),
            ])
            ->actions([
                Tables\Actions\Action::make(
                    'approve'
                )
                    ->label('Aprobar')
                    ->icon(
                        'heroicon-o-check-circle'
                    )
                    ->color('success')
                    ->requiresConfirmation()
                    ->modalHeading(
                        'Aprobar placa'
                    )
                    ->modalDescription(
                        'Publicará la placa en el hotel y se la entregará al personaje creador.'
                    )
                    ->visible(
                        fn (
                            BadgeSubmission $record
                        ): bool =>
                            $record->status ===
                            'pending'
                    )
                    ->action(
                        function (
                            BadgeSubmission $record
                        ): void {
                            try {
                                $result = app(
                                    BadgeSubmissionModerationService::class
                                )->approve(
                                    $record,
                                    auth()->user()
                                );

                                Notification::make()
                                    ->title(
                                        (
                                            $result[
                                                'already_completed'
                                            ] ??
                                            false
                                        )
                                            ? 'La placa ya estaba aprobada.'
                                            : 'Placa aprobada y entregada.'
                                    )
                                    ->body(
                                        'Código: ' .
                                        (
                                            $result[
                                                'badge_code'
                                            ] ??
                                            '—'
                                        )
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
                                        'No se pudo aprobar la placa'
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
                    'reject'
                )
                    ->label('Rechazar')
                    ->icon(
                        'heroicon-o-x-circle'
                    )
                    ->color('danger')
                    ->visible(
                        fn (
                            BadgeSubmission $record
                        ): bool =>
                            $record->status ===
                            'pending'
                    )
                    ->form([
                        Textarea::make('reason')
                            ->label(
                                'Motivo del rechazo'
                            )
                            ->helperText(
                                'El usuario verá este motivo. Al confirmar se devolverán automáticamente los 10 créditos.'
                            )
                            ->required()
                            ->minLength(3)
                            ->maxLength(500)
                            ->rows(4),
                    ])
                    ->modalHeading(
                        'Rechazar placa'
                    )
                    ->modalSubmitActionLabel(
                        'Rechazar y devolver 10 créditos'
                    )
                    ->action(
                        function (
                            BadgeSubmission $record,
                            array $data
                        ): void {
                            try {
                                app(
                                    BadgeSubmissionModerationService::class
                                )->reject(
                                    $record,
                                    auth()->user(),
                                    (string)
                                    $data['reason']
                                );

                                Notification::make()
                                    ->title(
                                        'Placa rechazada'
                                    )
                                    ->body(
                                        'Se han devuelto los 10 créditos de la solicitud original.'
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
                                        'Reembolso no completado'
                                    )
                                    ->body(
                                        $exception
                                            ->getMessage() .
                                        ' La solicitud queda bloqueada para reintentar el reembolso.'
                                    )
                                    ->danger()
                                    ->persistent()
                                    ->send();
                            }
                        }
                    ),

                Tables\Actions\Action::make(
                    'retry_reject'
                )
                    ->label(
                        'Reintentar reembolso'
                    )
                    ->icon(
                        'heroicon-o-arrow-path'
                    )
                    ->color('warning')
                    ->requiresConfirmation()
                    ->modalHeading(
                        'Reintentar reembolso'
                    )
                    ->modalDescription(
                        'Volverá a intentar devolver los 10 créditos usando el mismo refund_id. No puede generar un doble reembolso.'
                    )
                    ->visible(
                        fn (
                            BadgeSubmission $record
                        ): bool =>
                            $record->status ===
                            'rejecting'
                    )
                    ->action(
                        function (
                            BadgeSubmission $record
                        ): void {
                            try {
                                app(
                                    BadgeSubmissionModerationService::class
                                )->retryReject(
                                    $record,
                                    auth()->user()
                                );

                                Notification::make()
                                    ->title(
                                        'Reembolso completado'
                                    )
                                    ->body(
                                        'La placa ha quedado rechazada.'
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
                                        'El reembolso sigue pendiente'
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

    public static function getPages(): array
    {
        return [
            'index' =>
                Pages\ManageBadgeSubmissions::route(
                    '/'
                ),
        ];
    }

    public static function getNavigationBadge(): ?string
    {
        if (! static::canViewAny()) {
            return null;
        }

        $count =
            BadgeSubmission::query()
                ->whereIn(
                    'status',
                    [
                        'pending',
                        'rejecting',
                    ]
                )
                ->count();

        return
            $count > 0
                ? (string) $count
                : null;
    }

    public static function getNavigationBadgeColor(): ?string
    {
        return 'warning';
    }

    private static function username(
        int $userId
    ): string {
        return (string) (
            User::query()
                ->whereKey($userId)
                ->value('username')
            ??
            ('#' . $userId)
        );
    }

    private static function previewHtml(
        BadgeSubmission $record
    ): HtmlString {
        if (
            empty(
                $record->processed_path
            ) ||
            ! Storage::disk('local')
                ->exists(
                    (string)
                    $record->processed_path
                )
        ) {
            return new HtmlString(
                '<span class="text-xs text-gray-500">Sin imagen</span>'
            );
        }

        $bytes =
            Storage::disk('local')->get(
                (string)
                $record->processed_path
            );

        $src =
            'data:image/gif;base64,' .
            base64_encode($bytes);

        $alt =
            e(
                (string)
                $record->badge_name
            );

        return new HtmlString(
            '<div style="width:56px;height:56px;display:flex;align-items:center;justify-content:center;border:1px solid #9ca3af;border-radius:6px;background-color:#d1d5db;background-image:linear-gradient(45deg,#f3f4f6 25%,transparent 25%),linear-gradient(-45deg,#f3f4f6 25%,transparent 25%),linear-gradient(45deg,transparent 75%,#f3f4f6 75%),linear-gradient(-45deg,transparent 75%,#f3f4f6 75%);background-size:12px 12px;background-position:0 0,0 6px,6px -6px,-6px 0;">' .
            '<img src="' .
            $src .
            '" alt="' .
            $alt .
            '" width="40" height="40" style="width:40px;height:40px;object-fit:contain;image-rendering:pixelated;">' .
            '</div>'
        );
    }
}
