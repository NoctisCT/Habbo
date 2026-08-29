<?php

namespace App\Filament\Resources\Audit\AdminCreditAdjustmentResource\Pages;

use App\Filament\Resources\Audit\AdminCreditAdjustmentResource;
use Filament\Resources\Pages\ListRecords;

class ListAdminCreditAdjustments extends ListRecords
{
    protected static string $resource =
        AdminCreditAdjustmentResource::class;

    protected function getHeaderActions(): array
    {
        return [];
    }
}