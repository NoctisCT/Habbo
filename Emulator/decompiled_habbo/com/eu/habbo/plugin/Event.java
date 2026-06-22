/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin;

public abstract class Event {
    private boolean cancelled = false;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

