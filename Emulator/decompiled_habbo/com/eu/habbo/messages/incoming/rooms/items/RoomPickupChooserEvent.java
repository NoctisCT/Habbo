/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RoomPickupChooserEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int count = this.packet.readInt();
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room == null) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            int itemId = this.packet.readInt();
            HabboItem item = room.getHabboItem(itemId);
            if (item == null) continue;
            if (item.getUserId() == this.client.getHabbo().getHabboInfo().getId()) {
                room.pickUpItem(item, this.client.getHabbo());
                continue;
            }
            if (!room.hasRights(this.client.getHabbo())) continue;
            if (this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER)) {
                item.setUserId(this.client.getHabbo().getHabboInfo().getId());
            } else if (this.client.getHabbo().getHabboInfo().getId() != room.getOwnerId() && item.getUserId() == room.getOwnerId()) continue;
            room.ejectUserItem(item);
        }
    }
}

