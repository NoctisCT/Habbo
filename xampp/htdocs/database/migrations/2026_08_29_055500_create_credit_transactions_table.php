<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (Schema::hasTable('credit_transactions')) {
            return;
        }

        Schema::create('credit_transactions', function (Blueprint $table) {
            $table->bigIncrements('id');

            $table->unsignedInteger('account_id');
            $table->integer('user_id');

            $table->string('type', 50);
            $table->integer('amount');

            $table->integer('balance_before');
            $table->integer('balance_after');

            $table->json('metadata')->nullable();
            $table->timestamp('created_at')->useCurrent();

            $table->index(['account_id', 'created_at']);
            $table->index(['user_id', 'created_at']);
            $table->index('type');

            $table->foreign('account_id')
                ->references('id')
                ->on('accounts')
                ->cascadeOnDelete();

            $table->foreign('user_id')
                ->references('id')
                ->on('users')
                ->cascadeOnDelete();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('credit_transactions');
    }
};
