<?php

namespace App\Http\Controllers\Marketplace;

use App\Http\Controllers\Controller;
use App\Services\BadgeMarketplaceListingService;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;

class BadgeMarketplaceListingController extends Controller
{
    public function store(
        Request $request,
        int $creatorBadge,
        BadgeMarketplaceListingService $listings
    ): RedirectResponse {
        $min =
            $listings->sellerEarningsMin();

        $max =
            $listings->sellerEarningsMax();

        $validated = $request->validate(
            [
                'seller_earnings' => [
                    'required',
                    'integer',
                    'min:' . $min,
                    'max:' . $max,
                ],
            ],
            [
                'seller_earnings.required' =>
                    'Indica cuántos créditos quieres recibir por cada venta.',
                'seller_earnings.integer' =>
                    'Las ganancias deben ser un número entero de créditos.',
                'seller_earnings.min' =>
                    'Las ganancias no pueden ser inferiores a ' .
                    $min .
                    ' créditos.',
                'seller_earnings.max' =>
                    'Las ganancias no pueden superar los ' .
                    $max .
                    ' créditos.',
            ]
        );

        $listing =
            $listings->saveListing(
                $this->accountId(),
                $creatorBadge,
                (int)
                $validated['seller_earnings']
            );

        return to_route(
            'marketplace.badges.index',
            [
                'tab' => 'seller',
            ]
        )->with(
            'success',
            'Anuncio activo. Tú recibirás ' .
                (int)
                $listing->seller_earnings .
                ' créditos por venta y el comprador pagará ' .
                (int)
                $listing->buyer_price .
                ' créditos.'
        );
    }

    public function deactivate(
        int $creatorBadge,
        BadgeMarketplaceListingService $listings
    ): RedirectResponse {
        $listings->deactivateListing(
            $this->accountId(),
            $creatorBadge
        );

        return to_route(
            'marketplace.badges.index',
            [
                'tab' => 'seller',
            ]
        )->with(
            'success',
            'El anuncio se ha retirado del marketplace.'
        );
    }

    public function reactivate(
        int $creatorBadge,
        BadgeMarketplaceListingService $listings
    ): RedirectResponse {
        $listing =
            $listings->reactivateListing(
                $this->accountId(),
                $creatorBadge
            );

        return to_route(
            'marketplace.badges.index',
            [
                'tab' => 'seller',
            ]
        )->with(
            'success',
            'El anuncio vuelve a estar activo por ' .
                (int)
                $listing->buyer_price .
                ' créditos.'
        );
    }

    private function accountId(): int
    {
        $accountId = DB::table(
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

        return (int) $accountId;
    }
}
