<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('credit_bridge_transactions', function (Blueprint $table) {
            $table->string('transaction_id', 64)->primary();
            $table->integer('user_id');
            $table->unsignedInteger('amount');
            $table->integer('balance_before');
            $table->integer('balance_after');
            $table->string('status', 16)->default('pending');
            $table->timestamp('created_at')->useCurrent();
            $table->timestamp('updated_at')->nullable();

            $table->index(['user_id', 'status']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('credit_bridge_transactions');
    }
};