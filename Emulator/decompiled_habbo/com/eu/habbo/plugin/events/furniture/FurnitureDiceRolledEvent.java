/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.plugin.events.furniture.FurnitureUserEvent;

public class FurnitureDiceRolledEvent
extends FurnitureUserEvent {
    public int result;

    public FurnitureDiceRolledEvent(HabboItem furniture, Habbo habbo, int result) {
        super(furniture, habbo);
        this.result = result;
    }
}

