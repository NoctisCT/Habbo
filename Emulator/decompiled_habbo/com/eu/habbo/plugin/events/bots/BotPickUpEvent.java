/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.bots.BotEvent;

public class BotPickUpEvent
extends BotEvent {
    public final Habbo picker;

    public BotPickUpEvent(Bot bot, Habbo picker) {
        super(bot);
        this.picker = picker;
    }
}

