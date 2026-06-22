/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.ForumThreadComment;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PostUpdateMessageComposer
extends MessageComposer {
    public final int guildId;
    public final int threadId;
    public final ForumThreadComment comment;

    public PostUpdateMessageComposer(int guildId, int threadId, ForumThreadComment comment) {
        this.guildId = guildId;
        this.threadId = threadId;
        this.comment = comment;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(324);
        this.response.appendInt(this.guildId);
        this.response.appendInt(this.threadId);
        this.comment.serialize(this.response);
        return this.response;
    }

    public int getGuildId() {
        return this.guildId;
    }

    public int getThreadId() {
        return this.threadId;
    }

    public ForumThreadComment getComment() {
        return this.comment;
    }
}

