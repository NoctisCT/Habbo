/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UnknownStatusComposer
extends MessageComposer {
    public final int STATUS_ZERO = 0;
    public final int STATUS_ONE = 1;
    private final int status;

    public UnknownStatusComposer(int status) {
        this.status = status;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1243);
        this.response.appendInt(this.status);
        return this.response;
    }

    public int getStatus() {
        return this.status;
    }
}

