/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class RelativeTimeConverter
extends ClassicConverter {
    long lastTimestamp = -1L;
    String timesmapCache = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String convert(ILoggingEvent event) {
        long now = event.getTimeStamp();
        RelativeTimeConverter relativeTimeConverter = this;
        synchronized (relativeTimeConverter) {
            if (now != this.lastTimestamp) {
                this.lastTimestamp = now;
                this.timesmapCache = Long.toString(now - event.getLoggerContextVO().getBirthTime());
            }
            return this.timesmapCache;
        }
    }
}

