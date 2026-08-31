<?php

use App\Actions\Fortify\Controllers\TwoFactorAuthenticatedSessionController;
use App\Http\Controllers\Articles\ArticleController;
use App\Http\Controllers\Articles\WebsiteArticleCommentsController;
use App\Http\Controllers\Client\FlashController;
use App\Http\Controllers\Client\NitroController;
use App\Http\Controllers\Client\CharacterSelectController;
use App\Http\Controllers\Client\CharacterNitroController;
use App\Http\Controllers\Community\LeaderboardController;
use App\Http\Controllers\Community\PhotosController;
use App\Http\Controllers\Community\RoomController;
use App\Http\Controllers\Community\Staff\StaffApplicationsController;
use App\Http\Controllers\Community\Staff\StaffController;
use App\Http\Controllers\Community\Staff\WebsiteTeamsController;
use App\Http\Controllers\Community\WebsiteRareValuesController;
use App\Http\Controllers\Help\HelpCenterController;
use App\Http\Controllers\Help\TicketController;
use App\Http\Controllers\Help\TicketReplyController;
use App\Http\Controllers\Help\WebsiteRulesController;
use App\Http\Controllers\Miscellaneous\HomeController;
use App\Http\Controllers\Miscellaneous\InstallationController;
use App\Http\Controllers\Miscellaneous\LocaleController;
use App\Http\Controllers\Miscellaneous\LogoGeneratorController;
use App\Http\Controllers\Miscellaneous\MaintenanceController;
use App\Http\Controllers\Shop\LegacyEconomyDisabledController;
use App\Http\Controllers\Shop\PaypalController;
use App\Http\Controllers\Shop\ShopController;
use App\Http\Controllers\Shop\ShopVoucherController;
use App\Http\Controllers\User\AccountSettingsController;
use App\Http\Controllers\User\BannedController;
use App\Http\Controllers\User\ForgotPasswordController;
use App\Http\Controllers\User\GuestbookController;
use App\Http\Controllers\User\MeController;
use App\Http\Controllers\User\PasswordSettingsController;
use App\Http\Controllers\User\ProfileController;
use App\Http\Controllers\User\ReferralController;
use App\Http\Controllers\User\TwoFactorAuthenticationController;
use App\Http\Controllers\User\UserReferralController;
use Illuminate\Support\Facades\Route;
use Laravel\Fortify\Features;
use Laravel\Fortify\Http\Controllers\RegisteredUserController;

// Language route
Route::get('/language/{locale}', LocaleController::class)->name('language.select');

// Installation routes
Route::prefix('installation')->controller(InstallationController::class)->group(function () {
    Route::get('/', 'index')->name('installation.index');
    Route::get('/step/{step}', 'showStep')->name('installation.show-step');

    Route::post('/start-installation', 'storeInstallationKey')->name('installation.start-installation');
    Route::post('/restart-installation', 'restartInstallation')->name('installation.restart');
    Route::post('/previous-step', 'previousStep')->name('installation.previous-step');
    Route::post('/save-step', 'saveStepSettings')->name('installation.save-step');
    Route::post('/complete', 'completeInstallation')->name('installation.complete');
});

