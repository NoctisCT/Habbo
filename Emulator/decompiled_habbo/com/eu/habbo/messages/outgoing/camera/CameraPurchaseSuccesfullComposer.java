/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CameraPurchaseSuccesfullComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2783);
        return this.response;
    }
}

