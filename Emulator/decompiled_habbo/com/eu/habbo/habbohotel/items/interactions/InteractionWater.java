/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.items.interactions.InteractionWaterItem;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.set.hash.THashSet;
import java.awt.Rectangle;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.math3.util.Pair;

public class InteractionWater
extends InteractionDefault {
    private static final String DEEP_WATER_NAME = "bw_water_2";
    private final boolean isDeepWater;
    private boolean isInRoom;

    public InteractionWater(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.isDeepWater = baseItem.getName().equalsIgnoreCase(DEEP_WATER_NAME);
        this.isInRoom = this.getRoomId() != 0;
    }

    public InteractionWater(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.isDeepWater = false;
        this.isInRoom = this.getRoomId() != 0;
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        this.updateWaters(room, oldLocation);
    }

    @Override
    public void onPickUp(Room room) {
        this.isInRoom = false;
        this.updateWaters(room, null);
        Object[] empty = new Object[]{};
        for (Habbo habbo : room.getHabbosOnItem(this)) {
            try {
                this.onWalkOff(habbo.getRoomUnit(), room, empty);
            }
            catch (Exception exception) {}
        }
        for (Bot bot : room.getBotsOnItem(this)) {
            try {
                this.onWalkOff(bot.getRoomUnit(), room, empty);
            }
            catch (Exception exception) {}
        }
    }

    @Override
    public void onPlace(Room room) {
        this.isInRoom = true;
        this.updateWaters(room, null);
        super.onPlace(room);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
        Pet pet = room.getPet(roomUnit);
        if (pet == null) {
            return;
        }
        if (!pet.getRoomUnit().hasStatus(RoomUnitStatus.SWIM) && pet.getPetData().canSwim) {
            pet.getRoomUnit().setStatus(RoomUnitStatus.SWIM, "");
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
        Pet pet = room.getPet(roomUnit);
        if (pet == null) {
            return;
        }
        pet.getRoomUnit().removeStatus(RoomUnitStatus.SWIM);
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }

    @Override
    public boolean canToggle(Habbo habbo, Room room) {
        return false;
    }

    @Override
    public boolean canStackAt(Room room, List<Pair<RoomTile, THashSet<HabboItem>>> itemsAtLocation) {
        for (Pair<RoomTile, THashSet<HabboItem>> set : itemsAtLocation) {
            for (HabboItem item : set.getValue()) {
                if (item instanceof InteractionWater) continue;
                return false;
            }
        }
        return super.canStackAt(room, itemsAtLocation);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        if (!super.canWalkOn(roomUnit, room, objects)) {
            return false;
        }
        Pet pet = room.getPet(roomUnit);
        return pet == null || pet.getPetData().canSwim;
    }

    private void updateWaters(Room room, RoomTile oldLocation) {
        this.updateWater(room);
        Rectangle target = this.getRectangle(1, 1);
        Rectangle targetOld = null;
        if (oldLocation != null) {
            targetOld = RoomLayout.getRectangle(oldLocation.x - 1, oldLocation.y - 1, this.getBaseItem().getWidth() + 2, this.getBaseItem().getLength() + 2, this.getRotation());
        }
        for (HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionWater.class)) {
            Rectangle itemRectangle;
            if (item == this || !target.intersects(itemRectangle = item.getRectangle()) && (targetOld == null || !targetOld.intersects(itemRectangle))) continue;
            ((InteractionWater)item).updateWater(room);
        }
        if (targetOld != null) {
            for (HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionWaterItem.class)) {
                if (!targetOld.intersects(item.getRectangle())) continue;
                ((InteractionWaterItem)item).update();
            }
        }
    }

    private void updateWater(Room room) {
        Rectangle target = this.getRectangle();
        for (HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionWaterItem.class)) {
            if (!target.intersects(item.getRectangle())) continue;
            ((InteractionWaterItem)item).update();
        }
        int _1 = 0;
        int _2 = 0;
        int _3 = 0;
        int _4 = 0;
        int _5 = 0;
        int _6 = 0;
        int _7 = 0;
        int _8 = 0;
        int _9 = 0;
        int _10 = 0;
        int _11 = 0;
        int _12 = 0;
        if (this.isValidForMask(room, this.getX() - 1, this.getY() - 1, this.getZ(), true)) {
            _1 = 1;
        }
        if (this.isValidForMask(room, this.getX(), this.getY() - 1, this.getZ())) {
            _2 = 1;
        }
        if (this.isValidForMask(room, this.getX() + 1, this.getY() - 1, this.getZ())) {
            _3 = 1;
        }
        if (this.isValidForMask(room, this.getX() + 2, this.getY() - 1, this.getZ(), true)) {
            _4 = 1;
        }
        if (this.isValidForMask(room, this.getX() - 1, this.getY(), this.getZ())) {
            _5 = 1;
        }
        if (this.isValidForMask(room, this.getX() + 2, this.getY(), this.getZ())) {
            _6 = 1;
        }
        if (this.isValidForMask(room, this.getX() - 1, this.getY() + 1, this.getZ())) {
            _7 = 1;
        }
        if (this.isValidForMask(room, this.getX() + 2, this.getY() + 1, this.getZ())) {
            _8 = 1;
        }
        if (this.isValidForMask(room, this.getX() - 1, this.getY() + 2, this.getZ(), true)) {
            _9 = 1;
        }
        if (this.isValidForMask(room, this.getX(), this.getY() + 2, this.getZ())) {
            _10 = 1;
        }
        if (this.isValidForMask(room, this.getX() + 1, this.getY() + 2, this.getZ())) {
            _11 = 1;
        }
        if (this.isValidForMask(room, this.getX() + 2, this.getY() + 2, this.getZ(), true)) {
            _12 = 1;
        }
        if (_2 == 0 && room.getLayout().isVoidTile(this.getX(), (short)(this.getY() - 1))) {
            _2 = 1;
        }
        if (_3 == 0 && room.getLayout().isVoidTile((short)(this.getX() + 1), (short)(this.getY() - 1))) {
            _3 = 1;
        }
        if (_5 == 0 && room.getLayout().isVoidTile((short)(this.getX() - 1), this.getY())) {
            _5 = 1;
        }
        if (_6 == 0 && room.getLayout().isVoidTile((short)(this.getX() + 2), this.getY())) {
            _6 = 1;
        }
        if (_7 == 0 && room.getLayout().isVoidTile((short)(this.getX() - 1), (short)(this.getY() + 1))) {
            _7 = 1;
        }
        if (_8 == 0 && room.getLayout().isVoidTile((short)(this.getX() + 2), (short)(this.getY() + 1))) {
            _8 = 1;
        }
        if (_10 == 0 && room.getLayout().isVoidTile(this.getX(), (short)(this.getY() + 2))) {
            _10 = 1;
        }
        if (_11 == 0 && room.getLayout().isVoidTile((short)(this.getX() + 1), (short)(this.getY() + 2))) {
            _11 = 1;
        }
        int result = _1 << 11 | _2 << 10 | _3 << 9 | _4 << 8 | _5 << 7 | _6 << 6 | _7 << 5 | _8 << 4 | _9 << 3 | _10 << 2 | _11 << 1 | _12;
        String updatedData = String.valueOf(result);
        if (!this.getExtradata().equals(updatedData)) {
            this.setExtradata(updatedData);
            this.needsUpdate(true);
            room.updateItem(this);
        }
    }

    private boolean isValidForMask(Room room, int x, int y, double z) {
        return this.isValidForMask(room, x, y, z, false);
    }

    private boolean isValidForMask(Room room, int x, int y, double z, boolean corner) {
        for (HabboItem item : room.getItemsAt(x, y, z)) {
            if (!(item instanceof InteractionWater)) continue;
            InteractionWater water = (InteractionWater)item;
            if (!water.isInRoom || (!corner || this.isDeepWater) && water.isDeepWater != this.isDeepWater) continue;
            return true;
        }
        return false;
    }
}

