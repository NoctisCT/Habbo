/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomCategoryUpdateMessageComposer
extends MessageComposer {
    private final int unknownInt1;

    public RoomCategoryUpdateMessageComposer(int unknownInt1) {
        this.unknownInt1 = unknownInt1;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3896);
        this.response.appendInt(this.unknownInt1);
        return this.response;
    }

    public int getUnknownInt1() {
        return this.unknownInt1;
    }
}

