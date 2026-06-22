/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.generic;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MinimailCountComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2803);
        this.response.appendInt(0);
        return this.response;
    }
}

