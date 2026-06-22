/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RedeemVoucherErrorComposer
extends MessageComposer {
    public static final int INVALID_CODE = 0;
    public static final int TECHNICAL_ERROR = 1;
    private final int code;

    public RedeemVoucherErrorComposer(int code) {
        this.code = code;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(714);
        this.response.appendString("" + this.code);
        return this.response;
    }

    public int getCode() {
        return this.code;
    }
}

