package com.retro.inventorylock.handlers;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.inventorylock.ServicioInventoryLock;

public class ConsultarBloqueoInventario extends MessageHandler
{
    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int usuarioId = this.client.getHabbo().getHabboInfo().getId();

        try
        {
            ServicioInventoryLock.Estado estado =
                    ServicioInventoryLock.consultarEstado(usuarioId);

            ServerMessage respuesta = new ServerMessage(5031);
            respuesta.appendBoolean(estado.enabled);
            respuesta.appendBoolean(estado.unlocked);
            respuesta.appendInt(estado.blockedSeconds);

            this.client.sendResponse(respuesta);

        }
        catch(Exception error)
        {
            System.out.println(
                    "[InventoryLock] ERROR consultando estado: " +
                    error.getClass().getName() + ": " + error.getMessage()
            );
            error.printStackTrace();
        }
    }
}
