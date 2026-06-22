/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MachineIDComposer
extends MessageComposer {
    private final String machineId;

    public MachineIDComposer(String machineId) {
        this.machineId = machineId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1488);
        this.response.appendString(this.machineId);
        return this.response;
    }

    public String getMachineId() {
        return this.machineId;
    }
}

