/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UnknownAvatarEditorComposer
extends MessageComposer {
    private final int type;

    public UnknownAvatarEditorComposer(int type) {
        this.type = type;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3473);
        this.response.appendInt(this.type);
        return this.response;
    }

    public int getType() {
        return this.type;
    }
}

