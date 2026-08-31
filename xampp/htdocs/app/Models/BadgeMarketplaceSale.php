<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class BadgeMarketplaceSale extends Model
{
    public const STATUS_PAID_PENDING_DELIVERY =
        'paid_pending_delivery';

    public const STATUS_DELIVERED_PENDING_PAYOUT =
        'delivered_pending_payout';

    public const STATUS_COMPLETED =
        'completed';

    public const STATUS_REFUNDED =
        'refunded';

    public const STATUS_MANUAL_REVIEW =
        'manual_review';

    protected $table =
        'badge_marketplace_sales';

    protected $fillable = [
        'purchase_id',
        'refund_id',
        'listing_id',
        'creator_badge_id',
        'seller_account_id',
        'seller_user_id',
        'buyer_account_id',
        'buyer_user_id',
        'badge_code',
        'badge_name',
        'seller_username',
        'buyer_username',
        'seller_earnings',
        'hotel_commission',
        'buyer_price',
        'status',
        'payout_channel',
        'payout_transaction_id',
        'seller_balance_before',
        'seller_balance_after',
        'error_message',
        'delivered_at',
        'paid_out_at',
        'refunded_at',
    ];

    protected $casts = [
        'listing_id' => 'integer',
        'creator_badge_id' => 'integer',
        'seller_account_id' => 'integer',
        'seller_user_id' => 'integer',
        'buyer_account_id' => 'integer',
        'buyer_user_id' => 'integer',
        'seller_earnings' => 'integer',
        'hotel_commission' => 'integer',
        'buyer_price' => 'integer',
        'seller_balance_before' => 'integer',
        'seller_balance_after' => 'integer',
        'delivered_at' => 'datetime',
        'paid_out_at' => 'datetime',
        'refunded_at' => 'datetime',
    ];
}
