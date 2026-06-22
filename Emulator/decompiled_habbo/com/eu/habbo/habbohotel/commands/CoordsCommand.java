/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;

public class CoordsCommand
extends Command {
    public CoordsCommand() {
        super("cmd_coords", Emulator.getTexts().getValue("commands.keys.cmd_coords").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient.getHabbo().getRoomUnit() == null || gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null) {
            return false;
        }
        if (params.length == 1) {
            gameClient.getHabbo().alert(Emulator.getTexts().getValue("commands.generic.cmd_coords.title") + "\r\nx: " + gameClient.getHabbo().getRoomUnit().getX() + "\ry: " + gameClient.getHabbo().getRoomUnit().getY() + "\rz: " + String.valueOf(gameClient.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.SIT) ? gameClient.getHabbo().getRoomUnit().getStatus(RoomUnitStatus.SIT) : Double.valueOf(gameClient.getHabbo().getRoomUnit().getZ())) + "\r" + Emulator.getTexts().getValue("generic.rotation.head") + ": " + String.valueOf((Object)gameClient.getHabbo().getRoomUnit().getHeadRotation()) + "-" + gameClient.getHabbo().getRoomUnit().getHeadRotation().getValue() + "\r" + Emulator.getTexts().getValue("generic.rotation.body") + ": " + String.valueOf((Object)gameClient.getHabbo().getRoomUnit().getBodyRotation()) + "-" + gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() + "\r" + Emulator.getTexts().getValue("generic.sitting") + ": " + (gameClient.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.SIT) ? Emulator.getTexts().getValue("generic.yes") : Emulator.getTexts().getValue("generic.no")) + "\rTile State: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile((short)gameClient.getHabbo().getRoomUnit().getX(), (short)gameClient.getHabbo().getRoomUnit().getY()).state.name() + "\rTile Walkable: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(gameClient.getHabbo().getRoomUnit().getX(), gameClient.getHabbo().getRoomUnit().getY()).isWalkable() + "\rTile relative height: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(gameClient.getHabbo().getRoomUnit().getX(), gameClient.getHabbo().getRoomUnit().getY()).relativeHeight() + "\rTile stack height: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(gameClient.getHabbo().getRoomUnit().getX(), gameClient.getHabbo().getRoomUnit().getY()).getStackHeight());
        } else {
            RoomTile tile = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(Short.parseShort(params[1]), Short.parseShort(params[2]));
            if (tile != null) {
                gameClient.getHabbo().alert(Emulator.getTexts().getValue("commands.generic.cmd_coords.title") + "\r\nx: " + tile.x + "\ry: " + tile.y + "\rz: " + tile.z + "\rTile State: " + tile.state.name() + "\rTile Relative Height: " + tile.relativeHeight() + "\rTile Stack Height: " + tile.getStackHeight() + "\rTile Walkable: " + (tile.isWalkable() ? "Yes" : "No") + "\r");
            } else {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.tile.not.exists"));
            }
        }
        return true;
    }
}

