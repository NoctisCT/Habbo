/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class InventoryRefreshComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3151);
        return this.response;
    }
}

