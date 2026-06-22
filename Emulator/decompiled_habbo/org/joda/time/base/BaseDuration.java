/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.base;

import java.io.Serializable;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.base.AbstractDuration;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.DurationConverter;
import org.joda.time.field.FieldUtils;

public abstract class BaseDuration
extends AbstractDuration
implements ReadableDuration,
Serializable {
    private static final long serialVersionUID = 2581698638990L;
    private volatile long iMillis;

    protected BaseDuration(long l) {
        this.iMillis = l;
    }

    protected BaseDuration(long l, long l2) {
        this.iMillis = FieldUtils.safeSubtract(l2, l);
    }

    protected BaseDuration(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        if (readableInstant == readableInstant2) {
            this.iMillis = 0L;
        } else {
            long l = DateTimeUtils.getInstantMillis(readableInstant);
            long l2 = DateTimeUtils.getInstantMillis(readableInstant2);
            this.iMillis = FieldUtils.safeSubtract(l2, l);
        }
    }

    protected BaseDuration(Object object) {
        DurationConverter durationConverter = ConverterManager.getInstance().getDurationConverter(object);
        this.iMillis = durationConverter.getDurationMillis(object);
    }

    public long getMillis() {
        return this.iMillis;
    }

    protected void setMillis(long l) {
        this.iMillis = l;
    }

    public Period toPeriod(PeriodType periodType) {
        return new Period(this.getMillis(), periodType);
    }

    public Period toPeriod(Chronology chronology) {
        return new Period(this.getMillis(), chronology);
    }

    public Period toPeriod(PeriodType periodType, Chronology chronology) {
        return new Period(this.getMillis(), periodType, chronology);
    }

    public Period toPeriodFrom(ReadableInstant readableInstant) {
        return new Period(readableInstant, this);
    }

    public Period toPeriodFrom(ReadableInstant readableInstant, PeriodType periodType) {
        return new Period(readableInstant, this, periodType);
    }

    public Period toPeriodTo(ReadableInstant readableInstant) {
        return new Period(this, readableInstant);
    }

    public Period toPeriodTo(ReadableInstant readableInstant, PeriodType periodType) {
        return new Period(this, readableInstant, periodType);
    }

    public Interval toIntervalFrom(ReadableInstant readableInstant) {
        return new Interval(readableInstant, this);
    }

    public Interval toIntervalTo(ReadableInstant readableInstant) {
        return new Interval(this, readableInstant);
    }
}

