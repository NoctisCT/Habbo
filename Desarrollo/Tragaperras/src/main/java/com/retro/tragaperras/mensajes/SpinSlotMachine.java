package com.retro.tragaperras.mensajes;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.tragaperras.TragaperrasPlugin;

public final class SpinSlotMachine extends MessageHandler
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

        TragaperrasPlugin plugin =
                TragaperrasPlugin.getInstance();

        if(plugin == null)
        {
            return;
        }

        plugin.getManager()
                .spin(this.client, itemId);
    }
}