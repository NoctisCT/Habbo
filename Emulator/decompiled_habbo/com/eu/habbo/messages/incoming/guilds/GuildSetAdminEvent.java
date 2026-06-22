/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildMemberUpdateComposer;
import com.eu.habbo.plugin.events.guilds.GuildGivenAdminEvent;

public class GuildSetAdminEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int guildId = this.packet.readInt();
        int userId = this.packet.readInt();
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        if (guild != null && (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission(Permission.ACC_GUILD_ADMIN))) {
            Room room;
            Emulator.getGameEnvironment().getGuildManager().setAdmin(guild, userId);
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
            GuildGivenAdminEvent adminEvent = new GuildGivenAdminEvent(guild, userId, habbo, this.client.getHabbo());
            Emulator.getPluginManager().fireEvent(adminEvent);
            if (adminEvent.isCancelled()) {
                return;
            }
            if (habbo != null && (room = habbo.getHabboInfo().getCurrentRoom()) != null && room.getGuildId() == guildId) {
                room.refreshRightsForHabbo(habbo);
            }
            GuildMember guildMember = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, userId);
            this.client.sendResponse(new GuildMemberUpdateComposer(guild, guildMember));
        }
    }
}

