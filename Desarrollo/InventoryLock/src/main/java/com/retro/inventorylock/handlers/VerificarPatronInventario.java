package com.retro.inventorylock.handlers;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.inventorylock.ServicioInventoryLock;

public class VerificarPatronInventario extends MessageHandler
{
    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int usuarioId = this.client.getHabbo().getHabboInfo().getId();
        String patron = this.packet.readString();

        try
        {
            ServicioInventoryLock.ResultadoVerificacion resultado =
                    ServicioInventoryLock.verificar(usuarioId, patron, true);

            ServerMessage respuesta = new ServerMessage(5033);
            respuesta.appendBoolean(resultado.correcto);
            respuesta.appendInt(resultado.blockedSeconds);

            this.client.sendResponse(respuesta);

        }
        catch(Exception error)
        {
            System.out.println(
                    "[InventoryLock] ERROR verificando patron: " +
                    error.getClass().getName() + ": " + error.getMessage()
            );
            error.printStackTrace();
        }
    }
}
