/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewHideCommunityVoteButtonComposer
extends MessageComposer {
    private final boolean unknownBoolean;

    public HotelViewHideCommunityVoteButtonComposer(boolean unknownBoolean) {
        this.unknownBoolean = unknownBoolean;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1435);
        this.response.appendBoolean(this.unknownBoolean);
        return this.response;
    }

    public boolean isUnknownBoolean() {
        return this.unknownBoolean;
    }
}

