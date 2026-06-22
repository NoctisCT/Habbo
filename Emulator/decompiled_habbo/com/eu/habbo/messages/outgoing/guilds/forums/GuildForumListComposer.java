/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumDataComposer;
import java.util.Iterator;
import java.util.Set;

public class GuildForumListComposer
extends MessageComposer {
    private final Set<Guild> guilds;
    private final Habbo habbo;
    private final int mode;
    private final int index;

    public GuildForumListComposer(Set<Guild> guilds, Habbo habbo, int mode, int index) {
        this.guilds = guilds;
        this.habbo = habbo;
        this.mode = mode;
        this.index = index;
    }

    @Override
    protected ServerMessage composeInternal() {
        int i;
        this.response.init(3001);
        this.response.appendInt(this.mode);
        this.response.appendInt(this.guilds.size());
        this.response.appendInt(this.index);
        Iterator<Guild> it = this.guilds.iterator();
        int count = Math.min(this.guilds.size(), 20);
        this.response.appendInt(count);
        for (i = 0; i < this.index && it.hasNext(); ++i) {
            it.next();
        }
        for (i = 0; i < count && it.hasNext(); ++i) {
            GuildForumDataComposer.serializeForumData(this.response, it.next(), this.habbo);
        }
        return this.response;
    }

    public Set<Guild> getGuilds() {
        return this.guilds;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public int getMode() {
        return this.mode;
    }

    public int getIndex() {
        return this.index;
    }
}

