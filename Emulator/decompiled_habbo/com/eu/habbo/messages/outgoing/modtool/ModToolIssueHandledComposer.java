/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ModToolIssueHandledComposer
extends MessageComposer {
    public static final int HANDLED = 0;
    public static final int USELESS = 1;
    public static final int ABUSIVE = 2;
    private final int code;
    private final String message;

    public ModToolIssueHandledComposer(int code) {
        this.code = code;
        this.message = "";
    }

    public ModToolIssueHandledComposer(String message) {
        this.code = 0;
        this.message = message;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(934);
        this.response.appendInt(this.code);
        this.response.appendString(this.message);
        return this.response;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

