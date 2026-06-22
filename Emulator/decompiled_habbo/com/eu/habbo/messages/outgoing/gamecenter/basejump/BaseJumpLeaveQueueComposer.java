/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.gamecenter.basejump;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class BaseJumpLeaveQueueComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1477);
        this.response.appendInt(3);
        return this.response;
    }
}

