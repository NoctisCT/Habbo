/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.polls;

import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PollStartComposer
extends MessageComposer {
    private final Poll poll;

    public PollStartComposer(Poll poll) {
        this.poll = poll;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3785);
        this.response.appendInt(this.poll.id);
        this.response.appendString(this.poll.title);
        this.response.appendString(this.poll.thanksMessage);
        this.response.appendString(this.poll.title);
        return this.response;
    }

    public Poll getPoll() {
        return this.poll;
    }
}

