/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RemoveHabboItemComposer
extends MessageComposer {
    private final int itemId;

    public RemoveHabboItemComposer(int itemId) {
        this.itemId = itemId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(159);
        this.response.appendInt(this.itemId);
        return this.response;
    }

    public int getItemId() {
        return this.itemId;
    }
}

