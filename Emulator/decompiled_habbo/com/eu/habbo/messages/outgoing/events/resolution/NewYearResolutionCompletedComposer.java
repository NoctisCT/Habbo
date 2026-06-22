/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.events.resolution;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class NewYearResolutionCompletedComposer
extends MessageComposer {
    public final String badge;

    public NewYearResolutionCompletedComposer(String badge) {
        this.badge = badge;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(740);
        this.response.appendString(this.badge);
        this.response.appendString(this.badge);
        return this.response;
    }

    public String getBadge() {
        return this.badge;
    }
}

