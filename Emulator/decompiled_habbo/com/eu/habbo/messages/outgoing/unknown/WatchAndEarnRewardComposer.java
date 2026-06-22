/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class WatchAndEarnRewardComposer
extends MessageComposer {
    private final Item item;

    public WatchAndEarnRewardComposer(Item item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2125);
        this.response.appendString(this.item.getType().code);
        this.response.appendInt(this.item.getId());
        this.response.appendString(this.item.getName());
        this.response.appendString(this.item.getFullName());
        return this.response;
    }

    public Item getItem() {
        return this.item;
    }
}

