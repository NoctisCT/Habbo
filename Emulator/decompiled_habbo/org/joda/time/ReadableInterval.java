/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.MutableInterval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;

public interface ReadableInterval {
    public Chronology getChronology();

    public long getStartMillis();

    public DateTime getStart();

    public long getEndMillis();

    public DateTime getEnd();

    public boolean contains(ReadableInstant var1);

    public boolean contains(ReadableInterval var1);

    public boolean overlaps(ReadableInterval var1);

    public boolean isAfter(ReadableInstant var1);

    public boolean isAfter(ReadableInterval var1);

    public boolean isBefore(ReadableInstant var1);

    public boolean isBefore(ReadableInterval var1);

    public Interval toInterval();

    public MutableInterval toMutableInterval();

    public Duration toDuration();

    public long toDurationMillis();

    public Period toPeriod();

    public Period toPeriod(PeriodType var1);

    public boolean equals(Object var1);

    public int hashCode();

    public String toString();
}

