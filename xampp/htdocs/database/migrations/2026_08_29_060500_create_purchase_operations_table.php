<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (! Schema::hasTable('purchase_operations')) {
            Schema::create('purchase_operations', function (Blueprint $table) {
                $table->string('id', 64)->primary();

                $table->unsignedInteger('account_id');
                $table->integer('user_id');

                $table->string('type', 50);
                $table->integer('amount');

                $table->char('fingerprint', 64);

                $table->string('status', 24);
                $table->string('attempt_token', 64)->nullable();
                $table->timestamp('lease_expires_at')->nullable();

                $table->longText('result_json')->nullable();
                $table->longText('payment_json')->nullable();

                $table->string('error_code', 64)->nullable();

                $table->timestamp('created_at')->useCurrent();
                $table->timestamp('updated_at')->nullable();
                $table->timestamp('completed_at')->nullable();
                $table->timestamp('refunded_at')->nullable();

                $table->index(['account_id', 'created_at']);
                $table->index(['user_id', 'created_at']);
                $table->index(['status', 'lease_expires_at']);
                $table->index('type');
            });
        }

        if (
            Schema::hasTable('credit_transactions') &&
            ! Schema::hasColumn('credit_transactions', 'purchase_id')
        ) {
            Schema::table('credit_transactions', function (Blueprint $table) {
                $table->string('purchase_id', 64)
                    ->nullable()
                    ->after('user_id');

                $table->unique(
                    'purchase_id',
                    'credit_transactions_purchase_id_unique'
                );
            });
        }
    }

    public function down(): void
    {
        if (
            Schema::hasTable('credit_transactions') &&
            Schema::hasColumn('credit_transactions', 'purchase_id')
        ) {
            Schema::table('credit_transactions', function (Blueprint $table) {
                $table->dropUnique(
                    'credit_transactions_purchase_id_unique'
                );

                $table->dropColumn('purchase_id');
            });
        }

        Schema::dropIfExists('purchase_operations');
    }
};