<?php

namespace App\Services;

use App\Models\BadgeMarketplaceListing;
use Illuminate\Support\Collection;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;
use RuntimeException;

class BadgeMarketplaceListingService
{
    public function __construct(
        private readonly BadgeSellerEligibilityService $eligibility
    ) {
    }

public function publicListings(
    string $search = '',
    string $explore = 'all',
    string $sort = 'relevance',
    ?int $creatorUserId = null
): Collection {
    $search = trim($search);

    $sales = DB::table(
        'badge_marketplace_sales'
    )
        ->select([
            'listing_id',
            DB::raw(
                'COUNT(*) as sales_count'
            ),
        ])
        ->where(
            'status',
            'completed'
        )
        ->groupBy(
            'listing_id'
        );

    $query = DB::table(
        'badge_marketplace_listings as listings'
    )
        ->join(
            'creator_badges as badges',
            'badges.id',
            '=',
            'listings.creator_badge_id'
        )
        ->join(
            'badge_submissions as submissions',
            'submissions.id',
            '=',
            'badges.badge_submission_id'
        )
        ->join(
            'users as creator',
            'creator.id',
            '=',
            'badges.creator_user_id'
        )
        ->join(
            'account_characters as ac',
            function ($join): void {
                $join->on(
                    'ac.user_id',
                    '=',
                    'badges.creator_user_id'
                )
                    ->on(
                        'ac.account_id',
                        '=',
                        'badges.account_id'
                    );
            }
        )
        ->leftJoinSub(
            $sales,
            'sales',
            'sales.listing_id',
            '=',
            'listings.id'
        )
        ->where(
            'listings.status',
            BadgeMarketplaceListing::STATUS_ACTIVE
        )
        ->where(
            'badges.marketplace_enabled',
            1
        )
        ->where(
            'submissions.status',
            'approved'
        )
        ->whereNull(
            'ac.archived_at'
        );

    if ($creatorUserId !== null) {
        $query->where(
            'badges.creator_user_id',
            $creatorUserId
        );
    }

    if ($search !== '') {
        $needle =
            '%' .
            str_replace(
                ['\\', '%', '_'],
                ['\\\\', '\%', '\_'],
                $search
            ) .
            '%';

        $query->where(
            function ($query) use (
                $needle
            ): void {
                $query->where(
                    'badges.badge_name',
                    'like',
                    $needle
                )
                    ->orWhere(
                        'creator.username',
                        'like',
                        $needle
                    );
            }
        );
    }

    if ($explore === 'new') {
        $query->where(
            'listings.activated_at',
            '>=',
            now()->subDays(7)
        );
    }

    $effectiveSort = $sort;

    if (
        $sort === 'relevance' &&
        $explore === 'popular'
    ) {
        $effectiveSort =
            'sales_desc';
    }

    if (
        $sort === 'relevance' &&
        $explore === 'new'
    ) {
        $effectiveSort =
            'newest';
    }

    switch ($effectiveSort) {
        case 'sales_desc':
            $query->orderByRaw(
                'COALESCE(sales.sales_count, 0) DESC'
            )
                ->orderByDesc(
                    'listings.activated_at'
                );
            break;

        case 'price_asc':
            $query->orderBy(
                'listings.buyer_price'
            )
                ->orderByDesc(
                    'listings.activated_at'
                );
            break;

        case 'price_desc':
            $query->orderByDesc(
                'listings.buyer_price'
            )
                ->orderByDesc(
                    'listings.activated_at'
                );
            break;

        case 'oldest':
            $query->orderBy(
                'listings.activated_at'
            );
            break;

        case 'newest':
            $query->orderByDesc(
                'listings.activated_at'
            );
            break;

        default:
            if ($search !== '') {
                $prefix =
                    mb_strtolower(
                        $search
                    ) .
                    '%';

                $query->orderByRaw(
                    'CASE
                        WHEN LOWER(badges.badge_name) LIKE ? THEN 0
                        WHEN LOWER(creator.username) LIKE ? THEN 1
                        ELSE 2
                    END',
                    [
                        $prefix,
                        $prefix,
                    ]
                );
            }

            $query->orderByRaw(
                'COALESCE(sales.sales_count, 0) DESC'
            )
                ->orderByDesc(
                    'listings.activated_at'
                );
            break;
    }

    return $query
        ->get([
            'listings.id',
            'listings.creator_badge_id',
            'listings.seller_account_id',
            'listings.seller_earnings',
            'listings.hotel_commission',
            'listings.buyer_price',
            'listings.activated_at',
            'badges.creator_user_id',
            'badges.badge_code',
            'badges.badge_name',
            'badges.badge_description',
            'creator.username as creator_username',
            DB::raw(
                'COALESCE(sales.sales_count, 0) as sales_count'
            ),
        ])
        ->filter(
            fn (object $listing): bool =>
                $this->eligibility->canSell(
                    (int)
                    $listing->seller_account_id
                )
        )
        ->map(
            function (
                object $listing
            ): object {
                $listing->sales_count =
                    (int)
                    $listing->sales_count;

                $listing->is_new =
                    $listing->activated_at !==
                        null &&
                    strtotime(
                        (string)
                        $listing->activated_at
                    ) >=
                        now()
                            ->subDays(7)
                            ->timestamp;

                return $listing;
            }
        )
        ->values();
}

