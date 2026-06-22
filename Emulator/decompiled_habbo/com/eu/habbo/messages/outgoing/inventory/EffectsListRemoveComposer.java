/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class EffectsListRemoveComposer
extends MessageComposer {
    public final EffectsComponent.HabboEffect effect;

    public EffectsListRemoveComposer(EffectsComponent.HabboEffect effect) {
        this.effect = effect;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2228);
        this.response.appendInt(this.effect.effect);
        return this.response;
    }

    public EffectsComponent.HabboEffect getEffect() {
        return this.effect;
    }
}

