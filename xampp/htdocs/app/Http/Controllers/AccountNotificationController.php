<?php

namespace App\Http\Controllers;

use App\Models\AccountNotification;
use Illuminate\Http\RedirectResponse;
use Illuminate\Support\Facades\DB;

class AccountNotificationController extends Controller
{
    public function index()
    {
        $accountId = $this->currentAccountId();

        $notifications = AccountNotification::query()
            ->where('account_id', $accountId)
            ->latest('created_at')
            ->paginate(25);

        $unreadCount = AccountNotification::query()
            ->where('account_id', $accountId)
            ->whereNull('read_at')
            ->count();

        return view(
            'notifications.index',
            compact(
                'notifications',
                'unreadCount'
            )
        );
    }
    public function open(
        AccountNotification $notification
    ): RedirectResponse {
        $accountId = $this->currentAccountId();

        abort_unless(
            (int) $notification->account_id === $accountId,
            403
        );

        if ($notification->read_at === null) {
            $notification->forceFill([
                'read_at' => now(),
            ])->save();
        }

        $url = trim((string) $notification->url);

        if ($url !== '' && str_starts_with($url, '/')) {
            return redirect($url);
        }

        return redirect()->back();
    }

    public function markAllRead(): RedirectResponse
    {
        $accountId = $this->currentAccountId();

        AccountNotification::query()
            ->where('account_id', $accountId)
            ->whereNull('read_at')
            ->update([
                'read_at' => now(),
                'updated_at' => now(),
            ]);

        return redirect()->back();
    }

    private function currentAccountId(): int
    {
        $userId = (int) auth()->id();

        abort_if($userId <= 0, 401);

        $accountId = DB::table('account_characters')
            ->where('user_id', $userId)
            ->whereNull('archived_at')
            ->value('account_id');

        abort_if($accountId === null, 403);

        return (int) $accountId;
    }
}
