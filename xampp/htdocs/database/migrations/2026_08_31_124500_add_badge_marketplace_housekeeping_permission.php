<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    public function up(): void
    {
        DB::table('website_housekeeping_permissions')
            ->insert([
                'permission' => 'manage_badge_marketplace',
                'min_rank' => 6,
                'description' =>
                    'Gestionar solicitudes y licencias de vendedores de placas.',
                'created_at' => now(),
                'updated_at' => now(),
            ]);
    }

    public function down(): void
    {
        DB::table('website_housekeeping_permissions')
            ->where(
                'permission',
                'manage_badge_marketplace'
            )
            ->delete();
    }
};
