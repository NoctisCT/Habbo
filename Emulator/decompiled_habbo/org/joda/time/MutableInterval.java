/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.io.Serializable;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadWritableInterval;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BaseInterval;
import org.joda.time.field.FieldUtils;

public class MutableInterval
extends BaseInterval
implements ReadWritableInterval,
Cloneable,
Serializable {
    private static final long serialVersionUID = -5982824024992428470L;

    public static MutableInterval parse(String string) {
        return new MutableInterval(string);
    }

    public MutableInterval() {
        super(0L, 0L, null);
    }

    public MutableInterval(long l, long l2) {
        super(l, l2, null);
    }

    public MutableInterval(long l, long l2, Chronology chronology) {
        super(l, l2, chronology);
    }

    public MutableInterval(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        super(readableInstant, readableInstant2);
    }

    public MutableInterval(ReadableInstant readableInstant, ReadableDuration readableDuration) {
        super(readableInstant, readableDuration);
    }

    public MutableInterval(ReadableDuration readableDuration, ReadableInstant readableInstant) {
        super(readableDuration, readableInstant);
    }

    public MutableInterval(ReadableInstant readableInstant, ReadablePeriod readablePeriod) {
        super(readableInstant, readablePeriod);
    }

    public MutableInterval(ReadablePeriod readablePeriod, ReadableInstant readableInstant) {
        super(readablePeriod, readableInstant);
    }

    public MutableInterval(Object object) {
        super(object, null);
    }

    public MutableInterval(Object object, Chronology chronology) {
        super(object, chronology);
    }

    public void setInterval(long l, long l2) {
        super.setInterval(l, l2, this.getChronology());
    }

    public void setInterval(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            throw new IllegalArgumentException("Interval must not be null");
        }
        long l = readableInterval.getStartMillis();
        long l2 = readableInterval.getEndMillis();
        Chronology chronology = readableInterval.getChronology();
        super.setInterval(l, l2, chronology);
    }

    public void setInterval(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        if (readableInstant == null && readableInstant2 == null) {
            long l = DateTimeUtils.currentTimeMillis();
            this.setInterval(l, l);
        } else {
            long l = DateTimeUtils.getInstantMillis(readableInstant);
            long l2 = DateTimeUtils.getInstantMillis(readableInstant2);
            Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
            super.setInterval(l, l2, chronology);
        }
    }

    public void setChronology(Chronology chronology) {
        super.setInterval(this.getStartMillis(), this.getEndMillis(), chronology);
    }

    public void setStartMillis(long l) {
        super.setInterval(l, this.getEndMillis(), this.getChronology());
    }

    public void setStart(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        super.setInterval(l, this.getEndMillis(), this.getChronology());
    }

    public void setEndMillis(long l) {
        super.setInterval(this.getStartMillis(), l, this.getChronology());
    }

    public void setEnd(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        super.setInterval(this.getStartMillis(), l, this.getChronology());
    }

    public void setDurationAfterStart(long l) {
        this.setEndMillis(FieldUtils.safeAdd(this.getStartMillis(), l));
    }

    public void setDurationBeforeEnd(long l) {
        this.setStartMillis(FieldUtils.safeAdd(this.getEndMillis(), -l));
    }

    public void setDurationAfterStart(ReadableDuration readableDuration) {
        long l = DateTimeUtils.getDurationMillis(readableDuration);
        this.setEndMillis(FieldUtils.safeAdd(this.getStartMillis(), l));
    }

    public void setDurationBeforeEnd(ReadableDuration readableDuration) {
        long l = DateTimeUtils.getDurationMillis(readableDuration);
        this.setStartMillis(FieldUtils.safeAdd(this.getEndMillis(), -l));
    }

    public void setPeriodAfterStart(ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            this.setEndMillis(this.getStartMillis());
        } else {
            this.setEndMillis(this.getChronology().add(readablePeriod, this.getStartMillis(), 1));
        }
    }

    public void setPeriodBeforeEnd(ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            this.setStartMillis(this.getEndMillis());
        } else {
            this.setStartMillis(this.getChronology().add(readablePeriod, this.getEndMillis(), -1));
        }
    }

    public MutableInterval copy() {
        return (MutableInterval)this.clone();
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError("Clone error");
        }
    }
}

