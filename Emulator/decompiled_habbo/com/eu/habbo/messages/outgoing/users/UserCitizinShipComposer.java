/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UserCitizinShipComposer
extends MessageComposer {
    private final String name;

    public UserCitizinShipComposer(String name) {
        this.name = name;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1203);
        this.response.appendString(this.name);
        this.response.appendInt(4);
        this.response.appendInt(4);
        return this.response;
    }

    public String getName() {
        return this.name;
    }
}

