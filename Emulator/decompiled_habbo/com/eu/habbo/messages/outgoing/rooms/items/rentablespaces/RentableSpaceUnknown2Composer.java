/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.rentablespaces;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RentableSpaceUnknown2Composer
extends MessageComposer {
    private final int itemId;

    public RentableSpaceUnknown2Composer(int itemId) {
        this.itemId = itemId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1868);
        this.response.appendInt(this.itemId);
        return this.response;
    }

    public int getItemId() {
        return this.itemId;
    }
}

