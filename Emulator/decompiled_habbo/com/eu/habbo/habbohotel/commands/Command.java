/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public abstract class Command {
    public final String permission;
    public final String[] keys;

    public Command(String permission, String[] keys) {
        this.permission = permission;
        this.keys = keys;
    }

    public abstract boolean handle(GameClient var1, String[] var2) throws Exception;
}

