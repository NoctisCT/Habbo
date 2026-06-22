/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CanCreateEventComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2599);
        this.response.appendBoolean(true);
        this.response.appendInt(0);
        return this.response;
    }
}

