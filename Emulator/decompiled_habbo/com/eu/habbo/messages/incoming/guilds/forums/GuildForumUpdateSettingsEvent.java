/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.SettingsState;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumDataComposer;
import com.eu.habbo.messages.outgoing.handshake.ConnectionErrorComposer;

public class GuildForumUpdateSettingsEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int guildId = this.packet.readInt();
        int canRead = this.packet.readInt();
        int postMessages = this.packet.readInt();
        int postThreads = this.packet.readInt();
        int modForum = this.packet.readInt();
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        if (guild == null) {
            this.client.sendResponse(new ConnectionErrorComposer(404));
            return;
        }
        if (guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId()) {
            this.client.sendResponse(new ConnectionErrorComposer(403));
            return;
        }
        guild.setReadForum(SettingsState.fromValue(canRead));
        guild.setPostMessages(SettingsState.fromValue(postMessages));
        guild.setPostThreads(SettingsState.fromValue(postThreads));
        guild.setModForum(SettingsState.fromValue(modForum));
        guild.needsUpdate = true;
        Emulator.getThreading().run(guild);
        this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FORUMS_FORUM_SETTINGS_UPDATED.key).compose());
        this.client.sendResponse(new GuildForumDataComposer(guild, this.client.getHabbo()));
    }
}

