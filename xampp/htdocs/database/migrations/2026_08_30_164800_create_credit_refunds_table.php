<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (Schema::hasTable('credit_refunds')) {
            return;
        }

        Schema::create('credit_refunds', function (Blueprint $table) {
            $table->string('id', 64)->primary();

            $table->string('original_purchase_id', 64);

            $table->unsignedInteger('account_id');
            $table->integer('user_id');

            $table->unsignedInteger('amount');
            $table->string('reason', 500);

            $table->char('fingerprint', 64);

            $table->string('status', 24);
            $table->string('attempt_token', 64)->nullable();
            $table->timestamp('lease_expires_at')->nullable();

            $table->string('payment_channel', 32)->nullable();
            $table->string('bridge_transaction_id', 64)->nullable();

            $table->integer('balance_before')->nullable();
            $table->integer('balance_after')->nullable();

            $table->json('metadata')->nullable();
            $table->longText('result_json')->nullable();

            $table->string('error_code', 64)->nullable();

            $table->timestamp('created_at')->useCurrent();
            $table->timestamp('updated_at')->nullable();
            $table->timestamp('completed_at')->nullable();

            $table->index([
                'original_purchase_id',
                'status',
            ]);

            $table->index([
                'account_id',
                'created_at',
            ]);

            $table->index([
                'user_id',
                'created_at',
            ]);

            $table->unique(
                'bridge_transaction_id',
                'credit_refunds_bridge_transaction_unique'
            );
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('credit_refunds');
    }
};