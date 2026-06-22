/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import java.util.Collection;
import java.util.Set;

public interface ComponentTracker<C> {
    public static final int DEFAULT_TIMEOUT = 1800000;
    public static final int DEFAULT_MAX_COMPONENTS = Integer.MAX_VALUE;

    public int getComponentCount();

    public C find(String var1);

    public C getOrCreate(String var1, long var2);

    public void removeStaleComponents(long var1);

    public void endOfLife(String var1);

    public Collection<C> allComponents();

    public Set<String> allKeys();
}

