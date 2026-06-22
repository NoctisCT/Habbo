/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.set.hash.THashSet;

public class ClubGiftReceivedComposer
extends MessageComposer {
    private final String name;
    private final THashSet<Item> items;

    public ClubGiftReceivedComposer(String name, THashSet<Item> items) {
        this.name = name;
        this.items = items;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(659);
        this.response.appendString(this.name);
        this.response.appendInt(this.items.size());
        for (Item item : this.items) {
            item.serialize(this.response);
        }
        return this.response;
    }

    public String getName() {
        return this.name;
    }

    public THashSet<Item> getItems() {
        return this.items;
    }
}

