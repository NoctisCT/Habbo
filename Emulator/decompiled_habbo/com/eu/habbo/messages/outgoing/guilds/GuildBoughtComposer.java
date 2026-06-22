/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuildBoughtComposer
extends MessageComposer {
    private final Guild guild;

    public GuildBoughtComposer(Guild guild) {
        this.guild = guild;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2808);
        this.response.appendInt(this.guild.getRoomId());
        this.response.appendInt(this.guild.getId());
        return this.response;
    }

    public Guild getGuild() {
        return this.guild;
    }
}

