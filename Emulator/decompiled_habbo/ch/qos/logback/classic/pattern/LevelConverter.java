/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LevelConverter
extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent le) {
        return le.getLevel().toString();
    }
}

