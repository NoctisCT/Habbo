/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class FavoriteRoomChangedComposer
extends MessageComposer {
    private final int roomId;
    private final boolean favorite;

    public FavoriteRoomChangedComposer(int roomId, boolean favorite) {
        this.roomId = roomId;
        this.favorite = favorite;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2524);
        this.response.appendInt(this.roomId);
        this.response.appendBoolean(this.favorite);
        return this.response;
    }

    public int getRoomId() {
        return this.roomId;
    }

    public boolean isFavorite() {
        return this.favorite;
    }
}

