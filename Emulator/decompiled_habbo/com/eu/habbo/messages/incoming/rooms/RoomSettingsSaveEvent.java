/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.habbohotel.rooms.RoomState;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.RoomChatSettingsComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomEditSettingsErrorComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomSettingsSavedComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomSettingsUpdatedComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomThicknessComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomSettingsSaveEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomSettingsSaveEvent.class);

    @Override
    public void handle() throws Exception {
        int roomId = this.packet.readInt();
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);
        if (room != null && room.isOwner(this.client.getHabbo())) {
            String name = this.packet.readString();
            if (name.trim().isEmpty() || name.length() > 60) {
                this.client.sendResponse(new RoomEditSettingsErrorComposer(room.getId(), 7, ""));
                return;
            }
            if (!Emulator.getGameEnvironment().getWordFilter().filter(name, this.client.getHabbo()).equals(name)) {
                this.client.sendResponse(new RoomEditSettingsErrorComposer(room.getId(), 8, ""));
                return;
            }
            String description = this.packet.readString();
            if (description.length() > 255) {
                return;
            }
            if (!Emulator.getGameEnvironment().getWordFilter().filter(description, this.client.getHabbo()).equals(description)) {
                this.client.sendResponse(new RoomEditSettingsErrorComposer(room.getId(), 10, ""));
                return;
            }
            RoomState state = RoomState.values()[this.packet.readInt() % RoomState.values().length];
            String password = this.packet.readString();
            if (state == RoomState.PASSWORD && password.isEmpty() && (room.getPassword() == null || room.getPassword().isEmpty())) {
                this.client.sendResponse(new RoomEditSettingsErrorComposer(room.getId(), 5, ""));
                return;
            }
            int usersMax = this.packet.readInt();
            int categoryId = this.packet.readInt();
            StringBuilder tags = new StringBuilder();
            int count = Math.min(this.packet.readInt(), 2);
            for (int i = 0; i < count; ++i) {
                String tag = this.packet.readString();
                if (tag.length() > 15) {
                    this.client.sendResponse(new RoomEditSettingsErrorComposer(room.getId(), 13, ""));
                    return;
                }
                tags.append(tag).append(";");
            }
            if (!Emulator.getGameEnvironment().getWordFilter().filter(tags.toString(), this.client.getHabbo()).contentEquals(tags)) {
                this.client.sendResponse(new RoomEditSettingsErrorComposer(room.getId(), 11, ""));
                return;
            }
            if (tags.length() > 0) {
                for (String s : Emulator.getConfig().getValue("hotel.room.tags.staff").split(";")) {
                    if (!tags.toString().contains(s)) continue;
                    this.client.sendResponse(new RoomEditSettingsErrorComposer(room.getId(), 12, "1"));
                    return;
                }
            }
            room.setName(name);
            room.setDescription(description);
            room.setState(state);
            if (!password.isEmpty()) {
                room.setPassword(password);
            }
            room.setUsersMax(usersMax);
            if (Emulator.getGameEnvironment().getRoomManager().hasCategory(categoryId, this.client.getHabbo())) {
                room.setCategory(categoryId);
            } else {
                RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(categoryId);
                String message = category == null ? Emulator.getTexts().getValue("scripter.warning.roomsettings.category.nonexisting").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()) : Emulator.getTexts().getValue("scripter.warning.roomsettings.category.permission").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%category%", String.valueOf(Emulator.getGameEnvironment().getRoomManager().getCategory(categoryId)));
                ScripterManager.scripterDetected(this.client, message);
                LOGGER.info(message);
            }
            room.setTags(tags.toString());
            room.setTradeMode(this.packet.readInt());
            room.setAllowPets(this.packet.readBoolean());
            room.setAllowPetsEat(this.packet.readBoolean());
            room.setAllowWalkthrough(this.packet.readBoolean());
            room.setHideWall(this.packet.readBoolean());
            room.setWallSize(this.packet.readInt());
            room.setFloorSize(this.packet.readInt());
            room.setMuteOption(this.packet.readInt());
            room.setKickOption(this.packet.readInt());
            room.setBanOption(this.packet.readInt());
            room.setChatMode(this.packet.readInt());
            room.setChatWeight(this.packet.readInt());
            room.setChatSpeed(this.packet.readInt());
            room.setChatDistance(Math.abs(this.packet.readInt()));
            room.setChatProtection(this.packet.readInt());
            room.setNeedsUpdate(true);
            room.sendComposer(new RoomThicknessComposer(room).compose());
            room.sendComposer(new RoomChatSettingsComposer(room).compose());
            room.sendComposer(new RoomSettingsUpdatedComposer(room).compose());
            this.client.sendResponse(new RoomSettingsSavedComposer(room));
        }
    }
}

