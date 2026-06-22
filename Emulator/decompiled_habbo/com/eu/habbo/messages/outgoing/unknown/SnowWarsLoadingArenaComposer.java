/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SnowWarsLoadingArenaComposer
extends MessageComposer {
    private final int count;

    public SnowWarsLoadingArenaComposer(int count) {
        this.count = count;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3850);
        this.response.appendInt(this.count);
        this.response.appendInt(0);
        return this.response;
    }

    public int getCount() {
        return this.count;
    }
}

