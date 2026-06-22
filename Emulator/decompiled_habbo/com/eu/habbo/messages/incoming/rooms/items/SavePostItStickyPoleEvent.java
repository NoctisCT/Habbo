/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.items.PostItColor;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SavePostItStickyPoleEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SavePostItStickyPoleEvent.class);

    @Override
    public void handle() throws Exception {
        int itemId = this.packet.readInt();
        this.packet.readString();
        String color = this.packet.readString();
        if (itemId == -1234) {
            if (this.client.getHabbo().hasPermission("cmd_multi")) {
                String[] commands;
                for (String command : commands = this.packet.readString().split("\r")) {
                    command = command.replace("<br>", "\r");
                    CommandHandler.handleCommand(this.client, command);
                }
            } else {
                LOGGER.info("Scripter Alert! {} | {}", (Object)this.client.getHabbo().getHabboInfo().getUsername(), (Object)this.packet.readString());
            }
        } else {
            Object text = this.packet.readString();
            Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
            HabboItem sticky = room.getHabboItem(itemId);
            if (sticky != null && sticky.getUserId() == this.client.getHabbo().getHabboInfo().getId()) {
                sticky.setUserId(room.getOwnerId());
                if (color.equalsIgnoreCase(PostItColor.YELLOW.hexColor)) {
                    color = PostItColor.randomColorNotYellow().hexColor;
                }
                if (!InteractionPostIt.STICKYPOLE_PREFIX_TEXT.isEmpty()) {
                    text = InteractionPostIt.STICKYPOLE_PREFIX_TEXT.replace("\\r", "\r").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%timestamp%", LocalDate.now().toString()) + (String)text;
                }
                sticky.setUserId(room.getOwnerId());
                sticky.setExtradata(color + " " + (String)text);
                sticky.needsUpdate(true);
                room.updateItem(sticky);
                Emulator.getThreading().run(sticky);
            }
        }
    }
}

