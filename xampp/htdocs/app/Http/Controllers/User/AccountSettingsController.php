<?php

namespace App\Http\Controllers\User;

use App\Http\Controllers\Controller;
use App\Http\Requests\AccountSettingsFormRequest;
use App\Services\RconService;
use App\Services\User\SessionService;
use App\Services\User\UserService;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;
use Illuminate\View\View;

class AccountSettingsController extends Controller
{
    public function __construct(
        private readonly SessionService $sessionService,
        private readonly UserService $userService,
        private readonly RconService $rconService
    ) {
    }

    public function edit(): View
    {
        $user = Auth::user()->load('settings:allow_name_change');

        $account = DB::table('accounts')
            ->join('account_characters', 'account_characters.account_id', '=', 'accounts.id')
            ->where('account_characters.user_id', $user->id)
            ->select('accounts.id', 'accounts.email')
            ->first();

        abort_if(!$account, 403, 'Cuenta no encontrada.');

        return view('user.settings.account', [
            'user' => $user,
            'account' => $account,
        ]);
    }

    public function sessionLogs(Request $request): View
    {
        $sessions = $this->sessionService->fetchSessionLogs($request);

        return view('user.settings.session-logs', [
            'logs' => $sessions,
        ]);
    }

    public function update(AccountSettingsFormRequest $request): RedirectResponse
    {
        $user = Auth::user();

        if ($user === null) {
            return redirect()->back()->withErrors('User not found');
        }

        $accountId = DB::table('account_characters')
            ->where('user_id', $user->id)
            ->value('account_id');

        abort_if(!$accountId, 403, 'Cuenta no encontrada.');

        $allowedNameChange =
            $user->settings?->allow_name_change &&
            $user->username !== $request->input('username');

        if (!$this->rconService->isConnected() && $user->online === '1') {
            return back()->withErrors('You must be offline to change your account settings');
        }

        if ($allowedNameChange) {
            $this->rconService->disconnectUser($user);

            $this->userService->updateField(
                $user,
                'username',
                $request->input('username')
            );
        }

        $email = Str::lower(trim((string) $request->input('mail')));

        $emailInUse = DB::table('accounts')
            ->where('email', $email)
            ->where('id', '<>', $accountId)
            ->exists();

        if ($emailInUse) {
            throw ValidationException::withMessages([
                'mail' => 'Este correo ya pertenece a otra cuenta.',
            ]);
        }

        DB::transaction(function () use ($accountId, $user, $email) {
            DB::table('accounts')
                ->where('id', $accountId)
                ->update([
                    'email' => $email,
                    'updated_at' => now(),
                ]);

            /*
             * Compatibilidad temporal con Atom:
             * el personaje principal conserva una copia del email.
             */
            if ($user->mail !== $email) {
                $user->update([
                    'mail' => $email,
                ]);
            }
        });

        if ($user->motto !== $request->input('motto')) {
            $this->rconService->setMotto(
                $user,
                $request->input('motto')
            );

            $this->userService->updateField(
                $user,
                'motto',
                $request->input('motto')
            );
        }

        return redirect()
            ->back()
            ->with('success', __('Your account settings has been updated'));
    }

    public function twoFactor(): View
    {
        return view('user.settings.two-factor');
    }
}