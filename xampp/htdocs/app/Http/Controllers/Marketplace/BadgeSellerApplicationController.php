<?php

namespace App\Http\Controllers\Marketplace;

use App\Http\Controllers\Controller;
use App\Models\BadgeSellerLicense;
use App\Services\BadgeSellerEligibilityService;
use Illuminate\Http\RedirectResponse;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;

class BadgeSellerApplicationController extends Controller
{
    public function store(
        BadgeSellerEligibilityService $eligibility
    ): RedirectResponse {
        $accountId = $this->accountId();

        if (
            $eligibility->isBadgeDesigner(
                $accountId
            )
        ) {
            return to_route(
                'marketplace.badges.index',
                [
                    'tab' => 'seller',
                ]
            )->with(
                'success',
                'Ya eres Diseñador de Placas y tienes autorización automática para vender.'
            );
        }

        $existing = DB::table(
            'badge_seller_licenses'
        )
            ->where(
                'account_id',
                $accountId
            )
            ->first();

        if ($existing) {
            if (
                in_array(
                    $existing->status,
                    [
                        BadgeSellerLicense::STATUS_PENDING,
                        BadgeSellerLicense::STATUS_WAITLISTED,
                        BadgeSellerLicense::STATUS_ACTIVE,
                    ],
                    true
                )
            ) {
                return to_route(
                    'marketplace.badges.index',
                    [
                        'tab' => 'seller',
                    ]
                )->with(
                    'success',
                    match ($existing->status) {
                        BadgeSellerLicense::STATUS_PENDING =>
                            'Tu solicitud ya está pendiente de revisión.',
                        BadgeSellerLicense::STATUS_WAITLISTED =>
                            'Tu solicitud ya está en lista de espera.',
                        BadgeSellerLicense::STATUS_ACTIVE =>
                            'Ya tienes una licencia de vendedor activa.',
                        default =>
                            'Tu solicitud ya existe.',
                    }
                );
            }

            if (
                $existing->status ===
                BadgeSellerLicense::STATUS_REVOKED
            ) {
                throw ValidationException::withMessages([
                    'seller_license' =>
                        'Tu licencia anterior fue retirada. Un miembro del staff debe revisar tu caso antes de una nueva solicitud.',
                ]);
            }
        }

        $license =
            $eligibility
                ->applyCommunityLicense(
                    $accountId
                );

        $message =
            $license->status ===
            BadgeSellerLicense::STATUS_WAITLISTED
                ? 'Cumples los requisitos. Tu solicitud se ha añadido a la lista de espera porque las plazas comunitarias están ocupadas.'
                : 'Solicitud enviada. El staff la revisará antes de activar tu licencia de vendedor.';

        return to_route(
            'marketplace.badges.index',
            [
                'tab' => 'seller',
            ]
        )->with(
            'success',
            $message
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
