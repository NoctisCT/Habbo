package com.retro.inventorylock;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.users.UserDisconnectEvent;
import com.retro.inventorylock.handlers.CerrarSesionInventario;
import com.retro.inventorylock.handlers.ConfigurarPatronInventario;
import com.retro.inventorylock.handlers.ConsultarBloqueoInventario;
import com.retro.inventorylock.handlers.VerificarPatronInventario;

public class InventoryLock extends HabboPlugin implements EventListener
{
    @Override
    public void onEnable()
    {
        Emulator.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEmulatorLoaded(EmulatorLoadedEvent evento) throws Exception
    {
        BaseDatosInventoryLock.inicializar();

        Emulator.getGameServer().getPacketManager()
                .registerHandler(5030, ConsultarBloqueoInventario.class);

        Emulator.getGameServer().getPacketManager()
                .registerHandler(5032, VerificarPatronInventario.class);

        Emulator.getGameServer().getPacketManager()
                .registerHandler(5034, ConfigurarPatronInventario.class);

        Emulator.getGameServer().getPacketManager()
                .registerHandler(5036, CerrarSesionInventario.class);
    }

    @EventHandler
    public void onUserDisconnect(UserDisconnectEvent evento)
    {
        if(evento.habbo == null) return;

        SesionesInventoryLock.bloquear(
                evento.habbo.getHabboInfo().getId()
        );
    }

    @Override
    public void onDisable()
    {
        SesionesInventoryLock.limpiar();

    }

    @Override
    public boolean hasPermission(Habbo habbo, String permiso)
    {
        return false;
    }
}