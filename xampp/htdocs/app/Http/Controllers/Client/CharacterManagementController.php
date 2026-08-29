<?php

namespace App\Http\Controllers\Client;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Services\RconService;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;

class CharacterManagementController extends Controller
{
    public function __construct(
        private readonly RconService $rconService
    ) {
    }

    public function makePrimary(Request $request, User $user): RedirectResponse
    {
        $accountId = $this->accountId();

        DB::transaction(function () use ($accountId, $user) {
            $account = DB::table('accounts')
                ->where('id', $accountId)
                ->lockForUpdate()
                ->first();

            abort_unless($account, 404);

            $currentPrimary = DB::table('account_characters')
                ->where('account_id', $accountId)
                ->where('is_primary', 1)
                ->whereNull('archived_at')
                ->lockForUpdate()
                ->first();

            $target = DB::table('account_characters')
                ->where('account_id', $accountId)
                ->where('user_id', $user->id)
                ->whereNull('archived_at')
                ->lockForUpdate()
                ->first();

            abort_unless($currentPrimary && $target, 403);

            if ((int) $currentPrimary->user_id === (int) $user->id) {
                return;
            }

            $currentPrimaryUser = DB::table('users')
                ->where('id', $currentPrimary->user_id)
                ->first();

            abort_unless($currentPrimaryUser, 404);

            DB::table('account_characters')
                ->where('account_id', $accountId)
                ->whereNull('archived_at')
                ->update([
                    'is_primary' => 0,
                ]);

            DB::table('account_characters')
                ->where('account_id', $accountId)
                ->where('user_id', $user->id)
                ->whereNull('archived_at')
                ->update([
                    'is_primary' => 1,
                ]);

            DB::table('users')
                ->where('id', $currentPrimary->user_id)
                ->update([
                    'mail' => null,
                    'password' => Hash::make(Str::random(64)),
                    'two_factor_secret' => null,
                    'two_factor_recovery_codes' => null,
                    'two_factor_confirmed' => 0,
                    'two_factor_confirmed_at' => null,
                ]);

            DB::table('users')
                ->where('id', $user->id)
                ->update([
                    'mail' => $account->email,
                    'password' => $account->password,
                    'mail_verified' => $currentPrimaryUser->mail_verified,
                    'two_factor_secret' => $account->two_factor_secret,
                    'two_factor_recovery_codes' => $account->two_factor_recovery_codes,
                    'two_factor_confirmed' => (int) $account->two_factor_confirmed,
                    'two_factor_confirmed_at' => $account->two_factor_confirmed_at,
                ]);
        });

        $newPrimary = User::findOrFail($user->id);

        Auth::login($newPrimary);
        $request->session()->regenerate();

        return to_route('character-select')
            ->with('success', $newPrimary->username . ' ahora es tu personaje principal.');
    }

    public function updateMotto(Request $request, User $user): RedirectResponse
    {
        $accountId = $this->accountId();

        $belongsToAccount = DB::table('account_characters')
            ->where('account_id', $accountId)
            ->where('user_id', $user->id)
            ->whereNull('archived_at')
            ->exists();

        abort_unless($belongsToAccount, 403);

        $validated = $request->validate([
            'motto' => [
                'nullable',
                'string',
                'max:127',
            ],
        ]);

        $motto = (string) ($validated['motto'] ?? '');

        if ((bool) $user->online) {
            if (! $this->rconService->isConnected()) {
                return back()->withErrors(
                    'El personaje está conectado y RCON no está disponible. No se ha cambiado la misión.'
                );
            }

            $this->rconService->setMotto($user, $motto);
        }

        DB::table('users')
            ->where('id', $user->id)
            ->update([
                'motto' => $motto,
            ]);

        return to_route('character-select')
            ->with('success', 'Misión de ' . $user->username . ' actualizada.');
    }

    public function archive(User $user): RedirectResponse
    {
        $accountId = $this->accountId();

        $mapping = DB::table('account_characters')
            ->where('account_id', $accountId)
            ->where('user_id', $user->id)
            ->whereNull('archived_at')
            ->first();

        abort_unless($mapping, 403);

        if ((int) $mapping->is_primary === 1) {
            return back()->withErrors(
                'No puedes eliminar el personaje principal. Elige otro principal primero.'
            );
        }

        if ((bool) $user->online) {
            if (! $this->rconService->isConnected()) {
                return back()->withErrors(
                    'El personaje está conectado y RCON no está disponible. No se ha eliminado.'
                );
            }

            $this->rconService->disconnectUser($user);
        }

        DB::transaction(function () use ($accountId, $user) {
            $mapping = DB::table('account_characters')
                ->where('account_id', $accountId)
                ->where('user_id', $user->id)
                ->whereNull('archived_at')
                ->lockForUpdate()
                ->first();

            abort_unless($mapping, 403);

            if ((int) $mapping->is_primary === 1) {
                abort(409, 'El personaje principal no puede archivarse.');
            }

            DB::table('account_characters')
                ->where('account_id', $accountId)
                ->where('user_id', $user->id)
                ->update([
                    'slot' => null,
                    'is_primary' => 0,
                    'archived_at' => now(),
                ]);

            DB::table('users')
                ->where('id', $user->id)
                ->update([
                    'online' => '0',
                    'auth_ticket' => '',
                    'mail' => null,
                    'password' => Hash::make(Str::random(64)),
                    'two_factor_secret' => null,
                    'two_factor_recovery_codes' => null,
                    'two_factor_confirmed' => 0,
                    'two_factor_confirmed_at' => null,
                ]);
        });

        return to_route('character-select')
            ->with('success', $user->username . ' ha sido eliminado de tus personajes activos.');
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
}