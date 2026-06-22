/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.core.consolecommands;

import com.eu.habbo.core.consolecommands.ConsoleCommand;
import com.eu.habbo.habbohotel.commands.ShutdownCommand;

public class ConsoleShutdownCommand
extends ConsoleCommand {
    public ConsoleShutdownCommand() {
        super("stop", "Stop the emulator.");
    }

    @Override
    public void handle(String[] args) throws Exception {
        new ShutdownCommand().handle(null, args);
    }
}

