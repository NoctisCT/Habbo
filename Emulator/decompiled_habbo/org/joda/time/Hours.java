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
import org.joda.time.LocalTime;
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

public final class Hours
extends BaseSingleFieldPeriod {
    public static final Hours ZERO = new Hours(0);
    public static final Hours ONE = new Hours(1);
    public static final Hours TWO = new Hours(2);
    public static final Hours THREE = new Hours(3);
    public static final Hours FOUR = new Hours(4);
    public static final Hours FIVE = new Hours(5);
    public static final Hours SIX = new Hours(6);
    public static final Hours SEVEN = new Hours(7);
    public static final Hours EIGHT = new Hours(8);
    public static final Hours MAX_VALUE = new Hours(Integer.MAX_VALUE);
    public static final Hours MIN_VALUE = new Hours(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.hours());
    private static final long serialVersionUID = 87525275727380864L;

    public static Hours hours(int n) {
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
            case 8: {
                return EIGHT;
            }
            case 0x7FFFFFFF: {
                return MAX_VALUE;
            }
            case -2147483648: {
                return MIN_VALUE;
            }
        }
        return new Hours(n);
    }

    public static Hours hoursBetween(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        int n = BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.hours());
        return Hours.hours(n);
    }

    public static Hours hoursBetween(ReadablePartial readablePartial, ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalTime && readablePartial2 instanceof LocalTime) {
            Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology());
            int n = chronology.hours().getDifference(((LocalTime)readablePartial2).getLocalMillis(), ((LocalTime)readablePartial).getLocalMillis());
            return Hours.hours(n);
        }
        int n = BaseSingleFieldPeriod.between(readablePartial, readablePartial2, ZERO);
        return Hours.hours(n);
    }

    public static Hours hoursIn(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return ZERO;
        }
        int n = BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.hours());
        return Hours.hours(n);
    }

    public static Hours standardHoursIn(ReadablePeriod readablePeriod) {
        int n = BaseSingleFieldPeriod.standardPeriodIn(readablePeriod, 3600000L);
        return Hours.hours(n);
    }

    @FromString
    public static Hours parseHours(String string) {
        if (string == null) {
            return ZERO;
        }
        Period period = PARSER.parsePeriod(string);
        return Hours.hours(period.getHours());
    }

    private Hours(int n) {
        super(n);
    }

    private Object readResolve() {
        return Hours.hours(this.getValue());
    }

    public DurationFieldType getFieldType() {
        return DurationFieldType.hours();
    }

    public PeriodType getPeriodType() {
        return PeriodType.hours();
    }

    public Weeks toStandardWeeks() {
        return Weeks.weeks(this.getValue() / 168);
    }

    public Days toStandardDays() {
        return Days.days(this.getValue() / 24);
    }

    public Minutes toStandardMinutes() {
        return Minutes.minutes(FieldUtils.safeMultiply(this.getValue(), 60));
    }

    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(this.getValue(), 3600));
    }

    public Duration toStandardDuration() {
        long l = this.getValue();
        return new Duration(l * 3600000L);
    }

    public int getHours() {
        return this.getValue();
    }

    public Hours plus(int n) {
        if (n == 0) {
            return this;
        }
        return Hours.hours(FieldUtils.safeAdd(this.getValue(), n));
    }

    public Hours plus(Hours hours) {
        if (hours == null) {
            return this;
        }
        return this.plus(hours.getValue());
    }

    public Hours minus(int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }

    public Hours minus(Hours hours) {
        if (hours == null) {
            return this;
        }
        return this.minus(hours.getValue());
    }

    public Hours multipliedBy(int n) {
        return Hours.hours(FieldUtils.safeMultiply(this.getValue(), n));
    }

    public Hours dividedBy(int n) {
        if (n == 1) {
            return this;
        }
        return Hours.hours(this.getValue() / n);
    }

    public Hours negated() {
        return Hours.hours(FieldUtils.safeNegate(this.getValue()));
    }

    public boolean isGreaterThan(Hours hours) {
        if (hours == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > hours.getValue();
    }

    public boolean isLessThan(Hours hours) {
        if (hours == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < hours.getValue();
    }

    @ToString
    public String toString() {
        return "PT" + String.valueOf(this.getValue()) + "H";
    }
}

