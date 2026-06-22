/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Marker;

public class MarkerConverter
extends ClassicConverter {
    private static String EMPTY = "";

    @Override
    public String convert(ILoggingEvent le) {
        Marker marker = le.getMarker();
        if (marker == null) {
            return EMPTY;
        }
        return marker.toString();
    }
}

