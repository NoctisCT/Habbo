package com.retro.hologrid.handlers;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;

public class GetHoloGridWalkability extends MessageHandler
{
    private static final int PACKET_WALKABILITY = 5041;
    private static final double HEIGHT_SCALE = 1000.0;

    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        ServerMessage response = new ServerMessage(PACKET_WALKABILITY);

        if(room == null)
        {
            response.appendInt(-1);
            response.appendInt(0);
            response.appendInt(0);
            this.client.sendResponse(response);
            return;
        }

        THashSet<HabboItem> roomItems = room.getFloorItems();
        List<HabboItem> items = new ArrayList<HabboItem>();

        for(HabboItem item : roomItems)
        {
            if(item != null && item.getBaseItem() != null)
            {
                items.add(item);
            }
        }

        response.appendInt(room.getId());
        response.appendInt(items.size());

        for(HabboItem item : items)
        {
            response.appendInt(item.getId());
            response.appendBoolean(item.getBaseItem().allowWalk());
            response.appendBoolean(item.getBaseItem().allowSit());
            response.appendBoolean(item.getBaseItem().allowLay());
        }

        List<RoomTile> tiles = new ArrayList<RoomTile>();

        for(short x = 0; x < room.getLayout().getMapSizeX(); x++)
        {
            for(short y = 0; y < room.getLayout().getMapSizeY(); y++)
            {
                RoomTile tile = room.getLayout().getTile(x, y);

                if(tile != null)
                {
                    tiles.add(tile);
                }
            }
        }

        response.appendInt(tiles.size());

        for(RoomTile tile : tiles)
        {
            response.appendInt(tile.x);
            response.appendInt(tile.y);
            response.appendInt((int)Math.round(tile.getStackHeight() * HEIGHT_SCALE));
        }

        this.client.sendResponse(response);
    }
}