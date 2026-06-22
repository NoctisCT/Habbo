/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PurchaseOKComposer
extends MessageComposer {
    private final CatalogItem catalogItem;

    public PurchaseOKComposer(CatalogItem catalogItem) {
        this.catalogItem = catalogItem;
    }

    public PurchaseOKComposer() {
        this.catalogItem = null;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(869);
        if (this.catalogItem != null) {
            this.catalogItem.serialize(this.response);
        } else {
            this.response.appendInt(0);
            this.response.appendString("");
            this.response.appendBoolean(false);
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendBoolean(true);
            this.response.appendInt(1);
            this.response.appendString("s");
            this.response.appendInt(0);
            this.response.appendString("");
            this.response.appendInt(1);
            this.response.appendInt(0);
            this.response.appendString("");
            this.response.appendInt(1);
        }
        return this.response;
    }

    public CatalogItem getCatalogItem() {
        return this.catalogItem;
    }
}

