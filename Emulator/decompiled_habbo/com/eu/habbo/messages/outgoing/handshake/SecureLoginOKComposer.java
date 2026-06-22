/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SecureLoginOKComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2491);
        return this.response;
    }
}

