/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RemoveGuildFromRoomComposer
extends MessageComposer {
    private int guildId;

    public RemoveGuildFromRoomComposer(int guildId) {
        this.guildId = guildId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3129);
        this.response.appendInt(this.guildId);
        return this.response;
    }

    public int getGuildId() {
        return this.guildId;
    }
}

