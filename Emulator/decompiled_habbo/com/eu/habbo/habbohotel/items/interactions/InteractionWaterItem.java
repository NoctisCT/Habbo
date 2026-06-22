/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionMultiHeight;
import com.eu.habbo.habbohotel.items.interactions.InteractionWater;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.set.hash.THashSet;
import java.awt.Rectangle;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionWaterItem
extends InteractionMultiHeight {
    public InteractionWaterItem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionWaterItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onPlace(Room room) {
        this.update();
        super.onPlace(room);
    }

    @Override
    public void onPickUp(Room room) {
        super.onPickUp(room);
        this.setExtradata("0");
        this.needsUpdate(true);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        this.update();
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, new Object[0]);
    }

    public void update() {
        String updatedData;
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room == null) {
            return;
        }
        Rectangle rectangle = this.getRectangle();
        boolean foundWater = true;
        short x = (short)rectangle.x;
        while ((double)x < rectangle.getWidth() + (double)rectangle.x && foundWater) {
            short y = (short)rectangle.y;
            while ((double)y < rectangle.getHeight() + (double)rectangle.y && foundWater) {
                boolean tile = false;
                THashSet<HabboItem> items = room.getItemsAt(room.getLayout().getTile(x, y));
                for (HabboItem item : items) {
                    if (!(item instanceof InteractionWater)) continue;
                    tile = true;
                    break;
                }
                if (!tile) {
                    foundWater = false;
                }
                y = (short)(y + 1);
            }
            x = (short)(x + 1);
        }
        String string = updatedData = foundWater ? "1" : "0";
        if (!this.getExtradata().equals(updatedData)) {
            this.setExtradata(updatedData);
            this.needsUpdate(true);
            room.updateItemState(this);
        }
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }
}

