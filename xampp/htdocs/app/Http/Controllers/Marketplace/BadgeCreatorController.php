<?php

namespace App\Http\Controllers\Marketplace;

use App\Exceptions\CreditTransactionException;
use App\Http\Controllers\Controller;
use App\Services\BadgeMarketplaceListingService;
use App\Services\BadgePixelArtService;
use App\Services\BadgeSellerEligibilityService;
use App\Services\CreditTransactionService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;
use Illuminate\View\View;
use Throwable;

class BadgeCreatorController extends Controller
{
    private const PRICE = 10;
    private const MAX_PENDING = 3;
    private const PREVIEW_TTL_MINUTES = 30;

    public function index(
        Request $request,
        BadgeSellerEligibilityService $sellerEligibility,
        BadgeMarketplaceListingService $badgeListings
    ): View
    {
        $accountId = $this->accountId();

        $characters = DB::table('users')
            ->join(
                'account_characters as ac',
                'ac.user_id',
                '=',
                'users.id'
            )
            ->where('ac.account_id', $accountId)
            ->whereNull('ac.archived_at')
            ->orderBy('ac.slot')
            ->get([
                'users.id',
                'users.username',
                'users.credits',
                'users.online',
                'ac.is_primary',
            ]);

        $submissions = DB::table('badge_submissions')
            ->leftJoin(
                'users as creator',
                'creator.id',
                '=',
                'badge_submissions.creator_user_id'
            )
            ->where(
                'badge_submissions.account_id',
                $accountId
            )
            ->orderByDesc(
                'badge_submissions.created_at'
            )
            ->limit(30)
            ->get([
                'badge_submissions.*',
                'creator.username as creator_username',
            ]);


        foreach ($submissions as $submission) {
            $submission->created_at_display =
                \Carbon\Carbon::parse(
                    (string) $submission->created_at,
                    'UTC'
                )
                    ->setTimezone('Europe/Madrid')
                    ->format('d/m/Y H:i');

            $submission->preview_data_uri = null;

            if (
                ! empty($submission->processed_path) &&
                Storage::disk('local')->exists(
                    (string) $submission->processed_path
                )
            ) {
                $previewBytes =
                    Storage::disk('local')->get(
                        (string) $submission->processed_path
                    );

                $submission->preview_data_uri =
                    'data:image/gif;base64,' .
                    base64_encode($previewBytes);
            }
        }
        $creatorBadges = DB::table('creator_badges')
            ->leftJoin(
                'users as creator',
                'creator.id',
                '=',
                'creator_badges.creator_user_id'
            )
            ->leftJoin(
                'account_characters as ac',
                function ($join) use ($accountId) {
                    $join->on(
                        'ac.user_id',
                        '=',
                        'creator_badges.creator_user_id'
                    )
                        ->where(
                            'ac.account_id',
                            '=',
                            $accountId
                        );
                }
            )
            ->where(
                'creator_badges.account_id',
                $accountId
            )
            ->orderByDesc(
                'creator_badges.created_at'
            )
            ->get([
                'creator_badges.*',
                'creator.username as creator_username',
                'ac.archived_at as creator_archived_at',
            ]);

        $sellerLicense = DB::table(
            'badge_seller_licenses'
        )
            ->where(
                'account_id',
                $accountId
            )
            ->first();

        $approvedBadgeCount =
            $sellerEligibility
                ->approvedBadgeCount(
                    $accountId
                );

        $sellerMinimumBadges =
            $sellerEligibility
                ->minimumApprovedBadges();

        $isBadgeDesigner =
            $sellerEligibility
                ->isBadgeDesigner(
                    $accountId
                );

        $canSellBadges =
            $sellerEligibility
                ->canSell(
                    $accountId
                );

        $communitySlotsAvailable =
            $sellerEligibility
                ->communitySlotsAvailable();

        $marketSearch =
            mb_substr(
                trim(
                    (string)
                    $request->query(
                        'q',
                        ''
                    )
                ),
                0,
                60
            );

        $marketExplore =
            (string)
            $request->query(
                'explore',
                'all'
            );

        if (
            ! in_array(
                $marketExplore,
                [
                    'all',
                    'popular',
                    'new',
                ],
                true
            )
        ) {
            $marketExplore =
                'all';
        }

        $marketSort =
            (string)
            $request->query(
                'sort',
                'relevance'
            );

        if (
            ! in_array(
                $marketSort,
                [
                    'relevance',
                    'sales_desc',
                    'price_asc',
                    'price_desc',
                    'newest',
                    'oldest',
                ],
                true
            )
        ) {
            $marketSort =
                'relevance';
        }

        $marketCreatorUserId =
            (int)
            $request->query(
                'creator',
                0
            );

        if (
            $marketCreatorUserId <= 0
        ) {
            $marketCreatorUserId =
                null;
        }

        $marketListings =
            $badgeListings
                ->publicListings();

        $marketCreatorUsername =
            $marketCreatorUserId !== null
                ? DB::table('users')
                    ->where(
                        'id',
                        $marketCreatorUserId
                    )
                    ->value(
                        'username'
                    )
                : null;

        $sellerCatalog =
            $canSellBadges
                ? $badgeListings
                    ->sellerCatalog(
                        $accountId
                    )
                : collect();

        $sellerActiveListingCount =
            $canSellBadges
                ? $badgeListings
                    ->activeListingCount(
                        $accountId
                    )
                : 0;

        $sellerSalesStats =
            $badgeListings
                ->sellerSalesStats(
                    $accountId
                );

        $sellerSalesCount =
            $sellerSalesStats[
                'sales_count'
            ];

        $sellerCreditsEarned =
            $sellerSalesStats[
                'credits_earned'
            ];

        $sellerListingLimit =
            $sellerEligibility
                ->listingLimit(
                    $accountId
                );

        $sellerHasHabboClub =
            $sellerEligibility
                ->hasActiveHabboClub(
                    $accountId
                );

        $sellerEarningsMin =
            $badgeListings
                ->sellerEarningsMin();

        $sellerEarningsMax =
            $badgeListings
                ->sellerEarningsMax();

        $hotelCommission =
            $badgeListings
                ->hotelCommission();

        return view(
            'marketplace.badges',
            [
                'characters' => $characters,
                'submissions' => $submissions,
                'creatorBadges' => $creatorBadges,
                'sellerLicense' => $sellerLicense,
                'approvedBadgeCount' => $approvedBadgeCount,
                'sellerMinimumBadges' => $sellerMinimumBadges,
                'isBadgeDesigner' => $isBadgeDesigner,
                'canSellBadges' => $canSellBadges,
                'communitySlotsAvailable' => $communitySlotsAvailable,
                'marketListings' => $marketListings,
                'marketFilters' => [
                    'q' => $marketSearch,
                    'explore' => $marketExplore,
                    'sort' => $marketSort,
                    'creator' => $marketCreatorUserId,
                ],
                'marketCreatorUsername' => $marketCreatorUsername,
                'sellerCatalog' => $sellerCatalog,
                'sellerActiveListingCount' => $sellerActiveListingCount,
                'sellerSalesCount' => $sellerSalesCount,
                'sellerCreditsEarned' => $sellerCreditsEarned,
                'sellerListingLimit' => $sellerListingLimit,
                'sellerHasHabboClub' => $sellerHasHabboClub,
                'sellerEarningsMin' => $sellerEarningsMin,
                'sellerEarningsMax' => $sellerEarningsMax,
                'hotelCommission' => $hotelCommission,
                'badgePrice' => self::PRICE,
                'maxPending' => self::MAX_PENDING,
            ]
        );
    }

