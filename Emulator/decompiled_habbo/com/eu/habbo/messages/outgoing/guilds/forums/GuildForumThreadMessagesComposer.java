/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuildForumThreadMessagesComposer
extends MessageComposer {
    public final ForumThread thread;

    public GuildForumThreadMessagesComposer(ForumThread thread) {
        this.thread = thread;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1862);
        this.response.appendInt(this.thread.getGuildId());
        this.thread.serialize(this.response);
        return this.response;
    }

    public ForumThread getThread() {
        return this.thread;
    }
}

