/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CatalogSearchResultComposer
extends MessageComposer {
    private final CatalogItem item;

    public CatalogSearchResultComposer(CatalogItem item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3388);
        this.item.serialize(this.response);
        return this.response;
    }

    public CatalogItem getItem() {
        return this.item;
    }
}

