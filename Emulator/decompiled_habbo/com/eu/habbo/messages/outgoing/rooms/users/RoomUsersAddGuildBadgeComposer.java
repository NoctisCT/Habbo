/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomUsersAddGuildBadgeComposer
extends MessageComposer {
    private final Guild guild;

    public RoomUsersAddGuildBadgeComposer(Guild guild) {
        this.guild = guild;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2402);
        this.response.appendInt(1);
        this.response.appendInt(this.guild.getId());
        this.response.appendString(this.guild.getBadge());
        return this.response;
    }

    public Guild getGuild() {
        return this.guild;
    }
}

