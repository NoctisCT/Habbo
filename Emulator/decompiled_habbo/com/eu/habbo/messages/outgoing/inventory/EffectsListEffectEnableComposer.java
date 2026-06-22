/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class EffectsListEffectEnableComposer
extends MessageComposer {
    public final EffectsComponent.HabboEffect effect;

    public EffectsListEffectEnableComposer(EffectsComponent.HabboEffect effect) {
        this.effect = effect;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1959);
        this.response.appendInt(this.effect.effect);
        this.response.appendInt(this.effect.duration);
        this.response.appendBoolean(this.effect.enabled);
        return this.response;
    }

    public EffectsComponent.HabboEffect getEffect() {
        return this.effect;
    }
}