    public function preview(
        Request $request,
        BadgePixelArtService $pixelArt
    ): JsonResponse {
        $validated = $request->validate([
            'image' => [
                'required',
                'file',
                'image',
                'mimes:png,gif',
                'max:5120',
            ],
        ]);
$accountId = $this->accountId();

        $this->purgeExpiredPreviews($request);

        // La creación automática se ha retirado.
        // El servidor fuerza siempre el flujo de placa ya diseñada.
        $converted = $pixelArt->convert(
            $validated['image'],
            'pixelart',
            240,
            false
        );

        $token = (string) Str::uuid();

        $base = sprintf(
            'badge_creator/previews/%d/%s',
            $accountId,
            $token
        );

        $sourceExtension = strtolower(
            $validated['image']->guessExtension()
                ?: $validated['image']->extension()
                ?: 'bin'
        );

        $sourcePath =
            $base . '/source.' . $sourceExtension;

        $processedPath =
            $base . '/processed.gif';

        Storage::disk('local')->put(
            $sourcePath,
            file_get_contents(
                $validated['image']->getRealPath()
            )
        );

        Storage::disk('local')->put(
            $processedPath,
            $converted['image']
        );

        $previewData = [
            'account_id' => $accountId,
            'source_path' => $sourcePath,
            'processed_path' => $processedPath,
            'original_filename' =>
                $validated['image']
                    ->getClientOriginalName(),
            'mode' => $converted['mode'],
            'detail' => $converted['detail'],
            'background_removed' =>
                $converted['background_removed'],
            'created_at' => now()->timestamp,
        ];

        $request->session()->put(
            'badge_creator.preview.' . $token,
            $previewData
        );

        return response()->json([
            'ok' => true,
            'token' => $token,
            'preview' =>
                'data:image/gif;base64,' .
                base64_encode($converted['image']),
            'width' => $converted['width'],
            'height' => $converted['height'],
            'mode' => $converted['mode'],
            'detail' => $converted['detail'],
            'mime' => $converted['mime'],
            'extension' => $converted['extension'],
            'background_removed' =>
                $converted['background_removed'],
        ]);
    }

