/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.quests;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class QuestExpiredComposer
extends MessageComposer {
    private final boolean expired;

    public QuestExpiredComposer(boolean expired) {
        this.expired = expired;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3027);
        this.response.appendBoolean(this.expired);
        return this.response;
    }

    public boolean isExpired() {
        return this.expired;
    }
}

