<?php

namespace App\Services;

use App\Models\BadgeSellerLicense;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;
use RuntimeException;

class BadgeSellerEligibilityService
{
    private const BADGE_DESIGNER_TEAM =
        'Diseñador de Placas';

    private const MIN_APPROVED_BADGES = 5;

    public function __construct(
        private readonly AccountNotificationService $notifications
    ) {
    }

    public function isBadgeDesigner(
        int $accountId
    ): bool {
        return DB::table(
            'account_characters as ac'
        )
            ->join(
                'user_website_team as uwt',
                'uwt.user_id',
                '=',
                'ac.user_id'
            )
            ->join(
                'website_teams as wt',
                'wt.id',
                '=',
                'uwt.website_team_id'
            )
            ->where(
                'ac.account_id',
                $accountId
            )
            ->whereNull(
                'ac.archived_at'
            )
            ->where(
                'wt.rank_name',
                self::BADGE_DESIGNER_TEAM
            )
            ->exists();
    }

    public function approvedBadgeCount(
        int $accountId
    ): int {
        return DB::table(
            'creator_badges as cb'
        )
            ->join(
                'badge_submissions as bs',
                'bs.id',
                '=',
                'cb.badge_submission_id'
            )
            ->where(
                'cb.account_id',
                $accountId
            )
            ->where(
                'bs.status',
                'approved'
            )
            ->count();
    }

    public function minimumApprovedBadges(): int
    {
        return self::MIN_APPROVED_BADGES;
    }

    public function hasMinimumApprovedBadges(
        int $accountId
    ): bool {
        return $this->approvedBadgeCount(
            $accountId
        ) >= self::MIN_APPROVED_BADGES;
    }

    public function hasActiveCommunityLicense(
        int $accountId
    ): bool {
        return DB::table(
            'badge_seller_licenses'
        )
            ->where(
                'account_id',
                $accountId
            )
            ->where(
                'status',
                BadgeSellerLicense::STATUS_ACTIVE
            )
            ->whereNotNull(
                'community_slot'
            )
            ->whereNull(
                'revoked_at'
            )
            ->exists();
    }

    public function canSell(
        int $accountId
    ): bool {
        return $this->isBadgeDesigner(
            $accountId
        )
            || $this
                ->hasActiveCommunityLicense(
                    $accountId
                );
    }

    public function hasActiveHabboClub(
        int $accountId
    ): bool {
        $now = now()->timestamp;

        return DB::table(
            'account_characters as ac'
        )
            ->join(
                'users_subscriptions as us',
                'us.user_id',
                '=',
                'ac.user_id'
            )
            ->where(
                'ac.account_id',
                $accountId
            )
            ->whereNull(
                'ac.archived_at'
            )
            ->where(
                'us.subscription_type',
                'HABBO_CLUB'
            )
            ->where(
                'us.active',
                1
            )
            ->whereRaw(
                '(us.timestamp_start + us.duration) > ?',
                [$now]
            )
            ->exists();
    }

    public function listingLimit(
        int $accountId
    ): int {
        if (
            $this->hasActiveHabboClub(
                $accountId
            )
        ) {
            return (int) config(
                'badge_marketplace.hc_listing_limit',
                6
            );
        }

        return (int) config(
            'badge_marketplace.standard_listing_limit',
            3
        );
    }

    public function communityLicenseCap(): int
    {
        return max(
            0,
            (int) config(
                'badge_marketplace.community_license_cap',
                3
            )
        );
    }

    public function communitySlotsUsed(): int
    {
        return DB::table(
            'badge_seller_licenses'
        )
            ->where(
                'status',
                BadgeSellerLicense::STATUS_ACTIVE
            )
            ->whereNotNull(
                'community_slot'
            )
            ->whereNull(
                'revoked_at'
            )
            ->count();
    }

    public function communitySlotsAvailable(): int
    {
        return max(
            0,
            $this->communityLicenseCap()
                - $this
                    ->communitySlotsUsed()
        );
    }

