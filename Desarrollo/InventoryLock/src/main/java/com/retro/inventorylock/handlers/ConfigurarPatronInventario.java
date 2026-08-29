package com.retro.inventorylock.handlers;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.inventorylock.SeguridadPatron;
import com.retro.inventorylock.ServicioInventoryLock;

public class ConfigurarPatronInventario extends MessageHandler
{
    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int usuarioId = this.client.getHabbo().getHabboInfo().getId();

        int accion = this.packet.readInt().intValue();
        String patronActual = this.packet.readString();
        String patronNuevo = this.packet.readString();

        boolean success = false;
        boolean enabled = false;
        int codigo = 0;
        int blockedSeconds = 0;

        try
        {
            boolean estabaHabilitado = ServicioInventoryLock.estaHabilitado(usuarioId);
            enabled = estabaHabilitado;

            if(accion == 0)
            {
                if(estabaHabilitado)
                {
                    codigo = 2;
                }
                else if(!SeguridadPatron.patronValido(patronNuevo))
                {
                    codigo = 1;
                }
                else
                {
                    ServicioInventoryLock.guardarPatron(usuarioId, patronNuevo);
                    success = true;
                    enabled = true;
                }
            }
            else if(accion == 1)
            {
                if(!estabaHabilitado)
                {
                    codigo = 2;
                }
                else if(!SeguridadPatron.patronValido(patronNuevo))
                {
                    codigo = 1;
                }
                else
                {
                    ServicioInventoryLock.ResultadoVerificacion verificacion =
                            ServicioInventoryLock.verificar(
                                    usuarioId,
                                    patronActual,
                                    false
                            );

                    if(!verificacion.correcto)
                    {
                        blockedSeconds = verificacion.blockedSeconds;
                        codigo = blockedSeconds > 0 ? 4 : 3;
                    }
                    else
                    {
                        ServicioInventoryLock.guardarPatron(usuarioId, patronNuevo);
                        success = true;
                        enabled = true;
                    }
                }
            }
            else if(accion == 2)
            {
                if(!estabaHabilitado)
                {
                    success = true;
                    enabled = false;
                }
                else
                {
                    ServicioInventoryLock.ResultadoVerificacion verificacion =
                            ServicioInventoryLock.verificar(
                                    usuarioId,
                                    patronActual,
                                    false
                            );

                    if(!verificacion.correcto)
                    {
                        blockedSeconds = verificacion.blockedSeconds;
                        codigo = blockedSeconds > 0 ? 4 : 3;
                        enabled = true;
                    }
                    else
                    {
                        ServicioInventoryLock.desactivar(usuarioId);
                        success = true;
                        enabled = false;
                    }
                }
            }
            else
            {
                codigo = 5;
                enabled = estabaHabilitado;
            }
        }
        catch(Exception error)
        {
            codigo = 5;

            System.out.println(
                    "[InventoryLock] ERROR configurando patron: " +
                    error.getClass().getName() + ": " + error.getMessage()
            );

            error.printStackTrace();
        }

        ServerMessage respuesta = new ServerMessage(5035);
        respuesta.appendBoolean(success);
        respuesta.appendBoolean(enabled);
        respuesta.appendInt(codigo);
        respuesta.appendInt(blockedSeconds);

        this.client.sendResponse(respuesta);

    }
}