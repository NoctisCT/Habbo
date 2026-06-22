/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuideToolsComposer
extends MessageComposer {
    private final boolean onDuty;

    public GuideToolsComposer(boolean onDuty) {
        this.onDuty = onDuty;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1548);
        this.response.appendBoolean(this.onDuty);
        this.response.appendInt(0);
        this.response.appendInt(Emulator.getGameEnvironment().getGuideManager().getGuidesCount());
        this.response.appendInt(Emulator.getGameEnvironment().getGuideManager().getGuardiansCount());
        return this.response;
    }

    public boolean isOnDuty() {
        return this.onDuty;
    }
}

