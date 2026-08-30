package com.retro.hologrid;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.retro.hologrid.handlers.GetHoloGridWalkability;

public class HoloGridPlugin extends HabboPlugin implements EventListener
{
    public static final int PACKET_GET_WALKABILITY = 5040;

    @Override
    public void onEnable()
    {
        Emulator.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEmulatorLoaded(EmulatorLoadedEvent event) throws Exception
    {
        Emulator.getGameServer().getPacketManager()
                .registerHandler(PACKET_GET_WALKABILITY, GetHoloGridWalkability.class);
    }

    @Override
    public void onDisable()
    {
    }

    @Override
    public boolean hasPermission(Habbo habbo, String permission)
    {
        return false;
    }
}