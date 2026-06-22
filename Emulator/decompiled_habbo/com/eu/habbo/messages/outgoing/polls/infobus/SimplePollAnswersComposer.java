/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.polls.infobus;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SimplePollAnswersComposer
extends MessageComposer {
    private final int no;
    private final int yes;

    public SimplePollAnswersComposer(int no, int yes) {
        this.no = no;
        this.yes = yes;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1066);
        this.response.appendInt(-1);
        this.response.appendInt(2);
        this.response.appendString("0");
        this.response.appendInt(this.no);
        this.response.appendString("1");
        this.response.appendInt(this.yes);
        return this.response;
    }

    public int getNo() {
        return this.no;
    }

    public int getYes() {
        return this.yes;
    }
}

