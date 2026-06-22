/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class TradeCompleteComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3128);
        return this.response;
    }
}

