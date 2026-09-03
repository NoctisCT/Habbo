package com.retro.spaceinvaders.messages;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.spaceinvaders.SpaceInvadersPlugin;

public final class SubmitArcadeScore
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

        String token =
                this.packet.readString();

        int score =
                this.packet.readInt()
                        .intValue();

        int level =
                this.packet.readInt()
                        .intValue();

        SpaceInvadersPlugin plugin =
                SpaceInvadersPlugin.getInstance();

        if(plugin == null)
        {
            return;
        }

        plugin.getManager()
                .submitScore(
                        this.client,
                        itemId,
                        gameKey,
                        token,
                        score,
                        level
                );
    }
}
