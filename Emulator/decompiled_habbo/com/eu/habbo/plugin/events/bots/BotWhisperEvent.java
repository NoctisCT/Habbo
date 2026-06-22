/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.bots.BotChatEvent;

public class BotWhisperEvent
extends BotChatEvent {
    public Habbo target;

    public BotWhisperEvent(Bot bot, String message, Habbo target) {
        super(bot, message);
        this.target = target;
    }
}

