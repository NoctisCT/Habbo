/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.gamecenter.basejump;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class BaseJumpJoinQueueComposer
extends MessageComposer {
    private final int gameId;

    public BaseJumpJoinQueueComposer(int gameId) {
        this.gameId = gameId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2260);
        this.response.appendInt(this.gameId);
        return this.response;
    }

    public int getGameId() {
        return this.gameId;
    }
}

