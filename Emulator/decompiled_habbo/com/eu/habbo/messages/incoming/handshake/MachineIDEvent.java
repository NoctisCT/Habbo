/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.NoAuthMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoAuthMessage
public class MachineIDEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MachineIDEvent.class);
    private static final int HASH_LENGTH = 64;

    @Override
    public void handle() throws Exception {
        String storedMachineId = this.packet.readString();
        String clientFingerprint = this.packet.readString();
        String capabilities = this.packet.readString();
        if (storedMachineId.length() > 64) {
            storedMachineId = storedMachineId.substring(0, 64);
        }
        this.client.setMachineId(storedMachineId);
        LOGGER.debug("Setting client MachineId to {}", (Object)storedMachineId);
    }
}

