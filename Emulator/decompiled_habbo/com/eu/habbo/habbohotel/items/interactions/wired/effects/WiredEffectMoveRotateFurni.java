/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
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
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WiredEffectMoveRotateFurni
extends InteractionWiredEffect
implements ICycleable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiredEffectMoveRotateFurni.class);
    public static final WiredEffectType type = WiredEffectType.MOVE_ROTATE;
    private final THashSet<HabboItem> items = new THashSet(WiredHandler.MAXIMUM_FURNI_SELECTION / 2);
    private int direction;
    private int rotation;
    private THashSet<HabboItem> itemCooldowns = new THashSet();

    public WiredEffectMoveRotateFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMoveRotateFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        this.items.removeIf(item -> Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null);
        for (HabboItem item2 : this.items) {
            if (this.itemCooldowns.contains(item2)) continue;
            int newRotation = this.rotation > 0 ? this.getNewRotation(item2) : item2.getRotation();
            RoomTile newLocation = room.getLayout().getTile(item2.getX(), item2.getY());
            RoomTile oldLocation = room.getLayout().getTile(item2.getX(), item2.getY());
            double oldZ = item2.getZ();
            if (this.direction > 0) {
                RoomUserRotation moveDirection = this.getMovementDirection();
                newLocation = room.getLayout().getTile((short)(item2.getX() + (moveDirection == RoomUserRotation.WEST || moveDirection == RoomUserRotation.NORTH_WEST || moveDirection == RoomUserRotation.SOUTH_WEST ? -1 : (moveDirection == RoomUserRotation.EAST || moveDirection == RoomUserRotation.SOUTH_EAST || moveDirection == RoomUserRotation.NORTH_EAST ? 1 : 0))), (short)(item2.getY() + (moveDirection == RoomUserRotation.NORTH || moveDirection == RoomUserRotation.NORTH_EAST || moveDirection == RoomUserRotation.NORTH_WEST ? 1 : (moveDirection == RoomUserRotation.SOUTH || moveDirection == RoomUserRotation.SOUTH_EAST || moveDirection == RoomUserRotation.SOUTH_WEST ? -1 : 0))));
            }
            boolean slideAnimation = item2.getRotation() == newRotation;
            FurnitureMovementError furniMoveTest = room.furnitureFitsAt(newLocation, item2, newRotation, true);
            if (newLocation == null || newLocation.state == RoomTileState.INVALID || newLocation == oldLocation && newRotation == item2.getRotation() || furniMoveTest != FurnitureMovementError.NONE && (furniMoveTest != FurnitureMovementError.TILE_HAS_BOTS && furniMoveTest != FurnitureMovementError.TILE_HAS_HABBOS && furniMoveTest != FurnitureMovementError.TILE_HAS_PETS || newLocation != oldLocation) || room.furnitureFitsAt(newLocation, item2, newRotation, false) != FurnitureMovementError.NONE || room.moveFurniTo(item2, newLocation, newRotation, null, !slideAnimation) != FurnitureMovementError.NONE) continue;
            this.itemCooldowns.add(item2);
            if (!slideAnimation) continue;
            room.sendComposer(new FloorItemOnRollerComposer(item2, null, oldLocation, oldZ, newLocation, item2.getZ(), 0.0, room).compose());
        }
        return true;
    }

    @Override
    public String getWiredData() {
        THashSet<HabboItem> itemsToRemove = new THashSet<HabboItem>(this.items.size() / 2);
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        for (HabboItem item : this.items) {
            if (item.getRoomId() == this.getRoomId() && (room == null || room.getHabboItem(item.getId()) != null)) continue;
            itemsToRemove.add(item);
        }
        for (HabboItem item : itemsToRemove) {
            this.items.remove(item);
        }
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.direction, this.rotation, this.getDelay(), this.items.stream().map(HabboItem::getId).collect(Collectors.toList())));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        block5: {
            String wiredData;
            block4: {
                this.items.clear();
                wiredData = set.getString("wired_data");
                if (!wiredData.startsWith("{")) break block4;
                JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
                this.setDelay(data.delay);
                this.direction = data.direction;
                this.rotation = data.rotation;
                for (Integer id : data.itemIds) {
                    HabboItem item = room.getHabboItem(id);
                    if (item == null) continue;
                    this.items.add(item);
                }
                break block5;
            }
            String[] data = wiredData.split("\t");
            if (data.length != 4) break block5;
            try {
                this.direction = Integer.parseInt(data[0]);
                this.rotation = Integer.parseInt(data[1]);
                this.setDelay(Integer.parseInt(data[2]));
            }
            catch (Exception e) {
                System.out.println(e);
            }
            for (String s : data[3].split("\r")) {
                HabboItem item = room.getHabboItem(Integer.parseInt(s));
                if (item == null) continue;
                this.items.add(item);
            }
        }
    }

    @Override
    public void onPickUp() {
        this.direction = 0;
        this.rotation = 0;
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
        message.appendInt(2);
        message.appendInt(this.direction);
        message.appendInt(this.rotation);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room == null) {
            return false;
        }
        if (settings.getIntParams().length < 2) {
            throw new WiredSaveException("invalid data");
        }
        this.direction = settings.getIntParams()[0];
        this.rotation = settings.getIntParams()[1];
        int count = settings.getFurniIds().length;
        if (count > Emulator.getConfig().getInt("hotel.wired.furni.selection.count", 5)) {
            return false;
        }
        this.items.clear();
        for (int i = 0; i < count; ++i) {
            this.items.add(room.getHabboItem(settings.getFurniIds()[i]));
        }
        this.setDelay(settings.getDelay());
        return true;
    }

    private int getNewRotation(HabboItem item) {
        boolean rotationToAdd = false;
        if (item.getMaximumRotations() == 2) {
            return item.getRotation() == 0 ? 4 : 0;
        }
        if (item.getMaximumRotations() == 1) {
            return item.getRotation();
        }
        if (item.getMaximumRotations() > 4) {
            if (this.rotation == 1) {
                return item.getRotation() == item.getMaximumRotations() - 1 ? 0 : item.getRotation() + 1;
            }
            if (this.rotation == 2) {
                return item.getRotation() > 0 ? item.getRotation() - 1 : item.getMaximumRotations() - 1;
            }
            if (this.rotation == 3) {
                THashSet<Integer> possibleRotations = new THashSet<Integer>();
                for (int i = 0; i < item.getMaximumRotations(); ++i) {
                    possibleRotations.add(i);
                }
                possibleRotations.remove(item.getRotation());
                if (possibleRotations.size() > 0) {
                    int index = Emulator.getRandom().nextInt(possibleRotations.size());
                    Iterator iter = possibleRotations.iterator();
                    for (int i = 0; i < index; ++i) {
                        iter.next();
                    }
                    return (Integer)iter.next();
                }
            }
        } else {
            if (this.rotation == 1) {
                return (item.getRotation() + 2) % 8;
            }
            if (this.rotation == 2) {
                int rot = (item.getRotation() - 2) % 8;
                if (rot < 0) {
                    rot += 8;
                }
                return rot;
            }
            if (this.rotation == 3) {
                THashSet<Integer> possibleRotations = new THashSet<Integer>();
                for (int i = 0; i < item.getMaximumRotations(); ++i) {
                    possibleRotations.add(i * 2);
                }
                possibleRotations.remove(item.getRotation());
                if (possibleRotations.size() > 0) {
                    int index = Emulator.getRandom().nextInt(possibleRotations.size());
                    Iterator iter = possibleRotations.iterator();
                    for (int i = 0; i < index; ++i) {
                        iter.next();
                    }
                    return (Integer)iter.next();
                }
            }
        }
        return item.getRotation();
    }

    private RoomUserRotation getMovementDirection() {
        RoomUserRotation movemementDirection = RoomUserRotation.NORTH;
        if (this.direction == 1) {
            movemementDirection = RoomUserRotation.values()[Emulator.getRandom().nextInt(RoomUserRotation.values().length / 2) * 2];
        } else if (this.direction == 2) {
            movemementDirection = Emulator.getRandom().nextInt(2) == 1 ? RoomUserRotation.EAST : RoomUserRotation.WEST;
        } else if (this.direction == 3) {
            if (Emulator.getRandom().nextInt(2) != 1) {
                movemementDirection = RoomUserRotation.SOUTH;
            }
        } else if (this.direction == 4) {
            movemementDirection = RoomUserRotation.SOUTH;
        } else if (this.direction == 5) {
            movemementDirection = RoomUserRotation.EAST;
        } else if (this.direction == 7) {
            movemementDirection = RoomUserRotation.WEST;
        }
        return movemementDirection;
    }

    @Override
    public void cycle(Room room) {
        this.itemCooldowns.clear();
    }

    static class JsonData {
        int direction;
        int rotation;
        int delay;
        List<Integer> itemIds;

        public JsonData(int direction, int rotation, int delay, List<Integer> itemIds) {
            this.direction = direction;
            this.rotation = rotation;
            this.delay = delay;
            this.itemIds = itemIds;
        }
    }
}

