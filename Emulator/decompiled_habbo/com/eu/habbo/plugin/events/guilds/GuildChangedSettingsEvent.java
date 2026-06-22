/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.plugin.events.guilds.GuildEvent;

public class GuildChangedSettingsEvent
extends GuildEvent {
    public int state;
    public boolean rights;

    public GuildChangedSettingsEvent(Guild guild, int state, boolean rights) {
        super(guild);
        this.state = state;
        this.rights = rights;
    }
}

