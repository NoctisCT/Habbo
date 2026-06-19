package com.eu.habbo.bhrpg.stats;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.users.UserEnterRoomEvent; // Cambiado por seguridad de renderizado
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.bhrpg.plugin.commands.RpgStatsCommand;
import com.bhrpg.plugin.commands.SubirStatCommand;
import com.bhrpg.plugin.commands.ModStatCommand;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.bhrpg.combat.CombatManager;
import com.eu.habbo.bhrpg.combat.UserCombatStats;
import com.bhrpg.plugin.network.RpgStatsComposer;

public class StatsPlugin extends HabboPlugin implements EventListener {

    @Override
    public void onEnable() {
        Emulator.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean hasPermission(Habbo habbo, String permission) {
        return true;
    }

    @EventHandler
    public static void onEmulatorLoaded(EmulatorLoadedEvent event) {
        CommandHandler.addCommand(new RpgStatsCommand());
        CommandHandler.addCommand(new SubirStatCommand());
        CommandHandler.addCommand(new ModStatCommand());
        System.out.println("[BHRPG] Plugin de Estadisticas Cargado y Comandos Registrados.");
    }

    @EventHandler
    public static void onUserEnterRoom(UserEnterRoomEvent event) {
        if (event.habbo == null || event.habbo.getClient() == null) {
            return;
        }

        int userId = event.habbo.getHabboInfo().getId();
        UserCombatStats stats = CombatManager.getStats(userId);

        // Le damos 500ms de margen al cliente para que termine de abrir la sala y la UI
        Emulator.getThreading().run(() -> {
            if (event.habbo.getClient() != null) {
                event.habbo.getClient().sendResponse(new RpgStatsComposer(stats));
            }
        }, 500);
    }
}