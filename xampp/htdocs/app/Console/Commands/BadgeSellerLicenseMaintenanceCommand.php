<?php

namespace App\Console\Commands;

use App\Services\BadgeSellerEligibilityService;
use Illuminate\Console\Command;
use Throwable;

class BadgeSellerLicenseMaintenanceCommand extends Command
{
    protected $signature =
        'badge-marketplace:maintain-seller-licenses';

    protected $description =
        'Warns inactive badge sellers, revokes expired community licenses, and promotes approved waitlisted sellers.';

    public function handle(
        BadgeSellerEligibilityService $service
    ): int {
        try {
            $result =
                $service->maintainCommunityLicenses();

            $this->info(
                'Badge seller license maintenance completed.'
            );

            $this->line(
                'Warnings reset: ' .
                (int) $result['warnings_reset']
            );

            $this->line(
                'Warnings sent: ' .
                (int) $result['warnings_sent']
            );

            $this->line(
                'Licenses revoked: ' .
                (int) $result['revoked']
            );

            $this->line(
                'Waitlisted promoted: ' .
                (int) $result['promoted']
            );

            return self::SUCCESS;
        } catch (Throwable $exception) {
            $this->error(
                'Badge seller license maintenance failed: ' .
                $exception->getMessage()
            );

            report(
                $exception
            );

            return self::FAILURE;
        }
    }
}
