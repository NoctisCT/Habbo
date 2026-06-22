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
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.base.BaseSingleFieldPeriod;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

public final class Weeks
extends BaseSingleFieldPeriod {
    public static final Weeks ZERO = new Weeks(0);
    public static final Weeks ONE = new Weeks(1);
    public static final Weeks TWO = new Weeks(2);
    public static final Weeks THREE = new Weeks(3);
    public static final Weeks MAX_VALUE = new Weeks(Integer.MAX_VALUE);
    public static final Weeks MIN_VALUE = new Weeks(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.weeks());
    private static final long serialVersionUID = 87525275727380866L;

    public static Weeks weeks(int n) {
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
        return new Weeks(n);
    }

    public static Weeks weeksBetween(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        int n = BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.weeks());
        return Weeks.weeks(n);
    }

    public static Weeks weeksBetween(ReadablePartial readablePartial, ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalDate && readablePartial2 instanceof LocalDate) {
            Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology());
            int n = chronology.weeks().getDifference(((LocalDate)readablePartial2).getLocalMillis(), ((LocalDate)readablePartial).getLocalMillis());
            return Weeks.weeks(n);
        }
        int n = BaseSingleFieldPeriod.between(readablePartial, readablePartial2, ZERO);
        return Weeks.weeks(n);
    }

    public static Weeks weeksIn(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return ZERO;
        }
        int n = BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.weeks());
        return Weeks.weeks(n);
    }

    public static Weeks standardWeeksIn(ReadablePeriod readablePeriod) {
        int n = BaseSingleFieldPeriod.standardPeriodIn(readablePeriod, 604800000L);
        return Weeks.weeks(n);
    }

    @FromString
    public static Weeks parseWeeks(String string) {
        if (string == null) {
            return ZERO;
        }
        Period period = PARSER.parsePeriod(string);
        return Weeks.weeks(period.getWeeks());
    }

    private Weeks(int n) {
        super(n);
    }

    private Object readResolve() {
        return Weeks.weeks(this.getValue());
    }

    public DurationFieldType getFieldType() {
        return DurationFieldType.weeks();
    }

    public PeriodType getPeriodType() {
        return PeriodType.weeks();
    }

    public Days toStandardDays() {
        return Days.days(FieldUtils.safeMultiply(this.getValue(), 7));
    }

    public Hours toStandardHours() {
        return Hours.hours(FieldUtils.safeMultiply(this.getValue(), 168));
    }

    public Minutes toStandardMinutes() {
        return Minutes.minutes(FieldUtils.safeMultiply(this.getValue(), 10080));
    }

    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(this.getValue(), 604800));
    }

    public Duration toStandardDuration() {
        long l = this.getValue();
        return new Duration(l * 604800000L);
    }

    public int getWeeks() {
        return this.getValue();
    }

    public Weeks plus(int n) {
        if (n == 0) {
            return this;
        }
        return Weeks.weeks(FieldUtils.safeAdd(this.getValue(), n));
    }

    public Weeks plus(Weeks weeks) {
        if (weeks == null) {
            return this;
        }
        return this.plus(weeks.getValue());
    }

    public Weeks minus(int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }

    public Weeks minus(Weeks weeks) {
        if (weeks == null) {
            return this;
        }
        return this.minus(weeks.getValue());
    }

    public Weeks multipliedBy(int n) {
        return Weeks.weeks(FieldUtils.safeMultiply(this.getValue(), n));
    }

    public Weeks dividedBy(int n) {
        if (n == 1) {
            return this;
        }
        return Weeks.weeks(this.getValue() / n);
    }

    public Weeks negated() {
        return Weeks.weeks(FieldUtils.safeNegate(this.getValue()));
    }

    public boolean isGreaterThan(Weeks weeks) {
        if (weeks == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > weeks.getValue();
    }

    public boolean isLessThan(Weeks weeks) {
        if (weeks == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < weeks.getValue();
    }

    @ToString
    public String toString() {
        return "P" + String.valueOf(this.getValue()) + "W";
    }
}