    public function sellerCatalog(
        int $accountId
    ): Collection {
        $sales = DB::table(
            'badge_marketplace_sales'
        )
            ->select([
                'creator_badge_id',
                DB::raw(
                    'COUNT(*) as sales_count'
                ),
                DB::raw(
                    'COALESCE(SUM(seller_earnings), 0) as credits_earned'
                ),
            ])
            ->where(
                'seller_account_id',
                $accountId
            )
            ->where(
                'status',
                'completed'
            )
            ->groupBy(
                'creator_badge_id'
            );

        return DB::table(
            'creator_badges as badges'
        )
            ->join(
                'badge_submissions as submissions',
                'submissions.id',
                '=',
                'badges.badge_submission_id'
            )
            ->leftJoin(
                'users as creator',
                'creator.id',
                '=',
                'badges.creator_user_id'
            )
            ->leftJoin(
                'account_characters as ac',
                function ($join) use (
                    $accountId
                ): void {
                    $join->on(
                        'ac.user_id',
                        '=',
                        'badges.creator_user_id'
                    )
                        ->where(
                            'ac.account_id',
                            '=',
                            $accountId
                        );
                }
            )
            ->leftJoin(
                'badge_marketplace_listings as listings',
                'listings.creator_badge_id',
                '=',
                'badges.id'
            )
            ->leftJoinSub(
                $sales,
                'completed_sales',
                'completed_sales.creator_badge_id',
                '=',
                'badges.id'
            )
            ->where(
                'badges.account_id',
                $accountId
            )
            ->where(
                'submissions.status',
                'approved'
            )
            ->orderByDesc(
                'badges.created_at'
            )
            ->get([
                'badges.id',
                'badges.badge_code',
                'badges.badge_name',
                'badges.badge_description',
                'badges.creator_user_id',
                'badges.marketplace_enabled',
                'creator.username as creator_username',
                'ac.archived_at as creator_archived_at',
                'listings.id as listing_id',
                'listings.status as listing_status',
                'listings.seller_earnings',
                'listings.hotel_commission',
                'listings.buyer_price',
                'listings.activated_at',
                'listings.deactivated_at',
                DB::raw(
                    'COALESCE(completed_sales.sales_count, 0) as sales_count'
                ),
                DB::raw(
                    'COALESCE(completed_sales.credits_earned, 0) as credits_earned'
                ),
            ])
            ->map(
                function (
                    object $badge
                ): object {
                    $badge->sales_count =
                        (int)
                        $badge->sales_count;

                    $badge->credits_earned =
                        (int)
                        $badge->credits_earned;

                    return $badge;
                }
            );
    }

