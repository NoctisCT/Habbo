/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CantScratchPetNotOldEnoughComposer
extends MessageComposer {
    private final int currentAge;
    private final int requiredAge;

    public CantScratchPetNotOldEnoughComposer(int currentAge, int requiredAge) {
        this.currentAge = currentAge;
        this.requiredAge = requiredAge;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1130);
        this.response.appendInt(this.currentAge);
        this.response.appendInt(this.requiredAge);
        return this.response;
    }

    public int getCurrentAge() {
        return this.currentAge;
    }

    public int getRequiredAge() {
        return this.requiredAge;
    }
}

