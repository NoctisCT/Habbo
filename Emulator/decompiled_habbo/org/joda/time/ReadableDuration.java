/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.Duration;
import org.joda.time.Period;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ReadableDuration
extends Comparable<ReadableDuration> {
    public long getMillis();

    public Duration toDuration();

    public Period toPeriod();

    public boolean isEqual(ReadableDuration var1);

    public boolean isLongerThan(ReadableDuration var1);

    public boolean isShorterThan(ReadableDuration var1);

    public boolean equals(Object var1);

    public int hashCode();

    public String toString();
}

