/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.RoomRelativeMapComposer;
import java.util.ArrayList;

public class UpdateItemsCommand
extends Command {
    public UpdateItemsCommand() {
        super("cmd_update_items", Emulator.getTexts().getValue("commands.keys.cmd_update_items").split(";"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        Emulator.getGameEnvironment().getItemManager().loadItems();
        Emulator.getGameEnvironment().getItemManager().loadCrackable();
        Emulator.getGameEnvironment().getItemManager().loadSoundTracks();
        ArrayList<Room> arrayList = Emulator.getGameEnvironment().getRoomManager().getActiveRooms();
        synchronized (arrayList) {
            for (Room room : Emulator.getGameEnvironment().getRoomManager().getActiveRooms()) {
                if (!room.isLoaded() || room.getUserCount() <= 0 || room.getLayout() == null) continue;
                room.sendComposer(new RoomRelativeMapComposer(room).compose());
            }
        }
        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_update_items"), RoomChatMessageBubbles.ALERT);
        return true;
    }
}

