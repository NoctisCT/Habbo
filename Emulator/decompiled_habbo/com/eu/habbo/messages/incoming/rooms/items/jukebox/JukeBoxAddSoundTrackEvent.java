/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class JukeBoxAddSoundTrackEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        HabboItem item;
        if (!this.client.getHabbo().getHabboInfo().getCurrentRoom().hasRights(this.client.getHabbo())) {
            return;
        }
        int itemId = this.packet.readInt();
        int slotId = this.packet.readInt();
        Habbo habbo = this.client.getHabbo();
        if (habbo != null && (item = habbo.getInventory().getItemsComponent().getHabboItem(itemId)) instanceof InteractionMusicDisc && item.getRoomId() == 0) {
            this.client.getHabbo().getHabboInfo().getCurrentRoom().getTraxManager().addSong((InteractionMusicDisc)item, habbo);
        }
    }
}

