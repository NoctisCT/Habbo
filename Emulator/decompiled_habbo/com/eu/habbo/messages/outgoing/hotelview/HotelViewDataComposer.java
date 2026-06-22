/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewDataComposer
extends MessageComposer {
    private final String data;
    private final String key;

    public HotelViewDataComposer(String data, String key) {
        this.data = data;
        this.key = key;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1745);
        this.response.appendString(this.data);
        this.response.appendString(this.key);
        return this.response;
    }

    public String getData() {
        return this.data;
    }

    public String getKey() {
        return this.key;
    }
}

