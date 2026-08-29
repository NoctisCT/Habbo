<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Illuminate\Support\Facades\DB;
use RuntimeException;
use Throwable;

class RestoreCharacterCommand extends Command
{
    protected $signature = 'character:restore
                            {character : Nombre o ID del personaje}';

    protected $description = 'Restaura un personaje archivado en el primer slot libre de su cuenta';

    public function handle(): int
    {
        $character = trim((string) $this->argument('character'));

        try {
            $result = DB::transaction(function () use ($character) {
                $userQuery = DB::table('users');

                if (ctype_digit($character)) {
                    $userQuery->where('id', (int) $character);
                } else {
                    $userQuery->where('username', $character);
                }

                $user = $userQuery
                    ->lockForUpdate()
                    ->first();

                if (!$user) {
                    throw new RuntimeException('El personaje no existe.');
                }

                $mapping = DB::table('account_characters')
                    ->where('user_id', $user->id)
                    ->lockForUpdate()
                    ->first();

                if (!$mapping) {
                    throw new RuntimeException('El personaje no pertenece a ninguna cuenta.');
                }

                if ($mapping->archived_at === null) {
                    throw new RuntimeException('El personaje ya está activo.');
                }

                $account = DB::table('accounts')
                    ->where('id', $mapping->account_id)
                    ->lockForUpdate()
                    ->first();

                if (!$account) {
                    throw new RuntimeException('La cuenta del personaje no existe.');
                }

                $activeMappings = DB::table('account_characters')
                    ->where('account_id', $mapping->account_id)
                    ->whereNull('archived_at')
                    ->lockForUpdate()
                    ->get(['slot']);

                if ($activeMappings->count() >= (int) $account->character_slots) {
                    throw new RuntimeException(
                        "No se puede restaurar: la cuenta tiene todos sus {$account->character_slots} slots ocupados."
                    );
                }

                $usedSlots = [];

                foreach ($activeMappings as $activeMapping) {
                    if ($activeMapping->slot !== null) {
                        $usedSlots[(int) $activeMapping->slot] = true;
                    }
                }

                $freeSlot = null;

                for ($slot = 1; $slot <= (int) $account->character_slots; $slot++) {
                    if (!isset($usedSlots[$slot])) {
                        $freeSlot = $slot;
                        break;
                    }
                }

                if ($freeSlot === null) {
                    throw new RuntimeException('No se ha encontrado ningún slot libre.');
                }

                DB::table('account_characters')
                    ->where('account_id', $mapping->account_id)
                    ->where('user_id', $user->id)
                    ->update([
                        'slot' => $freeSlot,
                        'is_primary' => 0,
                        'archived_at' => null,
                    ]);

                return [
                    'id' => $user->id,
                    'username' => $user->username,
                    'account_id' => $mapping->account_id,
                    'slot' => $freeSlot,
                ];
            }, 5);

            $this->info(
                "Restaurado: {$result['username']} (#{$result['id']}) → cuenta {$result['account_id']}, slot {$result['slot']}."
            );

            return self::SUCCESS;
        } catch (Throwable $e) {
            $this->error($e->getMessage());

            return self::FAILURE;
        }
    }
}