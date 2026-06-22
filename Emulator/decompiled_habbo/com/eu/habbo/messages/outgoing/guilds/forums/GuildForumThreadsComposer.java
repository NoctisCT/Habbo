/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.handshake.ConnectionErrorComposer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class GuildForumThreadsComposer
extends MessageComposer {
    public final Guild guild;
    public final int index;

    public GuildForumThreadsComposer(Guild guild, int index) {
        this.guild = guild;
        this.index = index;
    }

    @Override
    protected ServerMessage composeInternal() {
        int i;
        ArrayList<ForumThread> threads;
        try {
            threads = new ArrayList<ForumThread>(ForumThread.getByGuildId(this.guild.getId()));
        }
        catch (Exception e) {
            return new ConnectionErrorComposer(500).compose();
        }
        threads.sort(Comparator.comparingInt(o -> o.isPinned() ? Integer.MAX_VALUE : o.getUpdatedAt()));
        Collections.reverse(threads);
        Iterator<ForumThread> it = threads.iterator();
        int count = Math.min(threads.size(), 20);
        this.response.init(1073);
        this.response.appendInt(this.guild.getId());
        this.response.appendInt(this.index);
        this.response.appendInt(count);
        for (i = 0; i < this.index && it.hasNext(); ++i) {
            it.next();
        }
        for (i = 0; i < count && it.hasNext(); ++i) {
            it.next().serialize(this.response);
        }
        return this.response;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public int getIndex() {
        return this.index;
    }
}

