/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PongComposer
extends MessageComposer {
    private final int id;

    public PongComposer(int id) {
        this.id = id;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(10);
        this.response.appendInt(this.id);
        return this.response;
    }

    public int getId() {
        return this.id;
    }
}

