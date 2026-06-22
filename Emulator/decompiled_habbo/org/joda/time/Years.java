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

public final class Years
extends BaseSingleFieldPeriod {
    public static final Years ZERO = new Years(0);
    public static final Years ONE = new Years(1);
    public static final Years TWO = new Years(2);
    public static final Years THREE = new Years(3);
    public static final Years MAX_VALUE = new Years(Integer.MAX_VALUE);
    public static final Years MIN_VALUE = new Years(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.years());
    private static final long serialVersionUID = 87525275727380868L;

    public static Years years(int n) {
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
        return new Years(n);
    }

    public static Years yearsBetween(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        int n = BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.years());
        return Years.years(n);
    }

    public static Years yearsBetween(ReadablePartial readablePartial, ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalDate && readablePartial2 instanceof LocalDate) {
            Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology());
            int n = chronology.years().getDifference(((LocalDate)readablePartial2).getLocalMillis(), ((LocalDate)readablePartial).getLocalMillis());
            return Years.years(n);
        }
        int n = BaseSingleFieldPeriod.between(readablePartial, readablePartial2, ZERO);
        return Years.years(n);
    }

    public static Years yearsIn(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return ZERO;
        }
        int n = BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.years());
        return Years.years(n);
    }

    @FromString
    public static Years parseYears(String string) {
        if (string == null) {
            return ZERO;
        }
        Period period = PARSER.parsePeriod(string);
        return Years.years(period.getYears());
    }

    private Years(int n) {
        super(n);
    }

    private Object readResolve() {
        return Years.years(this.getValue());
    }

    public DurationFieldType getFieldType() {
        return DurationFieldType.years();
    }

    public PeriodType getPeriodType() {
        return PeriodType.years();
    }

    public int getYears() {
        return this.getValue();
    }

    public Years plus(int n) {
        if (n == 0) {
            return this;
        }
        return Years.years(FieldUtils.safeAdd(this.getValue(), n));
    }

    public Years plus(Years years) {
        if (years == null) {
            return this;
        }
        return this.plus(years.getValue());
    }

    public Years minus(int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }

    public Years minus(Years years) {
        if (years == null) {
            return this;
        }
        return this.minus(years.getValue());
    }

    public Years multipliedBy(int n) {
        return Years.years(FieldUtils.safeMultiply(this.getValue(), n));
    }

    public Years dividedBy(int n) {
        if (n == 1) {
            return this;
        }
        return Years.years(this.getValue() / n);
    }

    public Years negated() {
        return Years.years(FieldUtils.safeNegate(this.getValue()));
    }

    public boolean isGreaterThan(Years years) {
        if (years == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > years.getValue();
    }

    public boolean isLessThan(Years years) {
        if (years == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < years.getValue();
    }

    @ToString
    public String toString() {
        return "P" + String.valueOf(this.getValue()) + "Y";
    }
}

