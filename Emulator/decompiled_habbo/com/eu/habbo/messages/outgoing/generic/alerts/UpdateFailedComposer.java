/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UpdateFailedComposer
extends MessageComposer {
    private final String message;

    public UpdateFailedComposer(String message) {
        this.message = message;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(156);
        this.response.appendString(this.message);
        return this.response;
    }

    public String getMessage() {
        return this.message;
    }
}