// All routes within this group is protected by maintenance, ban and 2FA middleware
Route::middleware(['maintenance', 'check.ban', 'force.staff.2fa'])->group(function () {
    // Maintenance route
    Route::get('/maintenance', MaintenanceController::class)->name('maintenance.show');

    // Banned route
    Route::get('/banned', BannedController::class)->name('banned.show');

    // Exceptions to the 2FA check and must only be visited if not logged in
    Route::middleware(['guest', 'throttle:15,1'])->withoutMiddleware('force.staff.2fa')->group(function () {
        Route::get('/login', static fn() => to_route('welcome'))->name('login');
        Route::get('/', HomeController::class)->name('welcome');

        Route::get('/register', [RegisteredUserController::class, 'create']);

        Route::post('/register', [RegisteredUserController::class, 'store'])
            ->name('register');

        Route::get('/register/{referral_code}', UserReferralController::class)->name('register.referral');

        // Password
        Route::get('forgot-password', ForgotPasswordController::class)->name('forgot.password.get');
        Route::post('forgot-password', [ForgotPasswordController::class, 'submitForgetPassword'])->name('forgot.password.post');
        Route::get('reset-password/{token}', [ForgotPasswordController::class, 'showResetPassword'])->name('reset.password.get');
        Route::post('reset-password/{token}', [ForgotPasswordController::class, 'submitResetPassword'])->name('reset.password.post');
    });

    // Can only be accessed if logged in
    Route::middleware('auth')->group(function () {
        Route::prefix('user')->group(function () {
            Route::get('/me', MeController::class)->name('me.show');
            Route::get('/claim/referral-reward', ReferralController::class)->name('claim.referral-reward');

            // User settings routes
            Route::prefix('settings')->group(function () {
                Route::get('/account', [AccountSettingsController::class, 'edit'])->name('settings.account.show');
                Route::put('/account', [AccountSettingsController::class, 'update'])->name('settings.account.update');

                Route::get('/password', [PasswordSettingsController::class, 'edit'])->name('settings.password.show');
                Route::put('/password', [PasswordSettingsController::class, 'update'])->name('settings.password.update');

                Route::get('/session-logs', [AccountSettingsController::class, 'sessionLogs'])->name('settings.session-logs');

                Route::get('/two-factor', [TwoFactorAuthenticationController::class, 'index'])->name('settings.two-factor');
                Route::post('/2fa-verify', [TwoFactorAuthenticationController::class, 'verify'])->name('two-factor.verify');
            });
        });

        // Profiles
        Route::get('/profile/{user:username}', ProfileController::class)->name('profile.show');
        Route::post('/profile/{user}/guestbook', [GuestbookController::class, 'store'])->name('guestbook.store');
        Route::delete('/profile/{user}/{guestbook}/delete', [GuestbookController::class, 'destroy'])->name('guestbook.destroy');

        // Community routes
        Route::prefix('community')->group(function () {
            Route::get('/photos', PhotosController::class)->name('photos.index');

            // Allowed to be visited without being logged in
            Route::withoutMiddleware('auth')->group(function () {
                Route::get('/articles', [ArticleController::class, 'index'])->name('article.index');
                Route::get('/article/{article:slug}', [ArticleController::class, 'show'])->name('article.show');
            });

            Route::get('/staff', StaffController::class)->name('staff.index');
            Route::get('/teams', WebsiteTeamsController::class)->name('teams.index');

            Route::get('/staff-applications', [StaffApplicationsController::class, 'index'])->name('staff-applications.index');
            Route::get('/staff-applications/{position}', [StaffApplicationsController::class, 'show'])->name('staff-applications.show');
            Route::post('/staff-applications/{position}', [StaffApplicationsController::class, 'store'])->name('staff-applications.store');

            Route::post('/article/{article:slug}/comment', [WebsiteArticleCommentsController::class, 'store'])->name('article.comment.store');
            Route::delete('/article/{comment}/comment', [WebsiteArticleCommentsController::class, 'destroy'])->name('article.comment.destroy');
            Route::post('/article/{article:slug}/toggle-reaction', [ArticleController::class, 'toggleReaction'])
                ->name('article.toggle-reaction')
                ->middleware('throttle:30,1');
        });

        // Leaderboard routes
        Route::get('/leaderboard', LeaderboardController::class)->name('leaderboard.index');

        // Shop routes
        Route::prefix('shop')->group(function () {
            Route::get('/{category:slug?}', LegacyEconomyDisabledController::class)->name('shop.index');

            Route::post('/purchase/{package}', LegacyEconomyDisabledController::class)->name('shop.buy');
            Route::post('/voucher', LegacyEconomyDisabledController::class)->name('shop.use-voucher');
        });

        // Help center
        Route::prefix('help-center')->as('help-center.')->withoutMiddleware('check.ban')->group(function () {
            Route::get('/', HelpCenterController::class)->name('index');

            Route::prefix('tickets')->as('ticket.')->group(function () {
                Route::get('/create', [TicketController::class, 'create'])->name('create');
                Route::post('/store', [TicketController::class, 'store'])->name('store');

                Route::get('/show/{ticket}', [TicketController::class, 'show'])->name('show');
                Route::get('/edit/{ticket}', [TicketController::class, 'edit'])->name('edit');
                Route::put('/edit/{ticket}', [TicketController::class, 'update'])->name('update');
                Route::delete('/delete/{ticket}', [TicketController::class, 'destroy'])->name('destroy');

                Route::put('/toggle-status/{ticket}', [TicketController::class, 'toggleTicketStatus'])->name('toggle-status');

                Route::post('/reply/{ticket}/store', [TicketReplyController::class, 'store'])->name('reply.store');
                Route::delete('/reply/{reply}/delete', [TicketController::class, 'destroy'])->name('reply.destroy');

                // All open tickets
                Route::get('/all', [TicketController::class, 'index'])->name('index');
            });

            // Rules
            Route::get('/rules', WebsiteRulesController::class)->name('rules.index')->withoutMiddleware('auth');
        });

        // Paypal routes
        Route::prefix('paypal')->group(function () {
            Route::get('/process-transaction', LegacyEconomyDisabledController::class)->name('paypal.process-transaction');
            Route::get('/successful-transaction', LegacyEconomyDisabledController::class)->name('paypal.successful-transaction');
            Route::get('/cancelled-transaction', LegacyEconomyDisabledController::class)->name('paypal.cancelled-transaction');
        });

        // Rare values routes
        Route::get('/values', [WebsiteRareValuesController::class, 'index'])->name('values.index');
        Route::post('/values/search', [WebsiteRareValuesController::class, 'search'])->name('values.search');
        Route::get('/values/category/{category}', [WebsiteRareValuesController::class, 'category'])->name('values.category');
        Route::get('/values/{value}', [WebsiteRareValuesController::class, 'value'])->name('values.value');

        // Client route
        Route::prefix('game')->middleware(['findretros.redirect', 'vpn.checker'])->group(function () {
            Route::get('/nitro', NitroController::class)->name('nitro-client');
            Route::get('/characters', CharacterSelectController::class)->name('character-select');
            Route::get('/characters/archived', [\App\Http\Controllers\Client\CharacterRestoreController::class, 'index'])->name('character-archived-index');
            Route::post('/characters', [CharacterSelectController::class, 'store'])->name('character-create');
            Route::post('/characters/slots', [CharacterSelectController::class, 'purchaseSlot'])->name('character-slot-purchase');
            Route::patch('/characters/{user}/primary', [\App\Http\Controllers\Client\CharacterManagementController::class, 'makePrimary'])->name('character-primary');
            Route::patch('/characters/{user}/motto', [\App\Http\Controllers\Client\CharacterManagementController::class, 'updateMotto'])->name('character-motto');
            Route::delete('/characters/{user}', [\App\Http\Controllers\Client\CharacterManagementController::class, 'archive'])->name('character-archive');
            Route::post('/characters/{user}/restore', [\App\Http\Controllers\Client\CharacterRestoreController::class, 'paid'])->name('character-restore-paid');
            Route::get('/nitro/character/{user}', CharacterNitroController::class)->name('nitro-character');
            Route::get('/flash', FlashController::class)->name('flash-client');
        });

        Route::prefix('administration')->group(function () {
            Route::get('/archived-characters', [\App\Http\Controllers\Client\CharacterRestoreController::class, 'adminIndex'])->name('admin.archived-characters');
            Route::post('/archived-characters/{user}/restore', [\App\Http\Controllers\Client\CharacterRestoreController::class, 'adminRestore'])->name('admin.character-restore');
        });

        // Logo generator
        Route::get('/logo-generator', [LogoGeneratorController::class, 'index'])->name('logo-generator.index');
        Route::post('/logo-generator', [LogoGeneratorController::class, 'store'])->name('store.generated-logo');
    });
});

