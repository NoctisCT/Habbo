<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('admin_credit_adjustments', function (Blueprint $table) {
            $table->string('id', 64)->primary();

            $table->unsignedInteger('account_id');
            $table->integer('actor_user_id');
            $table->integer('target_user_id');

            $table->integer('delta');
            $table->string('reason', 500);
            $table->char('fingerprint', 64);

            $table->string('status', 24);

            $table->string('attempt_token', 64)->nullable();
            $table->timestamp('lease_expires_at')->nullable();

            $table->integer('balance_before')->nullable();
            $table->integer('balance_after')->nullable();

            $table->string('bridge_transaction_id', 64)
                ->nullable()
                ->unique();

            $table->longText('result_json')->nullable();
            $table->string('error_code', 64)->nullable();

            $table->timestamp('created_at')->useCurrent();
            $table->timestamp('updated_at')->nullable();
            $table->timestamp('completed_at')->nullable();

            $table->index(
                ['actor_user_id', 'created_at'],
                'admin_credit_adjustments_actor_created_index'
            );

            $table->index(
                ['target_user_id', 'created_at'],
                'admin_credit_adjustments_target_created_index'
            );

            $table->index(
                ['status', 'lease_expires_at'],
                'admin_credit_adjustments_status_lease_index'
            );
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('admin_credit_adjustments');
    }
};