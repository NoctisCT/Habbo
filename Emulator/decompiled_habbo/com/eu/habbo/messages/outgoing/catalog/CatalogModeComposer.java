/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CatalogModeComposer
extends MessageComposer {
    private final int mode;

    public CatalogModeComposer(int mode) {
        this.mode = mode;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3828);
        this.response.appendInt(this.mode);
        return this.response;
    }

    public int getMode() {
        return this.mode;
    }
}

