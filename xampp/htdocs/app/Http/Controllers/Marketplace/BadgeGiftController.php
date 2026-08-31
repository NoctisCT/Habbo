<?php

namespace App\Http\Controllers\Marketplace;

use App\Exceptions\CreditTransactionException;
use App\Http\Controllers\Controller;
use App\Services\BadgeGiftService;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;
use Throwable;

class BadgeGiftController extends Controller
{
    public function store(
        Request $request,
        int $creatorBadge,
        BadgeGiftService $gifts
    ): RedirectResponse {
        $validated = $request->validate([
            'payer_user_id' => [
                'required',
                'integer',
            ],
            'recipient_username' => [
                'required',
                'string',
                'min:1',
                'max:100',
            ],
            'purchase_id' => [
                'required',
                'uuid',
            ],
        ]);

        try {
            $result = $gifts->gift(
                $this->accountId(),
                $creatorBadge,
                (int) $validated['payer_user_id'],
                (string) $validated['recipient_username'],
                (string) $validated['purchase_id']
            );
        } catch (CreditTransactionException $exception) {
            throw ValidationException::withMessages([
                'gift' => $exception->getMessage(),
            ]);
        } catch (ValidationException $exception) {
            throw $exception;
        } catch (Throwable $exception) {
            report($exception);

            throw ValidationException::withMessages([
                'gift' =>
                    'No se pudo completar el regalo. No se ha confirmado la operación.',
            ]);
        }

        $message =
            'Placa entregada a ' .
            $result['recipient_username'] .
            '.';

        if ($result['charged']) {
            $message .=
                ' Se han cobrado ' .
                BadgeGiftService::PRICE .
                ' créditos.';
        } else {
            $message .=
                ' Se recuperó una entrega pendiente sin realizar un segundo cobro.';
        }

        return to_route(
            'marketplace.badges.index',
            [
                'tab' => 'mine',
            ]
        )
            ->with('success', $message);
    }

    private function accountId(): int
    {
        $accountId = DB::table(
            'account_characters'
        )
            ->where('user_id', Auth::id())
            ->whereNull('archived_at')
            ->value('account_id');

        abort_unless($accountId, 403);

        return (int) $accountId;
    }
}
