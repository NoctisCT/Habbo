<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (Schema::hasTable('character_restorations')) {
            return;
        }

        Schema::create('character_restorations', function (Blueprint $table) {
            $table->bigIncrements('id');

            $table->unsignedInteger('account_id');
            $table->integer('character_user_id');
            $table->string('character_username', 25);

            $table->integer('payer_user_id')->nullable();
            $table->integer('restored_by_user_id')->nullable();

            $table->string('method', 20);
            $table->integer('price')->default(0);
            $table->unsignedSmallInteger('restored_slot');

            $table->timestamp('created_at')->useCurrent();

            $table->index(['account_id', 'created_at']);
            $table->index('character_user_id');
            $table->index('payer_user_id');
            $table->index('restored_by_user_id');
            $table->index('method');

            $table->foreign('account_id')
                ->references('id')
                ->on('accounts')
                ->cascadeOnDelete();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('character_restorations');
    }
};