<?php

namespace App\Actions\Fortify;

use App\Actions\Fortify\Rules\PasswordValidationRules;
use App\Models\Miscellaneous\WebsiteBetaCode;
use App\Models\User;
use App\Providers\RouteServiceProvider;
use App\Rules\BetaCodeRule;
use App\Rules\GoogleRecaptchaRule;
use App\Rules\WebsiteWordfilterRule;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Str;
use Illuminate\Validation\Rule;
use Illuminate\Validation\ValidationException;
use Laravel\Fortify\Contracts\CreatesNewUsers;
use RyanChandler\LaravelCloudflareTurnstile\Rules\Turnstile;

class CreateNewUser implements CreatesNewUsers
{
    use PasswordValidationRules;

    /**
     * Validate and create a newly registered account
     * together with its first/main character.
     */
    public function create(array $input)
    {
        if (setting('disable_registration')) {
            throw ValidationException::withMessages([
                'registration' => __('Registration is disabled.'),
            ]);
        }

        $ip = request()?->ip();

        if (!filter_var($ip, FILTER_VALIDATE_IP, FILTER_FLAG_IPV4 | FILTER_FLAG_IPV6)) {
            throw ValidationException::withMessages([
                'registration' => __('Your IP address seems to be invalid'),
            ]);
        }

        /*
         * Count ACCOUNTS using this IP, not characters.
         * Only the primary character represents the account here.
         */
        $matchingIpCount = DB::table('account_characters')
            ->join('users', 'users.id', '=', 'account_characters.user_id')
            ->where('account_characters.is_primary', 1)
            ->where(function ($query) use ($ip) {
                $query
                    ->where('users.ip_current', $ip)
                    ->orWhere('users.ip_register', $ip);
            })
            ->distinct()
            ->count('account_characters.account_id');

        if ($matchingIpCount >= (int) setting('max_accounts_per_ip')) {
            throw ValidationException::withMessages([
                'registration' => __('You have reached the max amount of allowed account'),
            ]);
        }

        $this->validate($input);

        $email = Str::lower(trim($input['mail']));
        $passwordHash = Hash::make($input['password']);

        $user = DB::transaction(function () use ($input, $ip, $email, $passwordHash) {
            /*
             * Commercial/security identity.
             */
            $accountId = DB::table('accounts')->insertGetId([
                'email' => $email,
                'password' => $passwordHash,
                'character_slots' => 3,
                'created_at' => now(),
                'updated_at' => now(),
            ]);

            /*
             * First gameplay identity.
             *
             * During the transitional phase we keep the account email and
             * password hash on the primary User too, because parts of Atom
             * still expect those fields to exist on Auth::user().
             */
            $user = User::create([
                'username' => $input['username'],
                'mail' => $email,
                'password' => $passwordHash,
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

            /*
             * Slot 1 is always the initial/main character.
             */
            DB::table('account_characters')->insert([
                'account_id' => $accountId,
                'user_id' => $user->id,
                'slot' => 1,
                'is_primary' => 1,
                'created_at' => now(),
            ]);

            return $user;
        });

        if (setting('requires_beta_code')) {
            WebsiteBetaCode::where('code', '=', $input['beta_code'])->update([
                'user_id' => $user->id,
            ]);
        }

        // Referral
        if (isset($input['referral_code'])) {
            $referralUser = User::query()
                ->where('referral_code', '=', $input['referral_code'])
                ->first();

            if (is_null($referralUser)) {
                return redirect(RouteServiceProvider::HOME);
            }

            // If same IP skip referral incrementation
            if ($referralUser->ip_current == $user->ip_current || $referralUser->ip_register == $user->ip_register) {
                return redirect(RouteServiceProvider::HOME);
            }

            $referralUser->referrals()->updateOrCreate(
                ['user_id' => $referralUser->id],
                [
                    'referrals_total' => $referralUser->referrals != null
                        ? $referralUser->referrals->referrals_total += 1
                        : 1,
                ]
            );

            $referralUser->userReferrals()->create([
                'referred_user_id' => $user->id,
                'referred_user_ip' => $ip,
            ]);
        }

        if (setting('enable_discord_webhook') === '1') {
            $this->sendDiscordWebhook(
                $user->username,
                $user->ip_register,
                $email
            );
        }

        return $user;
    }

    private function validate(array $inputs): array
    {
        $rules = [
            'username' => [
                'required',
                'string',
                sprintf('regex:%s', setting('username_regex')),
                'max:25',
                Rule::unique('users', 'username'),
                new WebsiteWordfilterRule,
            ],

            'mail' => [
                'required',
                'string',
                'email',
                'max:190',
                Rule::unique('accounts', 'email'),
                Rule::unique('users', 'mail'),
            ],

            'password' => $this->passwordRules(),

            'beta_code' => [
                'sometimes',
                'string',
                new BetaCodeRule,
            ],

            'terms' => [
                'required',
                'accepted',
            ],

            'g-recaptcha-response' => [
                'sometimes',
                'string',
                new GoogleRecaptchaRule(),
            ],

            'cf-turnstile-response' => [
                app(Turnstile::class),
            ],
        ];

        $messages = [
            'g-recaptcha-response.required' => __('The Google recaptcha must be completed'),
            'g-recaptcha-response.string' => __('The google recaptcha was submitted with an invalid type'),
        ];

        return Validator::make($inputs, $rules, $messages)->validate();
    }

    private function sendDiscordWebhook(string $username, string $ip, string $email): void
    {
        if (setting('discord_webhook_url') === '') {
            Log::error(
                'Discord webhook url not provided',
                ['Please provide a discord webhook url before being able to send any webhook requests.']
            );

            return;
        }

        $request = Http::asJson()->post(setting('discord_webhook_url'), [
            'username' => sprintf('%s Bot', setting('hotel_name')),
            'content' => "User: {$username} has just registered, with the IP: {$ip} and E-mail: {$email}",
        ]);

        if (!$request->successful()) {
            Log::error('Failed to send Discord webhook notification', [
                'username' => $username,
                'ip' => $ip,
                'email' => $email,
                'response_status' => $request->status(),
                'response_body' => $request->body(),
            ]);
        }
    }
}