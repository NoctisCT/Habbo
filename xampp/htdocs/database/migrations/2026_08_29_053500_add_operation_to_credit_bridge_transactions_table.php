<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('credit_bridge_transactions', function (Blueprint $table) {
            $table->string('operation', 16)
                ->default('debit')
                ->after('amount');

            $table->index(
                ['user_id', 'operation', 'status'],
                'credit_bridge_user_operation_status_index'
            );
        });
    }

    public function down(): void
    {
        Schema::table('credit_bridge_transactions', function (Blueprint $table) {
            $table->dropIndex(
                'credit_bridge_user_operation_status_index'
            );

            $table->dropColumn('operation');
        });
    }
};