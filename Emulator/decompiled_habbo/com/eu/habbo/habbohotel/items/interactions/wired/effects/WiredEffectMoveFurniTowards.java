/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.threading.runnables.WiredCollissionRunnable;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WiredEffectMoveFurniTowards
extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.CHASE;
    private THashSet<HabboItem> items = new THashSet();
    private THashMap<Integer, RoomUserRotation> lastDirections = new THashMap();

    public WiredEffectMoveFurniTowards(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMoveFurniTowards(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public List<RoomUserRotation> getAvailableDirections(HabboItem item, Room room) {
        RoomUserRotation[] rotations;
        ArrayList<RoomUserRotation> availableDirections = new ArrayList<RoomUserRotation>();
        RoomLayout layout = room.getLayout();
        RoomTile currentTile = layout.getTile(item.getX(), item.getY());
        for (RoomUserRotation rot : rotations = new RoomUserRotation[]{RoomUserRotation.NORTH, RoomUserRotation.EAST, RoomUserRotation.SOUTH, RoomUserRotation.WEST}) {
            HabboItem topItem;
            RoomTile tile = layout.getTileInFront(currentTile, rot.getValue());
            if (tile == null || tile.state == RoomTileState.BLOCKED || tile.state == RoomTileState.INVALID || !layout.tileExists(tile.x, tile.y) || room.furnitureFitsAt(tile, item, item.getRotation()) == FurnitureMovementError.INVALID_MOVE || (topItem = room.getTopItemAt(tile.x, tile.y)) != null && !topItem.getBaseItem().allowStack() || !tile.getAllowStack()) continue;
            availableDirections.add(rot);
        }
        return availableDirections;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        for (HabboItem item : this.items) {
            if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) != null) continue;
            items.add(item);
        }
        for (HabboItem item : items) {
            this.items.remove(item);
        }
        for (HabboItem item : this.items) {
            if (item == null) continue;
            RoomUserRotation moveDirection = null;
            RoomUserRotation lastDirection = this.lastDirections.get(item.getId());
            RoomUnit target = null;
            RoomLayout layout = room.getLayout();
            boolean collided = false;
            if (layout == null) break;
            block3: for (int i = 0; i < 3 && target == null; ++i) {
                RoomUserRotation[] rotations;
                for (RoomUserRotation rot : rotations = new RoomUserRotation[]{RoomUserRotation.NORTH, RoomUserRotation.EAST, RoomUserRotation.SOUTH, RoomUserRotation.WEST}) {
                    Collection<RoomUnit> roomUnitsAtTile;
                    RoomTile startTile = layout.getTile(item.getX(), item.getY());
                    for (int ii = 0; ii <= i && startTile != null; ++ii) {
                        startTile = layout.getTileInFront(startTile, rot.getValue());
                    }
                    if (startTile == null || !layout.tileExists(startTile.x, startTile.y) || (roomUnitsAtTile = room.getRoomUnitsAt(startTile)).size() <= 0) continue;
                    target = roomUnitsAtTile.iterator().next();
                    if (i != 0) continue block3;
                    collided = true;
                    Emulator.getThreading().run(new WiredCollissionRunnable(target, room, new Object[]{item}));
                    continue block3;
                }
            }
            if (collided) continue;
            if (target != null) {
                moveDirection = target.getX() == item.getX() ? (item.getY() < target.getY() ? RoomUserRotation.SOUTH : RoomUserRotation.NORTH) : (target.getY() == item.getY() ? (item.getX() < target.getX() ? RoomUserRotation.EAST : RoomUserRotation.WEST) : (target.getX() - item.getX() > target.getY() - item.getY() ? (target.getX() - item.getX() > 0 ? RoomUserRotation.EAST : RoomUserRotation.WEST) : (target.getY() - item.getY() > 0 ? RoomUserRotation.SOUTH : RoomUserRotation.NORTH)));
            }
            List<RoomUserRotation> availableDirections = this.getAvailableDirections(item, room);
            if (moveDirection != null && !availableDirections.contains((Object)moveDirection)) {
                moveDirection = null;
            }
            if (moveDirection == null) {
                if (availableDirections.size() == 0) continue;
                if (availableDirections.size() == 1) {
                    moveDirection = availableDirections.iterator().next();
                } else if (availableDirections.size() == 2) {
                    if (lastDirection == null) {
                        moveDirection = availableDirections.get(Emulator.getRandom().nextInt(availableDirections.size()));
                    } else {
                        RoomUserRotation oppositeLast = lastDirection.getOpposite();
                        moveDirection = availableDirections.get(0) == oppositeLast ? availableDirections.get(1) : availableDirections.get(0);
                    }
                } else {
                    if (lastDirection != null) {
                        RoomUserRotation opposite = lastDirection.getOpposite();
                        availableDirections.remove((Object)opposite);
                    }
                    moveDirection = availableDirections.get(Emulator.getRandom().nextInt(availableDirections.size()));
                }
            }
            RoomTile newTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), moveDirection.getValue());
            RoomTile oldLocation = room.getLayout().getTile(item.getX(), item.getY());
            double oldZ = item.getZ();
            if (newTile == null) continue;
            this.lastDirections.put(item.getId(), moveDirection);
            if (newTile.state == RoomTileState.INVALID || newTile == oldLocation || room.furnitureFitsAt(newTile, item, item.getRotation(), true) != FurnitureMovementError.NONE || room.moveFurniTo(item, newTile, item.getRotation(), null, false) != FurnitureMovementError.NONE) continue;
            room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, oldZ, newTile, item.getZ(), 0.0, room).compose());
        }
        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.getDelay(), this.items.stream().map(HabboItem::getId).collect(Collectors.toList())));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        block4: {
            String wiredData;
            block3: {
                this.items = new THashSet();
                wiredData = set.getString("wired_data");
                if (!wiredData.startsWith("{")) break block3;
                JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
                this.setDelay(data.delay);
                for (Integer id : data.itemIds) {
                    HabboItem item = room.getHabboItem(id);
                    if (item == null) continue;
                    this.items.add(item);
                }
                break block4;
            }
            String[] wiredDataOld = wiredData.split("\t");
            if (wiredDataOld.length >= 1) {
                this.setDelay(Integer.parseInt(wiredDataOld[0]));
            }
            if (wiredDataOld.length != 2 || !wiredDataOld[1].contains(";")) break block4;
            for (String s : wiredDataOld[1].split(";")) {
                HabboItem item = room.getHabboItem(Integer.parseInt(s));
                if (item == null) continue;
                this.items.add(item);
            }
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        for (HabboItem item : this.items) {
            if (item.getRoomId() == this.getRoomId() && Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) != null) continue;
            items.add(item);
        }
        for (HabboItem item : items) {
            this.items.remove(item);
        }
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (HabboItem item : this.items) {
            message.appendInt(item.getId());
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        int itemsCount = settings.getFurniIds().length;
        if (itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            throw new WiredSaveException("Too many furni selected");
        }
        ArrayList<HabboItem> newItems = new ArrayList<HabboItem>();
        for (int i = 0; i < itemsCount; ++i) {
            int itemId = settings.getFurniIds()[i];
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);
            if (it == null) {
                throw new WiredSaveException(String.format("Item %s not found", itemId));
            }
            newItems.add(it);
        }
        int delay = settings.getDelay();
        if (delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20)) {
            throw new WiredSaveException("Delay too long");
        }
        this.items.clear();
        this.items.addAll(newItems);
        this.setDelay(delay);
        return true;
    }

    @Override
    protected long requiredCooldown() {
        return 495L;
    }

    static class JsonData {
        int delay;
        List<Integer> itemIds;

        public JsonData(int delay, List<Integer> itemIds) {
            this.delay = delay;
            this.itemIds = itemIds;
        }
    }
}

