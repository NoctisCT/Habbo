<?php

namespace App\Http\Controllers\Client;

use App\Exceptions\CreditTransactionException;
use App\Http\Controllers\Controller;
use App\Services\CreditTransactionService;
use App\Models\User;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;
use Illuminate\View\View;

class CharacterRestoreController extends Controller
{
    public function index(): View
    {
        $accountId = $this->accountId();

        $account = DB::table('accounts')
            ->where('id', $accountId)
            ->first();

        abort_unless($account, 404);

        $characters = User::query()
            ->join('account_characters as ac', 'ac.user_id', '=', 'users.id')
            ->where('ac.account_id', $accountId)
            ->whereNull('ac.archived_at')
            ->orderBy('ac.slot')
            ->get([
                'users.*',
                'ac.slot',
            ]);

        $archivedCharacters = User::query()
            ->join('account_characters as ac', 'ac.user_id', '=', 'users.id')
            ->where('ac.account_id', $accountId)
            ->whereNotNull('ac.archived_at')
            ->orderByDesc('ac.archived_at')
            ->get([
                'users.*',
                'ac.archived_at',
            ]);

        return view('client.archived-characters', [
            'characters' => $characters,
            'archivedCharacters' => $archivedCharacters,
            'slotsUsed' => $characters->count(),
            'slotsTotal' => (int) $account->character_slots,
            'restorePrice' => (int) config('monetization.character_restore_price', 100),
        ]);
    }

    public function paid(
        Request $request,
        User $user,
        CreditTransactionService $creditTransactions
    ): RedirectResponse {
        $validated = $request->validate([
            'payer_user_id' => [
                'required',
                'integer',
            ],
            'purchase_id' => [
                'required',
                'string',
                'uuid',
            ],
        ]);

        $accountId = $this->accountId();
        $price = (int) config(
            'monetization.character_restore_price',
            100
        );

        try {
            $transaction = $creditTransactions->debitAndRun(
                $accountId,
                (int) $validated['payer_user_id'],
                $price,
                'character_restore',
                (string) $validated['purchase_id'],
                [
                    'target_user_id' => (int) $user->id,
                    'benefit' => 'character_restore',
                ],
                function ($payer, array $payment) use (
                    $accountId,
                    $price,
                    $user
                ) {
                    $mapping = DB::table('account_characters')
                        ->where('account_id', $accountId)
                        ->where('user_id', $user->id)
                        ->whereNotNull('archived_at')
                        ->lockForUpdate()
                        ->first();

                    abort_unless($mapping, 403);

                    $account = DB::table('accounts')
                        ->where('id', $accountId)
                        ->lockForUpdate()
                        ->first();

                    abort_unless($account, 404);

                    $freeSlot = $this->findFreeSlot(
                        $accountId,
                        (int) $account->character_slots
                    );

                    DB::table('account_characters')
                        ->where('account_id', $accountId)
                        ->where('user_id', $user->id)
                        ->update([
                            'slot' => $freeSlot,
                            'is_primary' => 0,
                            'archived_at' => null,
                        ]);

                    DB::table('character_restorations')->insert([
                        'account_id' => $accountId,
                        'character_user_id' => $user->id,
                        'character_username' => $user->username,
                        'payer_user_id' => $payer->id,
                        'restored_by_user_id' => null,
                        'method' => 'paid',
                        'price' => $price,
                        'restored_slot' => $freeSlot,
                        'created_at' => now(),
                    ]);

                    return [
                        'result' => [
                            'username' => $user->username,
                        ],
                        'metadata' => [
                            'target_user_id' => $user->id,
                            'target_username' => $user->username,
                            'restored_slot' => $freeSlot,
                            'price' => $price,
                        ],
                    ];
                }
            );
        } catch (CreditTransactionException $exception) {
            throw ValidationException::withMessages([
                'payer_user_id' => $exception->getMessage(),
            ]);
        }

        return to_route('character-archived-index')
            ->with(
                'success',
                "{$transaction['result']['username']} ha sido restaurado por {$price} cr\u{00E9}ditos."
            );
    }
    public function adminIndex(Request $request): View
    {
        abort_unless($this->isMaximumAdmin(), 403);

        $search = trim((string) $request->query('q', ''));

        $query = DB::table('account_characters as ac')
            ->join('users as u', 'u.id', '=', 'ac.user_id')
            ->join('accounts as a', 'a.id', '=', 'ac.account_id')
            ->whereNotNull('ac.archived_at')
            ->select([
                'ac.account_id',
                'ac.user_id',
                'ac.archived_at',
                'u.username',
                'u.look',
                'u.credits',
                'u.rank',
                'a.email as account_email',
                'a.character_slots',
            ]);

        if ($search !== '') {
            $query->where(function ($query) use ($search) {
                $query
                    ->where('u.username', 'like', '%' . $search . '%')
                    ->orWhere('a.email', 'like', '%' . $search . '%');

                if (ctype_digit($search)) {
                    $query->orWhere('u.id', (int) $search);
                }
            });
        }

        $archivedCharacters = $query
            ->orderByDesc('ac.archived_at')
            ->paginate(50)
            ->withQueryString();

        return view('administration.archived-characters', [
            'archivedCharacters' => $archivedCharacters,
            'search' => $search,
        ]);
    }

