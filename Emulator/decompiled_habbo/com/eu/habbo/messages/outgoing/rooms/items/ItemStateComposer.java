/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ItemStateComposer
extends MessageComposer {
    private final HabboItem item;

    public ItemStateComposer(HabboItem item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2376);
        this.response.appendInt(this.item.getId());
        try {
            int state = Integer.parseInt(this.item.getExtradata());
            this.response.appendInt(state);
        }
        catch (Exception e) {
            this.response.appendInt(0);
        }
        return this.response;
    }

    public HabboItem getItem() {
        return this.item;
    }
}

