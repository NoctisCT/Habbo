/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class ArcturusCommand
extends Command {
    public ArcturusCommand() {
        super(null, new String[]{"arcturus", "emulator"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            gameClient.getHabbo().whisper("This hotel is powered by Arcturus Emulator! \rCet h\u00f4tel est aliment\u00e9 par Arcturus \u00e9mulateur! \rDit hotel draait op Arcturus Emulator! \rEste hotel est\u00e1 propulsado por Arcturus emulador! \rHotellet drivs av Arcturus Emulator! \rDas Hotel geh\u00f6rt zu Arcturus Emulator betrieben!", RoomChatMessageBubbles.ALERT);
        }
        return true;
    }
}