    public function activeListingCount(
        int $accountId
    ): int {
        return DB::table(
            'badge_marketplace_listings'
        )
            ->where(
                'seller_account_id',
                $accountId
            )
            ->where(
                'status',
                BadgeMarketplaceListing::STATUS_ACTIVE
            )
            ->count();
    }

public function sellerSalesStats(
    int $accountId
): array {
    $stats = DB::table(
        'badge_marketplace_sales'
    )
        ->where(
            'seller_account_id',
            $accountId
        )
        ->where(
            'status',
            'completed'
        )
        ->selectRaw(
            'COUNT(*) as sales_count, ' .
            'COALESCE(SUM(seller_earnings), 0) as credits_earned'
        )
        ->first();

    return [
        'sales_count' =>
            (int)
            ($stats->sales_count ?? 0),
        'credits_earned' =>
            (int)
            ($stats->credits_earned ?? 0),
    ];
}

    public function saveListing(
        int $accountId,
        int $creatorBadgeId,
        int $sellerEarnings
    ): object {
        $this->assertSellerEarnings(
            $sellerEarnings
        );

        return DB::transaction(
            function () use (
                $accountId,
                $creatorBadgeId,
                $sellerEarnings
            ): object {
                $this->lockAccount(
                    $accountId
                );

                $this->assertCanSell(
                    $accountId
                );

                $badge =
                    $this->lockCreatorBadge(
                        $accountId,
                        $creatorBadgeId,
                        true
                    );

                $listing = DB::table(
                    'badge_marketplace_listings'
                )
                    ->where(
                        'creator_badge_id',
                        $creatorBadgeId
                    )
                    ->lockForUpdate()
                    ->first();

                if (
                    ! $listing ||
                    $listing->status !==
                    BadgeMarketplaceListing::STATUS_ACTIVE
                ) {
                    $this->assertListingCapacity(
                        $accountId
                    );
                }

                $commission =
                    $this->hotelCommission();

                $buyerPrice =
                    $sellerEarnings +
                    $commission;

                $now = now();

                if ($listing) {
                    DB::table(
                        'badge_marketplace_listings'
                    )
                        ->where(
                            'id',
                            $listing->id
                        )
                        ->update([
                            'seller_account_id' =>
                                $accountId,
                            'seller_earnings' =>
                                $sellerEarnings,
                            'hotel_commission' =>
                                $commission,
                            'buyer_price' =>
                                $buyerPrice,
                            'status' =>
                                BadgeMarketplaceListing::STATUS_ACTIVE,
                            'activated_at' =>
                                $listing->status ===
                                BadgeMarketplaceListing::STATUS_ACTIVE
                                    ? $listing->activated_at
                                    : $now,
                            'deactivated_at' =>
                                null,
                            'updated_at' =>
                                $now,
                        ]);

                    $listingId =
                        (int) $listing->id;
                } else {
                    $listingId = DB::table(
                        'badge_marketplace_listings'
                    )
                        ->insertGetId([
                            'creator_badge_id' =>
                                $creatorBadgeId,
                            'seller_account_id' =>
                                $accountId,
                            'seller_earnings' =>
                                $sellerEarnings,
                            'hotel_commission' =>
                                $commission,
                            'buyer_price' =>
                                $buyerPrice,
                            'status' =>
                                BadgeMarketplaceListing::STATUS_ACTIVE,
                            'activated_at' =>
                                $now,
                            'deactivated_at' =>
                                null,
                            'created_at' =>
                                $now,
                            'updated_at' =>
                                $now,
                        ]);
                }

                DB::table(
                    'creator_badges'
                )
                    ->where(
                        'id',
                        $badge->id
                    )
                    ->update([
                        'marketplace_enabled' =>
                            1,
                        'updated_at' =>
                            $now,
                    ]);

                $this->touchSellerActivity(
                    $accountId
                );

                return $this->listing(
                    $listingId
                );
            }
        );
    }

