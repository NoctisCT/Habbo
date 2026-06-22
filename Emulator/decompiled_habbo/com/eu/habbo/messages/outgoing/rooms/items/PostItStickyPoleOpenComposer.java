/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PostItStickyPoleOpenComposer
extends MessageComposer {
    private final HabboItem item;

    public PostItStickyPoleOpenComposer(HabboItem item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2366);
        this.response.appendInt(this.item == null ? -1234 : this.item.getId());
        this.response.appendString("");
        return this.response;
    }

    public HabboItem getItem() {
        return this.item;
    }
}

