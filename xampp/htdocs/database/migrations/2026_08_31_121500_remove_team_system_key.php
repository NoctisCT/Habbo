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
                'website_teams',
                'system_key'
            )
        ) {
            Schema::table(
                'website_teams',
                function (Blueprint $table) {
                    $table->dropUnique(
                        'website_teams_system_key_unique'
                    );

                    $table->dropColumn(
                        'system_key'
                    );
                }
            );
        }
    }

    public function down(): void
    {
        if (
            ! Schema::hasColumn(
                'website_teams',
                'system_key'
            )
        ) {
            Schema::table(
                'website_teams',
                function (Blueprint $table) {
                    $table
                        ->string(
                            'system_key',
                            80
                        )
                        ->nullable()
                        ->unique()
                        ->after(
                            'rank_name'
                        );
                }
            );
        }
    }
};
