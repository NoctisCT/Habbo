/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.gamecenter;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GameCenterAccountInfoComposer
extends MessageComposer {
    private final int gameId;
    private final int gamesLeft;

    public GameCenterAccountInfoComposer(int gameId, int gamesLeft) {
        this.gameId = gameId;
        this.gamesLeft = gamesLeft;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2893);
        this.response.appendInt(this.gameId);
        this.response.appendInt(this.gamesLeft);
        this.response.appendInt(1);
        return this.response;
    }

    public int getGameId() {
        return this.gameId;
    }

    public int getGamesLeft() {
        return this.gamesLeft;
    }
}

