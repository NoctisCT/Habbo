package com.retro.subastas;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.retro.subastas.mensajes.AbrirSubastas;
import com.retro.subastas.mensajes.ObtenerInventarioSubastas;
import com.retro.subastas.mensajes.ObtenerEstadoSesionSubastas;
import com.retro.subastas.mensajes.ObtenerHistorialSubastas;
import com.retro.subastas.mensajes.ObtenerFeedSubastas;
import com.retro.subastas.mensajes.ObtenerEstadisticasSubastas;
import com.retro.subastas.mensajes.ObtenerMisSubastas;
import com.retro.subastas.mensajes.PonerEnSubasta;
import com.retro.subastas.mensajes.PujarSubasta;
import com.retro.subastas.mensajes.RetirarSubasta;

public class Subastas extends HabboPlugin implements EventListener
{
    @Override
    public void onEnable()
    {
        Emulator.getPluginManager().registerEvents(this, this);
        Emulator.getLogging().logStart("[Subastas] Plugin cargado, esperando al emulador");
    }

    @EventHandler
    public void onEmulatorLoaded(EmulatorLoadedEvent evento) throws Exception
    {
        BaseDatosSubastas.inicializar();
        GestorSubastas.iniciar();
        GestorAvisosSesiones.iniciar();

        Emulator.getGameServer().getPacketManager().registerHandler(5000, AbrirSubastas.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5002, ObtenerInventarioSubastas.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5004, PonerEnSubasta.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5006, ObtenerMisSubastas.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5008, RetirarSubasta.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5010, PujarSubasta.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5012, ObtenerEstadoSesionSubastas.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5015, ObtenerHistorialSubastas.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5017, ObtenerFeedSubastas.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5019, ObtenerEstadisticasSubastas.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5021, com.retro.subastas.mensajes.ObtenerProximaSubasta.class);
        Emulator.getGameServer().getPacketManager().registerHandler(5023, com.retro.subastas.mensajes.ObtenerRecordsSubastas.class);

        Emulator.getLogging().logStart("[Subastas] Paquete 5000 registrado correctamente");
        Emulator.getLogging().logStart("[Subastas] Paquete 5002 registrado correctamente");
        Emulator.getLogging().logStart("[Subastas] Paquete 5004 registrado correctamente");
        Emulator.getLogging().logStart("[Subastas] Paquete 5006 registrado correctamente");
        Emulator.getLogging().logStart("[Subastas] Paquete 5008 registrado correctamente");
        Emulator.getLogging().logStart("[Subastas] Paquete 5010 registrado correctamente");
        Emulator.getLogging().logStart("[Subastas] Paquete 5012 registrado correctamente");
    }

    @Override
    public void onDisable()
    {
        GestorAvisosSesiones.detener();
        GestorSubastas.detener();
        Emulator.getLogging().logShutdownLine("[Subastas] Plugin detenido");
    }

    @Override
    public boolean hasPermission(Habbo habbo, String permiso)
    {
        return false;
    }
}