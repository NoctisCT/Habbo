<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('badge_gifts', function (Blueprint $table) {
            $table->id();
            $table->uuid('purchase_id')->unique();
            $table->uuid('refund_id')->nullable()->unique();

            $table->unsignedBigInteger('sender_account_id');
            $table->unsignedBigInteger('creator_badge_id');
            $table->unsignedBigInteger('creator_user_id');
            $table->unsignedBigInteger('payer_user_id');

            $table->unsignedBigInteger('recipient_account_id');
            $table->unsignedBigInteger('recipient_user_id');

            $table->string('badge_code', 100);
            $table->string('badge_name', 160);
            $table->string('sender_username', 100);
            $table->string('recipient_username', 100);

            $table->unsignedSmallInteger('amount')->default(3);
            $table->string('status', 40);
            $table->text('error_message')->nullable();

            $table->timestamp('delivered_at')->nullable();
            $table->timestamp('refunded_at')->nullable();
            $table->timestamps();

            $table->index(
                ['sender_account_id', 'status'],
                'badge_gifts_sender_status_idx'
            );

            $table->index(
                ['recipient_account_id', 'status'],
                'badge_gifts_recipient_status_idx'
            );

            $table->index(
                ['creator_badge_id', 'recipient_user_id'],
                'badge_gifts_badge_recipient_idx'
            );
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('badge_gifts');
    }
};
