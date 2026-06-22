/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.inventory;

import com.eu.habbo.habbohotel.users.HabboInventory;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.plugin.events.inventory.InventoryItemEvent;

public class InventoryItemAddedEvent
extends InventoryItemEvent {
    public InventoryItemAddedEvent(HabboInventory inventory, HabboItem item) {
        super(inventory, item);
    }
}

