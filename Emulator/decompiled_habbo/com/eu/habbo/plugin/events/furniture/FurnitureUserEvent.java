/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.plugin.events.furniture.FurnitureEvent;

public abstract class FurnitureUserEvent
extends FurnitureEvent {
    public final Habbo habbo;

    public FurnitureUserEvent(HabboItem furniture, Habbo habbo) {
        super(furniture);
        this.habbo = habbo;
    }
}

