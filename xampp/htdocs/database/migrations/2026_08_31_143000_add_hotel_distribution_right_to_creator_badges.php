<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (
            Schema::hasColumn(
                'creator_badges',
                'hotel_distribution_granted_at'
            )
        ) {
            return;
        }

        Schema::table(
            'creator_badges',
            function (Blueprint $table): void {
                $table
                    ->timestamp(
                        'hotel_distribution_granted_at'
                    )
                    ->nullable()
                    ->after(
                        'marketplace_enabled'
                    );
            }
        );
    }

    public function down(): void
    {
        if (
            ! Schema::hasColumn(
                'creator_badges',
                'hotel_distribution_granted_at'
            )
        ) {
            return;
        }

        Schema::table(
            'creator_badges',
            function (Blueprint $table): void {
                $table->dropColumn(
                    'hotel_distribution_granted_at'
                );
            }
        );
    }
};
