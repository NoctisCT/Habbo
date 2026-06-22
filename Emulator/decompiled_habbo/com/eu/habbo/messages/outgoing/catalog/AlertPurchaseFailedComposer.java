/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class AlertPurchaseFailedComposer
extends MessageComposer {
    public static final int SERVER_ERROR = 0;
    public static final int ALREADY_HAVE_BADGE = 1;
    private final int error;

    public AlertPurchaseFailedComposer(int error) {
        this.error = error;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1404);
        this.response.appendInt(this.error);
        return this.response;
    }

    public int getError() {
        return this.error;
    }
}

