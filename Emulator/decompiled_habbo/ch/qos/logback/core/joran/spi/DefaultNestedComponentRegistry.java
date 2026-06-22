/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.spi.HostClassAndPropertyDouble;
import java.util.HashMap;
import java.util.Map;

public class DefaultNestedComponentRegistry {
    Map<HostClassAndPropertyDouble, Class<?>> defaultComponentMap = new HashMap();

    public void add(Class<?> hostClass, String propertyName, Class<?> componentClass) {
        HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName.toLowerCase());
        this.defaultComponentMap.put(hpDouble, componentClass);
    }

    public Class<?> findDefaultComponentType(Class<?> hostClass, String propertyName) {
        propertyName = propertyName.toLowerCase();
        while (hostClass != null) {
            Class<?> componentClass = this.oneShotFind(hostClass, propertyName);
            if (componentClass != null) {
                return componentClass;
            }
            hostClass = hostClass.getSuperclass();
        }
        return null;
    }

    private Class<?> oneShotFind(Class<?> hostClass, String propertyName) {
        HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName);
        return this.defaultComponentMap.get(hpDouble);
    }
}

