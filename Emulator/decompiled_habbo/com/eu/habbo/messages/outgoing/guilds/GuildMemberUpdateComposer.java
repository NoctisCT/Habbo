/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuildMemberUpdateComposer
extends MessageComposer {
    private final Guild guild;
    private final GuildMember guildMember;

    public GuildMemberUpdateComposer(Guild guild, GuildMember guildMember) {
        this.guildMember = guildMember;
        this.guild = guild;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(265);
        this.response.appendInt(this.guild.getId());
        this.response.appendInt(this.guildMember.getRank().type);
        this.response.appendInt(this.guildMember.getUserId());
        this.response.appendString(this.guildMember.getUsername());
        this.response.appendString(this.guildMember.getLook());
        this.response.appendString((String)(this.guildMember.getRank().type != 0 ? "" + this.guildMember.getJoinDate() : ""));
        return this.response;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public GuildMember getGuildMember() {
        return this.guildMember;
    }
}

