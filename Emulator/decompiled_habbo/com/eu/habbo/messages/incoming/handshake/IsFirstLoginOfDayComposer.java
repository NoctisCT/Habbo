/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class IsFirstLoginOfDayComposer
extends MessageComposer {
    private final boolean isFirstLoginOfDay;

    public IsFirstLoginOfDayComposer(boolean isFirstLoginOfDay) {
        this.isFirstLoginOfDay = isFirstLoginOfDay;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(793);
        this.response.appendBoolean(this.isFirstLoginOfDay);
        return this.response;
    }
}

