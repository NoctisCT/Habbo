<?php

namespace App\Http\Controllers\Client;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\View\View;

class CharacterNitroController extends Controller
{
    public function __invoke(User $user): View
    {
        $accountId = DB::table('account_characters')
            ->where('user_id', Auth::id())
            ->whereNull('archived_at')
            ->value('account_id');

        abort_unless($accountId, 403);

        $belongsToAccount = DB::table('account_characters')
            ->where('account_id', $accountId)
            ->where('user_id', $user->id)
            ->whereNull('archived_at')
            ->exists();

        abort_unless($belongsToAccount, 403);

        $user->update([
            'ip_current' => request()->ip(),
        ]);

        return view('client.nitro', [
            'sso' => $user->ssoTicket(),
        ]);
    }
}