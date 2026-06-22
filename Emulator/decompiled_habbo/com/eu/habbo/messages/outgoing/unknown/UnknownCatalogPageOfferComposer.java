/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UnknownCatalogPageOfferComposer
extends MessageComposer {
    private final int pageId;
    private final CatalogItem catalogItem;

    public UnknownCatalogPageOfferComposer(int pageId, CatalogItem catalogItem) {
        this.pageId = pageId;
        this.catalogItem = catalogItem;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1889);
        this.response.appendInt(this.pageId);
        this.catalogItem.serialize(this.response);
        return this.response;
    }

    public int getPageId() {
        return this.pageId;
    }

    public CatalogItem getCatalogItem() {
        return this.catalogItem;
    }
}

