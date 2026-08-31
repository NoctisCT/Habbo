<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('website_teams', function (Blueprint $table) {
            $table
                ->string('system_key', 80)
                ->nullable()
                ->unique()
                ->after('rank_name');
        });

        Schema::create('user_website_team', function (Blueprint $table) {
            $table->id();
            $table->integer('user_id');
            $table->unsignedBigInteger('website_team_id');
            $table->integer('assigned_by_user_id')->nullable();
            $table->timestamp('created_at')->useCurrent();
            $table->timestamp('updated_at')->nullable();

            $table->unique(
                ['user_id', 'website_team_id'],
                'user_website_team_unique'
            );

            $table->index(
                ['website_team_id', 'user_id'],
                'user_website_team_team_user_idx'
            );
        });

        if (Schema::hasColumn('users', 'team_id')) {
            DB::table('users')
                ->whereNotNull('team_id')
                ->orderBy('id')
                ->get([
                    'id',
                    'team_id',
                ])
                ->each(function ($user): void {
                    DB::table('user_website_team')
                        ->insertOrIgnore([
                            'user_id' =>
                                (int) $user->id,
                            'website_team_id' =>
                                (int) $user->team_id,
                            'assigned_by_user_id' =>
                                null,
                            'created_at' => now(),
                            'updated_at' => null,
                        ]);
                });
        }

        DB::table('website_housekeeping_permissions')
            ->where(
                'permission',
                'manage_hotel_roles'
            )
            ->delete();

        Schema::dropIfExists('user_hotel_roles');
        Schema::dropIfExists('hotel_roles');
    }

    public function down(): void
    {
        if (
            Schema::hasTable('user_website_team') &&
            Schema::hasColumn('users', 'team_id')
        ) {
            DB::table('users')
                ->update([
                    'team_id' => null,
                ]);

            DB::table('user_website_team')
                ->orderBy('user_id')
                ->orderBy('website_team_id')
                ->get()
                ->groupBy('user_id')
                ->each(
                    function ($rows, $userId): void {
                        $first = $rows->first();

                        if (! $first) {
                            return;
                        }

                        DB::table('users')
                            ->where(
                                'id',
                                (int) $userId
                            )
                            ->update([
                                'team_id' =>
                                    (int)
                                    $first->website_team_id,
                            ]);
                    }
                );
        }

        Schema::dropIfExists('user_website_team');

        if (
            Schema::hasColumn(
                'website_teams',
                'system_key'
            )
        ) {
            Schema::table(
                'website_teams',
                function (Blueprint $table) {
                    $table->dropUnique(
                        'website_teams_system_key_unique'
                    );

                    $table->dropColumn(
                        'system_key'
                    );
                }
            );
        }

        if (! Schema::hasTable('hotel_roles')) {
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
        }

        if (! Schema::hasTable('user_hotel_roles')) {
            Schema::create(
                'user_hotel_roles',
                function (Blueprint $table) {
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
                }
            );
        }

        if (
            DB::table('hotel_roles')->count() === 0
        ) {
            DB::table('hotel_roles')->insert([
                [
                    'slug' => 'badge_designer',
                    'name' => 'Diseñador de Placas',
                    'description' => null,
                    'badge_code' => null,
                    'active' => 1,
                    'sort_order' => 10,
                    'created_at' => now(),
                    'updated_at' => null,
                ],
                [
                    'slug' => 'clothing_designer',
                    'name' => 'Diseñador de Ropa',
                    'description' => null,
                    'badge_code' => null,
                    'active' => 1,
                    'sort_order' => 20,
                    'created_at' => now(),
                    'updated_at' => null,
                ],
                [
                    'slug' => 'dj',
                    'name' => 'DJ',
                    'description' => null,
                    'badge_code' => null,
                    'active' => 1,
                    'sort_order' => 30,
                    'created_at' => now(),
                    'updated_at' => null,
                ],
                [
                    'slug' => 'game_manager',
                    'name' => 'Game Manager',
                    'description' => null,
                    'badge_code' => null,
                    'active' => 1,
                    'sort_order' => 40,
                    'created_at' => now(),
                    'updated_at' => null,
                ],
                [
                    'slug' => 'croupier',
                    'name' => 'Croupier',
                    'description' => null,
                    'badge_code' => null,
                    'active' => 1,
                    'sort_order' => 50,
                    'created_at' => now(),
                    'updated_at' => null,
                ],
            ]);
        }

        DB::table('website_housekeeping_permissions')
            ->updateOrInsert(
                [
                    'permission' =>
                        'manage_hotel_roles',
                ],
                [
                    'min_rank' => 6,
                    'description' =>
                        'Gestionar cargos compatibles del hotel.',
                    'created_at' => now(),
                    'updated_at' => now(),
                ]
            );
    }
};
