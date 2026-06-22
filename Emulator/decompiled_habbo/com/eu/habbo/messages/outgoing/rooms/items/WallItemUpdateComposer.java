/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class WallItemUpdateComposer
extends MessageComposer {
    private final HabboItem item;

    public WallItemUpdateComposer(HabboItem item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2009);
        this.item.serializeWallData(this.response);
        this.response.appendString("" + this.item.getUserId());
        return this.response;
    }

    public HabboItem getItem() {
        return this.item;
    }
}