    public function applyCommunityLicense(
        int $accountId
    ): object {
        if (
            $this->isBadgeDesigner(
                $accountId
            )
        ) {
            throw ValidationException::withMessages([
                'seller_license' =>
                    'Esta cuenta ya tiene un Diseñador de Placas y puede vender sin licencia comunitaria.',
            ]);
        }

        $approved =
            $this->approvedBadgeCount(
                $accountId
            );

        if (
            $approved <
            self::MIN_APPROVED_BADGES
        ) {
            throw ValidationException::withMessages([
                'seller_license' =>
                    'Necesitas al menos ' .
                    self::MIN_APPROVED_BADGES .
                    ' placas aprobadas para solicitar la licencia de vendedor. Actualmente tienes ' .
                    $approved .
                    '.',
            ]);
        }

        return DB::transaction(function () use (
            $accountId
        ) {
            $existing = DB::table(
                'badge_seller_licenses'
            )
                ->where(
                    'account_id',
                    $accountId
                )
                ->lockForUpdate()
                ->first();

            if (
                $existing &&
                in_array(
                    $existing->status,
                    [
                        BadgeSellerLicense::STATUS_PENDING,
                        BadgeSellerLicense::STATUS_WAITLISTED,
                        BadgeSellerLicense::STATUS_ACTIVE,
                    ],
                    true
                )
            ) {
                return $existing;
            }

            // Toda solicitud debe ser revisada por staff.
            // WAITLISTED significa exclusivamente:
            // aprobada, pero esperando una plaza comunitaria.
            $status =
                BadgeSellerLicense::STATUS_PENDING;

            $now = now();

            if ($existing) {
                DB::table(
                    'badge_seller_licenses'
                )
                    ->where(
                        'id',
                        $existing->id
                    )
                    ->update([
                        'status' => $status,
                        'community_slot' => null,
                        'applied_at' => $now,
                        'activated_at' => null,
                        'waitlisted_at' =>
                            $status ===
                            BadgeSellerLicense::STATUS_WAITLISTED
                                ? $now
                                : null,
                        'last_activity_at' => null,
                        'warning_sent_at' => null,
                        'revoked_at' => null,
                        'reviewed_by_user_id' => null,
                        'revocation_reason' => null,
                        'updated_at' => $now,
                    ]);

                return DB::table(
                    'badge_seller_licenses'
                )
                    ->where(
                        'id',
                        $existing->id
                    )
                    ->first();
            }

            $id = DB::table(
                'badge_seller_licenses'
            )
                ->insertGetId([
                    'account_id' => $accountId,
                    'status' => $status,
                    'community_slot' => null,
                    'applied_at' => $now,
                    'activated_at' => null,
                    'waitlisted_at' =>
                        $status ===
                        BadgeSellerLicense::STATUS_WAITLISTED
                            ? $now
                            : null,
                    'last_activity_at' => null,
                    'warning_sent_at' => null,
                    'revoked_at' => null,
                    'reviewed_by_user_id' => null,
                    'revocation_reason' => null,
                    'created_at' => $now,
                    'updated_at' => null,
                ]);

            return DB::table(
                'badge_seller_licenses'
            )
                ->where(
                    'id',
                    $id
                )
                ->first();
        });
    }

