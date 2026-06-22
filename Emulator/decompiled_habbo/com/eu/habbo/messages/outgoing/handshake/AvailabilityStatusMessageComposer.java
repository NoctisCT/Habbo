/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class AvailabilityStatusMessageComposer
extends MessageComposer {
    private final boolean isOpen;
    private final boolean isShuttingDown;
    private final boolean isAuthenticHabbo;

    public AvailabilityStatusMessageComposer(boolean isOpen, boolean isShuttingDown, boolean isAuthenticHabbo) {
        this.isOpen = isOpen;
        this.isShuttingDown = isShuttingDown;
        this.isAuthenticHabbo = isAuthenticHabbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2033);
        this.response.appendBoolean(this.isOpen);
        this.response.appendBoolean(this.isShuttingDown);
        this.response.appendBoolean(this.isAuthenticHabbo);
        return this.response;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public boolean isShuttingDown() {
        return this.isShuttingDown;
    }

    public boolean isAuthenticHabbo() {
        return this.isAuthenticHabbo;
    }
}

