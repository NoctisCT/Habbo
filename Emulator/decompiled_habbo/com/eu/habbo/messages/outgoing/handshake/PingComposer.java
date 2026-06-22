/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PingComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3928);
        return this.response;
    }
}

