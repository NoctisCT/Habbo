/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWired;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.InteractionWiredMatchFurniSettings;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import java.util.ArrayList;
import java.util.Optional;

public class WiredApplySetConditionsEvent
extends MessageHandler {
    @Override
    public int getRatelimit() {
        return 500;
    }

    @Override
    public void handle() throws Exception {
        int itemId = this.packet.readInt();
        if (!this.client.getHabbo().getRoomUnit().isInRoom()) {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNITURE_PLACEMENT_ERROR.key, FurnitureMovementError.NO_RIGHTS.errorCode));
            return;
        }
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null && (room.hasRights(this.client.getHabbo()) || room.isOwner(this.client.getHabbo()))) {
            HabboItem wiredItem;
            ArrayList<InteractionWired> wireds = new ArrayList<InteractionWired>();
            wireds.addAll(room.getRoomSpecialTypes().getConditions());
            wireds.addAll(room.getRoomSpecialTypes().getEffects());
            Optional<HabboItem> item = wireds.stream().filter(wired -> wired.getId() == itemId).findFirst();
            if (item.isPresent() && (wiredItem = item.get()) instanceof InteractionWiredMatchFurniSettings) {
                InteractionWiredMatchFurniSettings wired2 = (InteractionWiredMatchFurniSettings)((Object)wiredItem);
                wired2.getMatchFurniSettings().forEach(setting -> {
                    HabboItem matchItem = room.getHabboItem(setting.item_id);
                    if (wired2.shouldMatchState() && matchItem.allowWiredResetState() && !setting.state.equals(" ") && !matchItem.getExtradata().equals(setting.state)) {
                        matchItem.setExtradata(setting.state);
                        room.updateItemState(matchItem);
                    }
                    RoomTile oldLocation = room.getLayout().getTile(matchItem.getX(), matchItem.getY());
                    double oldZ = matchItem.getZ();
                    if (wired2.shouldMatchRotation() && !wired2.shouldMatchPosition()) {
                        if (matchItem.getRotation() != setting.rotation && room.furnitureFitsAt(oldLocation, matchItem, setting.rotation, false) == FurnitureMovementError.NONE) {
                            room.moveFurniTo(matchItem, oldLocation, setting.rotation, null, true);
                        }
                    } else if (wired2.shouldMatchPosition()) {
                        int newRotation;
                        boolean slideAnimation = !wired2.shouldMatchRotation() || matchItem.getRotation() == setting.rotation;
                        RoomTile newLocation = room.getLayout().getTile((short)setting.x, (short)setting.y);
                        int n = newRotation = wired2.shouldMatchRotation() ? setting.rotation : matchItem.getRotation();
                        if (newLocation != null && newLocation.state != RoomTileState.INVALID && (newLocation != oldLocation || newRotation != matchItem.getRotation()) && room.furnitureFitsAt(newLocation, matchItem, newRotation, true) == FurnitureMovementError.NONE && room.moveFurniTo(matchItem, newLocation, newRotation, null, !slideAnimation) == FurnitureMovementError.NONE && slideAnimation) {
                            room.sendComposer(new FloorItemOnRollerComposer(matchItem, null, oldLocation, oldZ, newLocation, matchItem.getZ(), 0.0, room).compose());
                        }
                    }
                });
            }
        }
    }
}

