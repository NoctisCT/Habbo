/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.CachingDateFormatter;
import java.util.List;
import java.util.TimeZone;

public class DateConverter
extends ClassicConverter {
    long lastTimestamp = -1L;
    String timestampStrCache = null;
    CachingDateFormatter cachingDateFormatter = null;

    @Override
    public void start() {
        String datePattern = this.getFirstOption();
        if (datePattern == null) {
            datePattern = "yyyy-MM-dd HH:mm:ss,SSS";
        }
        if (datePattern.equals("ISO8601")) {
            datePattern = "yyyy-MM-dd HH:mm:ss,SSS";
        }
        try {
            this.cachingDateFormatter = new CachingDateFormatter(datePattern);
        }
        catch (IllegalArgumentException e) {
            this.addWarn("Could not instantiate SimpleDateFormat with pattern " + datePattern, e);
            this.cachingDateFormatter = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss,SSS");
        }
        List<String> optionList = this.getOptionList();
        if (optionList != null && optionList.size() > 1) {
            TimeZone tz = TimeZone.getTimeZone(optionList.get(1));
            this.cachingDateFormatter.setTimeZone(tz);
        }
    }

    @Override
    public String convert(ILoggingEvent le) {
        long timestamp = le.getTimeStamp();
        return this.cachingDateFormatter.format(timestamp);
    }
}

