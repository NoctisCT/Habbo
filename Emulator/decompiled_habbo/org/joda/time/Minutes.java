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
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.base.BaseSingleFieldPeriod;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

public final class Minutes
extends BaseSingleFieldPeriod {
    public static final Minutes ZERO = new Minutes(0);
    public static final Minutes ONE = new Minutes(1);
    public static final Minutes TWO = new Minutes(2);
    public static final Minutes THREE = new Minutes(3);
    public static final Minutes MAX_VALUE = new Minutes(Integer.MAX_VALUE);
    public static final Minutes MIN_VALUE = new Minutes(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.minutes());
    private static final long serialVersionUID = 87525275727380863L;

    public static Minutes minutes(int n) {
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
        return new Minutes(n);
    }

    public static Minutes minutesBetween(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        int n = BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.minutes());
        return Minutes.minutes(n);
    }

    public static Minutes minutesBetween(ReadablePartial readablePartial, ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalTime && readablePartial2 instanceof LocalTime) {
            Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology());
            int n = chronology.minutes().getDifference(((LocalTime)readablePartial2).getLocalMillis(), ((LocalTime)readablePartial).getLocalMillis());
            return Minutes.minutes(n);
        }
        int n = BaseSingleFieldPeriod.between(readablePartial, readablePartial2, ZERO);
        return Minutes.minutes(n);
    }

    public static Minutes minutesIn(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return ZERO;
        }
        int n = BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.minutes());
        return Minutes.minutes(n);
    }

    public static Minutes standardMinutesIn(ReadablePeriod readablePeriod) {
        int n = BaseSingleFieldPeriod.standardPeriodIn(readablePeriod, 60000L);
        return Minutes.minutes(n);
    }

    @FromString
    public static Minutes parseMinutes(String string) {
        if (string == null) {
            return ZERO;
        }
        Period period = PARSER.parsePeriod(string);
        return Minutes.minutes(period.getMinutes());
    }

    private Minutes(int n) {
        super(n);
    }

    private Object readResolve() {
        return Minutes.minutes(this.getValue());
    }

    public DurationFieldType getFieldType() {
        return DurationFieldType.minutes();
    }

    public PeriodType getPeriodType() {
        return PeriodType.minutes();
    }

    public Weeks toStandardWeeks() {
        return Weeks.weeks(this.getValue() / 10080);
    }

    public Days toStandardDays() {
        return Days.days(this.getValue() / 1440);
    }

    public Hours toStandardHours() {
        return Hours.hours(this.getValue() / 60);
    }

    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(this.getValue(), 60));
    }

    public Duration toStandardDuration() {
        long l = this.getValue();
        return new Duration(l * 60000L);
    }

    public int getMinutes() {
        return this.getValue();
    }

    public Minutes plus(int n) {
        if (n == 0) {
            return this;
        }
        return Minutes.minutes(FieldUtils.safeAdd(this.getValue(), n));
    }

    public Minutes plus(Minutes minutes) {
        if (minutes == null) {
            return this;
        }
        return this.plus(minutes.getValue());
    }

    public Minutes minus(int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }

    public Minutes minus(Minutes minutes) {
        if (minutes == null) {
            return this;
        }
        return this.minus(minutes.getValue());
    }

    public Minutes multipliedBy(int n) {
        return Minutes.minutes(FieldUtils.safeMultiply(this.getValue(), n));
    }

    public Minutes dividedBy(int n) {
        if (n == 1) {
            return this;
        }
        return Minutes.minutes(this.getValue() / n);
    }

    public Minutes negated() {
        return Minutes.minutes(FieldUtils.safeNegate(this.getValue()));
    }

    public boolean isGreaterThan(Minutes minutes) {
        if (minutes == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > minutes.getValue();
    }

    public boolean isLessThan(Minutes minutes) {
        if (minutes == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < minutes.getValue();
    }

    @ToString
    public String toString() {
        return "PT" + String.valueOf(this.getValue()) + "M";
    }
}

