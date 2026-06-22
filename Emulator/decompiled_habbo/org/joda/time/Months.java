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
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePartial;
import org.joda.time.base.BaseSingleFieldPeriod;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

public final class Months
extends BaseSingleFieldPeriod {
    public static final Months ZERO = new Months(0);
    public static final Months ONE = new Months(1);
    public static final Months TWO = new Months(2);
    public static final Months THREE = new Months(3);
    public static final Months FOUR = new Months(4);
    public static final Months FIVE = new Months(5);
    public static final Months SIX = new Months(6);
    public static final Months SEVEN = new Months(7);
    public static final Months EIGHT = new Months(8);
    public static final Months NINE = new Months(9);
    public static final Months TEN = new Months(10);
    public static final Months ELEVEN = new Months(11);
    public static final Months TWELVE = new Months(12);
    public static final Months MAX_VALUE = new Months(Integer.MAX_VALUE);
    public static final Months MIN_VALUE = new Months(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.months());
    private static final long serialVersionUID = 87525275727380867L;

    public static Months months(int n) {
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
            case 9: {
                return NINE;
            }
            case 10: {
                return TEN;
            }
            case 11: {
                return ELEVEN;
            }
            case 12: {
                return TWELVE;
            }
            case 0x7FFFFFFF: {
                return MAX_VALUE;
            }
            case -2147483648: {
                return MIN_VALUE;
            }
        }
        return new Months(n);
    }

    public static Months monthsBetween(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        int n = BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.months());
        return Months.months(n);
    }

    public static Months monthsBetween(ReadablePartial readablePartial, ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalDate && readablePartial2 instanceof LocalDate) {
            Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology());
            int n = chronology.months().getDifference(((LocalDate)readablePartial2).getLocalMillis(), ((LocalDate)readablePartial).getLocalMillis());
            return Months.months(n);
        }
        int n = BaseSingleFieldPeriod.between(readablePartial, readablePartial2, ZERO);
        return Months.months(n);
    }

    public static Months monthsIn(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return ZERO;
        }
        int n = BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.months());
        return Months.months(n);
    }

    @FromString
    public static Months parseMonths(String string) {
        if (string == null) {
            return ZERO;
        }
        Period period = PARSER.parsePeriod(string);
        return Months.months(period.getMonths());
    }

    private Months(int n) {
        super(n);
    }

    private Object readResolve() {
        return Months.months(this.getValue());
    }

    public DurationFieldType getFieldType() {
        return DurationFieldType.months();
    }

    public PeriodType getPeriodType() {
        return PeriodType.months();
    }

    public int getMonths() {
        return this.getValue();
    }

    public Months plus(int n) {
        if (n == 0) {
            return this;
        }
        return Months.months(FieldUtils.safeAdd(this.getValue(), n));
    }

    public Months plus(Months months) {
        if (months == null) {
            return this;
        }
        return this.plus(months.getValue());
    }

    public Months minus(int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }

    public Months minus(Months months) {
        if (months == null) {
            return this;
        }
        return this.minus(months.getValue());
    }

    public Months multipliedBy(int n) {
        return Months.months(FieldUtils.safeMultiply(this.getValue(), n));
    }

    public Months dividedBy(int n) {
        if (n == 1) {
            return this;
        }
        return Months.months(this.getValue() / n);
    }

    public Months negated() {
        return Months.months(FieldUtils.safeNegate(this.getValue()));
    }

    public boolean isGreaterThan(Months months) {
        if (months == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > months.getValue();
    }

    public boolean isLessThan(Months months) {
        if (months == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < months.getValue();
    }

    @ToString
    public String toString() {
        return "P" + String.valueOf(this.getValue()) + "M";
    }
}

