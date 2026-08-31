<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create(
            'badge_marketplace_sales',
            function (Blueprint $table): void {
                $table->id();

                $table->uuid(
                    'purchase_id'
                )->unique();

                $table->uuid(
                    'refund_id'
                )->unique();

                $table->unsignedBigInteger(
                    'listing_id'
                );

                $table->unsignedBigInteger(
                    'creator_badge_id'
                );

                $table->unsignedInteger(
                    'seller_account_id'
                );

                $table->unsignedInteger(
                    'seller_user_id'
                );

                $table->unsignedInteger(
                    'buyer_account_id'
                );

                $table->unsignedInteger(
                    'buyer_user_id'
                );

                $table->string(
                    'badge_code',
                    64
                );

                $table->string(
                    'badge_name',
                    60
                );

                $table->string(
                    'seller_username',
                    64
                );

                $table->string(
                    'buyer_username',
                    64
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
                    32
                )->default(
                    'paid_pending_delivery'
                );

                $table->string(
                    'payout_channel',
                    40
                )->nullable();

                $table->string(
                    'payout_transaction_id',
                    120
                )->nullable()->unique();

                $table->integer(
                    'seller_balance_before'
                )->nullable();

                $table->integer(
                    'seller_balance_after'
                )->nullable();

                $table->text(
                    'error_message'
                )->nullable();

                $table->timestamp(
                    'delivered_at'
                )->nullable();

                $table->timestamp(
                    'paid_out_at'
                )->nullable();

                $table->timestamp(
                    'refunded_at'
                )->nullable();

                $table->timestamps();

                $table->index([
                    'listing_id',
                    'status',
                ]);

                $table->index([
                    'creator_badge_id',
                    'status',
                ]);

                $table->index([
                    'seller_account_id',
                    'status',
                ]);

                $table->index([
                    'buyer_account_id',
                    'status',
                ]);

                $table->index([
                    'buyer_user_id',
                    'badge_code',
                ]);
            }
        );
    }

    public function down(): void
    {
        Schema::dropIfExists(
            'badge_marketplace_sales'
        );
    }
};
