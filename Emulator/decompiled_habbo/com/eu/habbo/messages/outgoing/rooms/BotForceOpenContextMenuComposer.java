/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class BotForceOpenContextMenuComposer
extends MessageComposer {
    private final int botId;

    public BotForceOpenContextMenuComposer(int botId) {
        this.botId = botId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(296);
        this.response.appendInt(this.botId);
        return this.response;
    }

    public int getBotId() {
        return this.botId;
    }
}

