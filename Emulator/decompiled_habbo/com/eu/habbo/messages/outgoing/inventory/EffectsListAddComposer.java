/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class EffectsListAddComposer
extends MessageComposer {
    public final EffectsComponent.HabboEffect effect;

    public EffectsListAddComposer(EffectsComponent.HabboEffect effect) {
        this.effect = effect;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2867);
        this.response.appendInt(this.effect.effect);
        this.response.appendInt(0);
        this.response.appendInt(this.effect.duration > 0 ? this.effect.duration : Integer.MAX_VALUE);
        this.response.appendBoolean(this.effect.duration <= 0);
        return this.response;
    }

    public EffectsComponent.HabboEffect getEffect() {
        return this.effect;
    }
}

