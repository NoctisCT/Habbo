/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class TradeClosedComposer
extends MessageComposer {
    public static final int USER_CANCEL_TRADE = 0;
    public static final int ITEMS_NOT_FOUND = 1;
    private final int userId;
    private final int errorCode;

    public TradeClosedComposer(int userId, int errorCode) {
        this.userId = userId;
        this.errorCode = errorCode;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1373);
        this.response.appendInt(this.userId);
        this.response.appendInt(this.errorCode);
        return this.response;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

