/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class IgnoredUsersComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(126);
        this.response.appendInt(0);
        return this.response;
    }
}

