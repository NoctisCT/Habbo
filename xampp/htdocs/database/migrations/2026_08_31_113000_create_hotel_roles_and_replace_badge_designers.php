<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('hotel_roles', function (Blueprint $table) {
            $table->id();
            $table->string('slug', 80)->unique();
            $table->string('name', 100);
            $table->string('description', 255)->nullable();
            $table->string('badge_code', 32)->nullable();
            $table->boolean('active')->default(true);
            $table->unsignedInteger('sort_order')->default(0);
            $table->timestamp('created_at')->useCurrent();
            $table->timestamp('updated_at')->nullable();

            $table->index(['active', 'sort_order']);
        });

        Schema::create('user_hotel_roles', function (Blueprint $table) {
            $table->id();
            $table->integer('user_id');
            $table->unsignedBigInteger('hotel_role_id');

            $table->boolean('active')->default(true);

            $table->integer('granted_by_user_id')->nullable();
            $table->integer('revoked_by_user_id')->nullable();

            $table->timestamp('granted_at')->useCurrent();
            $table->timestamp('revoked_at')->nullable();

            $table->string('notes', 500)->nullable();

            $table->timestamp('created_at')->useCurrent();
            $table->timestamp('updated_at')->nullable();

            $table->unique(
                ['user_id', 'hotel_role_id'],
                'user_hotel_roles_user_role_unique'
            );

            $table->index(
                ['user_id', 'active'],
                'user_hotel_roles_user_active_idx'
            );

            $table->index(
                ['hotel_role_id', 'active'],
                'user_hotel_roles_role_active_idx'
            );
        });

        DB::table('hotel_roles')->insert([
            [
                'slug' => 'badge_designer',
                'name' => 'Diseñador de Placas',
                'description' =>
                    'Crea placas oficiales del hotel y puede vender sus placas sin consumir una licencia comunitaria.',
                'badge_code' => null,
                'active' => 1,
                'sort_order' => 10,
                'created_at' => now(),
                'updated_at' => null,
            ],
            [
                'slug' => 'clothing_designer',
                'name' => 'Diseñador de Ropa',
                'description' =>
                    'Crea ropa oficial del hotel y podrá publicar ropa en su marketplace específico.',
                'badge_code' => null,
                'active' => 1,
                'sort_order' => 20,
                'created_at' => now(),
                'updated_at' => null,
            ],
            [
                'slug' => 'dj',
                'name' => 'DJ',
                'description' =>
                    'Cargo de entretenimiento del hotel.',
                'badge_code' => null,
                'active' => 1,
                'sort_order' => 30,
                'created_at' => now(),
                'updated_at' => null,
            ],
            [
                'slug' => 'game_manager',
                'name' => 'Game Manager',
                'description' =>
                    'Organiza y gestiona juegos y actividades del hotel.',
                'badge_code' => null,
                'active' => 1,
                'sort_order' => 40,
                'created_at' => now(),
                'updated_at' => null,
            ],
            [
                'slug' => 'croupier',
                'name' => 'Croupier',
                'description' =>
                    'Cargo de entretenimiento vinculado a juegos y casino.',
                'badge_code' => null,
                'active' => 1,
                'sort_order' => 50,
                'created_at' => now(),
                'updated_at' => null,
            ],
        ]);

        DB::table('website_housekeeping_permissions')->insert([
            'permission' => 'manage_hotel_roles',
            'min_rank' => 6,
            'description' =>
                'Gestionar cargos compatibles del hotel.',
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        Schema::dropIfExists('badge_designers');
    }

    public function down(): void
    {
        DB::table('website_housekeeping_permissions')
            ->where('permission', 'manage_hotel_roles')
            ->delete();

        Schema::dropIfExists('user_hotel_roles');
        Schema::dropIfExists('hotel_roles');

        if (! Schema::hasTable('badge_designers')) {
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
        }
    }
};
