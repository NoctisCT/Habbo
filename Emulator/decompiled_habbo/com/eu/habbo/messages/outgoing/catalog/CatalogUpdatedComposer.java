/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CatalogUpdatedComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1866);
        this.response.appendBoolean(false);
        return this.response;
    }
}

