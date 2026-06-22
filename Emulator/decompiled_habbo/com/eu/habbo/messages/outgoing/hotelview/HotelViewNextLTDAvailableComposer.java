/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewNextLTDAvailableComposer
extends MessageComposer {
    private final int time;
    private final int pageId;
    private final int itemId;
    private final String itemName;

    public HotelViewNextLTDAvailableComposer(int time, int pageId, int itemId, String itemName) {
        this.time = time;
        this.pageId = pageId;
        this.itemId = itemId;
        this.itemName = itemName;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(44);
        this.response.appendInt(this.time);
        this.response.appendInt(this.pageId);
        this.response.appendInt(this.itemId);
        this.response.appendString(this.itemName);
        return this.response;
    }

    public int getTime() {
        return this.time;
    }

    public int getPageId() {
        return this.pageId;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getItemName() {
        return this.itemName;
    }
}

