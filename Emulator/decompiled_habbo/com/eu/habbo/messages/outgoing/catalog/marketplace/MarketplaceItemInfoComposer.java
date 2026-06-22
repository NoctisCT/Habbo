/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MarketplaceItemInfoComposer
extends MessageComposer {
    private final int itemId;

    public MarketplaceItemInfoComposer(int itemId) {
        this.itemId = itemId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(725);
        MarketPlace.serializeItemInfo(this.itemId, this.response);
        return this.response;
    }

    public int getItemId() {
        return this.itemId;
    }
}

