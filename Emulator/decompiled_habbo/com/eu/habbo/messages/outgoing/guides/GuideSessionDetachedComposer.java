/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuideSessionDetachedComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(138);
        return this.response;
    }
}

