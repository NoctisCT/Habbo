/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PetPackageComposer
extends MessageComposer {
    private final HabboItem item;

    public PetPackageComposer(HabboItem item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2380);
        this.response.appendInt(this.item.getId());
        return this.response;
    }

    public HabboItem getItem() {
        return this.item;
    }
}

