/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.polls.infobus;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SimplePollStartComposer
extends MessageComposer {
    public final int duration;
    public final String question;

    public SimplePollStartComposer(int duration, String question) {
        this.duration = duration;
        this.question = question;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2665);
        this.response.appendString(this.question);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(this.duration);
        this.response.appendInt(-1);
        this.response.appendInt(0);
        this.response.appendInt(3);
        this.response.appendString(this.question);
        return this.response;
    }

    public int getDuration() {
        return this.duration;
    }

    public String getQuestion() {
        return this.question;
    }
}