if (Features::enabled(Features::twoFactorAuthentication())) {
    $twoFactorLimiter = config('fortify.limiters.two-factor');

    Route::post('/two-factor-challenge', [TwoFactorAuthenticatedSessionController::class, 'store'])
        ->middleware(
            array_filter([
                'guest:' . config('fortify.guard'),
                $twoFactorLimiter ? 'throttle:' . $twoFactorLimiter : null,
            ])
        );
}

// HOLO-CREATOR-BADGES-ROUTES
Route::middleware([
    'maintenance',
    'check.ban',
    'force.staff.2fa',
    'auth',
])->prefix('marketplace')->as('marketplace.')->group(function () {
    Route::get(
        '/badges',
        [\App\Http\Controllers\Marketplace\BadgeCreatorController::class, 'index']
    )->name('badges.index');

    Route::post(
        '/badges/preview',
        [\App\Http\Controllers\Marketplace\BadgeCreatorController::class, 'preview']
    )->middleware('throttle:20,1')->name('badges.preview');

    Route::post(
        '/badges',
        [\App\Http\Controllers\Marketplace\BadgeCreatorController::class, 'store']
    )->middleware('throttle:10,1')->name('badges.store');
});
Route::post(
    '/marketplace/badges/{creatorBadge}/gift',
    [\App\Http\Controllers\Marketplace\BadgeGiftController::class, 'store']
)
    ->middleware([
        'auth',
        'throttle:10,1',
    ])
    ->name('marketplace.badges.gift');

