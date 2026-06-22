/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class FloodCounterComposer
extends MessageComposer {
    private final int time;

    public FloodCounterComposer(int time) {
        this.time = time;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(566);
        this.response.appendInt(this.time);
        return this.response;
    }

    public int getTime() {
        return this.time;
    }
}

