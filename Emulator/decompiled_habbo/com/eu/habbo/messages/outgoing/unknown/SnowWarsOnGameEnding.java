/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SnowWarsOnGameEnding
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1893);
        this.response.appendInt(0);
        this.response.appendBoolean(false);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(1);
        this.response.appendInt(1);
        this.response.appendInt(100);
        this.response.appendInt(1);
        this.response.appendString("Admin");
        this.response.appendInt(1);
        this.response.appendString("ca-1807-64.lg-275-78.hd-3093-1.hr-802-42.ch-3110-65-62.fa-1211-62");
        this.response.appendString("m");
        this.response.appendInt(1337);
        this.response.appendInt(1337);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(1337);
        this.response.appendInt(1338);
        return this.response;
    }
}

