/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class BullyReportClosedComposer
extends MessageComposer {
    public static final int CLOSED = 1;
    public static final int MISUSE = 2;
    public final int code;

    public BullyReportClosedComposer(int code) {
        this.code = code;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2674);
        this.response.appendInt(this.code);
        return this.response;
    }

    public int getCode() {
        return this.code;
    }
}

