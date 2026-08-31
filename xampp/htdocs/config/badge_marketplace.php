<?php

return [
    'community_license_cap' => (int) env(
        'BADGE_MARKETPLACE_COMMUNITY_LICENSE_CAP',
        3
    ),

    'seller_inactivity_warning_days' => (int) env(
        'BADGE_MARKETPLACE_SELLER_INACTIVITY_WARNING_DAYS',
        30
    ),

    'seller_inactivity_revoke_days' => (int) env(
        'BADGE_MARKETPLACE_SELLER_INACTIVITY_REVOKE_DAYS',
        45
    ),

    'standard_listing_limit' => 3,
    'hc_listing_limit' => 6,

    'seller_earnings_min' => 0,
    'seller_earnings_max' => 7,
    'hotel_commission' => 3,
];
