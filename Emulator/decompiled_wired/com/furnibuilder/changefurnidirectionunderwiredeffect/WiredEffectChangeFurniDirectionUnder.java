/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.eu.habbo.Emulator
 *  com.eu.habbo.habbohotel.gameclients.GameClient
 *  com.eu.habbo.habbohotel.items.Item
 *  com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper
 *  com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect
 *  com.eu.habbo.habbohotel.rooms.FurnitureMovementError
 *  com.eu.habbo.habbohotel.rooms.Room
 *  com.eu.habbo.habbohotel.rooms.RoomTile
 *  com.eu.habbo.habbohotel.rooms.RoomTileState
 *  com.eu.habbo.habbohotel.rooms.RoomUnit
 *  com.eu.habbo.habbohotel.rooms.RoomUnitType
 *  com.eu.habbo.habbohotel.rooms.RoomUserRotation
 *  com.eu.habbo.habbohotel.users.Habbo
 *  com.eu.habbo.habbohotel.users.HabboItem
 *  com.eu.habbo.habbohotel.wired.WiredChangeDirectionSetting
 *  com.eu.habbo.habbohotel.wired.WiredEffectType
 *  com.eu.habbo.habbohotel.wired.WiredHandler
 *  com.eu.habbo.habbohotel.wired.WiredTriggerType
 *  com.eu.habbo.messages.ClientMessage
 *  com.eu.habbo.messages.ServerMessage
 *  com.eu.habbo.messages.incoming.wired.WiredSaveException
 *  com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer
 *  com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer
 *  com.eu.habbo.plugin.Event
 *  com.eu.habbo.plugin.events.furniture.FurnitureBuildheightEvent
 *  com.eu.habbo.plugin.events.furniture.FurnitureMovedEvent
 *  com.eu.habbo.plugin.events.furniture.FurnitureRotatedEvent
 *  gnu.trove.iterator.hash.TObjectHashIterator
 *  gnu.trove.map.hash.THashMap
 *  gnu.trove.set.hash.THashSet
 *  org.apache.commons.math3.util.Pair
 */
package com.furnibuilder.changefurnidirectionunderwiredeffect;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredChangeDirectionSetting;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureBuildheightEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureMovedEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureRotatedEvent;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.math3.util.Pair;

