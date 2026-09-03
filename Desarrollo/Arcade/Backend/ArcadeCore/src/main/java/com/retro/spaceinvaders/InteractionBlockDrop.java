package com.retro.spaceinvaders;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.retro.spaceinvaders.arcade.ArcadeGameDefinition;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class InteractionBlockDrop
        extends InteractionDefault
{
    public InteractionBlockDrop(
            ResultSet set,
            Item baseItem)
            throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionBlockDrop(
            int id,
            int userId,
            Item item,
            String extradata,
            int limitedStack,
            int limitedSells)
    {
        super(
                id,
                userId,
                item,
                extradata,
                limitedStack,
                limitedSells
        );
    }

    @Override
    public void onClick(
            GameClient client,
            Room room,
            Object[] objects)
    {
        if(client == null ||
                client.getHabbo() == null ||
                room == null)
        {
            return;
        }

        SpaceInvadersPlugin plugin =
                SpaceInvadersPlugin.getInstance();

        if(plugin == null)
        {
            return;
        }

        plugin.getManager()
                .requestOpenMachine(
                        client,
                        room,
                        this,
                        ArcadeGameDefinition.BLOCK_DROP
                );
    }
}
