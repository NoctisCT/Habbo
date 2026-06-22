/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GenericAlertComposer
extends MessageComposer {
    private final String message;

    public GenericAlertComposer(String message) {
        this.message = message;
    }

    public GenericAlertComposer(String message, Habbo habbo) {
        this.message = message.replace("%username%", habbo.getHabboInfo().getUsername());
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3801);
        this.response.appendString(this.message);
        return this.response;
    }

    public String getMessage() {
        return this.message;
    }
}

