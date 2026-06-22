/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InteractionSwitch
extends InteractionDefault {
    public InteractionSwitch(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionSwitch(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canToggle(Habbo habbo, Room room) {
        return RoomLayout.tilesAdjecent(room.getLayout().getTile(this.getX(), this.getY()), habbo.getRoomUnit().getCurrentLocation());
    }

    @Override
    public boolean allowWiredResetState() {
        return true;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (client == null) {
            return;
        }
        if (!this.canToggle(client.getHabbo(), room)) {
            RoomTile closestTile = null;
            for (RoomTile tile : room.getLayout().getTilesAround(room.getLayout().getTile(this.getX(), this.getY()))) {
                if (!tile.isWalkable() || closestTile != null && !(closestTile.distance(client.getHabbo().getRoomUnit().getCurrentLocation()) > tile.distance(client.getHabbo().getRoomUnit().getCurrentLocation()))) continue;
                closestTile = tile;
            }
            if (closestTile != null && !closestTile.equals(client.getHabbo().getRoomUnit().getCurrentLocation())) {
                ArrayList<Runnable> onSuccess = new ArrayList<Runnable>();
                onSuccess.add(() -> {
                    try {
                        this.onClick(client, room, objects);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                client.getHabbo().getRoomUnit().setGoalLocation(closestTile);
                Emulator.getThreading().run(new RoomUnitWalkToLocation(client.getHabbo().getRoomUnit(), closestTile, room, onSuccess, new ArrayList<Runnable>()));
            }
        }
        super.onClick(client, room, objects);
    }
}

