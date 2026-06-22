/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;

public class ContextBasedDiscriminator
extends AbstractDiscriminator<ILoggingEvent> {
    private static final String KEY = "contextName";
    private String defaultValue;

    @Override
    public String getDiscriminatingValue(ILoggingEvent event) {
        String contextName = event.getLoggerContextVO().getName();
        if (contextName == null) {
            return this.defaultValue;
        }
        return contextName;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public void setKey(String key) {
        throw new UnsupportedOperationException("Key cannot be set. Using fixed key contextName");
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}