    public function deactivateListing(
        int $accountId,
        int $creatorBadgeId
    ): object {
        return DB::transaction(
            function () use (
                $accountId,
                $creatorBadgeId
            ): object {
                $this->lockAccount(
                    $accountId
                );

                $badge =
                    $this->lockCreatorBadge(
                        $accountId,
                        $creatorBadgeId,
                        false
                    );

                $listing = DB::table(
                    'badge_marketplace_listings'
                )
                    ->where(
                        'creator_badge_id',
                        $creatorBadgeId
                    )
                    ->where(
                        'seller_account_id',
                        $accountId
                    )
                    ->lockForUpdate()
                    ->first();

                if (! $listing) {
                    throw ValidationException::withMessages([
                        'listing' =>
                            'Esta placa no tiene un anuncio en el marketplace.',
                    ]);
                }

                if (
                    $listing->status !==
                    BadgeMarketplaceListing::STATUS_INACTIVE
                ) {
                    DB::table(
                        'badge_marketplace_listings'
                    )
                        ->where(
                            'id',
                            $listing->id
                        )
                        ->update([
                            'status' =>
                                BadgeMarketplaceListing::STATUS_INACTIVE,
                            'deactivated_at' =>
                                now(),
                            'updated_at' =>
                                now(),
                        ]);
                }

                DB::table(
                    'creator_badges'
                )
                    ->where(
                        'id',
                        $badge->id
                    )
                    ->update([
                        'marketplace_enabled' =>
                            0,
                        'updated_at' =>
                            now(),
                    ]);

                $this->touchSellerActivity(
                    $accountId
                );

                return $this->listing(
                    (int) $listing->id
                );
            }
        );
    }

    public function reactivateListing(
        int $accountId,
        int $creatorBadgeId
    ): object {
        return DB::transaction(
            function () use (
                $accountId,
                $creatorBadgeId
            ): object {
                $this->lockAccount(
                    $accountId
                );

                $this->assertCanSell(
                    $accountId
                );

                $badge =
                    $this->lockCreatorBadge(
                        $accountId,
                        $creatorBadgeId,
                        true
                    );

                $listing = DB::table(
                    'badge_marketplace_listings'
                )
                    ->where(
                        'creator_badge_id',
                        $creatorBadgeId
                    )
                    ->where(
                        'seller_account_id',
                        $accountId
                    )
                    ->lockForUpdate()
                    ->first();

                if (! $listing) {
                    throw ValidationException::withMessages([
                        'listing' =>
                            'Esta placa todavía no tiene un anuncio para reactivar.',
                    ]);
                }

                if (
                    $listing->status ===
                    BadgeMarketplaceListing::STATUS_ACTIVE
                ) {
                    return $listing;
                }

                $this->assertListingCapacity(
                    $accountId
                );

                DB::table(
                    'badge_marketplace_listings'
                )
                    ->where(
                        'id',
                        $listing->id
                    )
                    ->update([
                        'status' =>
                            BadgeMarketplaceListing::STATUS_ACTIVE,
                        'activated_at' =>
                            now(),
                        'deactivated_at' =>
                            null,
                        'updated_at' =>
                            now(),
                    ]);

                DB::table(
                    'creator_badges'
                )
                    ->where(
                        'id',
                        $badge->id
                    )
                    ->update([
                        'marketplace_enabled' =>
                            1,
                        'updated_at' =>
                            now(),
                    ]);

                $this->touchSellerActivity(
                    $accountId
                );

                return $this->listing(
                    (int) $listing->id
                );
            }
        );
    }

    public function sellerEarningsMin(): int
    {
        return (int) config(
            'badge_marketplace.seller_earnings_min',
            0
        );
    }

    public function sellerEarningsMax(): int
    {
        return (int) config(
            'badge_marketplace.seller_earnings_max',
            7
        );
    }

