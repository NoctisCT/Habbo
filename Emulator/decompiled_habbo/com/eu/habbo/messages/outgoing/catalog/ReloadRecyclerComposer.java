/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ReloadRecyclerComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3433);
        this.response.appendInt(1);
        this.response.appendInt(0);
        return this.response;
    }
}

