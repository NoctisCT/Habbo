/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import java.util.Map;

public interface PropertyContainer {
    public String getProperty(String var1);

    public Map<String, String> getCopyOfPropertyMap();
}

