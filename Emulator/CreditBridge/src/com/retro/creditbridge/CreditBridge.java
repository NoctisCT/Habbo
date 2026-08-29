package com.retro.creditbridge;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.retro.creditbridge.rcon.CreditBridgePing;
import com.retro.creditbridge.rcon.CreditCredits;
import com.retro.creditbridge.rcon.DebitCredits;

public class CreditBridge extends HabboPlugin implements EventListener
{
    @Override
    public void onEnable()
    {
        Emulator.getPluginManager()
            .registerEvents(this, this);
    }

    @EventHandler
    public void onEmulatorLoaded(
        EmulatorLoadedEvent event
    )
    {
        Emulator.getRconServer()
            .addRCONMessage(
                "creditbridgeping",
                CreditBridgePing.class
            );

        Emulator.getRconServer()
            .addRCONMessage(
                "debitcredits",
                DebitCredits.class
            );

        Emulator.getRconServer()
            .addRCONMessage(
                "creditcredits",
                CreditCredits.class
            );

        System.out.println(
            "[CreditBridge] RCON commands registered."
        );
    }

    @Override
    public void onDisable()
    {
    }

    @Override
    public boolean hasPermission(
        Habbo habbo,
        String permission
    )
    {
        return false;
    }
}