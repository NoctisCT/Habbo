/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CraftingRecipesAvailableComposer
extends MessageComposer {
    private final int count;
    private final boolean found;

    public CraftingRecipesAvailableComposer(int count, boolean found) {
        this.count = count;
        this.found = found;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2124);
        this.response.appendInt((this.found ? -1 : 0) + this.count);
        this.response.appendBoolean(this.found);
        return this.response;
    }

    public int getCount() {
        return this.count;
    }

    public boolean isFound() {
        return this.found;
    }
}

