/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UserAchievementScoreComposer
extends MessageComposer {
    private final Habbo habbo;

    public UserAchievementScoreComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1968);
        this.response.appendInt(this.habbo.getHabboStats().getAchievementScore());
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

