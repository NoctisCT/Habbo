/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class NewNavigatorMetaDataComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3052);
        this.response.appendInt(4);
        this.response.appendString("official_view");
        this.response.appendInt(0);
        this.response.appendString("hotel_view");
        this.response.appendInt(0);
        this.response.appendString("roomads_view");
        this.response.appendInt(0);
        this.response.appendString("myworld_view");
        this.response.appendInt(0);
        return this.response;
    }
}

