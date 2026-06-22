/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class YoutubeStateChangeComposer
extends MessageComposer {
    private final int furniId;
    private final int state;

    public YoutubeStateChangeComposer(int furniId, int state) {
        this.furniId = furniId;
        this.state = state;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1554);
        this.response.appendInt(this.furniId);
        this.response.appendInt(this.state);
        return this.response;
    }

    public int getFurniId() {
        return this.furniId;
    }

    public int getState() {
        return this.state;
    }
}

