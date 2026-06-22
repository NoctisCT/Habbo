/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomPaintComposer
extends MessageComposer {
    private final String type;
    private final String value;

    public RoomPaintComposer(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2454);
        this.response.appendString(this.type);
        this.response.appendString(this.value);
        return this.response;
    }

    public String getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }
}

