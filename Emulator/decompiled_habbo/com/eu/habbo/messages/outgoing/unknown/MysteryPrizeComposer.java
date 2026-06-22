/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MysteryPrizeComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(427);
        this.response.appendString("s");
        this.response.appendInt(230);
        return this.response;
    }
}

