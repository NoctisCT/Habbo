/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.spi.LifeCycle;
import java.util.HashSet;
import java.util.Set;

public class LifeCycleManager {
    private final Set<LifeCycle> components = new HashSet<LifeCycle>();

    public void register(LifeCycle component) {
        this.components.add(component);
    }

    public void reset() {
        for (LifeCycle component : this.components) {
            if (!component.isStarted()) continue;
            component.stop();
        }
        this.components.clear();
    }
}

