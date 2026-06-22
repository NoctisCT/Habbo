/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.NoAuthMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

@NoAuthMessage
public class ReleaseVersionEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.packet.readString();
    }
}

