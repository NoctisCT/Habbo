<?php

namespace App\Filament\Resources\Audit\AdminCreditAdjustmentResource\Pages;

use App\Filament\Resources\Audit\AdminCreditAdjustmentResource;
use Filament\Resources\Pages\ViewRecord;

class ViewAdminCreditAdjustment extends ViewRecord
{
    protected static string $resource =
        AdminCreditAdjustmentResource::class;

    protected function getHeaderActions(): array
    {
        return [];
    }
}