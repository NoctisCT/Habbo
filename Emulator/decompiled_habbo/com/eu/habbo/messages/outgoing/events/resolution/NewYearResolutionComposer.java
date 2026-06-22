/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.events.resolution;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class NewYearResolutionComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(66);
        this.response.appendInt(230);
        this.response.appendInt(2);
        this.response.appendInt(1);
        this.response.appendInt(1);
        this.response.appendString("NY2013RES");
        this.response.appendInt(3);
        this.response.appendInt(0);
        this.response.appendInt(2);
        this.response.appendInt(1);
        this.response.appendString("ADM");
        this.response.appendInt(2);
        this.response.appendInt(0);
        this.response.appendInt(1000);
        return this.response;
    }
}

