/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.base;

import java.io.Serializable;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.MutableInterval;
import org.joda.time.ReadWritableInterval;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.AbstractInterval;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.IntervalConverter;
import org.joda.time.field.FieldUtils;

public abstract class BaseInterval
extends AbstractInterval
implements ReadableInterval,
Serializable {
    private static final long serialVersionUID = 576586928732749278L;
    private volatile Chronology iChronology;
    private volatile long iStartMillis;
    private volatile long iEndMillis;

    protected BaseInterval(long l, long l2, Chronology chronology) {
        this.iChronology = DateTimeUtils.getChronology(chronology);
        this.checkInterval(l, l2);
        this.iStartMillis = l;
        this.iEndMillis = l2;
    }

    protected BaseInterval(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        if (readableInstant == null && readableInstant2 == null) {
            this.iStartMillis = this.iEndMillis = DateTimeUtils.currentTimeMillis();
            this.iChronology = ISOChronology.getInstance();
        } else {
            this.iChronology = DateTimeUtils.getInstantChronology(readableInstant);
            this.iStartMillis = DateTimeUtils.getInstantMillis(readableInstant);
            this.iEndMillis = DateTimeUtils.getInstantMillis(readableInstant2);
            this.checkInterval(this.iStartMillis, this.iEndMillis);
        }
    }

    protected BaseInterval(ReadableInstant readableInstant, ReadableDuration readableDuration) {
        this.iChronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iStartMillis = DateTimeUtils.getInstantMillis(readableInstant);
        long l = DateTimeUtils.getDurationMillis(readableDuration);
        this.iEndMillis = FieldUtils.safeAdd(this.iStartMillis, l);
        this.checkInterval(this.iStartMillis, this.iEndMillis);
    }

    protected BaseInterval(ReadableDuration readableDuration, ReadableInstant readableInstant) {
        this.iChronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iEndMillis = DateTimeUtils.getInstantMillis(readableInstant);
        long l = DateTimeUtils.getDurationMillis(readableDuration);
        this.iStartMillis = FieldUtils.safeAdd(this.iEndMillis, -l);
        this.checkInterval(this.iStartMillis, this.iEndMillis);
    }

    protected BaseInterval(ReadableInstant readableInstant, ReadablePeriod readablePeriod) {
        Chronology chronology;
        this.iChronology = chronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iStartMillis = DateTimeUtils.getInstantMillis(readableInstant);
        this.iEndMillis = readablePeriod == null ? this.iStartMillis : chronology.add(readablePeriod, this.iStartMillis, 1);
        this.checkInterval(this.iStartMillis, this.iEndMillis);
    }

    protected BaseInterval(ReadablePeriod readablePeriod, ReadableInstant readableInstant) {
        Chronology chronology;
        this.iChronology = chronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iEndMillis = DateTimeUtils.getInstantMillis(readableInstant);
        this.iStartMillis = readablePeriod == null ? this.iEndMillis : chronology.add(readablePeriod, this.iEndMillis, -1);
        this.checkInterval(this.iStartMillis, this.iEndMillis);
    }

    protected BaseInterval(Object object, Chronology chronology) {
        IntervalConverter intervalConverter = ConverterManager.getInstance().getIntervalConverter(object);
        if (intervalConverter.isReadableInterval(object, chronology)) {
            ReadableInterval readableInterval = (ReadableInterval)object;
            this.iChronology = chronology != null ? chronology : readableInterval.getChronology();
            this.iStartMillis = readableInterval.getStartMillis();
            this.iEndMillis = readableInterval.getEndMillis();
        } else if (this instanceof ReadWritableInterval) {
            intervalConverter.setInto((ReadWritableInterval)((Object)this), object, chronology);
        } else {
            MutableInterval mutableInterval = new MutableInterval();
            intervalConverter.setInto(mutableInterval, object, chronology);
            this.iChronology = mutableInterval.getChronology();
            this.iStartMillis = mutableInterval.getStartMillis();
            this.iEndMillis = mutableInterval.getEndMillis();
        }
        this.checkInterval(this.iStartMillis, this.iEndMillis);
    }

    public Chronology getChronology() {
        return this.iChronology;
    }

    public long getStartMillis() {
        return this.iStartMillis;
    }

    public long getEndMillis() {
        return this.iEndMillis;
    }

    protected void setInterval(long l, long l2, Chronology chronology) {
        this.checkInterval(l, l2);
        this.iStartMillis = l;
        this.iEndMillis = l2;
        this.iChronology = DateTimeUtils.getChronology(chronology);
    }
}

