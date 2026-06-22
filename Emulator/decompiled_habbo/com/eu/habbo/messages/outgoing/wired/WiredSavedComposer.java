/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class WiredSavedComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1155);
        return this.response;
    }
}

