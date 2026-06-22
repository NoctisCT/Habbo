/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.guilds.GuildEvent;

public class GuildRemovedFavoriteEvent
extends GuildEvent {
    public final Habbo habbo;

    public GuildRemovedFavoriteEvent(Guild guild, Habbo habbo) {
        super(guild);
        this.habbo = habbo;
    }
}

