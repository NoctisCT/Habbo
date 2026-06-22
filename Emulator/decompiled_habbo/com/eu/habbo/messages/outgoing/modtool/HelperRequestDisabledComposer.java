/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HelperRequestDisabledComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1651);
        this.response.appendString("");
        return this.response;
    }
}

