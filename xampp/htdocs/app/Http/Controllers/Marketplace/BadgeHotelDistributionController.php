<?php

namespace App\Http\Controllers\Marketplace;

use App\Http\Controllers\Controller;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use RuntimeException;

class BadgeHotelDistributionController extends Controller
{
    public function store(
        Request $request,
        int $creatorBadge
    ): RedirectResponse {
        $request->validate(
            [
                'confirm_permanent' => [
                    'required',
                    'accepted',
                ],
            ],
            [
                'confirm_permanent.required' =>
                    'Debes confirmar que entiendes el carácter permanente de esta autorización.',
                'confirm_permanent.accepted' =>
                    'Debes aceptar expresamente que esta autorización es permanente e irreversible.',
            ]
        );

        $accountId =
            $this->accountId();

        $result =
            DB::transaction(
                function () use (
                    $accountId,
                    $creatorBadge
                ): array {
                    $badge =
                        DB::table(
                            'creator_badges'
                        )
                            ->where(
                                'id',
                                $creatorBadge
                            )
                            ->where(
                                'account_id',
                                $accountId
                            )
                            ->lockForUpdate()
                            ->first();

                    abort_unless(
                        $badge,
                        404
                    );

                    if (
                        $badge
                            ->hotel_distribution_granted_at !==
                        null
                    ) {
                        return [
                            'newly_granted' =>
                                false,
                            'badge_name' =>
                                (string)
                                $badge->badge_name,
                            'granted_at' =>
                                (string)
                                $badge
                                    ->hotel_distribution_granted_at,
                        ];
                    }

                    $grantedAt =
                        now();

                    $updated =
                        DB::table(
                            'creator_badges'
                        )
                            ->where(
                                'id',
                                $creatorBadge
                            )
                            ->where(
                                'account_id',
                                $accountId
                            )
                            ->whereNull(
                                'hotel_distribution_granted_at'
                            )
                            ->update([
                                'hotel_distribution_granted_at' =>
                                    $grantedAt,
                                'updated_at' =>
                                    now(),
                            ]);

                    if ($updated !== 1) {
                        throw new RuntimeException(
                            'No se pudo registrar el derecho permanente de distribución.'
                        );
                    }

                    Log::info(
                        'Hotel distribution right granted',
                        [
                            'creator_badge_id' =>
                                $creatorBadge,
                            'account_id' =>
                                $accountId,
                            'actor_user_id' =>
                                Auth::id(),
                            'badge_code' =>
                                (string)
                                $badge->badge_code,
                            'granted_at' =>
                                $grantedAt->toIso8601String(),
                        ]
                    );

                    return [
                        'newly_granted' =>
                            true,
                        'badge_name' =>
                            (string)
                            $badge->badge_name,
                        'granted_at' =>
                            $grantedAt->toDateTimeString(),
                    ];
                },
                5
            );

        $message =
            $result['newly_granted']
                ? 'Has concedido a Biribiri un derecho permanente y no exclusivo para distribuir la placa "' .
                    $result['badge_name'] .
                    '". La autoría sigue siendo tuya.'
                : 'La placa "' .
                    $result['badge_name'] .
                    '" ya tiene concedido el derecho permanente de distribución a Biribiri.';

        return to_route(
            'marketplace.badges.index',
            [
                'tab' => 'mine',
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
