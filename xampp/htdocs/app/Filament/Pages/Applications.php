<?php

namespace App\Filament\Pages;

use App\Models\BadgeSellerLicense;
use App\Models\BadgeSubmission;
use App\Services\BadgeSellerEligibilityService;
use Filament\Pages\Page;

class Applications extends Page
{
    protected static ?string $navigationIcon =
        'heroicon-o-inbox-stack';

    protected static ?string $navigationGroup =
        'Hotel';

    protected static ?string $navigationLabel =
        'Solicitudes';

    protected static ?string $title =
        'Solicitudes';

    protected static ?string $slug =
        'hotel/applications';

    protected static ?int $navigationSort = 30;

    protected static string $view =
        'filament.pages.applications';

    public string $activeTab = 'badges';

    public function mount(): void
    {
        $requested = (string) request()
            ->query('tab', '');

        if (
            $requested === 'sellers' &&
            static::hasSellerPermission()
        ) {
            $this->activeTab = 'sellers';

            return;
        }

        if (static::hasBadgePermission()) {
            $this->activeTab = 'badges';

            return;
        }

        if (static::hasSellerPermission()) {
            $this->activeTab = 'sellers';

            return;
        }

        abort(403);
    }

    public static function canAccess(): bool
    {
        return static::hasBadgePermission()
            || static::hasSellerPermission();
    }

    public static function hasBadgePermission(): bool
    {
        return hasHousekeepingPermission(
            'manage_badge_submissions'
        );
    }

    public static function hasSellerPermission(): bool
    {
        return hasHousekeepingPermission(
            'manage_badge_marketplace'
        );
    }

    public function showBadgeTab(): bool
    {
        return static::hasBadgePermission();
    }

    public function showSellerTab(): bool
    {
        return static::hasSellerPermission();
    }

    public function selectTab(
        string $tab
    ): void {
        if (
            $tab === 'badges' &&
            static::hasBadgePermission()
        ) {
            $this->activeTab = 'badges';

            return;
        }

        if (
            $tab === 'sellers' &&
            static::hasSellerPermission()
        ) {
            $this->activeTab = 'sellers';

            return;
        }

        abort(403);
    }

    public function badgePendingCount(): int
    {
        if (! static::hasBadgePermission()) {
            return 0;
        }

        return BadgeSubmission::query()
            ->whereIn(
                'status',
                [
                    'pending',
                    'rejecting',
                ]
            )
            ->count();
    }

    public function sellerPendingCount(): int
    {
        if (! static::hasSellerPermission()) {
            return 0;
        }

        return BadgeSellerLicense::query()
            ->whereIn(
                'status',
                [
                    BadgeSellerLicense::STATUS_PENDING,
                    BadgeSellerLicense::STATUS_WAITLISTED,
                ]
            )
            ->count();
    }

    public function sellerCapacity(): array
    {
        $service = app(
            BadgeSellerEligibilityService::class
        );

        return [
            'used' =>
                $service->communitySlotsUsed(),
            'cap' =>
                $service->communityLicenseCap(),
            'available' =>
                $service->communitySlotsAvailable(),
        ];
    }

    public static function getNavigationBadge(): ?string
    {
        if (! static::canAccess()) {
            return null;
        }

        $count = 0;

        if (static::hasBadgePermission()) {
            $count += BadgeSubmission::query()
                ->whereIn(
                    'status',
                    [
                        'pending',
                        'rejecting',
                    ]
                )
                ->count();
        }

        if (static::hasSellerPermission()) {
            $count += BadgeSellerLicense::query()
                ->whereIn(
                    'status',
                    [
                        BadgeSellerLicense::STATUS_PENDING,
                        BadgeSellerLicense::STATUS_WAITLISTED,
                    ]
                )
                ->count();
        }

        return $count > 0
            ? (string) $count
            : null;
    }

    public static function getNavigationBadgeColor(): ?string
    {
        return 'warning';
    }
}
