/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelWillCloseInMinutesAndBackInComposer
extends MessageComposer {
    private final int closeInMinutes;
    private final int reopenInMinutes;

    public HotelWillCloseInMinutesAndBackInComposer(int closeInMinutes, int reopenInMinutes) {
        this.closeInMinutes = closeInMinutes;
        this.reopenInMinutes = reopenInMinutes;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1350);
        this.response.appendBoolean(true);
        this.response.appendInt(this.closeInMinutes);
        this.response.appendInt(this.reopenInMinutes);
        return this.response;
    }

    public int getCloseInMinutes() {
        return this.closeInMinutes;
    }

    public int getReopenInMinutes() {
        return this.reopenInMinutes;
    }
}

