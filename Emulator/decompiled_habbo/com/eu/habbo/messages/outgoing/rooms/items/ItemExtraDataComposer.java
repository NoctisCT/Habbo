/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ItemExtraDataComposer
extends MessageComposer {
    private final HabboItem item;

    public ItemExtraDataComposer(HabboItem item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2547);
        this.response.appendString("" + this.item.getId());
        this.item.serializeExtradata(this.response);
        return this.response;
    }

    public HabboItem getItem() {
        return this.item;
    }
}

