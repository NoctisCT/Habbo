/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class AddWallItemComposer
extends MessageComposer {
    private final HabboItem item;
    private final String itemOwnerName;

    public AddWallItemComposer(HabboItem item, String itemOwnerName) {
        this.item = item;
        this.itemOwnerName = itemOwnerName;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2187);
        this.item.serializeWallData(this.response);
        this.response.appendString(this.itemOwnerName);
        return this.response;
    }

    public HabboItem getItem() {
        return this.item;
    }

    public String getItemOwnerName() {
        return this.itemOwnerName;
    }
}

