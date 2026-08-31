<?php

namespace App\Http\Controllers\Marketplace;

use App\Exceptions\CreditTransactionException;
use App\Http\Controllers\Controller;
use App\Services\BadgeMarketplacePurchaseService;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;
use Throwable;

class BadgeMarketplacePurchaseController extends Controller
{
    public function store(
        Request $request,
        int $listing,
        BadgeMarketplacePurchaseService $purchases
    ): RedirectResponse {
        $validated =
            $request->validate(
                [
                    'payer_user_id' => [
                        'required',
                        'integer',
                        'min:1',
                    ],
                    'expected_price' => [
                        'required',
                        'integer',
                        'min:3',
                        'max:10',
                    ],
                    'purchase_id' => [
                        'required',
                        'uuid',
                    ],
                ],
                [
                    'payer_user_id.required' =>
                        'Selecciona el personaje que pagará la placa.',
                    'payer_user_id.integer' =>
                        'El personaje pagador no es válido.',
                    'expected_price.required' =>
                        'No se ha podido confirmar el precio de la placa.',
                    'expected_price.integer' =>
                        'El precio de la placa no es válido.',
                    'purchase_id.required' =>
                        'No se ha podido identificar esta compra.',
                    'purchase_id.uuid' =>
                        'El identificador de compra no es válido.',
                ]
            );

        try {
            $result =
                $purchases->purchase(
                    $this->accountId(),
                    $listing,
                    (int)
                    $validated['payer_user_id'],
                    (int)
                    $validated['expected_price'],
                    (string)
                    $validated['purchase_id']
                );
        } catch (
            CreditTransactionException $exception
        ) {
            throw ValidationException::withMessages([
                'purchase' =>
                    $exception->getMessage(),
            ]);
        } catch (
            ValidationException $exception
        ) {
            throw $exception;
        } catch (Throwable $exception) {
            report($exception);

            throw ValidationException::withMessages([
                'purchase' =>
                    'No se pudo completar la compra. Si llegó a producirse un cobro, el sistema conservará la operación para recuperarla de forma segura.',
            ]);
        }

        $message =
            'Has comprado la placa "' .
            $result['badge_name'] .
            '" por ' .
            $result['buyer_price'] .
            ' créditos.';

        if (
            $result['delivered'] &&
            ! $result['seller_paid']
        ) {
            $message .=
                ' La placa ya está entregada; el pago al vendedor ha quedado pendiente de revisión interna.';
        }

        return to_route(
            'marketplace.badges.index',
            [
                'tab' =>
                    'market',
            ]
        )->with(
            'success',
            $message
        );
    }

    private function accountId(): int
    {
        $accountId =
            DB::table(
                'account_characters'
            )
                ->where(
                    'user_id',
                    Auth::id()
                )
                ->whereNull(
                    'archived_at'
                )
                ->value(
                    'account_id'
                );

        abort_unless(
            $accountId,
            403
        );

        return (int)
        $accountId;
    }
}
