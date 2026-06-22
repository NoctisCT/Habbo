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
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
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

public final class Days
extends BaseSingleFieldPeriod {
    public static final Days ZERO = new Days(0);
    public static final Days ONE = new Days(1);
    public static final Days TWO = new Days(2);
    public static final Days THREE = new Days(3);
    public static final Days FOUR = new Days(4);
    public static final Days FIVE = new Days(5);
    public static final Days SIX = new Days(6);
    public static final Days SEVEN = new Days(7);
    public static final Days MAX_VALUE = new Days(Integer.MAX_VALUE);
    public static final Days MIN_VALUE = new Days(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.days());
    private static final long serialVersionUID = 87525275727380865L;

    public static Days days(int n) {
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
            case 4: {
                return FOUR;
            }
            case 5: {
                return FIVE;
            }
            case 6: {
                return SIX;
            }
            case 7: {
                return SEVEN;
            }
            case 0x7FFFFFFF: {
                return MAX_VALUE;
            }
            case -2147483648: {
                return MIN_VALUE;
            }
        }
        return new Days(n);
    }

    public static Days daysBetween(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        int n = BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.days());
        return Days.days(n);
    }

    public static Days daysBetween(ReadablePartial readablePartial, ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalDate && readablePartial2 instanceof LocalDate) {
            Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology());
            int n = chronology.days().getDifference(((LocalDate)readablePartial2).getLocalMillis(), ((LocalDate)readablePartial).getLocalMillis());
            return Days.days(n);
        }
        int n = BaseSingleFieldPeriod.between(readablePartial, readablePartial2, ZERO);
        return Days.days(n);
    }

    public static Days daysIn(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return ZERO;
        }
        int n = BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.days());
        return Days.days(n);
    }

    public static Days standardDaysIn(ReadablePeriod readablePeriod) {
        int n = BaseSingleFieldPeriod.standardPeriodIn(readablePeriod, 86400000L);
        return Days.days(n);
    }

    @FromString
    public static Days parseDays(String string) {
        if (string == null) {
            return ZERO;
        }
        Period period = PARSER.parsePeriod(string);
        return Days.days(period.getDays());
    }

    private Days(int n) {
        super(n);
    }

    private Object readResolve() {
        return Days.days(this.getValue());
    }

    public DurationFieldType getFieldType() {
        return DurationFieldType.days();
    }

    public PeriodType getPeriodType() {
        return PeriodType.days();
    }

    public Weeks toStandardWeeks() {
        return Weeks.weeks(this.getValue() / 7);
    }

    public Hours toStandardHours() {
        return Hours.hours(FieldUtils.safeMultiply(this.getValue(), 24));
    }

    public Minutes toStandardMinutes() {
        return Minutes.minutes(FieldUtils.safeMultiply(this.getValue(), 1440));
    }

    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(this.getValue(), 86400));
    }

    public Duration toStandardDuration() {
        long l = this.getValue();
        return new Duration(l * 86400000L);
    }

    public int getDays() {
        return this.getValue();
    }

    public Days plus(int n) {
        if (n == 0) {
            return this;
        }
        return Days.days(FieldUtils.safeAdd(this.getValue(), n));
    }

    public Days plus(Days days) {
        if (days == null) {
            return this;
        }
        return this.plus(days.getValue());
    }

    public Days minus(int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }

    public Days minus(Days days) {
        if (days == null) {
            return this;
        }
        return this.minus(days.getValue());
    }

    public Days multipliedBy(int n) {
        return Days.days(FieldUtils.safeMultiply(this.getValue(), n));
    }

    public Days dividedBy(int n) {
        if (n == 1) {
            return this;
        }
        return Days.days(this.getValue() / n);
    }

    public Days negated() {
        return Days.days(FieldUtils.safeNegate(this.getValue()));
    }

    public boolean isGreaterThan(Days days) {
        if (days == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > days.getValue();
    }

    public boolean isLessThan(Days days) {
        if (days == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < days.getValue();
    }

    @ToString
    public String toString() {
        return "P" + String.valueOf(this.getValue()) + "D";
    }
}

