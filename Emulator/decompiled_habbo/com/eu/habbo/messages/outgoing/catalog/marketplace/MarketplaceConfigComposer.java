/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MarketplaceConfigComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1823);
        this.response.appendBoolean(true);
        this.response.appendInt(1);
        this.response.appendInt(10);
        this.response.appendInt(5);
        this.response.appendInt(1);
        this.response.appendInt(1000000);
        this.response.appendInt(48);
        this.response.appendInt(7);
        return this.response;
    }
}

