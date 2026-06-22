/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ModToolIssueResponseAlertComposer
extends MessageComposer {
    private final String message;

    public ModToolIssueResponseAlertComposer(String message) {
        this.message = message;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3796);
        this.response.appendString(this.message);
        return this.response;
    }

    public String getMessage() {
        return this.message;
    }
}