    public function store(
        Request $request,
        CreditTransactionService $creditTransactions
    ): RedirectResponse {
        $validated = $request->validate([
            'preview_token' => [
                'required',
                'uuid',
            ],
            'creator_user_id' => [
                'required',
                'integer',
            ],
            'payer_user_id' => [
                'required',
                'integer',
            ],
            'purchase_id' => [
                'required',
                'uuid',
            ],
            'badge_name' => [
                'required',
                'string',
                'min:2',
                'max:60',
            ],
            'badge_description' => [
                'required',
                'string',
                'min:3',
                'max:255',
            ],
        ], [
            'badge_name.required' =>
                'Debes indicar un nombre para la placa.',
            'badge_name.string' =>
                'El nombre de la placa no es válido.',
            'badge_name.min' =>
                'El nombre de la placa debe tener al menos 2 caracteres.',
            'badge_name.max' =>
                'El nombre de la placa no puede superar los 60 caracteres.',
            'badge_description.required' =>
                'Debes indicar una descripción para la placa.',
            'badge_description.string' =>
                'La descripción de la placa no es válida.',
            'badge_description.min' =>
                'La descripción de la placa debe tener al menos 3 caracteres.',
            'badge_description.max' =>
                'La descripción de la placa no puede superar los 255 caracteres.',
        ]);

        $accountId = $this->accountId();

        $pending = DB::table(
            'badge_submissions'
        )
            ->where('account_id', $accountId)
            ->where('status', 'pending')
            ->count();

        if ($pending >= self::MAX_PENDING) {
            throw ValidationException::withMessages([
                'badge_name' =>
                    'Ya tienes el máximo de ' .
                    self::MAX_PENDING .
                    ' placas pendientes de revisión.',
            ]);
        }

        $this->assertActiveCharacter(
            $accountId,
            (int) $validated['creator_user_id']
        );

        $previewKey =
            'badge_creator.preview.' .
            $validated['preview_token'];

        $preview = $request->session()->get(
            $previewKey
        );

        if (
            ! is_array($preview) ||
            (int) ($preview['account_id'] ?? 0)
                !== $accountId
        ) {
            throw ValidationException::withMessages([
                'preview_token' =>
                    'La vista previa ha caducado. Procesa la imagen de nuevo.',
            ]);
        }

        $createdAt =
            (int) ($preview['created_at'] ?? 0);

        if (
            $createdAt <= 0 ||
            now()->timestamp - $createdAt >
                self::PREVIEW_TTL_MINUTES * 60
        ) {
            $this->deletePreviewFiles($preview);
            $request->session()->forget(
                $previewKey
            );

            throw ValidationException::withMessages([
                'preview_token' =>
                    'La vista previa ha caducado. Procesa la imagen de nuevo.',
            ]);
        }

        if (
            ! Storage::disk('local')->exists(
                $preview['source_path']
            ) ||
            ! Storage::disk('local')->exists(
                $preview['processed_path']
            )
        ) {
            throw ValidationException::withMessages([
                'preview_token' =>
                    'No encuentro los archivos de la vista previa.',
            ]);
        }

        $purchaseId =
            (string) $validated['purchase_id'];

        $submissionFolder =
            'badge_creator/submissions/' .
            $purchaseId;

        $sourceExtension = pathinfo(
            (string) $preview['source_path'],
            PATHINFO_EXTENSION
        );

        $finalSourcePath =
            $submissionFolder .
            '/source.' .
            $sourceExtension;

        $finalProcessedPath =
            $submissionFolder .
            '/processed.gif';

        try {
            $creditTransactions->debitAndRun(
                $accountId,
                (int) $validated['payer_user_id'],
                self::PRICE,
                'badge_submission',
                $purchaseId,
                [
                    'benefit' =>
                        'custom_badge_submission',
                    'creator_user_id' =>
                        (int) $validated[
                            'creator_user_id'
                        ],
                    'source_mode' =>
                        (string) $preview['mode'],
                ],
                function (
                    $payer,
                    array $payment
                ) use (
                    $accountId,
                    $validated,
                    $preview,
                    $purchaseId,
                    $finalSourcePath,
                    $finalProcessedPath
                ) {
                    Storage::disk('local')->put(
                        $finalSourcePath,
                        Storage::disk('local')->get(
                            $preview['source_path']
                        )
                    );

                    Storage::disk('local')->put(
                        $finalProcessedPath,
                        Storage::disk('local')->get(
                            $preview['processed_path']
                        )
                    );

                    $submissionId =
                        DB::table(
                            'badge_submissions'
                        )->insertGetId([
                            'account_id' =>
                                $accountId,
                            'creator_user_id' =>
                                (int) $validated[
                                    'creator_user_id'
                                ],
                            'payer_user_id' =>
                                (int) $validated[
                                    'payer_user_id'
                                ],
                            'purchase_id' =>
                                $purchaseId,
                            'refund_id' => null,
                            'source_mode' =>
                                (string) $preview['mode'],
                            'source_path' =>
                                $finalSourcePath,
                            'processed_path' =>
                                $finalProcessedPath,
                            'original_filename' =>
                                (string) (
                                    $preview[
                                        'original_filename'
                                    ] ?? ''
                                ),
                            'badge_name' =>
                                trim(
                                    (string) $validated[
                                        'badge_name'
                                    ]
                                ),
                            'badge_description' =>
                                trim(
                                    (string) $validated[
                                        'badge_description'
                                    ]
                                ),
                            'status' => 'pending',
                            'created_at' => now(),
                            'updated_at' => now(),
                        ]);

                    return [
                        'result' => [
                            'submission_id' =>
                                $submissionId,
                            'status' => 'pending',
                        ],
                        'metadata' => [
                            'submission_id' =>
                                $submissionId,
                            'creator_user_id' =>
                                (int) $validated[
                                    'creator_user_id'
                                ],
                            'badge_name' =>
                                trim(
                                    (string) $validated[
                                        'badge_name'
                                    ]
                                ),
                            'source_mode' =>
                                (string) $preview['mode'],
                            'background_removed' =>
                                (bool) (
                                    $preview[
                                        'background_removed'
                                    ] ?? false
                                ),
                            'price' => self::PRICE,
                            'payment_channel' =>
                                $payment['channel']
                                    ?? null,
                        ],
                    ];
                }
            );
        } catch (CreditTransactionException $exception) {
            throw ValidationException::withMessages([
                'payer_user_id' =>
                    $exception->getMessage(),
            ]);
        } catch (Throwable $exception) {
            report($exception);

            throw ValidationException::withMessages([
                'badge_name' =>
                    'No se pudo enviar la placa. No se ha completado la operación.',
            ]);
        }

        $this->deletePreviewFiles($preview);

        $request->session()->forget(
            $previewKey
        );

        return to_route(
            'marketplace.badges.index'
        )->with(
            'success',
            'Placa enviada a revisión. Se han cobrado ' .
            self::PRICE .
            ' créditos.'
        );
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

    private function assertActiveCharacter(
        int $accountId,
        int $userId
    ): void {
        $exists = DB::table(
            'account_characters'
        )
            ->where('account_id', $accountId)
            ->where('user_id', $userId)
            ->whereNull('archived_at')
            ->exists();

        if (! $exists) {
            throw ValidationException::withMessages([
                'creator_user_id' =>
                    'El personaje creador no pertenece a tus personajes activos.',
            ]);
        }
    }

    private function purgeExpiredPreviews(
        Request $request
    ): void {
        $previews = $request->session()->get(
            'badge_creator.preview',
            []
        );

        if (! is_array($previews)) {
            return;
        }

        foreach ($previews as $token => $preview) {
            if (! is_array($preview)) {
                $request->session()->forget(
                    'badge_creator.preview.' .
                    $token
                );
                continue;
            }

            $created =
                (int) ($preview['created_at'] ?? 0);

            if (
                $created > 0 &&
                now()->timestamp - $created <=
                    self::PREVIEW_TTL_MINUTES * 60
            ) {
                continue;
            }

            $this->deletePreviewFiles(
                $preview
            );

            $request->session()->forget(
                'badge_creator.preview.' .
                $token
            );
        }
    }

    private function deletePreviewFiles(
        array $preview
    ): void {
        foreach (
            [
                'source_path',
                'processed_path',
            ] as $key
        ) {
            $path = $preview[$key] ?? null;

            if (
                is_string($path) &&
                $path !== ''
            ) {
                Storage::disk('local')->delete(
                    $path
                );
            }
        }
    }
}