    public function adminRestore(User $user): RedirectResponse
    {
        abort_unless($this->isMaximumAdmin(), 403);

        $adminUserId = (int) Auth::id();

        $result = DB::transaction(function () use ($adminUserId, $user) {
            $mapping = DB::table('account_characters')
                ->where('user_id', $user->id)
                ->whereNotNull('archived_at')
                ->lockForUpdate()
                ->first();

            if (! $mapping) {
                throw ValidationException::withMessages([
                    'restore' => 'El personaje no esta archivado.',
                ]);
            }

            $account = DB::table('accounts')
                ->where('id', $mapping->account_id)
                ->lockForUpdate()
                ->first();

            abort_unless($account, 404);

            $freeSlot = $this->findFreeSlot(
                (int) $mapping->account_id,
                (int) $account->character_slots
            );

            DB::table('account_characters')
                ->where('account_id', $mapping->account_id)
                ->where('user_id', $user->id)
                ->update([
                    'slot' => $freeSlot,
                    'is_primary' => 0,
                    'archived_at' => null,
                ]);

            DB::table('character_restorations')->insert([
                'account_id' => $mapping->account_id,
                'character_user_id' => $user->id,
                'character_username' => $user->username,
                'payer_user_id' => null,
                'restored_by_user_id' => $adminUserId,
                'method' => 'admin',
                'price' => 0,
                'restored_slot' => $freeSlot,
                'created_at' => now(),
            ]);

            return [
                'username' => $user->username,
            ];
        }, 5);

        return to_route('admin.archived-characters')
            ->with(
                'success',
                "{$result['username']} ha sido restaurado como administrador."
            );
    }

    private function findFreeSlot(int $accountId, int $slotsTotal): int
    {
        $activeMappings = DB::table('account_characters')
            ->where('account_id', $accountId)
            ->whereNull('archived_at')
            ->lockForUpdate()
            ->get([
                'slot',
            ]);

        if ($activeMappings->count() >= $slotsTotal) {
            throw ValidationException::withMessages([
                'restore' => 'La cuenta no tiene ningun slot libre.',
            ]);
        }

        $usedSlots = [];

        foreach ($activeMappings as $mapping) {
            if ($mapping->slot !== null) {
                $usedSlots[(int) $mapping->slot] = true;
            }
        }

        for ($slot = 1; $slot <= $slotsTotal; $slot++) {
            if (! isset($usedSlots[$slot])) {
                return $slot;
            }
        }

        throw ValidationException::withMessages([
            'restore' => 'No se pudo encontrar un slot libre.',
        ]);
    }

    private function accountId(): int
    {
        $accountId = DB::table('account_characters')
            ->where('user_id', Auth::id())
            ->whereNull('archived_at')
            ->value('account_id');

        abort_unless($accountId, 403);

        return (int) $accountId;
    }

    private function isMaximumAdmin(): bool
    {
        $user = Auth::user();

        if (! $user) {
            return false;
        }

        $currentLevel = DB::table('permissions')
            ->where('id', (int) $user->rank)
            ->value('level');

        $maximumLevel = DB::table('permissions')
            ->max('level');

        if ($currentLevel === null || $maximumLevel === null) {
            return false;
        }

        return (int) $currentLevel === (int) $maximumLevel;
    }
}