    public function activateCommunityLicense(
        int $licenseId,
        int $reviewerUserId
    ): object {
        $result = DB::transaction(function () use (
            $licenseId,
            $reviewerUserId
        ) {
            $license = DB::table(
                'badge_seller_licenses'
            )
                ->where(
                    'id',
                    $licenseId
                )
                ->lockForUpdate()
                ->first();

            if (! $license) {
                throw new RuntimeException(
                    'La licencia de vendedor no existe.'
                );
            }

            if (
                $this->isBadgeDesigner(
                    (int)
                    $license->account_id
                )
            ) {
                throw ValidationException::withMessages([
                    'seller_license' =>
                        'La cuenta ya tiene un Diseñador de Placas y no necesita consumir una licencia comunitaria.',
                ]);
            }

            if (
                ! $this->hasMinimumApprovedBadges(
                    (int)
                    $license->account_id
                )
            ) {
                throw ValidationException::withMessages([
                    'seller_license' =>
                        'La cuenta ya no cumple el mínimo de ' .
                        self::MIN_APPROVED_BADGES .
                        ' placas aprobadas.',
                ]);
            }

            if (
                $license->status ===
                BadgeSellerLicense::STATUS_ACTIVE &&
                $license->community_slot !== null
            ) {
                return $license;
            }

            $cap =
                $this->communityLicenseCap();

            $used = DB::table(
                'badge_seller_licenses'
            )
                ->where(
                    'status',
                    BadgeSellerLicense::STATUS_ACTIVE
                )
                ->whereNotNull(
                    'community_slot'
                )
                ->whereNull(
                    'revoked_at'
                )
                ->lockForUpdate()
                ->pluck(
                    'community_slot'
                )
                ->map(
                    fn ($slot) =>
                        (int) $slot
                )
                ->all();

            $slot = null;

            for (
                $candidate = 1;
                $candidate <= $cap;
                $candidate++
            ) {
                if (
                    ! in_array(
                        $candidate,
                        $used,
                        true
                    )
                ) {
                    $slot = $candidate;
                    break;
                }
            }

            if ($slot === null) {
                if (
                    $license->status ===
                        BadgeSellerLicense::STATUS_WAITLISTED &&
                    $license->reviewed_by_user_id !==
                        null
                ) {
                    return $license;
                }

                DB::table(
                    'badge_seller_licenses'
                )
                    ->where(
                        'id',
                        $licenseId
                    )
                    ->update([
                        'status' =>
                            BadgeSellerLicense::STATUS_WAITLISTED,
                        'community_slot' =>
                            null,
                        'waitlisted_at' =>
                            now(),
                        'reviewed_by_user_id' =>
                            $reviewerUserId,
                        'updated_at' =>
                            now(),
                    ]);

                return DB::table(
                    'badge_seller_licenses'
                )
                    ->where(
                        'id',
                        $licenseId
                    )
                    ->first();
            }

            DB::table(
                'badge_seller_licenses'
            )
                ->where(
                    'id',
                    $licenseId
                )
                ->update([
                    'status' =>
                        BadgeSellerLicense::STATUS_ACTIVE,
                    'community_slot' =>
                        $slot,
                    'activated_at' =>
                        now(),
                    'waitlisted_at' =>
                        null,
                    'last_activity_at' =>
                        now(),
                    'warning_sent_at' =>
                        null,
                    'revoked_at' =>
                        null,
                    'reviewed_by_user_id' =>
                        $reviewerUserId,
                    'revocation_reason' =>
                        null,
                    'updated_at' =>
                        now(),
                ]);

            return DB::table(
                'badge_seller_licenses'
            )
                ->where(
                    'id',
                    $licenseId
                )
                ->first();
        });

        if (
            $result->status ===
            BadgeSellerLicense::STATUS_ACTIVE
        ) {
            $this->notifications->send(
                (int) $result->account_id,
                'badge.seller_license_approved',
                'Solicitud de vendedor aprobada',
                'Tu solicitud para vender placas ha sido aprobada. Ya puedes publicar tus placas en el marketplace. Plaza comunitaria #' .
                    (int) $result->community_slot .
                    '.',
                route(
                    'marketplace.badges.index',
                    [
                        'tab' => 'seller',
                    ],
                    false
                ),
                [
                    'badge_seller_license_id' =>
                        (int) $result->id,
                    'community_slot' =>
                        (int)
                        $result->community_slot,
                    'reviewed_by_user_id' =>
                        $reviewerUserId,
                ],
                'badge-seller-license:' .
                    (int) $result->id .
                    ':approved'
            );
        }


        if (
            $result->status ===
            BadgeSellerLicense::STATUS_WAITLISTED
        ) {
            $this->notifications->send(
                (int) $result->account_id,
                'badge.seller_license_waitlisted',
                'Solicitud aprobada: lista de espera',
                'Tu solicitud para vender placas ha sido aprobada, pero las plazas comunitarias están ocupadas. Entrarás automáticamente cuando se libere una plaza.',
                route(
                    'marketplace.badges.index',
                    [
                        'tab' => 'seller',
                    ],
                    false
                ),
                [
                    'badge_seller_license_id' =>
                        (int) $result->id,
                    'waitlisted_at' =>
                        (string)
                        $result->waitlisted_at,
                    'reviewed_by_user_id' =>
                        $reviewerUserId,
                ],
                'badge-seller-license:' .
                    (int) $result->id .
                    ':waitlisted:' .
                    str_replace(
                        [
                            '-',
                            ':',
                            ' ',
                        ],
                        '',
                        (string)
                        $result->waitlisted_at
                    )
            );
        }

        return $result;
    }

