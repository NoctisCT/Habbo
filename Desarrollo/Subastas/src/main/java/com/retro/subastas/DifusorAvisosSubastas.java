package com.retro.subastas;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;

public final class DifusorAvisosSubastas
{
    private DifusorAvisosSubastas()
    {
    }

    public static void difundir(String mensaje)
    {
        if(mensaje == null || mensaje.isBlank())
        {
            return;
        }

        for(Habbo habbo : Emulator.getGameEnvironment()
                .getHabboManager()
                .getOnlineHabbos()
                .values())
        {
            if(habbo == null || habbo.getClient() == null)
            {
                continue;
            }

            ServerMessage respuesta = new ServerMessage(5014);
            respuesta.appendString(mensaje);
            habbo.getClient().sendResponse(respuesta);
        }

        System.out.println("[Subastas] Aviso global: " + mensaje);
    }
}