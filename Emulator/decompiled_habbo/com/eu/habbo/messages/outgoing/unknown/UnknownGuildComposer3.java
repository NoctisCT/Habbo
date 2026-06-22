/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UnknownGuildComposer3
extends MessageComposer {
    private final int userId;

    public UnknownGuildComposer3(int userId) {
        this.userId = userId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(876);
        this.response.appendInt(this.userId);
        return this.response;
    }

    public int getUserId() {
        return this.userId;
    }
}

