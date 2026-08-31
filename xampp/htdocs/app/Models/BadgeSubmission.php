<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class BadgeSubmission extends Model
{
    protected $fillable = [
        'account_id',
        'creator_user_id',
        'payer_user_id',
        'purchase_id',
        'refund_id',
        'source_mode',
        'source_path',
        'processed_path',
        'original_filename',
        'badge_name',
        'badge_description',
        'status',
        'badge_code',
        'moderator_user_id',
        'moderation_reason',
        'approved_at',
        'rejected_at',
    ];

    protected $casts = [
        'approved_at' => 'datetime',
        'rejected_at' => 'datetime',
    ];
}