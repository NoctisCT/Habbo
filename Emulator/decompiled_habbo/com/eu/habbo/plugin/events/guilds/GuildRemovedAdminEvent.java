/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.guilds.GuildEvent;

public class GuildRemovedAdminEvent
extends GuildEvent {
    public final int userId;
    public final Habbo admin;

    public GuildRemovedAdminEvent(Guild guild, int userId, Habbo admin) {
        super(guild);
        this.userId = userId;
        this.admin = admin;
    }
}

