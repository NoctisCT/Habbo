/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UnknownHintComposer
extends MessageComposer {
    private final String key;

    public UnknownHintComposer(String key) {
        this.key = key;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1787);
        this.response.appendString(this.key);
        return this.response;
    }

    public String getKey() {
        return this.key;
    }
}

