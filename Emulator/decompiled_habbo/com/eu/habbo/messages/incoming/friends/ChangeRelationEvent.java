/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.UpdateFriendComposer;
import com.eu.habbo.plugin.events.users.friends.UserRelationShipEvent;

public class ChangeRelationEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        UserRelationShipEvent event;
        int userId = this.packet.readInt();
        int relationId = this.packet.readInt();
        MessengerBuddy buddy = this.client.getHabbo().getMessenger().getFriends().get(userId);
        if (buddy != null && relationId >= 0 && relationId <= 3 && !(event = new UserRelationShipEvent(this.client.getHabbo(), buddy, relationId)).isCancelled()) {
            buddy.setRelation(event.relationShip);
            this.client.sendResponse(new UpdateFriendComposer(this.client.getHabbo(), buddy, (Integer)0));
        }
    }
}

