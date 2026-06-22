/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.core.consolecommands;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.consolecommands.ConsoleCommand;
import com.eu.habbo.habbohotel.users.Habbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleTestCommand
extends ConsoleCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleTestCommand.class);

    public ConsoleTestCommand() {
        super("test", "This is just a test.");
    }

    @Override
    public void handle(String[] args) throws Exception {
        if (Emulator.debugging) {
            LOGGER.info("This is a test command for live debugging.");
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(1);
            habbo.getHabboInfo().getMachineID();
        }
    }
}

