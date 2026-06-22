/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class AlertPurchaseUnavailableComposer
extends MessageComposer {
    public static final int ILLEGAL = 0;
    public static final int REQUIRES_CLUB = 1;
    private final int code;

    public AlertPurchaseUnavailableComposer(int code) {
        this.code = code;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3770);
        this.response.appendInt(this.code);
        return this.response;
    }

    public int getCode() {
        return this.code;
    }
}

