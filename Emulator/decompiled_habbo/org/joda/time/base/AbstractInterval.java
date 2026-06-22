/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.base;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.MutableInterval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public abstract class AbstractInterval
implements ReadableInterval {
    protected AbstractInterval() {
    }

    protected void checkInterval(long l, long l2) {
        if (l2 < l) {
            throw new IllegalArgumentException("The end instant must be greater than the start instant");
        }
    }

    public DateTime getStart() {
        return new DateTime(this.getStartMillis(), this.getChronology());
    }

    public DateTime getEnd() {
        return new DateTime(this.getEndMillis(), this.getChronology());
    }

    public boolean contains(long l) {
        long l2 = this.getStartMillis();
        long l3 = this.getEndMillis();
        return l >= l2 && l < l3;
    }

    public boolean containsNow() {
        return this.contains(DateTimeUtils.currentTimeMillis());
    }

    public boolean contains(ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.containsNow();
        }
        return this.contains(readableInstant.getMillis());
    }

    public boolean contains(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return this.containsNow();
        }
        long l = readableInterval.getStartMillis();
        long l2 = readableInterval.getEndMillis();
        long l3 = this.getStartMillis();
        long l4 = this.getEndMillis();
        return l3 <= l && l < l4 && l2 <= l4;
    }

    public boolean overlaps(ReadableInterval readableInterval) {
        long l = this.getStartMillis();
        long l2 = this.getEndMillis();
        if (readableInterval == null) {
            long l3 = DateTimeUtils.currentTimeMillis();
            return l < l3 && l3 < l2;
        }
        long l4 = readableInterval.getStartMillis();
        long l5 = readableInterval.getEndMillis();
        return l < l5 && l4 < l2;
    }

    public boolean isEqual(ReadableInterval readableInterval) {
        return this.getStartMillis() == readableInterval.getStartMillis() && this.getEndMillis() == readableInterval.getEndMillis();
    }

    public boolean isBefore(long l) {
        return this.getEndMillis() <= l;
    }

    public boolean isBeforeNow() {
        return this.isBefore(DateTimeUtils.currentTimeMillis());
    }

    public boolean isBefore(ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.isBeforeNow();
        }
        return this.isBefore(readableInstant.getMillis());
    }

    public boolean isBefore(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return this.isBeforeNow();
        }
        return this.isBefore(readableInterval.getStartMillis());
    }

    public boolean isAfter(long l) {
        return this.getStartMillis() > l;
    }

    public boolean isAfterNow() {
        return this.isAfter(DateTimeUtils.currentTimeMillis());
    }

    public boolean isAfter(ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.isAfterNow();
        }
        return this.isAfter(readableInstant.getMillis());
    }

    public boolean isAfter(ReadableInterval readableInterval) {
        long l = readableInterval == null ? DateTimeUtils.currentTimeMillis() : readableInterval.getEndMillis();
        return this.getStartMillis() >= l;
    }

    public Interval toInterval() {
        return new Interval(this.getStartMillis(), this.getEndMillis(), this.getChronology());
    }

    public MutableInterval toMutableInterval() {
        return new MutableInterval(this.getStartMillis(), this.getEndMillis(), this.getChronology());
    }

    public long toDurationMillis() {
        return FieldUtils.safeSubtract(this.getEndMillis(), this.getStartMillis());
    }

    public Duration toDuration() {
        long l = this.toDurationMillis();
        if (l == 0L) {
            return Duration.ZERO;
        }
        return new Duration(l);
    }

    public Period toPeriod() {
        return new Period(this.getStartMillis(), this.getEndMillis(), this.getChronology());
    }

    public Period toPeriod(PeriodType periodType) {
        return new Period(this.getStartMillis(), this.getEndMillis(), periodType, this.getChronology());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ReadableInterval)) {
            return false;
        }
        ReadableInterval readableInterval = (ReadableInterval)object;
        return this.getStartMillis() == readableInterval.getStartMillis() && this.getEndMillis() == readableInterval.getEndMillis() && FieldUtils.equals(this.getChronology(), readableInterval.getChronology());
    }

    public int hashCode() {
        long l = this.getStartMillis();
        long l2 = this.getEndMillis();
        int n = 97;
        n = 31 * n + (int)(l ^ l >>> 32);
        n = 31 * n + (int)(l2 ^ l2 >>> 32);
        n = 31 * n + this.getChronology().hashCode();
        return n;
    }

    public String toString() {
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        dateTimeFormatter = dateTimeFormatter.withChronology(this.getChronology());
        StringBuffer stringBuffer = new StringBuffer(48);
        dateTimeFormatter.printTo(stringBuffer, this.getStartMillis());
        stringBuffer.append('/');
        dateTimeFormatter.printTo(stringBuffer, this.getEndMillis());
        return stringBuffer.toString();
    }
}

