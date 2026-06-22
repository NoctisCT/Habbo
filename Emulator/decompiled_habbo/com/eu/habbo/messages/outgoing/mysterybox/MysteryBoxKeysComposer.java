/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.mysterybox;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MysteryBoxKeysComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2833);
        this.response.appendString("");
        this.response.appendString("");
        return this.response;
    }
}

