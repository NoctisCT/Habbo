/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RemoveBotComposer
extends MessageComposer {
    private final Bot bot;

    public RemoveBotComposer(Bot bot) {
        this.bot = bot;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(233);
        this.response.appendInt(this.bot.getId());
        return this.response;
    }

    public Bot getBot() {
        return this.bot;
    }
}

