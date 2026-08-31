<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class CreatorBadge extends Model
{
    public $timestamps = false;

    protected $fillable = [
        'account_id',
        'creator_user_id',
        'badge_submission_id',
        'badge_code',
        'badge_name',
        'badge_description',
        'image_path',
        'marketplace_enabled',
        'created_at',
        'updated_at',
    ];

    protected $casts = [
        'marketplace_enabled' => 'boolean',
        'created_at' => 'datetime',
        'updated_at' => 'datetime',
    ];
}