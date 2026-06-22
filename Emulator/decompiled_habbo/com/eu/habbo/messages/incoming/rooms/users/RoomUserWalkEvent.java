/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import gnu.trove.set.hash.THashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomUserWalkEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomUserWalkEvent.class);

    @Override
    public int getRatelimit() {
        return 500;
    }

    @Override
    public void handle() throws Exception {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            int x = this.packet.readInt();
            int y = this.packet.readInt();
            Habbo habbo = this.client.getHabbo();
            RoomUnit roomUnit = this.client.getHabbo().getRoomUnit();
            if (roomUnit.isTeleporting) {
                return;
            }
            if (roomUnit.isKicked) {
                return;
            }
            if (roomUnit.getCacheable().get("control") != null && (habbo = (Habbo)roomUnit.getCacheable().get("control")).getHabboInfo().getCurrentRoom() != this.client.getHabbo().getHabboInfo().getCurrentRoom()) {
                habbo.getRoomUnit().getCacheable().remove("controller");
                this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                habbo = this.client.getHabbo();
            }
            roomUnit = habbo.getRoomUnit();
            Room room = habbo.getHabboInfo().getCurrentRoom();
            try {
                if (roomUnit != null && roomUnit.isInRoom() && roomUnit.canWalk()) {
                    if (!roomUnit.cmdTeleport) {
                        THashSet<HabboItem> items;
                        HabboItem bed;
                        RoomTile tile;
                        if (habbo.getHabboInfo().getRiding() != null && habbo.getHabboInfo().getRiding().getTask() != null && habbo.getHabboInfo().getRiding().getTask().equals((Object)PetTasks.JUMP)) {
                            return;
                        }
                        if (x == roomUnit.getX() && y == roomUnit.getY()) {
                            return;
                        }
                        if (room == null || room.getLayout() == null) {
                            return;
                        }
                        if (roomUnit.isIdle()) {
                            UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.WALKED, false);
                            Emulator.getPluginManager().fireEvent(event);
                            if (!event.isCancelled() && !event.idle) {
                                if (roomUnit.getRoom() != null) {
                                    roomUnit.getRoom().unIdle(habbo);
                                }
                                roomUnit.resetIdleTimer();
                            }
                        }
                        if ((tile = room.getLayout().getTile((short)x, (short)y)) == null) {
                            return;
                        }
                        if ((habbo.getRoomUnit().hasStatus(RoomUnitStatus.LAY) || habbo.getRoomUnit().hasStatus(RoomUnitStatus.SNOWWAR_PICK) || habbo.getRoomUnit().hasStatus(RoomUnitStatus.SNOWWAR_DIE_FRONT) || habbo.getRoomUnit().hasStatus(RoomUnitStatus.SNOWWAR_DIE_BACK)) && room.getLayout().getTilesInFront(habbo.getRoomUnit().getCurrentLocation(), habbo.getRoomUnit().getBodyRotation().getValue(), 2).contains(tile)) {
                            return;
                        }
                        if (room.canLayAt(tile.x, tile.y) && (bed = room.getTopItemAt(tile.x, tile.y)) != null && bed.getBaseItem().allowLay()) {
                            RoomTile pillow = room.getLayout().getTile(bed.getX(), bed.getY());
                            switch (bed.getRotation()) {
                                case 0: 
                                case 4: {
                                    pillow = room.getLayout().getTile((short)x, bed.getY());
                                    break;
                                }
                                case 2: 
                                case 8: {
                                    pillow = room.getLayout().getTile(bed.getX(), (short)y);
                                }
                            }
                            if (pillow != null && room.canLayAt(pillow.x, pillow.y)) {
                                roomUnit.setGoalLocation(pillow);
                                return;
                            }
                        }
                        if ((items = room.getItemsAt(tile)).size() > 0) {
                            for (HabboItem item : items) {
                                RoomTile overriddenTile = item.getOverrideGoalTile(roomUnit, room, tile);
                                if (overriddenTile == null) {
                                    return;
                                }
                                if (overriddenTile.equals(tile) || !overriddenTile.isWalkable()) continue;
                                tile = overriddenTile;
                                break;
                            }
                        }
                        if (tile.isWalkable() || room.canSitOrLayAt(tile.x, tile.y)) {
                            if (roomUnit.getMoveBlockingTask() != null) {
                                roomUnit.getMoveBlockingTask().get();
                            }
                            roomUnit.setGoalLocation(tile);
                        }
                    } else {
                        RoomTile t = room.getLayout().getTile((short)x, (short)y);
                        room.sendComposer(new RoomUnitOnRollerComposer(roomUnit, t, room).compose());
                        if (habbo.getHabboInfo().getRiding() != null) {
                            room.sendComposer(new RoomUnitOnRollerComposer(habbo.getHabboInfo().getRiding().getRoomUnit(), t, room).compose());
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
    }
}

