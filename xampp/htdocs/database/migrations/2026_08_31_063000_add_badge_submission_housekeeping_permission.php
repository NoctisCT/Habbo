<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    private const PERMISSION = 'manage_badge_submissions';

    public function up(): void
    {
        if (! Schema::hasTable('website_housekeeping_permissions')) {
            throw new \RuntimeException(
                'No existe website_housekeeping_permissions.'
            );
        }

        $existing = DB::table('website_housekeeping_permissions')
            ->where('permission', self::PERMISSION)
            ->first();

        if ($existing) {
            if ((int) $existing->min_rank !== 6) {
                throw new \RuntimeException(
                    'manage_badge_submissions ya existe con un rango distinto de 6.'
                );
            }

            return;
        }

        $data = [
            'permission' => self::PERMISSION,
            'min_rank' => 6,
        ];

        if (
            Schema::hasColumn(
                'website_housekeeping_permissions',
                'created_at'
            )
        ) {
            $data['created_at'] = now();
        }

        if (
            Schema::hasColumn(
                'website_housekeeping_permissions',
                'updated_at'
            )
        ) {
            $data['updated_at'] = now();
        }

        DB::table('website_housekeeping_permissions')
            ->insert($data);
    }

    public function down(): void
    {
        if (! Schema::hasTable('website_housekeeping_permissions')) {
            return;
        }

        DB::table('website_housekeeping_permissions')
            ->where('permission', self::PERMISSION)
            ->delete();
    }
};
