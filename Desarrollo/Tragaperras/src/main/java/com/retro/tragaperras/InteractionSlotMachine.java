package com.retro.tragaperras;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionSlotMachine extends InteractionDefault
{
    public InteractionSlotMachine(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionSlotMachine(
            int id,
            int userId,
            Item item,
            String extradata,
            int limitedStack,
            int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public SlotCurrency getSlotCurrency()
    {
        return SlotCurrency.CREDITS;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects)
    {
        if(client == null || client.getHabbo() == null || room == null)
        {
            return;
        }

        TragaperrasPlugin plugin = TragaperrasPlugin.getInstance();

        if(plugin == null)
        {
            return;
        }

        plugin.getManager().requestOpenMachine(client, room, this);
    }
}