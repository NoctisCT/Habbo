/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.plugin.events.guilds.GuildEvent;

public class GuildChangedColorsEvent
extends GuildEvent {
    public int colorOne;
    public int colorTwo;

    public GuildChangedColorsEvent(Guild guild, int colorOne, int colorTwo) {
        super(guild);
        this.colorOne = colorOne;
        this.colorTwo = colorTwo;
    }
}

