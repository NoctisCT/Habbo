/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveReward;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserWiredRewardReceived
extends UserEvent {
    public final WiredEffectGiveReward wiredEffectGiveReward;
    public final String type;
    public String value;

    public UserWiredRewardReceived(Habbo habbo, WiredEffectGiveReward wiredEffectGiveReward, String type, String value) {
        super(habbo);
        this.wiredEffectGiveReward = wiredEffectGiveReward;
        this.type = type;
        this.value = value;
    }
}

