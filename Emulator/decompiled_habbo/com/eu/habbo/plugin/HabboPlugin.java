/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.HabboPluginConfiguration;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

public abstract class HabboPlugin {
    public final THashMap<Class<? extends Event>, THashSet<Method>> registeredEvents = new THashMap();
    public HabboPluginConfiguration configuration;
    public URLClassLoader classLoader;
    public InputStream stream;

    public abstract void onEnable() throws Exception;

    public abstract void onDisable() throws Exception;

    public boolean isRegistered(Class<? extends Event> clazz) {
        return this.registeredEvents.containsKey(clazz);
    }

    public abstract boolean hasPermission(Habbo var1, String var2);
}

