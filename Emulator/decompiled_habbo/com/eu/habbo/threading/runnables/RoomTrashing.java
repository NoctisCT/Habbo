/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import gnu.trove.set.hash.THashSet;

public class RoomTrashing
implements Runnable {
    public static RoomTrashing INSTANCE;
    private Habbo habbo;
    private Room room;

    public RoomTrashing(Habbo habbo, Room room) {
        this.habbo = habbo;
        this.room = room;
        INSTANCE = this;
    }

    @EventHandler
    public static void onUserWalkEvent(UserTakeStepEvent event) {
        if (INSTANCE == null) {
            return;
        }
        if (RoomTrashing.INSTANCE.habbo == null) {
            return;
        }
        if (!RoomTrashing.INSTANCE.habbo.isOnline()) {
            RoomTrashing.INSTANCE.habbo = null;
        }
        if (RoomTrashing.INSTANCE.habbo == event.habbo && event.habbo.getHabboInfo().getCurrentRoom() != null) {
            if (event.habbo.getHabboInfo().getCurrentRoom().equals(RoomTrashing.INSTANCE.room)) {
                double offsetZ;
                RoomTile s;
                int offset;
                THashSet<ServerMessage> messages = new THashSet<ServerMessage>();
                THashSet<HabboItem> items = RoomTrashing.INSTANCE.room.getItemsAt(event.toLocation);
                RoomTile t = null;
                for (offset = Emulator.getRandom().nextInt(4) + 2; offset > 0; --offset) {
                    t = RoomTrashing.INSTANCE.room.getLayout().getTileInFront(RoomTrashing.INSTANCE.room.getLayout().getTile(event.toLocation.x, event.toLocation.y), event.habbo.getRoomUnit().getBodyRotation().getValue(), (short)offset);
                    if (RoomTrashing.INSTANCE.room.getLayout().tileWalkable(t.x, t.y)) break;
                }
                for (HabboItem item : items) {
                    double offsetZ2 = RoomTrashing.INSTANCE.room.getTopHeightAt(t.x, t.y) - item.getZ();
                    messages.add(new FloorItemOnRollerComposer(item, null, t, offsetZ2, RoomTrashing.INSTANCE.room).compose());
                }
                t = null;
                for (offset = Emulator.getRandom().nextInt(4) + 2; offset > 0; --offset) {
                    t = RoomTrashing.INSTANCE.room.getLayout().getTileInFront(RoomTrashing.INSTANCE.room.getLayout().getTile(event.toLocation.x, event.toLocation.y), event.habbo.getRoomUnit().getBodyRotation().getValue() + 7, (short)offset);
                    if (RoomTrashing.INSTANCE.room.getLayout().tileWalkable(t.x, t.y)) break;
                }
                if ((s = RoomTrashing.INSTANCE.room.getLayout().getTileInFront(RoomTrashing.INSTANCE.habbo.getRoomUnit().getCurrentLocation(), RoomTrashing.INSTANCE.habbo.getRoomUnit().getBodyRotation().getValue() + 7)) != null) {
                    items = RoomTrashing.INSTANCE.room.getItemsAt(s);
                }
                for (HabboItem item : items) {
                    offsetZ = RoomTrashing.INSTANCE.room.getTopHeightAt(t.x, t.y) - item.getZ();
                    messages.add(new FloorItemOnRollerComposer(item, null, t, offsetZ, RoomTrashing.INSTANCE.room).compose());
                }
                t = null;
                for (offset = Emulator.getRandom().nextInt(4) + 2; offset > 0; --offset) {
                    t = INSTANCE.getRoom().getLayout().getTileInFront(event.toLocation, event.habbo.getRoomUnit().getBodyRotation().getValue() + 1, (short)offset);
                    if (RoomTrashing.INSTANCE.room.getLayout().tileWalkable(t.x, t.y)) break;
                }
                s = INSTANCE.getRoom().getLayout().getTileInFront(RoomTrashing.INSTANCE.habbo.getRoomUnit().getCurrentLocation(), RoomTrashing.INSTANCE.habbo.getRoomUnit().getBodyRotation().getValue() + 1);
                items = RoomTrashing.INSTANCE.room.getItemsAt(s);
                for (HabboItem item : items) {
                    offsetZ = RoomTrashing.INSTANCE.room.getTopHeightAt(t.x, t.y) - item.getZ();
                    messages.add(new FloorItemOnRollerComposer(item, null, t, offsetZ, RoomTrashing.INSTANCE.room).compose());
                }
                for (ServerMessage message : messages) {
                    RoomTrashing.INSTANCE.room.sendComposer(message);
                }
            } else {
                RoomTrashing.INSTANCE.habbo = null;
                RoomTrashing.INSTANCE.room = null;
            }
        }
    }

    @Override
    public void run() {
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public void setHabbo(Habbo habbo) {
        this.habbo = habbo;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}

