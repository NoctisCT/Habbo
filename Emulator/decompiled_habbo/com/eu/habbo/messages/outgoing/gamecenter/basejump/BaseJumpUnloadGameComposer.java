/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.gamecenter.basejump;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class BaseJumpUnloadGameComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1715);
        this.response.appendInt(3);
        this.response.appendString("basejump");
        return this.response;
    }
}

