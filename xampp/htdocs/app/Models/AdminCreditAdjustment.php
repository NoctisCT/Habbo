<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class AdminCreditAdjustment extends Model
{
    protected $table = 'admin_credit_adjustments';

    protected $primaryKey = 'id';

    public $incrementing = false;

    protected $keyType = 'string';

    protected $guarded = [];

    protected $casts = [
        'delta' => 'integer',
        'balance_before' => 'integer',
        'balance_after' => 'integer',
        'lease_expires_at' => 'datetime',
        'created_at' => 'datetime',
        'updated_at' => 'datetime',
        'completed_at' => 'datetime',
    ];

    public function actor(): BelongsTo
    {
        return $this->belongsTo(
            User::class,
            'actor_user_id'
        );
    }

    public function target(): BelongsTo
    {
        return $this->belongsTo(
            User::class,
            'target_user_id'
        );
    }
}