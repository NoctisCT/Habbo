/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CanCreateRoomComposer
extends MessageComposer {
    private final int count;
    private final int max;

    public CanCreateRoomComposer(int count, int max) {
        this.count = count;
        this.max = max;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(378);
        this.response.appendInt(this.count >= this.max ? 1 : 0);
        this.response.appendInt(this.max);
        return this.response;
    }

    public int getCount() {
        return this.count;
    }

    public int getMax() {
        return this.max;
    }
}

