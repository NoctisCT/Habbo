/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceOffer;
import com.eu.habbo.plugin.events.marketplace.MarketPlaceEvent;

public class MarketPlaceItemCancelledEvent
extends MarketPlaceEvent {
    public final MarketPlaceOffer offer;

    public MarketPlaceItemCancelledEvent(MarketPlaceOffer offer) {
        this.offer = offer;
    }
}

