/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.habbohotel.guilds.forums.ForumThreadState;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumThreadMessagesComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumThreadsComposer;
import com.eu.habbo.messages.outgoing.handshake.ConnectionErrorComposer;

public class GuildForumModerateThreadEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        boolean isGuildAdmin;
        int guildId = this.packet.readInt();
        int threadId = this.packet.readInt();
        int state = this.packet.readInt();
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        ForumThread thread = ForumThread.getById(threadId);
        if (guild == null || thread == null) {
            this.client.sendResponse(new ConnectionErrorComposer(404));
            return;
        }
        GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, this.client.getHabbo().getHabboInfo().getId());
        boolean hasStaffPerms = this.client.getHabbo().hasPermission(Permission.ACC_MODTOOL_TICKET_Q);
        boolean bl = isGuildAdmin = guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || member.getRank().equals((Object)GuildRank.ADMIN);
        if (member == null) {
            this.client.sendResponse(new ConnectionErrorComposer(401));
            return;
        }
        if (!isGuildAdmin && !hasStaffPerms) {
            this.client.sendResponse(new ConnectionErrorComposer(403));
            return;
        }
        thread.setState(ForumThreadState.fromValue(state));
        thread.run();
        switch (state) {
            case 10: 
            case 20: {
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FORUMS_THREAD_HIDDEN.key).compose());
                break;
            }
            case 1: {
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FORUMS_THREAD_RESTORED.key).compose());
            }
        }
        this.client.sendResponse(new GuildForumThreadMessagesComposer(thread));
        this.client.sendResponse(new GuildForumThreadsComposer(guild, 0));
    }
}

