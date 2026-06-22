/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class WelcomeGiftErrorComposer
extends MessageComposer {
    public static final int EMAIL_INVALID = 0;
    public static final int EMAIL_LENGTH_EXCEEDED = 1;
    public static final int EMAIL_IN_USE = 3;
    public static final int EMAIL_LIMIT_CHANGE = 4;
    private final int error;

    public WelcomeGiftErrorComposer(int error) {
        this.error = error;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2293);
        this.response.appendInt(this.error);
        return this.response;
    }

    public int getError() {
        return this.error;
    }
}

