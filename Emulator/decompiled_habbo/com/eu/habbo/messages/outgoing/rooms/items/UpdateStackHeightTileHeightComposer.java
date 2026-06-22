/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UpdateStackHeightTileHeightComposer
extends MessageComposer {
    private final HabboItem item;
    private final int height;

    public UpdateStackHeightTileHeightComposer(HabboItem item, int height) {
        this.item = item;
        this.height = height;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2816);
        this.response.appendInt(this.item.getId());
        this.response.appendInt(this.height);
        return this.response;
    }

    public HabboItem getItem() {
        return this.item;
    }

    public int getHeight() {
        return this.height;
    }
}

