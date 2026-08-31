<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class BadgeSellerLicense extends Model
{
    public const STATUS_PENDING = 'pending';
    public const STATUS_WAITLISTED = 'waitlisted';
    public const STATUS_ACTIVE = 'active';
    public const STATUS_REVOKED = 'revoked';

    protected $table = 'badge_seller_licenses';

    public $timestamps = false;

    protected $fillable = [
        'account_id',
        'status',
        'community_slot',
        'applied_at',
        'activated_at',
        'waitlisted_at',
        'last_activity_at',
        'warning_sent_at',
        'revoked_at',
        'reviewed_by_user_id',
        'revocation_reason',
        'created_at',
        'updated_at',
    ];

    protected $casts = [
        'community_slot' => 'integer',
        'applied_at' => 'datetime',
        'activated_at' => 'datetime',
        'waitlisted_at' => 'datetime',
        'last_activity_at' => 'datetime',
        'warning_sent_at' => 'datetime',
        'revoked_at' => 'datetime',
        'created_at' => 'datetime',
        'updated_at' => 'datetime',
    ];
}
