/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.habboway;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HabboWayQuizComposer2
extends MessageComposer {
    public final String name;
    public final int[] items;

    public HabboWayQuizComposer2(String name, int[] items) {
        this.name = name;
        this.items = items;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2927);
        this.response.appendString(this.name);
        this.response.appendInt(this.items.length);
        for (int item : this.items) {
            this.response.appendInt(item);
        }
        return this.response;
    }

    public String getName() {
        return this.name;
    }

    public int[] getItems() {
        return this.items;
    }
}

