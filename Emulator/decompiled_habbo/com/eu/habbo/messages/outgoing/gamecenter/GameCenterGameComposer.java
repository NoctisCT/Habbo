/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.gamecenter;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GameCenterGameComposer
extends MessageComposer {
    public static final int OK = 0;
    public static final int ERROR = 1;
    public final int gameId;
    public final int status;

    public GameCenterGameComposer(int gameId, int status) {
        this.gameId = gameId;
        this.status = status;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3805);
        this.response.appendInt(this.gameId);
        this.response.appendInt(this.status);
        return this.response;
    }

    public int getGameId() {
        return this.gameId;
    }

    public int getStatus() {
        return this.status;
    }
}

