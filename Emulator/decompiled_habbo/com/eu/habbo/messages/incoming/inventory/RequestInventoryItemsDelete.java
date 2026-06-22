/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.inventory.RemoveHabboItemComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItems;
import gnu.trove.map.hash.TIntObjectHashMap;

public class RequestInventoryItemsDelete
extends MessageHandler {
    @Override
    public int getRatelimit() {
        return 500;
    }

    @Override
    public void handle() {
        int itemId = this.packet.readInt();
        int amount = this.packet.readInt();
        HabboItem habboItem = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(itemId);
        if (habboItem == null) {
            return;
        }
        Item item = habboItem.getBaseItem();
        if (item == null) {
            return;
        }
        if (!this.hasFurnitureInInventory(this.client.getHabbo(), item, Math.abs(amount))) {
            return;
        }
        Habbo habbo = this.client.getHabbo();
        if (habbo == null) {
            return;
        }
        TIntObjectHashMap<HabboItem> toRemove = new TIntObjectHashMap<HabboItem>();
        for (int i = 0; i < Math.abs(amount); ++i) {
            HabboItem habboInventoryItem = habbo.getInventory().getItemsComponent().getAndRemoveHabboItem(item);
            if (habboInventoryItem == null) continue;
            toRemove.put(habboInventoryItem.getId(), habboInventoryItem);
        }
        toRemove.forEachValue(object -> {
            habbo.getClient().sendResponse(new RemoveHabboItemComposer(object.getGiftAdjustedId()));
            return true;
        });
        habbo.getClient().sendResponse(new InventoryRefreshComposer());
        Emulator.getThreading().run(new QueryDeleteHabboItems(toRemove));
    }

    private boolean hasFurnitureInInventory(Habbo habbo, Item item, Integer amount) {
        int count = 0;
        for (HabboItem habboItem : habbo.getInventory().getItemsComponent().getItemsAsValueCollection()) {
            if (habboItem.getBaseItem().getId() != item.getId()) continue;
            ++count;
        }
        return count >= amount;
    }
}

