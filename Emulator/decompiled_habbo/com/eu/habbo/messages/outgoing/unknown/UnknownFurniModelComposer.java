/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UnknownFurniModelComposer
extends MessageComposer {
    private final HabboItem item;
    private final int unknownInt;

    public UnknownFurniModelComposer(HabboItem item, int unknownInt) {
        this.item = item;
        this.unknownInt = unknownInt;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1501);
        this.response.appendInt(this.item.getId());
        this.response.appendInt(this.unknownInt);
        return this.response;
    }

    public HabboItem getItem() {
        return this.item;
    }

    public int getUnknownInt() {
        return this.unknownInt;
    }
}

