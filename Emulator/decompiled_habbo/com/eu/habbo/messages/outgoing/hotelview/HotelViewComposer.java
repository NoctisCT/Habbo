/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(122);
        return this.response;
    }
}

