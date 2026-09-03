package com.retro.spaceinvaders.messages;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.spaceinvaders.SpaceInvadersPlugin;

public final class StartArcadeGame
        extends MessageHandler
{
    @Override
    public void handle()
    {
        if(this.client == null ||
                this.client.getHabbo() == null ||
                this.packet == null)
        {
            return;
        }

        int itemId =
                this.packet.readInt()
                        .intValue();

        String gameKey =
                this.packet.readString();

        SpaceInvadersPlugin plugin =
                SpaceInvadersPlugin.getInstance();

        if(plugin == null)
        {
            return;
        }

        plugin.getManager()
                .startRun(
                        this.client,
                        itemId,
                        gameKey
                );
    }
}
