/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class OpenRoomCreationWindowComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2064);
        return this.response;
    }
}

