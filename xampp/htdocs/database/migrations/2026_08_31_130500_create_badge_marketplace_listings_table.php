<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create(
            'badge_marketplace_listings',
            function (Blueprint $table): void {
                $table->id();

                $table->unsignedBigInteger(
                    'creator_badge_id'
                )->unique();

                $table->unsignedInteger(
                    'seller_account_id'
                );

                $table->unsignedTinyInteger(
                    'seller_earnings'
                );

                $table->unsignedTinyInteger(
                    'hotel_commission'
                );

                $table->unsignedTinyInteger(
                    'buyer_price'
                );

                $table->string(
                    'status',
                    16
                )->default('active');

                $table->timestamp(
                    'activated_at'
                )->nullable();

                $table->timestamp(
                    'deactivated_at'
                )->nullable();

                $table->timestamps();

                $table->index([
                    'seller_account_id',
                    'status',
                ]);

                $table->index([
                    'status',
                    'activated_at',
                ]);
            }
        );
    }

    public function down(): void
    {
        Schema::dropIfExists(
            'badge_marketplace_listings'
        );
    }
};
