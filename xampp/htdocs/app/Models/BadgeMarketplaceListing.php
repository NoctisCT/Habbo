<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class BadgeMarketplaceListing extends Model
{
    public const STATUS_ACTIVE = 'active';
    public const STATUS_INACTIVE = 'inactive';

    protected $table =
        'badge_marketplace_listings';

    protected $fillable = [
        'creator_badge_id',
        'seller_account_id',
        'seller_earnings',
        'hotel_commission',
        'buyer_price',
        'status',
        'activated_at',
        'deactivated_at',
    ];

    protected $casts = [
        'creator_badge_id' => 'integer',
        'seller_account_id' => 'integer',
        'seller_earnings' => 'integer',
        'hotel_commission' => 'integer',
        'buyer_price' => 'integer',
        'activated_at' => 'datetime',
        'deactivated_at' => 'datetime',
    ];
}
