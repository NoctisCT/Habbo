/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;
import com.eu.habbo.messages.outgoing.users.UpdateUserLookComposer;

public class FacelessCommand
extends Command {
    public FacelessCommand() {
        super("cmd_faceless", Emulator.getTexts().getValue("commands.keys.cmd_faceless").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            try {
                String[] figureParts;
                for (String part : figureParts = gameClient.getHabbo().getHabboInfo().getLook().split("\\.")) {
                    if (!part.startsWith("hd")) continue;
                    String[] headParts = part.split("-");
                    if (!headParts[1].equals("99999")) {
                        headParts[1] = "99999";
                        String newHead = "hd-" + headParts[1] + "-" + headParts[2];
                        gameClient.getHabbo().getHabboInfo().setLook(gameClient.getHabbo().getHabboInfo().getLook().replace(part, newHead));
                        gameClient.sendResponse(new UpdateUserLookComposer(gameClient.getHabbo()));
                        gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDataComposer(gameClient.getHabbo()).compose());
                        return true;
                    }
                    break;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return false;
    }
}

