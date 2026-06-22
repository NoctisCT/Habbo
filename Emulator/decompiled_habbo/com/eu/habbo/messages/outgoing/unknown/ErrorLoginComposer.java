/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ErrorLoginComposer
extends MessageComposer {
    private final int errorCode;

    public ErrorLoginComposer(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(4000);
        this.response.appendInt(this.errorCode);
        return this.response;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

