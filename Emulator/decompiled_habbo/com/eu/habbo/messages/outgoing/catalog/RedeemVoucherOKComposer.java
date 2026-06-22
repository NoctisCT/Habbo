/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RedeemVoucherOKComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3336);
        this.response.appendString("");
        this.response.appendString("");
        return this.response;
    }
}

