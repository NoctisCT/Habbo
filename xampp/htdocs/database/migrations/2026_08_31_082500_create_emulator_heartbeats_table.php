<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create(
            'emulator_heartbeats',
            function (Blueprint $table) {
                $table
                    ->string('heartbeat_key', 64)
                    ->primary();

                $table
                    ->unsignedBigInteger('last_seen_at')
                    ->index();
            }
        );
    }

    public function down(): void
    {
        Schema::dropIfExists('emulator_heartbeats');
    }
};
