/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MutedWhisperComposer
extends MessageComposer {
    private final int seconds;

    public MutedWhisperComposer(int seconds) {
        this.seconds = seconds;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(826);
        this.response.appendInt(this.seconds);
        return this.response;
    }

    public int getSeconds() {
        return this.seconds;
    }
}

