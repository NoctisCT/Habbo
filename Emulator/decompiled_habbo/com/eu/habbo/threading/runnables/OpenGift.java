/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryUpdateItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.PresentItemOpenedComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import com.eu.habbo.threading.runnables.RemoveFloorItemTask;
import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenGift
implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenGift.class);
    private final HabboItem item;
    private final Habbo habbo;
    private final Room room;

    public OpenGift(HabboItem item, Habbo habbo, Room room) {
        this.item = item;
        this.habbo = habbo;
        this.room = room;
    }

    @Override
    public void run() {
        try {
            HabboItem inside = null;
            THashSet<HabboItem> items = ((InteractionGift)this.item).loadItems();
            for (HabboItem i : items) {
                if (inside == null) {
                    inside = i;
                }
                i.setUserId(this.habbo.getHabboInfo().getId());
                i.needsUpdate(true);
                i.run();
            }
            if (inside != null) {
                inside.setFromGift(true);
            }
            this.habbo.getInventory().getItemsComponent().addItems(items);
            RoomTile tile = this.room.getLayout().getTile(this.item.getX(), this.item.getY());
            if (tile != null) {
                this.room.updateTile(tile);
            }
            Emulator.getThreading().run(new QueryDeleteHabboItem(this.item.getId()));
            Emulator.getThreading().run(new RemoveFloorItemTask(this.room, this.item), this.item.getBaseItem().getName().contains("present_wrap") ? 5000L : 0L);
            this.habbo.getClient().sendResponse(new InventoryRefreshComposer());
            HashMap<AddHabboItemComposer.AddHabboItemCategory, List<Integer>> unseenItems = new HashMap<AddHabboItemComposer.AddHabboItemCategory, List<Integer>>();
            for (HabboItem item : items) {
                switch (item.getBaseItem().getType()) {
                    case WALL: 
                    case FLOOR: {
                        if (!unseenItems.containsKey((Object)AddHabboItemComposer.AddHabboItemCategory.OWNED_FURNI)) {
                            unseenItems.put(AddHabboItemComposer.AddHabboItemCategory.OWNED_FURNI, new ArrayList());
                        }
                        ((List)unseenItems.get((Object)AddHabboItemComposer.AddHabboItemCategory.OWNED_FURNI)).add(item.getGiftAdjustedId());
                        break;
                    }
                    case BADGE: {
                        if (!unseenItems.containsKey((Object)AddHabboItemComposer.AddHabboItemCategory.BADGE)) {
                            unseenItems.put(AddHabboItemComposer.AddHabboItemCategory.BADGE, new ArrayList());
                        }
                        ((List)unseenItems.get((Object)AddHabboItemComposer.AddHabboItemCategory.BADGE)).add(item.getId());
                        break;
                    }
                    case PET: {
                        if (!unseenItems.containsKey((Object)AddHabboItemComposer.AddHabboItemCategory.PET)) {
                            unseenItems.put(AddHabboItemComposer.AddHabboItemCategory.PET, new ArrayList());
                        }
                        ((List)unseenItems.get((Object)AddHabboItemComposer.AddHabboItemCategory.PET)).add(item.getGiftAdjustedId());
                        break;
                    }
                    case ROBOT: {
                        if (!unseenItems.containsKey((Object)AddHabboItemComposer.AddHabboItemCategory.BOT)) {
                            unseenItems.put(AddHabboItemComposer.AddHabboItemCategory.BOT, new ArrayList());
                        }
                        ((List)unseenItems.get((Object)AddHabboItemComposer.AddHabboItemCategory.BOT)).add(item.getGiftAdjustedId());
                    }
                }
            }
            this.habbo.getClient().sendResponse(new AddHabboItemComposer(unseenItems));
            if (inside != null) {
                this.habbo.getClient().sendResponse(new InventoryUpdateItemComposer(inside));
                this.habbo.getClient().sendResponse(new PresentItemOpenedComposer(inside, "", false));
            }
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }
}