    public function maintainCommunityLicenses(): array
    {
        $warningDays =
            max(
                1,
                (int) config(
                    'badge_marketplace.seller_inactivity_warning_days',
                    30
                )
            );

        $revokeDays =
            max(
                $warningDays + 1,
                (int) config(
                    'badge_marketplace.seller_inactivity_revoke_days',
                    45
                )
            );

        $graceDays =
            max(
                1,
                $revokeDays -
                    $warningDays
            );

        $now =
            now();

        $warningThreshold =
            $now->copy()
                ->subDays(
                    $warningDays
                );

        $revokeThreshold =
            $now->copy()
                ->subDays(
                    $revokeDays
                );

        $warningGraceThreshold =
            $now->copy()
                ->subDays(
                    $graceDays
                );

        $warningsReset =
            DB::table(
                'badge_seller_licenses'
            )
                ->where(
                    'status',
                    BadgeSellerLicense::STATUS_ACTIVE
                )
                ->whereNotNull(
                    'community_slot'
                )
                ->whereNull(
                    'revoked_at'
                )
                ->whereNotNull(
                    'warning_sent_at'
                )
                ->whereNotNull(
                    'last_activity_at'
                )
                ->whereColumn(
                    'last_activity_at',
                    '>=',
                    'warning_sent_at'
                )
                ->update([
                    'warning_sent_at' =>
                        null,
                    'updated_at' =>
                        $now,
                ]);

        $warningIds =
            DB::table(
                'badge_seller_licenses'
            )
                ->where(
                    'status',
                    BadgeSellerLicense::STATUS_ACTIVE
                )
                ->whereNotNull(
                    'community_slot'
                )
                ->whereNull(
                    'revoked_at'
                )
                ->whereNull(
                    'warning_sent_at'
                )
                ->whereNotNull(
                    'last_activity_at'
                )
                ->where(
                    'last_activity_at',
                    '<=',
                    $warningThreshold
                )
                ->orderBy(
                    'last_activity_at'
                )
                ->orderBy(
                    'id'
                )
                ->pluck(
                    'id'
                )
                ->map(
                    fn ($id) =>
                        (int) $id
                )
                ->all();

        $warningsSent = 0;

        foreach ($warningIds as $licenseId) {
            $warned =
                DB::transaction(
                    function () use (
                        $licenseId,
                        $warningThreshold,
                        $now
                    ): ?object {
                        $license =
                            DB::table(
                                'badge_seller_licenses'
                            )
                                ->where(
                                    'id',
                                    $licenseId
                                )
                                ->lockForUpdate()
                                ->first();

                        if (
                            ! $license ||
                            $license->status !==
                                BadgeSellerLicense::STATUS_ACTIVE ||
                            $license->community_slot ===
                                null ||
                            $license->revoked_at !==
                                null ||
                            $license->warning_sent_at !==
                                null ||
                            $license->last_activity_at ===
                                null ||
                            $license->last_activity_at >
                                $warningThreshold
                        ) {
                            return null;
                        }

                        DB::table(
                            'badge_seller_licenses'
                        )
                            ->where(
                                'id',
                                $licenseId
                            )
                            ->update([
                                'warning_sent_at' =>
                                    $now,
                                'updated_at' =>
                                    $now,
                            ]);

                        return DB::table(
                            'badge_seller_licenses'
                        )
                            ->where(
                                'id',
                                $licenseId
                            )
                            ->first();
                    },
                    5
                );

            if (! $warned) {
                continue;
            }

            $warningsSent++;

            $this->notifications->send(
                (int) $warned->account_id,
                'badge.seller_license_inactivity_warning',
                'Tu licencia de vendedor está inactiva',
                'Llevas ' .
                    $warningDays .
                    ' días sin actividad real en el marketplace de placas. Si alcanzas ' .
                    $revokeDays .
                    ' días sin actividad, perderás la plaza comunitaria. Publicar, actualizar o reactivar anuncios y completar ventas vuelve a contar como actividad.',
                route(
                    'marketplace.badges.index',
                    [
                        'tab' => 'seller',
                    ],
                    false
                ),
                [
                    'badge_seller_license_id' =>
                        (int) $warned->id,
                    'warning_days' =>
                        $warningDays,
                    'revoke_days' =>
                        $revokeDays,
                    'last_activity_at' =>
                        (string)
                        $warned->last_activity_at,
                ],
                'badge-seller-license:' .
                    (int) $warned->id .
                    ':inactivity-warning:' .
                    str_replace(
                        [
                            '-',
                            ':',
                            ' ',
                        ],
                        '',
                        (string)
                        $warned->last_activity_at
                    )
            );
        }

        $revokeIds =
            DB::table(
                'badge_seller_licenses'
            )
                ->where(
                    'status',
                    BadgeSellerLicense::STATUS_ACTIVE
                )
                ->whereNotNull(
                    'community_slot'
                )
                ->whereNull(
                    'revoked_at'
                )
                ->whereNotNull(
                    'warning_sent_at'
                )
                ->whereNotNull(
                    'last_activity_at'
                )
                ->where(
                    'last_activity_at',
                    '<=',
                    $revokeThreshold
                )
                ->where(
                    'warning_sent_at',
                    '<=',
                    $warningGraceThreshold
                )
                ->orderBy(
                    'last_activity_at'
                )
                ->orderBy(
                    'id'
                )
                ->pluck(
                    'id'
                )
                ->map(
                    fn ($id) =>
                        (int) $id
                )
                ->all();

        $revoked = 0;

        foreach ($revokeIds as $licenseId) {
            $license =
                DB::table(
                    'badge_seller_licenses'
                )
                    ->where(
                        'id',
                        $licenseId
                    )
                    ->first();

            if (
                ! $license ||
                $license->status !==
                    BadgeSellerLicense::STATUS_ACTIVE ||
                $license->revoked_at !==
                    null
            ) {
                continue;
            }

            $this->revokeCommunityLicense(
                $licenseId,
                null,
                'Retirada automática por inactividad: ' .
                    $revokeDays .
                    ' días sin actividad real en el marketplace de placas.',
                false
            );

            $revoked++;
        }

        $promoted =
            $this->promoteNextWaitlisted();

        return [
            'warnings_reset' =>
                (int) $warningsReset,
            'warnings_sent' =>
                $warningsSent,
            'revoked' =>
                $revoked,
            'promoted' =>
                count(
                    $promoted
                ),
        ];
    }

