/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomScoreComposer
extends MessageComposer {
    private final int score;
    private final boolean canVote;

    public RoomScoreComposer(int score, boolean canVote) {
        this.score = score;
        this.canVote = canVote;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(482);
        this.response.appendInt(this.score);
        this.response.appendBoolean(this.canVote);
        return this.response;
    }

    public int getScore() {
        return this.score;
    }

    public boolean isCanVote() {
        return this.canVote;
    }
}

