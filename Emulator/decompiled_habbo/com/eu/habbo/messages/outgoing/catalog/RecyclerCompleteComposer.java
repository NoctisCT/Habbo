/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RecyclerCompleteComposer
extends MessageComposer {
    public static final int RECYCLING_COMPLETE = 1;
    public static final int RECYCLING_CLOSED = 2;
    private final int code;

    public RecyclerCompleteComposer(int code) {
        this.code = code;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(468);
        this.response.appendInt(this.code);
        this.response.appendInt(0);
        return this.response;
    }

    public int getCode() {
        return this.code;
    }
}

