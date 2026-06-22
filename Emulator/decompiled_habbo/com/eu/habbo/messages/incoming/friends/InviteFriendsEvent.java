/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.RoomInviteComposer;

public class InviteFriendsEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        if (this.client.getHabbo().getHabboStats().allowTalk()) {
            int[] userIds = new int[this.packet.readInt().intValue()];
            for (int i = 0; i < userIds.length; ++i) {
                userIds[i] = this.packet.readInt();
            }
            String message = this.packet.readString();
            message = Emulator.getGameEnvironment().getWordFilter().filter(message, this.client.getHabbo());
            for (int i : userIds) {
                Habbo habbo;
                if (i == 0 || (habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(i)) == null || habbo.getHabboStats().blockRoomInvites) continue;
                habbo.getClient().sendResponse(new RoomInviteComposer(this.client.getHabbo().getHabboInfo().getId(), message));
            }
        }
    }
}

