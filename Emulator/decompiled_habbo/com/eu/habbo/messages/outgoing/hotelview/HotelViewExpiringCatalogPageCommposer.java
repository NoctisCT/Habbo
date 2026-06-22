/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewExpiringCatalogPageCommposer
extends MessageComposer {
    private final CatalogPage page;
    private final String image;

    public HotelViewExpiringCatalogPageCommposer(CatalogPage page, String image) {
        this.page = page;
        this.image = image;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2515);
        this.response.appendString(this.page.getCaption());
        this.response.appendInt(this.page.getId());
        this.response.appendString(this.image);
        return this.response;
    }

    public CatalogPage getPage() {
        return this.page;
    }

    public String getImage() {
        return this.image;
    }
}

