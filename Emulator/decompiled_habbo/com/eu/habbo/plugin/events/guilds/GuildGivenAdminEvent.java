/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.guilds.GuildEvent;

public class GuildGivenAdminEvent
extends GuildEvent {
    public final int userId;
    public final Habbo habbo;
    public final Habbo admin;

    public GuildGivenAdminEvent(Guild guild, int userId, Habbo habbo, Habbo admin) {
        super(guild);
        this.userId = userId;
        this.habbo = habbo;
        this.admin = admin;
    }
}

