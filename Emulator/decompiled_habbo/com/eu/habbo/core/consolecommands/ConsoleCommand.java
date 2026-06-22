/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.core.consolecommands;

import com.eu.habbo.core.consolecommands.ConsoleInfoCommand;
import com.eu.habbo.core.consolecommands.ConsoleReconnectCameraCommand;
import com.eu.habbo.core.consolecommands.ConsoleShutdownCommand;
import com.eu.habbo.core.consolecommands.ConsoleTestCommand;
import com.eu.habbo.core.consolecommands.ShowInteractionsCommand;
import com.eu.habbo.core.consolecommands.ShowRCONCommands;
import com.eu.habbo.core.consolecommands.ThankyouArcturusCommand;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConsoleCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleCommand.class);
    private static final THashMap<String, ConsoleCommand> commands = new THashMap();
    public final String key;
    public final String usage;

    public ConsoleCommand(String key, String usage) {
        this.key = key;
        this.usage = usage;
    }

    public static void load() {
        ConsoleCommand.addCommand(new ConsoleShutdownCommand());
        ConsoleCommand.addCommand(new ConsoleInfoCommand());
        ConsoleCommand.addCommand(new ConsoleTestCommand());
        ConsoleCommand.addCommand(new ConsoleReconnectCameraCommand());
        ConsoleCommand.addCommand(new ShowInteractionsCommand());
        ConsoleCommand.addCommand(new ShowRCONCommands());
        ConsoleCommand.addCommand(new ThankyouArcturusCommand());
    }

    public static void addCommand(ConsoleCommand command) {
        commands.put(command.key, command);
    }

    public static ConsoleCommand findCommand(String key) {
        return commands.get(key);
    }

    public static boolean handle(String line) {
        String[] message = line.split(" ");
        if (message.length > 0) {
            ConsoleCommand command = ConsoleCommand.findCommand(message[0]);
            if (command != null) {
                try {
                    command.handle(message);
                    return true;
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
            } else {
                LOGGER.info("Unknown Console Command {}", (Object)message[0]);
                LOGGER.info("Commands Available ({}): ", (Object)commands.size());
                for (ConsoleCommand c : commands.values()) {
                    LOGGER.info("{} - {}", (Object)c.key, (Object)c.usage);
                }
            }
        }
        return false;
    }

    public abstract void handle(String[] var1) throws Exception;
}

