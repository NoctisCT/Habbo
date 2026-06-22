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
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WiredEffectMoveFurniAway
extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.FLEE;
    private THashSet<HabboItem> items = new THashSet();

    public WiredEffectMoveFurniAway(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMoveFurniAway(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        for (HabboItem item : this.items) {
            if (item.getRoomId() != 0) continue;
            items.add(item);
        }
        this.items.removeAll(items);
        for (HabboItem item : this.items) {
            RoomTile t = room.getLayout().getTile(item.getX(), item.getY());
            RoomUnit target = room.getRoomUnits().stream().min(Comparator.comparingDouble(a -> a.getCurrentLocation().distance(t))).orElse(null);
            if (target == null) continue;
            if (target.getCurrentLocation().distance(t) <= 1.0) {
                Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.COLLISION, target, room, new Object[]{item}), 500L);
                continue;
            }
            int x = 0;
            int y = 0;
            if (target.getX() == item.getX()) {
                y = item.getY() < target.getY() ? --y : ++y;
            } else if (target.getY() == item.getY()) {
                x = item.getX() < target.getX() ? --x : ++x;
            } else if (target.getX() - item.getX() > target.getY() - item.getY()) {
                x = target.getX() - item.getX() > 0 ? --x : ++x;
            } else {
                y = target.getY() - item.getY() > 0 ? --y : ++y;
            }
            RoomTile newLocation = room.getLayout().getTile((short)(item.getX() + x), (short)(item.getY() + y));
            RoomTile oldLocation = room.getLayout().getTile(item.getX(), item.getY());
            double oldZ = item.getZ();
            if (newLocation == null || newLocation.state == RoomTileState.INVALID || newLocation == oldLocation || room.furnitureFitsAt(newLocation, item, item.getRotation(), true) != FurnitureMovementError.NONE || room.moveFurniTo(item, newLocation, item.getRotation(), null, false) != FurnitureMovementError.NONE) continue;
            room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, oldZ, newLocation, item.getZ(), 0.0, room).compose());
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

