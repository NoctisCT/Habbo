/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.io.Serializable;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BaseInterval;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

public final class Interval
extends BaseInterval
implements ReadableInterval,
Serializable {
    private static final long serialVersionUID = 4922451897541386752L;

    public static Interval parse(String string) {
        return new Interval(string);
    }

    public static Interval parseWithOffset(String string) {
        int n = string.indexOf(47);
        if (n < 0) {
            throw new IllegalArgumentException("Format requires a '/' separator: " + string);
        }
        String string2 = string.substring(0, n);
        if (string2.length() <= 0) {
            throw new IllegalArgumentException("Format invalid: " + string);
        }
        String string3 = string.substring(n + 1);
        if (string3.length() <= 0) {
            throw new IllegalArgumentException("Format invalid: " + string);
        }
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser().withOffsetParsed();
        PeriodFormatter periodFormatter = ISOPeriodFormat.standard();
        DateTime dateTime = null;
        Period period = null;
        char c = string2.charAt(0);
        if (c == 'P' || c == 'p') {
            period = periodFormatter.withParseType(PeriodType.standard()).parsePeriod(string2);
        } else {
            dateTime = dateTimeFormatter.parseDateTime(string2);
        }
        c = string3.charAt(0);
        if (c == 'P' || c == 'p') {
            if (period != null) {
                throw new IllegalArgumentException("Interval composed of two durations: " + string);
            }
            period = periodFormatter.withParseType(PeriodType.standard()).parsePeriod(string3);
            return new Interval((ReadableInstant)dateTime, period);
        }
        DateTime dateTime2 = dateTimeFormatter.parseDateTime(string3);
        if (period != null) {
            return new Interval(period, (ReadableInstant)dateTime2);
        }
        return new Interval((ReadableInstant)dateTime, (ReadableInstant)dateTime2);
    }

    public Interval(long l, long l2) {
        super(l, l2, null);
    }

    public Interval(long l, long l2, DateTimeZone dateTimeZone) {
        super(l, l2, ISOChronology.getInstance(dateTimeZone));
    }

    public Interval(long l, long l2, Chronology chronology) {
        super(l, l2, chronology);
    }

    public Interval(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        super(readableInstant, readableInstant2);
    }

    public Interval(ReadableInstant readableInstant, ReadableDuration readableDuration) {
        super(readableInstant, readableDuration);
    }

    public Interval(ReadableDuration readableDuration, ReadableInstant readableInstant) {
        super(readableDuration, readableInstant);
    }

    public Interval(ReadableInstant readableInstant, ReadablePeriod readablePeriod) {
        super(readableInstant, readablePeriod);
    }

    public Interval(ReadablePeriod readablePeriod, ReadableInstant readableInstant) {
        super(readablePeriod, readableInstant);
    }

    public Interval(Object object) {
        super(object, null);
    }

    public Interval(Object object, Chronology chronology) {
        super(object, chronology);
    }

    public Interval toInterval() {
        return this;
    }

    public Interval overlap(ReadableInterval readableInterval) {
        if (!this.overlaps(readableInterval = DateTimeUtils.getReadableInterval(readableInterval))) {
            return null;
        }
        long l = Math.max(this.getStartMillis(), readableInterval.getStartMillis());
        long l2 = Math.min(this.getEndMillis(), readableInterval.getEndMillis());
        return new Interval(l, l2, this.getChronology());
    }

    public Interval gap(ReadableInterval readableInterval) {
        readableInterval = DateTimeUtils.getReadableInterval(readableInterval);
        long l = readableInterval.getStartMillis();
        long l2 = readableInterval.getEndMillis();
        long l3 = this.getStartMillis();
        long l4 = this.getEndMillis();
        if (l3 > l2) {
            return new Interval(l2, l3, this.getChronology());
        }
        if (l > l4) {
            return new Interval(l4, l, this.getChronology());
        }
        return null;
    }

    public boolean abuts(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            long l = DateTimeUtils.currentTimeMillis();
            return this.getStartMillis() == l || this.getEndMillis() == l;
        }
        return readableInterval.getEndMillis() == this.getStartMillis() || this.getEndMillis() == readableInterval.getStartMillis();
    }

    public Interval withChronology(Chronology chronology) {
        if (this.getChronology() == chronology) {
            return this;
        }
        return new Interval(this.getStartMillis(), this.getEndMillis(), chronology);
    }

    public Interval withStartMillis(long l) {
        if (l == this.getStartMillis()) {
            return this;
        }
        return new Interval(l, this.getEndMillis(), this.getChronology());
    }

    public Interval withStart(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        return this.withStartMillis(l);
    }

    public Interval withEndMillis(long l) {
        if (l == this.getEndMillis()) {
            return this;
        }
        return new Interval(this.getStartMillis(), l, this.getChronology());
    }

    public Interval withEnd(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        return this.withEndMillis(l);
    }

    public Interval withDurationAfterStart(ReadableDuration readableDuration) {
        long l = DateTimeUtils.getDurationMillis(readableDuration);
        if (l == this.toDurationMillis()) {
            return this;
        }
        Chronology chronology = this.getChronology();
        long l2 = this.getStartMillis();
        long l3 = chronology.add(l2, l, 1);
        return new Interval(l2, l3, chronology);
    }

    public Interval withDurationBeforeEnd(ReadableDuration readableDuration) {
        long l = DateTimeUtils.getDurationMillis(readableDuration);
        if (l == this.toDurationMillis()) {
            return this;
        }
        Chronology chronology = this.getChronology();
        long l2 = this.getEndMillis();
        long l3 = chronology.add(l2, l, -1);
        return new Interval(l3, l2, chronology);
    }

    public Interval withPeriodAfterStart(ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            return this.withDurationAfterStart(null);
        }
        Chronology chronology = this.getChronology();
        long l = this.getStartMillis();
        long l2 = chronology.add(readablePeriod, l, 1);
        return new Interval(l, l2, chronology);
    }

    public Interval withPeriodBeforeEnd(ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            return this.withDurationBeforeEnd(null);
        }
        Chronology chronology = this.getChronology();
        long l = this.getEndMillis();
        long l2 = chronology.add(readablePeriod, l, -1);
        return new Interval(l2, l, chronology);
    }
}

