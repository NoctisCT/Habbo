<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('badge_designers', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('account_id')->unique();
            $table->boolean('active')->default(true);
            $table->integer('granted_by_user_id')->nullable();
            $table->timestamp('granted_at')->useCurrent();
            $table->timestamp('revoked_at')->nullable();
            $table->string('notes', 500)->nullable();
            $table->timestamp('created_at')->useCurrent();
            $table->timestamp('updated_at')->nullable();

            $table->index(['active', 'granted_at']);
        });

        Schema::create('badge_seller_licenses', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('account_id')->unique();

            $table->string('status', 20)->default('pending');
            $table->unsignedTinyInteger('community_slot')->nullable()->unique();

            $table->timestamp('applied_at')->useCurrent();
            $table->timestamp('activated_at')->nullable();
            $table->timestamp('waitlisted_at')->nullable();
            $table->timestamp('last_activity_at')->nullable();
            $table->timestamp('warning_sent_at')->nullable();
            $table->timestamp('revoked_at')->nullable();

            $table->integer('reviewed_by_user_id')->nullable();
            $table->string('revocation_reason', 255)->nullable();

            $table->timestamp('created_at')->useCurrent();
            $table->timestamp('updated_at')->nullable();

            $table->index(['status', 'activated_at']);
            $table->index(['status', 'last_activity_at']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('badge_seller_licenses');
        Schema::dropIfExists('badge_designers');
    }
};
