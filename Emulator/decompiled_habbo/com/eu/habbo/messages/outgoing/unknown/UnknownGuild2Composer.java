/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UnknownGuild2Composer
extends MessageComposer {
    private final int guildId;

    public UnknownGuild2Composer(int guildId) {
        this.guildId = guildId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1459);
        this.response.appendInt(this.guildId);
        return this.response;
    }

    public int getGuildId() {
        return this.guildId;
    }
}

