/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.plugin.events.bots.BotEvent;

public class BotSavedNameEvent
extends BotEvent {
    public String name;

    public BotSavedNameEvent(Bot bot, String name) {
        super(bot);
        this.name = name;
    }
}

