/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.MutedWhisperComposer;

public class RoomUserMuteEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        Habbo habbo;
        int userId = this.packet.readInt();
        int roomId = this.packet.readInt();
        int minutes = this.packet.readInt();
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);
        if (room != null && (room.hasRights(this.client.getHabbo()) || this.client.getHabbo().hasPermission("cmd_mute") || this.client.getHabbo().hasPermission(Permission.ACC_AMBASSADOR)) && (habbo = room.getHabbo(userId)) != null) {
            room.muteHabbo(habbo, minutes);
            habbo.getClient().sendResponse(new MutedWhisperComposer(minutes * 60));
            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("SelfModMuteSeen"));
        }
    }
}

