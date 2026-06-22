/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewCustomTimerComposer
extends MessageComposer {
    private final String name;
    private final int seconds;

    public HotelViewCustomTimerComposer(String name, int seconds) {
        this.name = name;
        this.seconds = seconds;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(-1);
        this.response.appendString(this.name);
        this.response.appendInt(this.seconds);
        return this.response;
    }

    public String getName() {
        return this.name;
    }

    public int getSeconds() {
        return this.seconds;
    }
}

