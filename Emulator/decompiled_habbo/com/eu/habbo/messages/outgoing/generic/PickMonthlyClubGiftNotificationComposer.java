/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.generic;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PickMonthlyClubGiftNotificationComposer
extends MessageComposer {
    private final int count;

    public PickMonthlyClubGiftNotificationComposer(int count) {
        this.count = count;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2188);
        this.response.appendInt(this.count);
        return this.response;
    }

    public int getCount() {
        return this.count;
    }
}

