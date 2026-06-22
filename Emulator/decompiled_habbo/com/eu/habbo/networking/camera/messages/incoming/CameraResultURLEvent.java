/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.camera.messages.incoming;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.camera.CameraURLComposer;
import com.eu.habbo.networking.camera.CameraIncomingMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class CameraResultURLEvent
extends CameraIncomingMessage {
    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR = 1;

    public CameraResultURLEvent(Short header, ByteBuf body) {
        super(header, body);
    }

    @Override
    public void handle(Channel client) throws Exception {
        int userId = this.readInt();
        int status = this.readInt();
        String URL2 = this.readString();
        if (!Emulator.getConfig().getBoolean("camera.use.https", true)) {
            URL2 = URL2.replace("https://", "http://");
        }
        int roomId = this.readInt();
        int timestamp = this.readInt();
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
        if (status == 1 && habbo != null) {
            habbo.getHabboInfo().setPhotoTimestamp(0);
            habbo.getHabboInfo().setPhotoJSON("");
            habbo.getHabboInfo().setPhotoURL("");
            habbo.alert(Emulator.getTexts().getValue("camera.error.creation"));
            return;
        }
        if (status == 0 && habbo != null && timestamp == habbo.getHabboInfo().getPhotoTimestamp()) {
            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("CameraPhotoCount"), 1);
            habbo.getClient().sendResponse(new CameraURLComposer(URL2));
            habbo.getHabboInfo().setPhotoJSON(habbo.getHabboInfo().getPhotoJSON().replace("%room_id%", "" + roomId).replace("%url%", URL2));
            habbo.getHabboInfo().setPhotoURL(URL2);
        }
    }
}

