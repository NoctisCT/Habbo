/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.map.hash.THashMap;
import java.util.Collection;

public class UserEffectsListComposer
extends MessageComposer {
    public final Habbo habbo;
    public final Collection<EffectsComponent.HabboEffect> effects;

    public UserEffectsListComposer(Habbo habbo, Collection<EffectsComponent.HabboEffect> effects) {
        this.habbo = habbo;
        this.effects = effects;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(340);
        if (this.habbo == null || this.habbo.getInventory() == null || this.habbo.getInventory().getEffectsComponent() == null || this.habbo.getInventory().getEffectsComponent().effects == null) {
            this.response.appendInt(0);
        } else {
            THashMap<Integer, EffectsComponent.HabboEffect> tHashMap = this.habbo.getInventory().getEffectsComponent().effects;
            synchronized (tHashMap) {
                this.response.appendInt(this.effects.size());
                for (EffectsComponent.HabboEffect effect : this.effects) {
                    this.response.appendInt(effect.effect);
                    this.response.appendInt(0);
                    this.response.appendInt(effect.duration > 0 ? effect.duration : Integer.MAX_VALUE);
                    this.response.appendInt(effect.duration > 0 ? effect.total - (effect.isActivated() ? 1 : 0) : 0);
                    if (!effect.isActivated() && effect.duration > 0) {
                        this.response.appendInt(0);
                    } else {
                        this.response.appendInt(effect.duration > 0 ? Emulator.getIntUnixTimestamp() - effect.activationTimestamp + effect.duration : 0);
                    }
                    this.response.appendBoolean(effect.duration <= 0);
                }
            }
        }
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public Collection<EffectsComponent.HabboEffect> getEffects() {
        return this.effects;
    }
}

