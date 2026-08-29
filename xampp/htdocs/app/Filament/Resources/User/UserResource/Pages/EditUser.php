<?php

namespace App\Filament\Resources\User\UserResource\Pages;

use App\Actions\SendCurrency;
use App\Enums\CurrencyTypes;
use App\Models\Game\Player\UserCurrency;
use Filament\Actions;
use App\Services\RconService;
use Filament\Support\Exceptions\Halt;
use Illuminate\Database\Eloquent\Model;
use Filament\Notifications\Notification;
use Filament\Resources\Pages\EditRecord;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use App\Filament\Resources\User\UserResource;

class EditUser extends EditRecord
{
    protected static string $resource = UserResource::class;

    protected function getActions(): array
    {
        return [
            Actions\Action::make('adjustCredits')
                ->label('Ajustar créditos')
                ->icon('heroicon-o-banknotes')
                ->color('warning')
                ->visible(function (): bool {
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
                })
                ->form([
                    \Filament\Forms\Components\Hidden::make(
                        'adjustment_id'
                    )
                        ->default(
                            fn () =>
                                (string) \Illuminate\Support\Str::uuid()
                        ),

                    \Filament\Forms\Components\TextInput::make(
                        'delta'
                    )
                        ->label('Ajuste')
                        ->helperText(
                            'Usa un número positivo para añadir y negativo para retirar. Ejemplo: 53 o -53.'
                        )
                        ->numeric()
                        ->required()
                        ->rules([
                            'integer',
                            'not_in:0',
                            'between:-2147483647,2147483647',
                        ]),

                    \Filament\Forms\Components\Textarea::make(
                        'reason'
                    )
                        ->label('Motivo')
                        ->helperText(
                            'Quedará guardado permanentemente en el registro económico.'
                        )
                        ->required()
                        ->minLength(3)
                        ->maxLength(500)
                        ->rows(3),
                ])
                ->modalHeading('Ajustar créditos')
                ->modalDescription(
                    'Esta operación modifica créditos premium y quedará auditada.'
                )
                ->modalSubmitActionLabel('Aplicar ajuste')
                ->action(function (array $data): void {
                    $actor = auth()->user();

                    if (! $actor) {
                        Notification::make()
                            ->danger()
                            ->title('No autorizado')
                            ->send();

                        $this->halt();
                    }

                    try {
                        $result = app(
                            \App\Services\AdminCreditAdjustmentService::class
                        )->adjust(
                            (int) $actor->id,
                            (int) $this->getRecord()->id,
                            (int) $data['delta'],
                            (string) $data['reason'],
                            (string) $data['adjustment_id']
                        );

                        $before = number_format(
                            (int) $result['balance_before'],
                            0,
                            ',',
                            '.'
                        );

                        $after = number_format(
                            (int) $result['balance_after'],
                            0,
                            ',',
                            '.'
                        );

                        $delta = (int) $result['delta'];

                        $deltaText =
                            ($delta > 0 ? '+' : '') .
                            number_format(
                                $delta,
                                0,
                                ',',
                                '.'
                            );

                        Notification::make()
                            ->success()
                            ->title('Créditos ajustados')
                            ->body(
                                "{$deltaText} créditos. {$before} → {$after}"
                            )
                            ->send();

                        $this->refreshFormData([
                            'credits',
                        ]);
                    } catch (
                        \App\Exceptions\AdminCreditAdjustmentException $exception
                    ) {
                        Notification::make()
                            ->danger()
                            ->title(
                                'No se pudo ajustar el saldo'
                            )
                            ->body(
                                $exception->getMessage()
                            )
                            ->send();

                        $this->halt();
                    }
                }),

            Actions\DeleteAction::make(),
        ];
    }

    protected function mutateFormDataBeforeFill(array $data): array
    {
        return static::$resource::fillWithOutsideData(
            $this->getRecord(),
            $data
        );
    }

    public static function getEloquentQuery(): Builder
    {
        return static::getModel()::query()->with(['currencies', 'settings']);
    }

