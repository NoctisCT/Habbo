/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildListComposer;
import gnu.trove.set.hash.THashSet;

public class RequestOwnGuildsEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        THashSet<Guild> guilds = new THashSet<Guild>();
        for (int i : this.client.getHabbo().getHabboStats().guilds) {
            Guild g;
            if (i == 0 || (g = Emulator.getGameEnvironment().getGuildManager().getGuild(i)) == null) continue;
            guilds.add(g);
        }
        this.client.sendResponse(new GuildListComposer(guilds, this.client.getHabbo()));
    }
}

