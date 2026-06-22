/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewCatalogPageExpiringComposer
extends MessageComposer {
    private final String name;
    private final int time;
    private final String image;

    public HotelViewCatalogPageExpiringComposer(String name, int time, String image) {
        this.name = name;
        this.time = time;
        this.image = image;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(690);
        this.response.appendString(this.name);
        this.response.appendInt(this.time);
        this.response.appendString(this.image);
        return this.response;
    }

    public String getName() {
        return this.name;
    }

    public int getTime() {
        return this.time;
    }

    public String getImage() {
        return this.image;
    }
}

