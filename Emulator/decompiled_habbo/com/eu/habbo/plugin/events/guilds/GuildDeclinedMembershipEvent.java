/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.guilds.GuildEvent;

public class GuildDeclinedMembershipEvent
extends GuildEvent {
    public final int userId;
    public final Habbo user;
    public final Habbo admin;

    public GuildDeclinedMembershipEvent(Guild guild, int userId, Habbo user, Habbo admin) {
        super(guild);
        this.userId = userId;
        this.user = user;
        this.admin = admin;
    }
}

