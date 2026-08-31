<?php

namespace App\Filament\Resources\Hotel\BadgeSubmissionResource\Pages;

use App\Filament\Resources\Hotel\BadgeSubmissionResource;
use Filament\Resources\Pages\ManageRecords;

class ManageBadgeSubmissions extends ManageRecords
{
    protected static string $resource =
        BadgeSubmissionResource::class;

    protected function getHeaderActions(): array
    {
        return [];
    }
}