    public function promoteNextWaitlisted(): array
    {
        $promoted = [];

        while (
            $this->communitySlotsAvailable() > 0
        ) {
            $license =
                DB::transaction(
                    function (): ?object {
                        $cap =
                            $this->communityLicenseCap();

                        if ($cap <= 0) {
                            return null;
                        }

                        $used =
                            DB::table(
                                'badge_seller_licenses'
                            )
                                ->where(
                                    'status',
                                    BadgeSellerLicense::STATUS_ACTIVE
                                )
                                ->whereNotNull(
                                    'community_slot'
                                )
                                ->whereNull(
                                    'revoked_at'
                                )
                                ->lockForUpdate()
                                ->pluck(
                                    'community_slot'
                                )
                                ->map(
                                    fn ($slot) =>
                                        (int) $slot
                                )
                                ->all();

                        $slot = null;

                        for (
                            $candidate = 1;
                            $candidate <=
                                $cap;
                            $candidate++
                        ) {
                            if (
                                ! in_array(
                                    $candidate,
                                    $used,
                                    true
                                )
                            ) {
                                $slot =
                                    $candidate;

                                break;
                            }
                        }

                        if ($slot === null) {
                            return null;
                        }

                        $candidate =
                            DB::table(
                                'badge_seller_licenses'
                            )
                                ->where(
                                    'status',
                                    BadgeSellerLicense::STATUS_WAITLISTED
                                )
                                ->whereNull(
                                    'community_slot'
                                )
                                ->whereNull(
                                    'revoked_at'
                                )
                                ->whereNotNull(
                                    'reviewed_by_user_id'
                                )
                                ->orderByRaw(
                                    'CASE WHEN waitlisted_at IS NULL THEN 1 ELSE 0 END'
                                )
                                ->orderBy(
                                    'waitlisted_at'
                                )
                                ->orderBy(
                                    'id'
                                )
                                ->lockForUpdate()
                                ->first();

                        if (! $candidate) {
                            return null;
                        }

                        $now =
                            now();

                        DB::table(
                            'badge_seller_licenses'
                        )
                            ->where(
                                'id',
                                (int)
                                $candidate->id
                            )
                            ->where(
                                'status',
                                BadgeSellerLicense::STATUS_WAITLISTED
                            )
                            ->whereNull(
                                'community_slot'
                            )
                            ->whereNull(
                                'revoked_at'
                            )
                            ->update([
                                'status' =>
                                    BadgeSellerLicense::STATUS_ACTIVE,
                                'community_slot' =>
                                    $slot,
                                'activated_at' =>
                                    $now,
                                'waitlisted_at' =>
                                    null,
                                'last_activity_at' =>
                                    $now,
                                'warning_sent_at' =>
                                    null,
                                'revoked_at' =>
                                    null,
                                'revocation_reason' =>
                                    null,
                                'updated_at' =>
                                    $now,
                            ]);

                        return DB::table(
                            'badge_seller_licenses'
                        )
                            ->where(
                                'id',
                                (int)
                                $candidate->id
                            )
                            ->first();
                    },
                    5
                );

            if (! $license) {
                break;
            }

            $promoted[] =
                $license;

            $this->notifications->send(
                (int) $license->account_id,
                'badge.seller_license_approved',
                'Ya tienes plaza para vender placas',
                'Se ha liberado una plaza comunitaria y tu solicitud aprobada ha pasado automáticamente de la lista de espera a activa. Ocupas la plaza #' .
                    (int)
                    $license->community_slot .
                    '.',
                route(
                    'marketplace.badges.index',
                    [
                        'tab' => 'seller',
                    ],
                    false
                ),
                [
                    'badge_seller_license_id' =>
                        (int) $license->id,
                    'community_slot' =>
                        (int)
                        $license->community_slot,
                    'promoted_from_waitlist' =>
                        true,
                ],
                'badge-seller-license:' .
                    (int) $license->id .
                    ':waitlist-promoted:' .
                    str_replace(
                        [
                            '-',
                            ':',
                            ' ',
                        ],
                        '',
                        (string)
                        $license->activated_at
                    )
            );
        }

        return $promoted;
    }
    public function revokeCommunityLicense(
        int $licenseId,
        ?int $reviewerUserId,
        string $reason,
        bool $promoteWaitlist = true
    ): object {
        $reason = trim($reason);

        if ($reason === '') {
            throw ValidationException::withMessages([
                'revocation_reason' =>
                    'Indica el motivo de retirada de la licencia.',
            ]);
        }

        $result =
            DB::transaction(function () use (
                $licenseId,
                $reviewerUserId,
                $reason
            ) {
                $license = DB::table(
                    'badge_seller_licenses'
                )
                    ->where(
                        'id',
                        $licenseId
                    )
                    ->lockForUpdate()
                    ->first();

                if (! $license) {
                    throw new RuntimeException(
                        'La licencia de vendedor no existe.'
                    );
                }

                if (
                    $license->status ===
                    BadgeSellerLicense::STATUS_REVOKED
                ) {
                    return $license;
                }

                $now =
                    now();

                DB::table(
                    'badge_seller_licenses'
                )
                    ->where(
                        'id',
                        $licenseId
                    )
                    ->update([
                        'status' =>
                            BadgeSellerLicense::STATUS_REVOKED,
                        'community_slot' =>
                            null,
                        'revoked_at' =>
                            $now,
                        'reviewed_by_user_id' =>
                            $reviewerUserId,
                        'revocation_reason' =>
                            mb_substr(
                                $reason,
                                0,
                                255
                            ),
                        'updated_at' =>
                            $now,
                    ]);

                DB::table(
                    'badge_marketplace_listings'
                )
                    ->where(
                        'seller_account_id',
                        (int)
                        $license->account_id
                    )
                    ->where(
                        'status',
                        'active'
                    )
                    ->update([
                        'status' =>
                            'inactive',
                        'deactivated_at' =>
                            $now,
                        'updated_at' =>
                            $now,
                    ]);

                return DB::table(
                    'badge_seller_licenses'
                )
                    ->where(
                        'id',
                        $licenseId
                    )
                    ->first();
            });

        $this->notifications->send(
            (int) $result->account_id,
            'badge.seller_license_revoked',
            'Licencia de vendedor retirada',
            'Tu licencia comunitaria para vender placas ha sido retirada. Motivo: ' .
                (string)
                $result->revocation_reason,
            route(
                'marketplace.badges.index',
                [
                    'tab' => 'seller',
                ],
                false
            ),
            [
                'badge_seller_license_id' =>
                    (int) $result->id,
                'revocation_reason' =>
                    (string)
                    $result->revocation_reason,
                'automatic' =>
                    $reviewerUserId === null,
            ],
            'badge-seller-license:' .
                (int) $result->id .
                ':revoked:' .
                str_replace(
                    [
                        '-',
                        ':',
                        ' ',
                    ],
                    '',
                    (string)
                    $result->revoked_at
                )
        );

        if ($promoteWaitlist) {
            $this->promoteNextWaitlisted();
        }

        return $result;
    }
}
