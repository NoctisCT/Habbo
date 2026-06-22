/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.StalkErrorComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class StalkFriendEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int friendId = this.packet.readInt();
        MessengerBuddy buddy = this.client.getHabbo().getMessenger().getFriend(friendId);
        if (buddy == null) {
            this.client.sendResponse(new StalkErrorComposer(0));
            return;
        }
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(friendId);
        if (habbo == null || !habbo.isOnline()) {
            this.client.sendResponse(new StalkErrorComposer(1));
            return;
        }
        if (habbo.getHabboStats().blockFollowing && !this.client.getHabbo().hasPermission("acc_can_stalk")) {
            this.client.sendResponse(new StalkErrorComposer(3));
            return;
        }
        if (habbo.getHabboInfo().getCurrentRoom() == null) {
            this.client.sendResponse(new StalkErrorComposer(2));
            return;
        }
        if (habbo.getHabboInfo().getCurrentRoom() != this.client.getHabbo().getHabboInfo().getCurrentRoom()) {
            this.client.sendResponse(new ForwardToRoomComposer(habbo.getHabboInfo().getCurrentRoom().getId()));
        } else {
            this.client.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("stalk.failed.same.room").replace("%user%", habbo.getHabboInfo().getUsername()), this.client.getHabbo(), this.client.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }
    }
}

