/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.guilds.GuildEvent;

public class GuildDeletedEvent
extends GuildEvent {
    public final Habbo deleter;

    public GuildDeletedEvent(Guild guild, Habbo deleter) {
        super(guild);
        this.deleter = deleter;
    }
}

