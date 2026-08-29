<?php

namespace App\Http\Controllers\User;

use App\Http\Controllers\Controller;
use App\Http\Requests\PasswordSettingsFormRequest;
use Illuminate\Http\RedirectResponse;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\View\View;

class PasswordSettingsController extends Controller
{
    public function edit(): View
    {
        return view('user.settings.password');
    }

    public function update(PasswordSettingsFormRequest $request): RedirectResponse
    {
        $user = Auth::user();

        abort_if(!$user, 403, 'Usuario no encontrado.');

        $accountId = DB::table('account_characters')
            ->where('user_id', $user->id)
            ->value('account_id');

        abort_if(!$accountId, 403, 'Cuenta no encontrada.');

        $passwordHash = Hash::make(
            $request->input('password')
        );

        DB::transaction(function () use ($accountId, $user, $passwordHash) {
            DB::table('accounts')
                ->where('id', $accountId)
                ->update([
                    'password' => $passwordHash,
                    'updated_at' => now(),
                ]);

            /*
             * Compatibilidad temporal:
             * Fortify y otras partes de Atom todavía pueden
             * validar contraseña contra Auth::user().
             */
            $user->update([
                'password' => $passwordHash,
            ]);
        });

        return redirect()
            ->back()
            ->with('success', __('Your password has been changed!'));
    }
}