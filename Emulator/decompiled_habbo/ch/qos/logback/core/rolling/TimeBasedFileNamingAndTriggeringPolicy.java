/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.spi.ContextAware;

public interface TimeBasedFileNamingAndTriggeringPolicy<E>
extends TriggeringPolicy<E>,
ContextAware {
    public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> var1);

    public String getElapsedPeriodsFileName();

    public String getCurrentPeriodsFileNameWithoutCompressionSuffix();

    public ArchiveRemover getArchiveRemover();

    public long getCurrentTime();

    public void setCurrentTime(long var1);
}

