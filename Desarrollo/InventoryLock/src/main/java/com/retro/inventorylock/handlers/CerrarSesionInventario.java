package com.retro.inventorylock.handlers;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.inventorylock.SesionesInventoryLock;

public class CerrarSesionInventario extends MessageHandler
{
    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int usuarioId = this.client.getHabbo().getHabboInfo().getId();

        SesionesInventoryLock.bloquear(usuarioId);

    }
}
