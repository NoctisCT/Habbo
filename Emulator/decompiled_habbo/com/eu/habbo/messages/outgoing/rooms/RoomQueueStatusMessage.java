/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomQueueStatusMessage
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2208);
        this.response.appendInt(1);
        this.response.appendString("TEST");
        this.response.appendInt(94);
        this.response.appendInt(1);
        this.response.appendString("d");
        this.response.appendInt(1);
        return this.response;
    }
}

