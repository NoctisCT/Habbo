<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('badge_submissions', function (Blueprint $table) {
            $table->id();

            $table->unsignedInteger('account_id');
            $table->integer('creator_user_id');
            $table->integer('payer_user_id');

            $table->string('purchase_id', 64)->unique();
            $table->string('refund_id', 64)->nullable()->unique();

            $table->string('source_mode', 20);
            $table->string('source_path');
            $table->string('processed_path');
            $table->string('original_filename')->nullable();

            $table->string('badge_name', 60);
            $table->string('badge_description', 255);

            $table->string('status', 24)->default('pending');
            $table->string('badge_code', 32)->nullable()->unique();

            $table->integer('moderator_user_id')->nullable();
            $table->string('moderation_reason', 500)->nullable();

            $table->timestamp('approved_at')->nullable();
            $table->timestamp('rejected_at')->nullable();
            $table->timestamps();

            $table->index(['account_id', 'status']);
            $table->index(['creator_user_id', 'status']);
            $table->index(['payer_user_id', 'created_at']);
            $table->index(['status', 'created_at']);
        });

        Schema::create('creator_badges', function (Blueprint $table) {
            $table->id();

            $table->unsignedInteger('account_id');
            $table->integer('creator_user_id');
            $table->unsignedBigInteger('badge_submission_id')->unique();

            $table->string('badge_code', 32)->unique();
            $table->string('badge_name', 60);
            $table->string('badge_description', 255);
            $table->string('image_path');

            $table->boolean('marketplace_enabled')->default(false);
            $table->timestamp('created_at')->useCurrent();
            $table->timestamp('updated_at')->nullable();

            $table->index(['account_id', 'created_at']);
            $table->index(['creator_user_id', 'created_at']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('creator_badges');
        Schema::dropIfExists('badge_submissions');
    }
};