public class WiredEffectChangeFurniDirectionUnder
extends InteractionWiredEffect {
    public static final int ACTION_WAIT = 0;
    public static final int ACTION_TURN_RIGHT_45 = 1;
    public static final int ACTION_TURN_RIGHT_90 = 2;
    public static final int ACTION_TURN_LEFT_45 = 3;
    public static final int ACTION_TURN_LEFT_90 = 4;
    public static final int ACTION_TURN_BACK = 5;
    public static final int ACTION_TURN_RANDOM = 6;
    public static final WiredEffectType type = WiredEffectType.MOVE_DIRECTION;
    private final THashMap<HabboItem, WiredChangeDirectionSetting> items = new THashMap(0);
    private RoomUserRotation startRotation = RoomUserRotation.NORTH;
    private int blockedAction = 0;

    public WiredEffectChangeFurniDirectionUnder(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectChangeFurniDirectionUnder(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        HabboItem item;
        THashSet items = new THashSet();
        for (HabboItem item2 : this.items.keySet()) {
            if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item2.getId()) != null) continue;
            items.add((Object)item2);
        }
        for (HabboItem item2 : items) {
            this.items.remove((Object)item2);
        }
        if (this.items.isEmpty()) {
            return false;
        }
        for (Map.Entry entry : this.items.entrySet()) {
            item = (HabboItem)entry.getKey();
            RoomTile targetTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), ((WiredChangeDirectionSetting)entry.getValue()).direction.getValue());
            for (int count = 1; (targetTile == null || targetTile.state == RoomTileState.INVALID || room.furnitureFitsAt(targetTile, item, item.getRotation(), false) != FurnitureMovementError.NONE) && count < 8; ++count) {
                ((WiredChangeDirectionSetting)entry.getValue()).direction = this.nextRotation(((WiredChangeDirectionSetting)entry.getValue()).direction);
                RoomTile tile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), ((WiredChangeDirectionSetting)entry.getValue()).direction.getValue());
                if (tile == null || tile.state == RoomTileState.INVALID) continue;
                targetTile = tile;
            }
        }
        for (Map.Entry entry : this.items.entrySet()) {
            item = (HabboItem)entry.getKey();
            int newDirection = ((WiredChangeDirectionSetting)entry.getValue()).direction.getValue();
            RoomTile targetTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), newDirection);
            if (item.getRotation() != ((WiredChangeDirectionSetting)entry.getValue()).rotation) {
                if (room.furnitureFitsAt(targetTile, item, ((WiredChangeDirectionSetting)entry.getValue()).rotation, false) != FurnitureMovementError.NONE) continue;
                this.moveFurniTo(room, (HabboItem)entry.getKey(), targetTile, ((WiredChangeDirectionSetting)entry.getValue()).rotation, null, true);
            }
            boolean hasRoomUnits = false;
            THashSet newOccupiedTiles = room.getLayout().getTilesAt(targetTile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            for (RoomTile tile : newOccupiedTiles) {
                for (RoomUnit _roomUnit : room.getRoomUnits(tile)) {
                    if (!_roomUnit.getRoomUnitType().equals((Object)RoomUnitType.USER)) {
                        hasRoomUnits = true;
                    }
                    if (_roomUnit.getCurrentLocation() != targetTile) continue;
                    Emulator.getThreading().run(() -> WiredHandler.handle((WiredTriggerType)WiredTriggerType.COLLISION, (RoomUnit)_roomUnit, (Room)room, (Object[])new Object[]{entry.getKey()}));
                    try {
                        item.onWalkOn(_roomUnit, room, null);
                    }
                    catch (Exception exception) {}
                }
            }
            if (targetTile == null || targetTile.state == RoomTileState.INVALID || room.furnitureFitsAt(targetTile, item, item.getRotation(), false) != FurnitureMovementError.NONE || hasRoomUnits) continue;
            RoomTile oldLocation = room.getLayout().getTile(((HabboItem)entry.getKey()).getX(), ((HabboItem)entry.getKey()).getY());
            double oldZ = ((HabboItem)entry.getKey()).getZ();
            if (this.moveFurniTo(room, (HabboItem)entry.getKey(), targetTile, item.getRotation(), null, false) != FurnitureMovementError.NONE) continue;
            room.sendComposer(new FloorItemOnRollerComposer((HabboItem)entry.getKey(), null, oldLocation, oldZ, targetTile, ((HabboItem)entry.getKey()).getZ(), 0.0, room).compose());
        }
        return false;
    }

    private FurnitureMovementError moveFurniTo(Room room, HabboItem item, RoomTile tile, int rotation, Habbo actor, boolean sendUpdates) {
        FurnitureBuildheightEvent event;
        double height;
        RoomTile oldLocation = room.getLayout().getTile(item.getX(), item.getY());
        boolean pluginHelper = false;
        if (Emulator.getPluginManager().isRegistered(FurnitureMovedEvent.class, true)) {
            FurnitureMovedEvent event2 = (FurnitureMovedEvent)Emulator.getPluginManager().fireEvent((Event)new FurnitureMovedEvent(item, actor, oldLocation, tile));
            if (event2.isCancelled()) {
                return FurnitureMovementError.CANCEL_PLUGIN_MOVE;
            }
            pluginHelper = event2.hasPluginHelper();
        }
        boolean magicTile = item instanceof InteractionStackHelper;
        boolean canHaveUser = false;
        if (item.getBaseItem().allowSit() || item.getBaseItem().allowWalk() || item.getBaseItem().allowLay()) {
            canHaveUser = true;
        }
        Optional<HabboItem> stackHelper = room.getItemsAt(tile).stream().filter(i -> i instanceof InteractionStackHelper).findAny();
        THashSet occupiedTiles = room.getLayout().getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);
        HabboItem topItem = room.getTopItemAt(occupiedTiles, null);
        if (!stackHelper.isPresent() && !pluginHelper) {
            if (oldLocation != tile) {
                for (TObjectHashIterator t : occupiedTiles) {
                    HabboItem tileTopItem = room.getTopItemAt((int)t.x, (int)t.y);
                    if (!magicTile && (tileTopItem != null && tileTopItem != item ? t.state.equals((Object)RoomTileState.INVALID) || !t.getAllowStack() || !tileTopItem.getBaseItem().allowStack() : this.calculateTileState(room, (RoomTile)t, item).equals((Object)RoomTileState.INVALID))) {
                        return FurnitureMovementError.CANT_STACK;
                    }
                    if (!magicTile && !canHaveUser && room.hasHabbosAt((int)t.x, (int)t.y)) {
                        return FurnitureMovementError.TILE_HAS_HABBOS;
                    }
                    if (!magicTile && room.hasBotsAt((int)t.x, (int)t.y)) {
                        return FurnitureMovementError.TILE_HAS_BOTS;
                    }
                    if (magicTile || !room.hasPetsAt((int)t.x, (int)t.y)) continue;
                    return FurnitureMovementError.TILE_HAS_PETS;
                }
            }
            ArrayList<Pair> tileFurniList = new ArrayList<Pair>();
            for (RoomTile t : occupiedTiles) {
                tileFurniList.add(Pair.create((Object)t, (Object)room.getItemsAt(t)));
            }
            if (!magicTile && !item.canStackAt(room, tileFurniList)) {
                return FurnitureMovementError.CANT_STACK;
            }
        }
        THashSet oldOccupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
        int oldRotation = item.getRotation();
        if (oldRotation != rotation) {
            item.setRotation(rotation);
            if (Emulator.getPluginManager().isRegistered(FurnitureRotatedEvent.class, true)) {
                FurnitureRotatedEvent furnitureRotatedEvent = new FurnitureRotatedEvent(item, actor, oldRotation);
                Emulator.getPluginManager().fireEvent((Event)furnitureRotatedEvent);
                if (furnitureRotatedEvent.isCancelled()) {
                    item.setRotation(oldRotation);
                    return FurnitureMovementError.CANCEL_PLUGIN_ROTATE;
                }
            }
            if (!stackHelper.isPresent() && topItem != null && topItem != item && !topItem.getBaseItem().allowStack() || topItem != null && topItem != item && topItem.getZ() + Item.getCurrentHeight((HabboItem)topItem) + Item.getCurrentHeight((HabboItem)item) > Room.MAXIMUM_FURNI_HEIGHT) {
                item.setRotation(oldRotation);
                return FurnitureMovementError.CANT_STACK;
            }
        }
        if (stackHelper.isPresent()) {
            height = stackHelper.get().getExtradata().isEmpty() ? Double.parseDouble("0.0") : Double.parseDouble(stackHelper.get().getExtradata()) / 100.0;
        } else if (item == topItem) {
            height = item.getZ();
        } else {
            height = room.getStackHeight(tile.x, tile.y, false, item);
            for (RoomTile til : occupiedTiles) {
                double sHeight = room.getStackHeight(til.x, til.y, false, item);
                if (!(sHeight > height)) continue;
                height = sHeight;
            }
        }
        if (Emulator.getPluginManager().isRegistered(FurnitureBuildheightEvent.class, true) && (event = (FurnitureBuildheightEvent)Emulator.getPluginManager().fireEvent((Event)new FurnitureBuildheightEvent(item, actor, 0.0, height))).hasChangedHeight()) {
            height = event.getUpdatedHeight();
        }
        if (height > Room.MAXIMUM_FURNI_HEIGHT) {
            return FurnitureMovementError.CANT_STACK;
        }
        if (height < (double)room.getLayout().getHeightAtSquare((int)tile.x, (int)tile.y)) {
            return FurnitureMovementError.CANT_STACK;
        }
        item.setX(tile.x);
        item.setY(tile.y);
        item.setZ(height);
        if (magicTile) {
            item.setZ((double)tile.z);
            item.setExtradata("" + item.getZ() * 100.0);
        }
        if (item.getZ() > Room.MAXIMUM_FURNI_HEIGHT) {
            item.setZ(Room.MAXIMUM_FURNI_HEIGHT);
        }
        item.onMove(room, oldLocation, tile);
        item.needsUpdate(true);
        Emulator.getThreading().run((Runnable)item);
        if (sendUpdates) {
            room.sendComposer(new FloorItemUpdateComposer(item).compose());
        }
        occupiedTiles.removeAll((Collection)oldOccupiedTiles);
        occupiedTiles.addAll((Collection)oldOccupiedTiles);
        room.updateTiles(occupiedTiles);
        for (RoomTile t : occupiedTiles) {
            room.updateHabbosAt(t.x, t.y, room.getHabbosAt(t.x, t.y));
            room.updateBotsAt(t.x, t.y);
        }
        return FurnitureMovementError.NONE;
    }

    private RoomTileState calculateTileState(Room room, RoomTile tile, HabboItem exclude) {
        if (tile == null || tile.state == RoomTileState.INVALID) {
            return RoomTileState.INVALID;
        }
        RoomTileState result = RoomTileState.OPEN;
        HabboItem highestItem = null;
        HabboItem lowestChair = room.getLowestChair(tile);
        THashSet items = room.getItemsAt(tile);
        if (items == null) {
            return RoomTileState.INVALID;
        }
        for (HabboItem item : items) {
            if (exclude != null && item == exclude) continue;
            if (item.getBaseItem().allowLay()) {
                return RoomTileState.LAY;
            }
            if (highestItem != null && highestItem.getZ() + Item.getCurrentHeight((HabboItem)highestItem) > item.getZ() + Item.getCurrentHeight((HabboItem)item)) continue;
            highestItem = item;
            if (result != RoomTileState.OPEN) continue;
            result = this.checkStateForItem(room, item, tile);
        }
        if (lowestChair != null) {
            return RoomTileState.SIT;
        }
        return result;
    }

    private RoomTileState checkStateForItem(Room room, HabboItem item, RoomTile tile) {
        RoomTileState overriddenState;
        RoomTileState result = RoomTileState.BLOCKED;
        if (item.isWalkable()) {
            result = RoomTileState.OPEN;
        }
        if (item.getBaseItem().allowSit()) {
            result = RoomTileState.SIT;
        }
        if (item.getBaseItem().allowLay()) {
            result = RoomTileState.LAY;
        }
        if ((overriddenState = item.getOverrideTileState(tile, room)) != null) {
            result = overriddenState;
        }
        return result;
    }

    public String getWiredData() {
        ArrayList<WiredChangeDirectionSetting> settings = new ArrayList<WiredChangeDirectionSetting>(this.items.values());
        return WiredHandler.getGsonBuilder().create().toJson((Object)new JsonData(this.startRotation, this.blockedAction, settings, this.getDelay()));
    }

    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items.clear();
        String wiredData = set.getString("wired_data");
        if (wiredData.startsWith("{")) {
            JsonData data = (JsonData)WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.setDelay(data.delay);
            this.startRotation = data.start_direction;
            this.blockedAction = data.blocked_action;
            for (WiredChangeDirectionSetting setting : data.items) {
                HabboItem item = room.getHabboItem(setting.item_id);
                if (item == null) continue;
                this.items.put((Object)item, (Object)setting);
            }
        } else {
            String[] data = wiredData.split("\t");
            if (data.length >= 4) {
                this.setDelay(Integer.parseInt(data[0]));
                this.startRotation = RoomUserRotation.fromValue((int)Integer.parseInt(data[1]));
                this.blockedAction = Integer.parseInt(data[2]);
                int itemCount = Integer.parseInt(data[3]);
                if (itemCount > 0) {
                    for (int i = 4; i < data.length; ++i) {
                        HabboItem item;
                        String[] subData = data[i].split(":");
                        if (subData.length < 2 || (item = room.getHabboItem(Integer.parseInt(subData[0]))) == null) continue;
                        int rotation = item.getRotation();
                        if (subData.length > 2) {
                            rotation = Integer.parseInt(subData[2]);
                        }
                        this.items.put((Object)item, (Object)new WiredChangeDirectionSetting(item.getId(), rotation, RoomUserRotation.fromValue((int)Integer.parseInt(subData[1]))));
                    }
                }
            }
            this.needsUpdate(true);
        }
    }

    public void onPickUp() {
        this.setDelay(0);
        this.items.clear();
        this.blockedAction = 0;
        this.startRotation = RoomUserRotation.NORTH;
    }

    public WiredEffectType getType() {
        return type;
    }

    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(Boolean.valueOf(false));
        message.appendInt(Integer.valueOf(WiredHandler.MAXIMUM_FURNI_SELECTION));
        message.appendInt(Integer.valueOf(this.items.size()));
        for (Map.Entry item : this.items.entrySet()) {
            message.appendInt(Integer.valueOf(((HabboItem)item.getKey()).getId()));
        }
        message.appendInt(Integer.valueOf(this.getBaseItem().getSpriteId()));
        message.appendInt(Integer.valueOf(this.getId()));
        message.appendString("");
        message.appendInt(Integer.valueOf(2));
        message.appendInt(Integer.valueOf(this.startRotation != null ? this.startRotation.getValue() : 0));
        message.appendInt(Integer.valueOf(this.blockedAction));
        message.appendInt(Integer.valueOf(0));
        message.appendInt(Integer.valueOf(this.getType().code));
        message.appendInt(Integer.valueOf(this.getDelay()));
        message.appendInt(Integer.valueOf(0));
    }

    public boolean saveData(ClientMessage packet, GameClient gameClient) throws WiredSaveException {
        packet.readInt();
        int startDirectionInt = packet.readInt();
        if (startDirectionInt < 0 || startDirectionInt > 7 || startDirectionInt % 2 != 0) {
            throw new WiredSaveException("Start direction is invalid");
        }
        RoomUserRotation startDirection = RoomUserRotation.fromValue((int)startDirectionInt);
        int blockedActionInt = packet.readInt();
        if (blockedActionInt < 0 || blockedActionInt > 6) {
            throw new WiredSaveException("Blocked action is invalid");
        }
        packet.readString();
        int itemsCount = packet.readInt();
        if (itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            throw new WiredSaveException("Too many furni selected");
        }
        THashMap newItems = new THashMap();
        for (int i = 0; i < itemsCount; ++i) {
            int itemId = packet.readInt();
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);
            if (it == null) {
                throw new WiredSaveException(String.format("Item %s not found", itemId));
            }
            newItems.put((Object)it, (Object)new WiredChangeDirectionSetting(it.getId(), it.getRotation(), startDirection));
        }
        int delay = packet.readInt();
        if (delay > Emulator.getConfig().getInt("hotel.wired.max_delay", Integer.valueOf(20))) {
            throw new WiredSaveException("Delay too long");
        }
        this.items.clear();
        this.items.putAll((Map)newItems);
        this.startRotation = startDirection;
        this.blockedAction = blockedActionInt;
        this.setDelay(delay);
        return true;
    }

    private RoomUserRotation nextRotation(RoomUserRotation currentRotation) {
        switch (this.blockedAction) {
            case 5: {
                return RoomUserRotation.fromValue((int)currentRotation.getValue()).getOpposite();
            }
            case 3: {
                return RoomUserRotation.counterClockwise((RoomUserRotation)currentRotation);
            }
            case 4: {
                return RoomUserRotation.counterClockwise((RoomUserRotation)RoomUserRotation.counterClockwise((RoomUserRotation)currentRotation));
            }
            case 1: {
                return RoomUserRotation.clockwise((RoomUserRotation)currentRotation);
            }
            case 2: {
                return RoomUserRotation.clockwise((RoomUserRotation)RoomUserRotation.clockwise((RoomUserRotation)currentRotation));
            }
            case 6: {
                return RoomUserRotation.fromValue((int)Emulator.getRandom().nextInt(8));
            }
        }
        return currentRotation;
    }

    protected long requiredCooldown() {
        return 495L;
    }

    static class JsonData {
        RoomUserRotation start_direction;
        int blocked_action;
        List<WiredChangeDirectionSetting> items;
        int delay;

        public JsonData(RoomUserRotation start_direction, int blocked_action, List<WiredChangeDirectionSetting> items, int delay) {
            this.start_direction = start_direction;
            this.blocked_action = blocked_action;
            this.items = items;
            this.delay = delay;
        }
    }
}