    /**
     * @throws Halt
     */
    protected function beforeSave(): void
    {
        $user = $this->getRecord();
        $data = $this->form->getState();

        if ($data['rank'] > auth()->user()->rank) {
            Notification::make()
                ->danger()
                ->title(__('You cannot edit this user!'))
                ->body(__('You cannot edit users with a higher rank than yours.'))
                ->send();

            $this->halt();
        }

        $rcon = app(RconService::class);

        if (!$user->online) {
            DB::transaction(function () use ($user, $data) {
                $this->treatChangedCurrenciesWithoutRcon($user, $data);
            });
            return;
        }

        if ($user->online && !$rcon->isConnected()) {
            Notification::make()
                ->danger()
                ->title(__('RCON is not enabled!'))
                ->body(__('You cannot edit users because RCON is not enabled and the user is online.'))
                ->send();

            $this->halt();
        }

        DB::transaction(function () use ($user, $data, $rcon) {
$this->checkUsernameChangedPermission($user, $data, $rcon);
            $this->treatChangedCurrencies($user, $data, $rcon);
            $this->treatChangedUserRank($user, $data, $rcon);
            $this->treatChangedUserMotto($user, $data, $rcon);
        });
    }

    private function treatChangedCurrenciesWithoutRcon(Model $user, array $data): void
	{
		$user->currencies->each(function (UserCurrency $currency) use ($data, $user) {
        $updatedCurrencyAmount = $data["currency_{$currency->type}"] ?? $currency->amount;
		if ($updatedCurrencyAmount == $currency->amount) {
			return;
		}

        $updated = $user->currencies()->where('type', $currency->type)->update(['amount' => $updatedCurrencyAmount]);

        if ($updated) {
            activity()
                ->performedOn($currency)
                ->withProperties(['old_amount' => $currency->amount, 'new_amount' => $updatedCurrencyAmount, 'user_id' => $user->id, 'type' => $currency->type])
                ->event('updated')
                ->log("Currency updated for user {$user->username}");

        } else {
            activity()
                ->withProperties(['user_id' => $user->id, 'type' => $currency->type])
                ->event('failed_update')
                ->log("Failed to update currency for user {$user->username}");
        }
    });

    $user->settings->update(['can_change_name' => $data['allow_change_username'] ? '1' : '0']);
	}

    private function checkUsernameChangedPermission(Model $user, array $data, RconService $rcon): void
    {
        if ($data['allow_change_username'] == $user->settings->can_change_name) return;

        if (!$rcon->isConnected()) {
            Notification::make()
                ->danger()
                ->title(__('RCON is not enabled!'))
                ->body(__('You cannot edit users because RCON is not enabled and the user is online.'))
                ->send();

            $this->halt();
        }

        $rcon->disconnectUser($user);
        $user->settings->update(['can_change_name' => $data['allow_change_username'] ? '1' : '0']);
    }

    private function treatChangedCurrencies(Model $user, array $data, RconService $rcon): void
    {
        $user->currencies->each(function (UserCurrency $currency) use ($data, $user, $rcon) {
            $updatedCurrencyAmount = $data["currency_{$currency->type}"] ?? $currency->amount;
            $currencyType = match ($currency->type) {
                CurrencyTypes::Duckets => 'duckets',
                CurrencyTypes::Diamonds => 'diamonds',
                CurrencyTypes::Points => 'points',
            };

            if ($updatedCurrencyAmount == $currency->amount) return;

            app(SendCurrency::class)->execute($user, $currencyType, -$currency->amount + $updatedCurrencyAmount);
        });
    }

    private function treatChangedUserRank(Model $user, array $data, RconService $rcon): void
    {
        if ($data['rank'] == $user->rank) return;
        if ($data['rank'] > auth()->user()->rank) return;

        if ($user->online && !$rcon->isConnected()) {
            Notification::make()
                ->danger()
                ->title(__('RCON is not enabled!'))
                ->body(__('You cannot edit users because RCON is not enabled and the user is online.'))
                ->send();

            $this->halt();
        }

        if (!$user->online) {
            $user->update(['rank' => $data['rank']]);

            return;
        }

        $rcon->alertUser($user, __('You have been disconnected because your rank has been changed. Please re-enter the hotel.'));
        sleep(2);

        $rcon->disconnectUser($user);
        $rcon->setRank($user, $data['rank']);
    }

    private function treatChangedUserMotto(Model $user, array $data, RconService $rcon): void
    {
        if ($data['motto'] == $user->motto) return;

        if ($user->online && !$rcon->isConnected()) {
            Notification::make()
                ->danger()
                ->title(__('RCON is not enabled!'))
                ->body(__('You cannot edit users because RCON is not enabled and the user is online.'))
                ->send();

            $this->halt();
        }

        if (!$user->online) {
            $user->update(['motto' => $data['motto']]);

            return;
        }

        $rcon->setMotto($user, $data['motto']);
        $rcon->alertUser($user, __('Your motto has been changed by a staff member.'));
    }
}
