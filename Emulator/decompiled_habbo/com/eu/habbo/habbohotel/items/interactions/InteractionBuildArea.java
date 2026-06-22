/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionCustomValues;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RoomFloorItemsComposer;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class InteractionBuildArea
extends InteractionCustomValues {
    public static THashMap<String, String> defaultValues = new THashMap<String, String>(){
        {
            this.put("tilesLeft", "0");
            this.put("tilesRight", "0");
            this.put("tilesFront", "0");
            this.put("tilesBack", "0");
            this.put("builders", "");
        }
    };
    private THashSet<RoomTile> tiles = new THashSet();

    public InteractionBuildArea(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem, defaultValues);
    }

    public InteractionBuildArea(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells, defaultValues);
    }

    @Override
    public void onPlace(Room room) {
        super.onPlace(room);
        this.regenAffectedTiles(room);
    }

    @Override
    public void onPickUp(Room room) {
        super.onPickUp(room);
        ArrayList<String> builderNames = new ArrayList<String>(Arrays.asList(((String)this.values.get("builders")).split(";")));
        THashSet<Integer> canBuild = new THashSet<Integer>();
        for (String builderName : builderNames) {
            Habbo builder = Emulator.getGameEnvironment().getHabboManager().getHabbo(builderName);
            HabboInfo builderInfo = builder != null ? builder.getHabboInfo() : HabboManager.getOfflineHabboInfo(builderName);
            if (builderInfo == null) continue;
            canBuild.add(builderInfo.getId());
        }
        if (!canBuild.isEmpty()) {
            for (RoomTile tile : this.tiles) {
                THashSet<HabboItem> tileItems = room.getItemsAt(tile);
                for (HabboItem tileItem : tileItems) {
                    if (!canBuild.contains(tileItem.getUserId()) || tileItem == this) continue;
                    room.pickUpItem(tileItem, null);
                }
            }
        }
        this.tiles.clear();
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        ArrayList<String> builderNames = new ArrayList<String>(Arrays.asList(((String)this.values.get("builders")).split(";")));
        THashSet<Integer> canBuild = new THashSet<Integer>();
        for (String builderName : builderNames) {
            Habbo builder = Emulator.getGameEnvironment().getHabboManager().getHabbo(builderName);
            HabboInfo builderInfo = builder != null ? builder.getHabboInfo() : HabboManager.getOfflineHabboInfo(builderName);
            if (builderInfo == null) continue;
            canBuild.add(builderInfo.getId());
        }
        THashSet<RoomTile> oldTiles = this.tiles;
        THashSet<RoomTile> newTiles = new THashSet<RoomTile>();
        int minX = Math.max(0, newLocation.x - Integer.parseInt((String)this.values.get("tilesBack")));
        int minY = Math.max(0, newLocation.y - Integer.parseInt((String)this.values.get("tilesRight")));
        int maxX = Math.min(room.getLayout().getMapSizeX(), newLocation.x + Integer.parseInt((String)this.values.get("tilesFront")));
        int maxY = Math.min(room.getLayout().getMapSizeY(), newLocation.y + Integer.parseInt((String)this.values.get("tilesLeft")));
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                RoomTile tile = room.getLayout().getTile((short)x, (short)y);
                if (tile == null || tile.state == RoomTileState.INVALID) continue;
                newTiles.add(tile);
            }
        }
        if (!canBuild.isEmpty()) {
            for (RoomTile tile : oldTiles) {
                THashSet<HabboItem> tileItems = room.getItemsAt(tile);
                if (newTiles.contains(tile)) continue;
                for (HabboItem tileItem : tileItems) {
                    if (!canBuild.contains(tileItem.getUserId()) || tileItem == this) continue;
                    room.pickUpItem(tileItem, null);
                }
            }
        }
        this.regenAffectedTiles(room);
    }

    public boolean inSquare(RoomTile location) {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room != null && this.tiles.size() == 0) {
            this.regenAffectedTiles(room);
        }
        return this.tiles.contains(location);
    }

    private void regenAffectedTiles(Room room) {
        int minX = Math.max(0, this.getX() - Integer.parseInt((String)this.values.get("tilesBack")));
        int minY = Math.max(0, this.getY() - Integer.parseInt((String)this.values.get("tilesRight")));
        int maxX = Math.min(room.getLayout().getMapSizeX(), this.getX() + Integer.parseInt((String)this.values.get("tilesFront")));
        int maxY = Math.min(room.getLayout().getMapSizeY(), this.getY() + Integer.parseInt((String)this.values.get("tilesLeft")));
        this.tiles.clear();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                RoomTile tile = room.getLayout().getTile((short)x, (short)y);
                if (tile == null || tile.state == RoomTileState.INVALID) continue;
                this.tiles.add(tile);
            }
        }
    }

    @Override
    public void onCustomValuesSaved(Room room, GameClient client, THashMap<String, String> oldValues) {
        Item effectItem;
        this.regenAffectedTiles(room);
        ArrayList<String> builderNames = new ArrayList<String>(Arrays.asList(((String)this.values.get("builders")).split(";")));
        THashSet<Integer> canBuild = new THashSet<Integer>();
        for (String builderName : builderNames) {
            Habbo builder = Emulator.getGameEnvironment().getHabboManager().getHabbo(builderName);
            HabboInfo builderInfo = builder != null ? builder.getHabboInfo() : HabboManager.getOfflineHabboInfo(builderName);
            if (builderInfo == null) continue;
            canBuild.add(builderInfo.getId());
        }
        THashSet<RoomTile> oldTiles = new THashSet<RoomTile>();
        int minX = Math.max(0, this.getX() - Integer.parseInt(oldValues.get("tilesBack")));
        int minY = Math.max(0, this.getY() - Integer.parseInt(oldValues.get("tilesRight")));
        int maxX = Math.min(room.getLayout().getMapSizeX(), this.getX() + Integer.parseInt(oldValues.get("tilesFront")));
        int maxY = Math.min(room.getLayout().getMapSizeY(), this.getY() + Integer.parseInt(oldValues.get("tilesLeft")));
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                RoomTile tile = room.getLayout().getTile((short)x, (short)y);
                if (tile == null || tile.state == RoomTileState.INVALID || this.tiles.contains(tile)) continue;
                oldTiles.add(tile);
            }
        }
        if (!canBuild.isEmpty()) {
            for (RoomTile tile : oldTiles) {
                THashSet<HabboItem> tileItems = room.getItemsAt(tile);
                for (HabboItem tileItem : tileItems) {
                    if (!canBuild.contains(tileItem.getUserId()) || tileItem == this) continue;
                    room.pickUpItem(tileItem, null);
                }
            }
        }
        if ((effectItem = Emulator.getGameEnvironment().getItemManager().getItem("mutearea_sign2")) != null) {
            TIntObjectMap<String> ownerNames = TCollections.synchronizedMap(new TIntObjectHashMap(0));
            ownerNames.put(-1, "System");
            THashSet<InteractionDefault> items = new THashSet<InteractionDefault>();
            int id = 0;
            for (RoomTile tile : this.tiles) {
                InteractionDefault item = new InteractionDefault(--id, -1, effectItem, "1", 0, 0);
                item.setX(tile.x);
                item.setY(tile.y);
                item.setZ(tile.relativeHeight());
                items.add(item);
            }
            client.sendResponse(new RoomFloorItemsComposer(ownerNames, items));
            Emulator.getThreading().run(() -> {
                for (HabboItem item : items) {
                    client.sendResponse(new RemoveFloorItemComposer(item, true));
                }
            }, 3000L);
        }
    }

    public boolean isBuilder(String Username) {
        return Arrays.asList(((String)this.values.get("builders")).split(";")).contains(Username);
    }
}

