/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ItemIntStateComposer
extends MessageComposer {
    private final int id;
    private final int value;

    public ItemIntStateComposer(int id, int value) {
        this.id = id;
        this.value = value;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3431);
        this.response.appendInt(this.id);
        this.response.appendInt(this.value);
        return this.response;
    }

    public int getId() {
        return this.id;
    }

    public int getValue() {
        return this.value;
    }
}

