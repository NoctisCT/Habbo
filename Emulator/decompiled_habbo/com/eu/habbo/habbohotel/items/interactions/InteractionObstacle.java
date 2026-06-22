/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class InteractionObstacle
extends HabboItem
implements ICycleable {
    private THashSet<RoomTile> middleTiles;

    public InteractionObstacle(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtradata("0");
        this.middleTiles = new THashSet();
    }

    public InteractionObstacle(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
        this.middleTiles = new THashSet();
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt(this.isLimited() ? 256 : 0);
        serverMessage.appendString(this.getExtradata());
        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        Pet pet;
        super.onWalkOn(roomUnit, room, objects);
        Habbo habbo = room.getHabbo(roomUnit);
        if (habbo == null && (pet = room.getPet(roomUnit)) instanceof HorsePet && ((HorsePet)pet).getRider() != null && roomUnit.getBodyRotation().getValue() % 2 == 0) {
            if (this.getRotation() == 2) {
                if (roomUnit.getBodyRotation().equals((Object)RoomUserRotation.WEST)) {
                    ((HorsePet)pet).getRider().getRoomUnit().setGoalLocation(room.getLayout().getTile((short)(roomUnit.getX() - 3), roomUnit.getY()));
                } else if (roomUnit.getBodyRotation().equals((Object)RoomUserRotation.EAST)) {
                    ((HorsePet)pet).getRider().getRoomUnit().setGoalLocation(room.getLayout().getTile((short)(roomUnit.getX() + 3), roomUnit.getY()));
                }
            } else if (this.getRotation() == 4) {
                if (roomUnit.getBodyRotation().equals((Object)RoomUserRotation.NORTH)) {
                    ((HorsePet)pet).getRider().getRoomUnit().setGoalLocation(room.getLayout().getTile(roomUnit.getX(), (short)(roomUnit.getY() - 3)));
                } else if (roomUnit.getBodyRotation().equals((Object)RoomUserRotation.SOUTH)) {
                    ((HorsePet)pet).getRider().getRoomUnit().setGoalLocation(room.getLayout().getTile(roomUnit.getX(), (short)(roomUnit.getY() + 3)));
                }
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        Pet pet;
        super.onWalkOff(roomUnit, room, objects);
        Habbo habbo = room.getHabbo(roomUnit);
        if (habbo == null && (pet = room.getPet(roomUnit)) instanceof HorsePet && ((HorsePet)pet).getRider() != null) {
            pet.getRoomUnit().removeStatus(RoomUnitStatus.JUMP);
        }
    }

    @Override
    public void onPlace(Room room) {
        super.onPlace(room);
        this.calculateMiddleTiles(room);
    }

    @Override
    public void onPickUp(Room room) {
        super.onPickUp(room);
        this.middleTiles.clear();
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        this.calculateMiddleTiles(room);
    }

    private void calculateMiddleTiles(Room room) {
        this.middleTiles.clear();
        if (this.getRotation() == 2) {
            this.middleTiles.add(room.getLayout().getTile((short)(this.getX() + 1), this.getY()));
            this.middleTiles.add(room.getLayout().getTile((short)(this.getX() + 1), (short)(this.getY() + 1)));
        } else if (this.getRotation() == 4) {
            this.middleTiles.add(room.getLayout().getTile(this.getX(), (short)(this.getY() + 1)));
            this.middleTiles.add(room.getLayout().getTile((short)(this.getX() + 1), (short)(this.getY() + 1)));
        }
    }

    @Override
    public RoomTileState getOverrideTileState(RoomTile tile, Room room) {
        if (this.middleTiles.contains(tile)) {
            return RoomTileState.BLOCKED;
        }
        return null;
    }

    @Override
    public void cycle(Room room) {
        if (this.middleTiles.size() == 0) {
            this.calculateMiddleTiles(room);
        }
        for (RoomTile tile : this.middleTiles) {
            for (RoomUnit unit : tile.getUnits()) {
                if (unit.getPath().size() != 0 || unit.hasStatus(RoomUnitStatus.MOVE) || unit.getBodyRotation().getValue() != this.getRotation() && Objects.requireNonNull(unit.getBodyRotation().getOpposite()).getValue() != this.getRotation()) continue;
                RoomTile tileInfront = room.getLayout().getTileInFront(unit.getCurrentLocation(), unit.getBodyRotation().getValue());
                if (tileInfront.state != RoomTileState.INVALID && tileInfront.state != RoomTileState.BLOCKED && room.getRoomUnitsAt(tileInfront).size() == 0) {
                    unit.setGoalLocation(tileInfront);
                    continue;
                }
                RoomTile tileBehind = room.getLayout().getTileInFront(unit.getCurrentLocation(), Objects.requireNonNull(unit.getBodyRotation().getOpposite()).getValue());
                if (tileBehind.state == RoomTileState.INVALID || tileBehind.state == RoomTileState.BLOCKED || room.getRoomUnitsAt(tileBehind).size() != 0) continue;
                unit.setGoalLocation(tileBehind);
            }
        }
    }
}

