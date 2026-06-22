/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.InventoryItemsComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestInventoryItemsEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestInventoryItemsEvent.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handle() throws Exception {
        int totalItems = this.client.getHabbo().getInventory().getItemsComponent().getItems().size();
        if (totalItems == 0) {
            this.client.sendResponse(new InventoryItemsComposer(0, 1, new TIntObjectHashMap<HabboItem>()));
            return;
        }
        int totalFragments = (int)Math.ceil((double)totalItems / 1000.0);
        if (totalFragments == 0) {
            totalFragments = 1;
        }
        TIntObjectMap<HabboItem> tIntObjectMap = this.client.getHabbo().getInventory().getItemsComponent().getItems();
        synchronized (tIntObjectMap) {
            TIntObjectHashMap<HabboItem> items = new TIntObjectHashMap<HabboItem>();
            TIntObjectIterator<HabboItem> iterator = this.client.getHabbo().getInventory().getItemsComponent().getItems().iterator();
            int count = 0;
            int fragmentNumber = 0;
            int i = this.client.getHabbo().getInventory().getItemsComponent().getItems().size();
            while (i-- > 0) {
                if (count == 0) {
                    ++fragmentNumber;
                }
                try {
                    iterator.advance();
                    items.put(iterator.key(), iterator.value());
                }
                catch (NoSuchElementException e) {
                    LOGGER.error("Caught exception", e);
                    break;
                }
                if (++count != 1000) continue;
                this.client.sendResponse(new InventoryItemsComposer(fragmentNumber, totalFragments, items));
                count = 0;
                items.clear();
            }
            if (count > 0 && items.size() > 0) {
                this.client.sendResponse(new InventoryItemsComposer(fragmentNumber, totalFragments, items));
            }
        }
    }
}