Route::post(
    '/marketplace/badges/seller-license',
    [
        \App\Http\Controllers\Marketplace\BadgeSellerApplicationController::class,
        'store',
    ]
)
    ->middleware([
        'auth',
        'throttle:3,1',
    ])
    ->name(
        'marketplace.badges.seller-license.apply'
    );
Route::post(
    '/marketplace/badges/{creatorBadge}/listing',
    [
        \App\Http\Controllers\Marketplace\BadgeMarketplaceListingController::class,
        'store',
    ]
)
    ->middleware([
        'auth',
        'throttle:20,1',
    ])
    ->name(
        'marketplace.badges.listing.store'
    );

Route::delete(
    '/marketplace/badges/{creatorBadge}/listing',
    [
        \App\Http\Controllers\Marketplace\BadgeMarketplaceListingController::class,
        'deactivate',
    ]
)
    ->middleware([
        'auth',
        'throttle:20,1',
    ])
    ->name(
        'marketplace.badges.listing.deactivate'
    );

Route::post(
    '/marketplace/badges/{creatorBadge}/listing/reactivate',
    [
        \App\Http\Controllers\Marketplace\BadgeMarketplaceListingController::class,
        'reactivate',
    ]
)
    ->middleware([
        'auth',
        'throttle:20,1',
    ])
    ->name(
        'marketplace.badges.listing.reactivate'
    );
Route::post(
    '/marketplace/badges/listings/{listing}/purchase',
    [
        \App\Http\Controllers\Marketplace\BadgeMarketplacePurchaseController::class,
        'store',
    ]
)
    ->middleware([
        'auth',
        'throttle:10,1',
    ])
    ->name(
        'marketplace.badges.purchase'
    );
Route::post(
    '/marketplace/badges/{creatorBadge}/hotel-distribution',
    [
        \App\Http\Controllers\Marketplace\BadgeHotelDistributionController::class,
        'store',
    ]
)
    ->middleware([
        'auth',
        'throttle:5,1',
    ])
    ->name(
        'marketplace.badges.hotel-distribution.grant'
    );
// /HOLO-CREATOR-BADGES-ROUTES

/*
|--------------------------------------------------------------------------
| Notificaciones de cuenta
|--------------------------------------------------------------------------
*/
Route::middleware('auth')->group(function () {
    Route::get(
        '/notifications',
        [\App\Http\Controllers\AccountNotificationController::class, 'index']
    )->name('notifications.index');

    Route::post(
        '/notifications/{notification}/open',
        [\App\Http\Controllers\AccountNotificationController::class, 'open']
    )->name('notifications.open');

    Route::post(
        '/notifications/mark-all-read',
        [\App\Http\Controllers\AccountNotificationController::class, 'markAllRead']
    )->name('notifications.mark-all-read');
});
