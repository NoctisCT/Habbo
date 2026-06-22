/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewSecondsUntilComposer
extends MessageComposer {
    private final String dateString;
    private final int seconds;

    public HotelViewSecondsUntilComposer(String dateString, int seconds) {
        this.dateString = dateString;
        this.seconds = seconds;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3926);
        this.response.appendString(this.dateString);
        this.response.appendInt(this.seconds);
        return this.response;
    }

    public String getDateString() {
        return this.dateString;
    }

    public int getSeconds() {
        return this.seconds;
    }
}

