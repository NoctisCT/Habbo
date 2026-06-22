/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceOffer;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MarketplaceCancelSaleComposer
extends MessageComposer {
    private final MarketPlaceOffer offer;
    private final boolean success;

    public MarketplaceCancelSaleComposer(MarketPlaceOffer offer, Boolean success) {
        this.offer = offer;
        this.success = success;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3264);
        this.response.appendInt(this.offer.getOfferId());
        this.response.appendBoolean(this.success);
        return this.response;
    }

    public MarketPlaceOffer getOffer() {
        return this.offer;
    }

    public boolean isSuccess() {
        return this.success;
    }
}

