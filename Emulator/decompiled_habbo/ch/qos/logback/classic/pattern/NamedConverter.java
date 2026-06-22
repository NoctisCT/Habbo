/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.Abbreviator;
import ch.qos.logback.classic.pattern.ClassNameOnlyAbbreviator;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator;
import ch.qos.logback.classic.spi.ILoggingEvent;

public abstract class NamedConverter
extends ClassicConverter {
    Abbreviator abbreviator = null;

    protected abstract String getFullyQualifiedName(ILoggingEvent var1);

    @Override
    public void start() {
        String optStr = this.getFirstOption();
        if (optStr != null) {
            try {
                int targetLen = Integer.parseInt(optStr);
                if (targetLen == 0) {
                    this.abbreviator = new ClassNameOnlyAbbreviator();
                } else if (targetLen > 0) {
                    this.abbreviator = new TargetLengthBasedClassNameAbbreviator(targetLen);
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
    }

    @Override
    public String convert(ILoggingEvent event) {
        String fqn = this.getFullyQualifiedName(event);
        if (this.abbreviator == null) {
            return fqn;
        }
        return this.abbreviator.abbreviate(fqn);
    }
}

