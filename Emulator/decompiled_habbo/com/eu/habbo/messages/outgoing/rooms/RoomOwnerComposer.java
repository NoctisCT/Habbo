/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomOwnerComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(339);
        return this.response;
    }
}

