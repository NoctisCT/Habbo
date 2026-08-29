<?php

namespace App\Http\Controllers\Client;

use App\Exceptions\CreditTransactionException;
use App\Http\Controllers\Controller;
use App\Services\CreditTransactionService;
use App\Models\User;
use App\Rules\WebsiteWordfilterRule;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;
use Illuminate\Validation\Rule;
use Illuminate\Validation\ValidationException;
use Illuminate\View\View;

class CharacterSelectController extends Controller
{
    private const SLOT_PRICE = 50;

    public function __invoke(): View
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
                'ac.is_primary',
            ]);

        $archivedCount = DB::table('account_characters')
            ->where('account_id', $accountId)
            ->whereNotNull('archived_at')
            ->count();

        return view('client.characters', [
            'characters' => $characters,
            'slotsUsed' => $characters->count(),
            'slotsTotal' => (int) $account->character_slots,
            'archivedCount' => $archivedCount,
            'slotPrice' => self::SLOT_PRICE,
        ]);
    }

    public function store(Request $request): RedirectResponse
    {
        $validated = $request->validate([
            'username' => [
                'required',
                'string',
                sprintf('regex:%s', setting('username_regex')),
                'max:25',
                Rule::unique('users', 'username'),
                new WebsiteWordfilterRule(),
            ],
        ]);

        $accountId = $this->accountId();
        $ip = $request->ip();

        DB::transaction(function () use ($validated, $accountId, $ip) {
            $account = DB::table('accounts')
                ->where('id', $accountId)
                ->lockForUpdate()
                ->first();

            abort_unless($account, 404);

            $slotsTotal = (int) $account->character_slots;

            $usedSlots = DB::table('account_characters')
                ->where('account_id', $accountId)
                ->whereNull('archived_at')
                ->whereNotNull('slot')
                ->pluck('slot')
                ->map(fn ($slot) => (int) $slot)
                ->all();

            $slot = null;

            for ($i = 1; $i <= $slotsTotal; $i++) {
                if (! in_array($i, $usedSlots, true)) {
                    $slot = $i;
                    break;
                }
            }

            if ($slot === null) {
                throw ValidationException::withMessages([
                    'username' => 'No tienes slots de personaje libres.',
                ]);
            }

            $user = User::create([
                'username' => $validated['username'],
                'mail' => null,
                'password' => Hash::make(Str::random(64)),
                'account_created' => time(),
                'last_login' => time(),
                'motto' => setting('start_motto'),
                'look' => setting('start_look'),
                'credits' => setting('start_credits'),
                'ip_register' => $ip,
                'ip_current' => $ip,
                'auth_ticket' => '',
                'home_room' => (int) setting('hotel_home_room'),
            ]);

            $user->update([
                'referral_code' => sprintf('%s%s', $user->id, Str::random(8)),
            ]);

            DB::table('account_characters')->insert([
                'account_id' => $accountId,
                'user_id' => $user->id,
                'slot' => $slot,
                'is_primary' => 0,
                'created_at' => now(),
                'archived_at' => null,
            ]);
        });

        return to_route('character-select')
            ->with('success', 'Personaje creado correctamente.');
    }


    public function purchaseSlot(
        Request $request,
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
        $price = self::SLOT_PRICE;

        try {
            $creditTransactions->debitAndRun(
                $accountId,
                (int) $validated['payer_user_id'],
                $price,
                'slot_purchase',
                (string) $validated['purchase_id'],
                [
                    'benefit' => 'account_character_slot',
                ],
                function ($payer, array $payment) use ($accountId, $price) {
                    $account = DB::table('accounts')
                        ->where('id', $accountId)
                        ->lockForUpdate()
                        ->first();

                    abort_unless($account, 404);

                    $slotsBefore = (int) $account->character_slots;
                    $slotsAfter = $slotsBefore + 1;

                    DB::table('accounts')
                        ->where('id', $accountId)
                        ->update([
                            'character_slots' => $slotsAfter,
                            'updated_at' => now(),
                        ]);

                    return [
                        'result' => [
                            'slots_before' => $slotsBefore,
                            'slots_after' => $slotsAfter,
                        ],
                        'metadata' => [
                            'slots_before' => $slotsBefore,
                            'slots_after' => $slotsAfter,
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

        return to_route('character-select')
            ->with(
                'success',
                "Slot adicional comprado por {$price} cr\u{00E9}ditos."
            );
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