/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UserBCLimitsComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(-1);
        this.response.appendInt(0);
        this.response.appendInt(500);
        this.response.appendInt(0);
        return this.response;
    }
}

