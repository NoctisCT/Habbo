/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class EnableNotificationsComposer
extends MessageComposer {
    private final boolean enabled;

    public EnableNotificationsComposer(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3284);
        this.response.appendBoolean(this.enabled);
        return this.response;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

