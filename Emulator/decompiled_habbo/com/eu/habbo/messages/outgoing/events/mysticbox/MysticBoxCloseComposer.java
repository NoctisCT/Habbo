/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.events.mysticbox;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MysticBoxCloseComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(596);
        return this.response;
    }
}

