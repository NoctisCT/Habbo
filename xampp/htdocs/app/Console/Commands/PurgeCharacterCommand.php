<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Illuminate\Support\Facades\DB;
use RuntimeException;
use Throwable;

class PurgeCharacterCommand extends Command
{
    protected $signature = 'character:purge
                            {character : Nombre o ID del personaje}
                            {--force : Confirma el borrado físico definitivo}';

    protected $description = 'Borra físicamente un personaje archivado y todos los datos sujetos a cascada';

    public function handle(): int
    {
        $character = trim((string) $this->argument('character'));

        $userQuery = DB::table('users');

        if (ctype_digit($character)) {
            $userQuery->where('id', (int) $character);
        } else {
            $userQuery->where('username', $character);
        }

        $user = $userQuery->first();

        if (!$user) {
            $this->error('El personaje no existe.');

            return self::FAILURE;
        }

        $mapping = DB::table('account_characters')
            ->where('user_id', $user->id)
            ->first();

        if (!$mapping) {
            $this->error('El personaje no pertenece a ninguna cuenta.');

            return self::FAILURE;
        }

        if ($mapping->archived_at === null) {
            $this->error('SEGURIDAD: solo se pueden purgar personajes que ya estén archivados.');

            return self::FAILURE;
        }

        if ((int) $mapping->is_primary === 1) {
            $this->error('SEGURIDAD: un personaje principal nunca puede purgarse.');

            return self::FAILURE;
        }

        if (!$this->option('force')) {
            $this->warn("Vas a borrar DEFINITIVAMENTE {$user->username} (#{$user->id}).");
            $this->warn('Esto elimina el users y puede eliminar mediante CASCADE inventario, datos y relaciones.');

            if (!$this->confirm('¿Continuar con el borrado físico?')) {
                $this->info('Operación cancelada.');

                return self::SUCCESS;
            }
        }

        try {
            DB::transaction(function () use ($user) {
                $lockedUser = DB::table('users')
                    ->where('id', $user->id)
                    ->lockForUpdate()
                    ->first();

                if (!$lockedUser) {
                    throw new RuntimeException('El personaje ya no existe.');
                }

                $lockedMapping = DB::table('account_characters')
                    ->where('user_id', $user->id)
                    ->lockForUpdate()
                    ->first();

                if (!$lockedMapping) {
                    throw new RuntimeException('El mapping del personaje ya no existe.');
                }

                if ($lockedMapping->archived_at === null) {
                    throw new RuntimeException('SEGURIDAD: el personaje ya no está archivado.');
                }

                if ((int) $lockedMapping->is_primary === 1) {
                    throw new RuntimeException('SEGURIDAD: el personaje se ha convertido en principal.');
                }

                DB::table('users')
                    ->where('id', $user->id)
                    ->delete();
            }, 5);

            $this->info("Eliminado definitivamente: {$user->username} (#{$user->id}).");

            return self::SUCCESS;
        } catch (Throwable $e) {
            $this->error('No se pudo borrar físicamente el personaje.');
            $this->error($e->getMessage());
            $this->warn('La transacción se ha revertido; no se ha hecho un borrado parcial.');

            return self::FAILURE;
        }
    }
}