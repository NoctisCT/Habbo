/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.core.consolecommands;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.consolecommands.ConsoleCommand;
import com.eu.habbo.habbohotel.catalog.CatalogManager;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleInfoCommand
extends ConsoleCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleInfoCommand.class);

    public ConsoleInfoCommand() {
        super("info", "Show current statistics.");
    }

    @Override
    public void handle(String[] args) throws Exception {
        int seconds = Emulator.getIntUnixTimestamp() - Emulator.getTimeStarted();
        int day = (int)TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (long)day * 24L;
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
        long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
        LOGGER.info("Emulator version: Arcturus Morningstar 3.6.1 ");
        LOGGER.info("Emulator build: {}", (Object)Emulator.build);
        LOGGER.info("");
        LOGGER.info("Hotel Statistics");
        LOGGER.info("- Users: {}", (Object)Emulator.getGameEnvironment().getHabboManager().getOnlineCount());
        LOGGER.info("- Rooms: {}", (Object)Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size());
        LOGGER.info("- Shop:  {} pages and {} items.", (Object)Emulator.getGameEnvironment().getCatalogManager().catalogPages.size(), (Object)CatalogManager.catalogItemAmount);
        LOGGER.info("- Furni: {} items.", (Object)Emulator.getGameEnvironment().getItemManager().getItems().size());
        LOGGER.info("");
        LOGGER.info("Server Statistics");
        LOGGER.info("- Uptime: {}{}{}{}{}{}{}{}", day, day > 1 ? " days, " : " day, ", hours, hours > 1L ? " hours, " : " hour, ", minute, minute > 1L ? " minutes, " : " minute, ", second, second > 1L ? " seconds!" : " second!");
        LOGGER.info("- RAM Usage: {}/{}MB", (Object)((Emulator.getRuntime().totalMemory() - Emulator.getRuntime().freeMemory()) / 0x100000L), (Object)(Emulator.getRuntime().freeMemory() / 0x100000L));
        LOGGER.info("- CPU Cores: {}", (Object)Emulator.getRuntime().availableProcessors());
        LOGGER.info("- Total Memory: {}MB", (Object)(Emulator.getRuntime().maxMemory() / 0x100000L));
    }
}

