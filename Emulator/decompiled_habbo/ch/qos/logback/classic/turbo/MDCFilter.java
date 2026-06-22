/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.MatchingFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

public class MDCFilter
extends MatchingFilter {
    String MDCKey;
    String value;

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (this.MDCKey == null) {
            return FilterReply.NEUTRAL;
        }
        String value = MDC.get(this.MDCKey);
        if (this.value.equals(value)) {
            return this.onMatch;
        }
        return this.onMismatch;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setMDCKey(String MDCKey) {
        this.MDCKey = MDCKey;
    }
}

