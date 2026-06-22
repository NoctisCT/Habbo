/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SnowWarsResetTimerComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(294);
        this.response.appendInt(100);
        return this.response;
    }
}

