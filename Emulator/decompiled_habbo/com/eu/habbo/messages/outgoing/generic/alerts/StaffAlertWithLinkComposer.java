/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class StaffAlertWithLinkComposer
extends MessageComposer {
    private final String message;
    private final String link;

    public StaffAlertWithLinkComposer(String message, String link) {
        this.message = message;
        this.link = link;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2030);
        this.response.appendString(this.message);
        this.response.appendString(this.link);
        return this.response;
    }

    public String getMessage() {
        return this.message;
    }

    public String getLink() {
        return this.link;
    }
}

