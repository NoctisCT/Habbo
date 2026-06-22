/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HideDoorbellComposer
extends MessageComposer {
    private final String username;

    public HideDoorbellComposer(String username) {
        this.username = username;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3783);
        this.response.appendString(this.username);
        return this.response;
    }

    public String getUsername() {
        return this.username;
    }
}