    public function hotelCommission(): int
    {
        return (int) config(
            'badge_marketplace.hotel_commission',
            3
        );
    }

    private function assertSellerEarnings(
        int $sellerEarnings
    ): void {
        $min =
            $this->sellerEarningsMin();

        $max =
            $this->sellerEarningsMax();

        if (
            $sellerEarnings < $min ||
            $sellerEarnings > $max
        ) {
            throw ValidationException::withMessages([
                'seller_earnings' =>
                    'Tus ganancias por venta deben estar entre ' .
                    $min .
                    ' y ' .
                    $max .
                    ' créditos.',
            ]);
        }
    }

    private function assertCanSell(
        int $accountId
    ): void {
        if (
            ! $this->eligibility->canSell(
                $accountId
            )
        ) {
            throw ValidationException::withMessages([
                'listing' =>
                    'Tu cuenta no tiene autorización activa para vender placas.',
            ]);
        }
    }

    private function assertListingCapacity(
        int $accountId
    ): void {
        $used =
            $this->activeListingCount(
                $accountId
            );

        $limit =
            $this->eligibility
                ->listingLimit(
                    $accountId
                );

        if ($used >= $limit) {
            throw ValidationException::withMessages([
                'listing' =>
                    'Ya tienes el máximo de ' .
                    $limit .
                    ' anuncios activos.',
            ]);
        }
    }

    private function lockAccount(
        int $accountId
    ): void {
        $account = DB::table(
            'accounts'
        )
            ->where(
                'id',
                $accountId
            )
            ->lockForUpdate()
            ->first();

        if (! $account) {
            throw new RuntimeException(
                'La cuenta vendedora no existe.'
            );
        }
    }

    private function lockCreatorBadge(
        int $accountId,
        int $creatorBadgeId,
        bool $requireActiveCreator
    ): object {
        $badge = DB::table(
            'creator_badges'
        )
            ->where(
                'id',
                $creatorBadgeId
            )
            ->where(
                'account_id',
                $accountId
            )
            ->lockForUpdate()
            ->first();

        if (! $badge) {
            throw ValidationException::withMessages([
                'listing' =>
                    'La placa no pertenece a esta cuenta.',
            ]);
        }

        $approved = DB::table(
            'badge_submissions'
        )
            ->where(
                'id',
                $badge->badge_submission_id
            )
            ->where(
                'status',
                'approved'
            )
            ->exists();

        if (! $approved) {
            throw ValidationException::withMessages([
                'listing' =>
                    'Solo se pueden vender placas aprobadas.',
            ]);
        }

        if ($requireActiveCreator) {
            $activeCreator = DB::table(
                'account_characters'
            )
                ->where(
                    'account_id',
                    $accountId
                )
                ->where(
                    'user_id',
                    $badge->creator_user_id
                )
                ->whereNull(
                    'archived_at'
                )
                ->exists();

            if (! $activeCreator) {
                throw ValidationException::withMessages([
                    'listing' =>
                        'El personaje autor está archivado. Restaura ese personaje antes de publicar o reactivar la placa.',
                ]);
            }
        }

        return $badge;
    }

    private function listing(
        int $listingId
    ): object {
        $listing = DB::table(
            'badge_marketplace_listings'
        )
            ->where(
                'id',
                $listingId
            )
            ->first();

        if (! $listing) {
            throw new RuntimeException(
                'El anuncio no se pudo recuperar.'
            );
        }

        return $listing;
    }

    private function touchSellerActivity(
        int $accountId
    ): void {
        DB::table(
            'badge_seller_licenses'
        )
            ->where(
                'account_id',
                $accountId
            )
            ->where(
                'status',
                'active'
            )
            ->whereNull(
                'revoked_at'
            )
            ->update([
                'last_activity_at' =>
                    now(),
                'updated_at' =>
                    now(),
            ]);
    }
}
