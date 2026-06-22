/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuildConfirmRemoveMemberComposer
extends MessageComposer {
    private int userId;
    private int furniCount;

    public GuildConfirmRemoveMemberComposer(int userId, int furniCount) {
        this.userId = userId;
        this.furniCount = furniCount;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1876);
        this.response.appendInt(this.userId);
        this.response.appendInt(this.furniCount);
        return this.response;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getFurniCount() {
        return this.furniCount;
    }
}

