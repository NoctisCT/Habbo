/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.habboway.nux;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class NuxAlertComposer
extends MessageComposer {
    private final String link;

    public NuxAlertComposer(String link) {
        this.link = link;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2023);
        this.response.appendString(this.link);
        return this.response;
    }

    public String getLink() {
        return this.link;
    }
}

