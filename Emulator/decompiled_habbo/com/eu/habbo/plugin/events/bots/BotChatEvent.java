/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.plugin.events.bots.BotEvent;

public abstract class BotChatEvent
extends BotEvent {
    public String message;

    public BotChatEvent(Bot bot, String message) {
        super(bot);
        this.message = message;
    }
}

