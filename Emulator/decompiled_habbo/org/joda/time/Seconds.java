/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.FromString
 *  org.joda.convert.ToString
 */
package org.joda.time;

import org.joda.convert.FromString;
import org.joda.convert.ToString;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Hours;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.joda.time.base.BaseSingleFieldPeriod;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

public final class Seconds
extends BaseSingleFieldPeriod {
    public static final Seconds ZERO = new Seconds(0);
    public static final Seconds ONE = new Seconds(1);
    public static final Seconds TWO = new Seconds(2);
    public static final Seconds THREE = new Seconds(3);
    public static final Seconds MAX_VALUE = new Seconds(Integer.MAX_VALUE);
    public static final Seconds MIN_VALUE = new Seconds(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.seconds());
    private static final long serialVersionUID = 87525275727380862L;

    public static Seconds seconds(int n) {
        switch (n) {
            case 0: {
                return ZERO;
            }
            case 1: {
                return ONE;
            }
            case 2: {
                return TWO;
            }
            case 3: {
                return THREE;
            }
            case 0x7FFFFFFF: {
                return MAX_VALUE;
            }
            case -2147483648: {
                return MIN_VALUE;
            }
        }
        return new Seconds(n);
    }

    public static Seconds secondsBetween(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        int n = BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.seconds());
        return Seconds.seconds(n);
    }

    public static Seconds secondsBetween(ReadablePartial readablePartial, ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalTime && readablePartial2 instanceof LocalTime) {
            Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology());
            int n = chronology.seconds().getDifference(((LocalTime)readablePartial2).getLocalMillis(), ((LocalTime)readablePartial).getLocalMillis());
            return Seconds.seconds(n);
        }
        int n = BaseSingleFieldPeriod.between(readablePartial, readablePartial2, ZERO);
        return Seconds.seconds(n);
    }

    public static Seconds secondsIn(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return ZERO;
        }
        int n = BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.seconds());
        return Seconds.seconds(n);
    }

    public static Seconds standardSecondsIn(ReadablePeriod readablePeriod) {
        int n = BaseSingleFieldPeriod.standardPeriodIn(readablePeriod, 1000L);
        return Seconds.seconds(n);
    }

    @FromString
    public static Seconds parseSeconds(String string) {
        if (string == null) {
            return ZERO;
        }
        Period period = PARSER.parsePeriod(string);
        return Seconds.seconds(period.getSeconds());
    }

    private Seconds(int n) {
        super(n);
    }

    private Object readResolve() {
        return Seconds.seconds(this.getValue());
    }

    public DurationFieldType getFieldType() {
        return DurationFieldType.seconds();
    }

    public PeriodType getPeriodType() {
        return PeriodType.seconds();
    }

    public Weeks toStandardWeeks() {
        return Weeks.weeks(this.getValue() / 604800);
    }

    public Days toStandardDays() {
        return Days.days(this.getValue() / 86400);
    }

    public Hours toStandardHours() {
        return Hours.hours(this.getValue() / 3600);
    }

    public Minutes toStandardMinutes() {
        return Minutes.minutes(this.getValue() / 60);
    }

    public Duration toStandardDuration() {
        long l = this.getValue();
        return new Duration(l * 1000L);
    }

    public int getSeconds() {
        return this.getValue();
    }

    public Seconds plus(int n) {
        if (n == 0) {
            return this;
        }
        return Seconds.seconds(FieldUtils.safeAdd(this.getValue(), n));
    }

    public Seconds plus(Seconds seconds) {
        if (seconds == null) {
            return this;
        }
        return this.plus(seconds.getValue());
    }

    public Seconds minus(int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }

    public Seconds minus(Seconds seconds) {
        if (seconds == null) {
            return this;
        }
        return this.minus(seconds.getValue());
    }

    public Seconds multipliedBy(int n) {
        return Seconds.seconds(FieldUtils.safeMultiply(this.getValue(), n));
    }

    public Seconds dividedBy(int n) {
        if (n == 1) {
            return this;
        }
        return Seconds.seconds(this.getValue() / n);
    }

    public Seconds negated() {
        return Seconds.seconds(FieldUtils.safeNegate(this.getValue()));
    }

    public boolean isGreaterThan(Seconds seconds) {
        if (seconds == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > seconds.getValue();
    }

    public boolean isLessThan(Seconds seconds) {
        if (seconds == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < seconds.getValue();
    }

    @ToString
    public String toString() {
        return "PT" + String.valueOf(this.getValue()) + "S";
    }
}

