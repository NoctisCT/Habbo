/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.Chronology;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePeriod;

public interface ReadWritableInterval
extends ReadableInterval {
    public void setInterval(long var1, long var3);

    public void setInterval(ReadableInterval var1);

    public void setInterval(ReadableInstant var1, ReadableInstant var2);

    public void setChronology(Chronology var1);

    public void setStartMillis(long var1);

    public void setStart(ReadableInstant var1);

    public void setEndMillis(long var1);

    public void setEnd(ReadableInstant var1);

    public void setDurationAfterStart(ReadableDuration var1);

    public void setDurationBeforeEnd(ReadableDuration var1);

    public void setPeriodAfterStart(ReadablePeriod var1);

    public void setPeriodBeforeEnd(ReadablePeriod var1);
}

