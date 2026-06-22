/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelWillCloseInMinutesComposer
extends MessageComposer {
    private final int minutes;

    public HotelWillCloseInMinutesComposer(int minutes) {
        this.minutes = minutes;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1050);
        this.response.appendInt(this.minutes);
        return this.response;
    }

    public int getMinutes() {
        return this.minutes;
    }
}

