/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CachingDateFormatter {
    long lastTimestamp = -1L;
    String cachedStr = null;
    final SimpleDateFormat sdf;

    public CachingDateFormatter(String pattern) {
        this.sdf = new SimpleDateFormat(pattern);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final String format(long now) {
        CachingDateFormatter cachingDateFormatter = this;
        synchronized (cachingDateFormatter) {
            if (now != this.lastTimestamp) {
                this.lastTimestamp = now;
                this.cachedStr = this.sdf.format(new Date(now));
            }
            return this.cachedStr;
        }
    }

    public void setTimeZone(TimeZone tz) {
        this.sdf.setTimeZone(tz);
    }
}

