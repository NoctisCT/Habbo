/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.ForumThreadComment;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuildForumAddCommentComposer
extends MessageComposer {
    private final ForumThreadComment comment;

    public GuildForumAddCommentComposer(ForumThreadComment comment) {
        this.comment = comment;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2049);
        this.response.appendInt(this.comment.getThread().getGuildId());
        this.response.appendInt(this.comment.getThreadId());
        this.comment.serialize(this.response);
        return this.response;
    }

    public ForumThreadComment getComment() {
        return this.comment;
    }
}

