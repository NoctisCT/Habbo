<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('account_notifications', function (Blueprint $table) {
            $table->bigIncrements('id');
            $table->unsignedBigInteger('account_id');
            $table->string('type', 100);
            $table->string('title', 160);
            $table->text('message');
            $table->string('url', 2048)->nullable();
            $table->json('data')->nullable();
            $table->string('dedupe_key', 191)->nullable();
            $table->timestamp('read_at')->nullable();
            $table->timestamps();

            $table->index(
                ['account_id', 'read_at'],
                'account_notifications_account_read_idx'
            );

            $table->index(
                ['account_id', 'created_at'],
                'account_notifications_account_created_idx'
            );

            $table->unique(
                ['account_id', 'dedupe_key'],
                'account_notifications_account_dedupe_unique'
            );
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('account_notifications');
    }
};
