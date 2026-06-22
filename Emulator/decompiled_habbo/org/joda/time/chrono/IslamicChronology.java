/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.chrono.BasicMonthOfYearDateTimeField;
import org.joda.time.chrono.BasicSingleEraDateTimeField;
import org.joda.time.chrono.LimitChronology;
import org.joda.time.chrono.ZonedChronology;

public final class IslamicChronology
extends BasicChronology {
    private static final long serialVersionUID = -3663823829888L;
    public static final int AH = 1;
    private static final DateTimeField ERA_FIELD = new BasicSingleEraDateTimeField("AH");
    public static final LeapYearPatternType LEAP_YEAR_15_BASED = new LeapYearPatternType(0, 623158436);
    public static final LeapYearPatternType LEAP_YEAR_16_BASED = new LeapYearPatternType(1, 623191204);
    public static final LeapYearPatternType LEAP_YEAR_INDIAN = new LeapYearPatternType(2, 690562340);
    public static final LeapYearPatternType LEAP_YEAR_HABASH_AL_HASIB = new LeapYearPatternType(3, 0x9292925);
    private static final int MIN_YEAR = -292269337;
    private static final int MAX_YEAR = 292271022;
    private static final int MONTH_PAIR_LENGTH = 59;
    private static final int LONG_MONTH_LENGTH = 30;
    private static final int SHORT_MONTH_LENGTH = 29;
    private static final long MILLIS_PER_MONTH_PAIR = 5097600000L;
    private static final long MILLIS_PER_MONTH = 2551440384L;
    private static final long MILLIS_PER_LONG_MONTH = 2592000000L;
    private static final long MILLIS_PER_YEAR = 30617280288L;
    private static final long MILLIS_PER_SHORT_YEAR = 30585600000L;
    private static final long MILLIS_PER_LONG_YEAR = 30672000000L;
    private static final long MILLIS_YEAR_1 = -42521587200000L;
    private static final int CYCLE = 30;
    private static final long MILLIS_PER_CYCLE = 918518400000L;
    private static final ConcurrentHashMap<DateTimeZone, IslamicChronology[]> cCache = new ConcurrentHashMap();
    private static final IslamicChronology INSTANCE_UTC = IslamicChronology.getInstance(DateTimeZone.UTC);
    private final LeapYearPatternType iLeapYears;

    public static IslamicChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }

    public static IslamicChronology getInstance() {
        return IslamicChronology.getInstance(DateTimeZone.getDefault(), LEAP_YEAR_16_BASED);
    }

    public static IslamicChronology getInstance(DateTimeZone dateTimeZone) {
        return IslamicChronology.getInstance(dateTimeZone, LEAP_YEAR_16_BASED);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static IslamicChronology getInstance(DateTimeZone dateTimeZone, LeapYearPatternType leapYearPatternType) {
        IslamicChronology islamicChronology;
        IslamicChronology[] islamicChronologyArray;
        IslamicChronology[] islamicChronologyArray2;
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if ((islamicChronologyArray2 = cCache.get(dateTimeZone)) == null && (islamicChronologyArray = cCache.putIfAbsent(dateTimeZone, islamicChronologyArray2 = new IslamicChronology[4])) != null) {
            islamicChronologyArray2 = islamicChronologyArray;
        }
        if ((islamicChronology = islamicChronologyArray2[leapYearPatternType.index]) != null) return islamicChronology;
        islamicChronologyArray = islamicChronologyArray2;
        synchronized (islamicChronologyArray2) {
            islamicChronology = islamicChronologyArray2[leapYearPatternType.index];
            if (islamicChronology != null) return islamicChronology;
            if (dateTimeZone == DateTimeZone.UTC) {
                islamicChronology = new IslamicChronology(null, null, leapYearPatternType);
                DateTime dateTime = new DateTime(1, 1, 1, 0, 0, 0, 0, islamicChronology);
                islamicChronology = new IslamicChronology((Chronology)LimitChronology.getInstance(islamicChronology, dateTime, null), null, leapYearPatternType);
            } else {
                islamicChronology = IslamicChronology.getInstance(DateTimeZone.UTC, leapYearPatternType);
                islamicChronology = new IslamicChronology((Chronology)ZonedChronology.getInstance(islamicChronology, dateTimeZone), null, leapYearPatternType);
            }
            islamicChronologyArray2[leapYearPatternType.index] = islamicChronology;
            // ** MonitorExit[var4_3] (shouldn't be in output)
            return islamicChronology;
        }
    }

    IslamicChronology(Chronology chronology, Object object, LeapYearPatternType leapYearPatternType) {
        super(chronology, object, 4);
        this.iLeapYears = leapYearPatternType;
    }

    private Object readResolve() {
        Chronology chronology = this.getBase();
        return chronology == null ? IslamicChronology.getInstanceUTC() : IslamicChronology.getInstance(chronology.getZone());
    }

    public LeapYearPatternType getLeapYearPatternType() {
        return this.iLeapYears;
    }

    public Chronology withUTC() {
        return INSTANCE_UTC;
    }

    public Chronology withZone(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if (dateTimeZone == this.getZone()) {
            return this;
        }
        return IslamicChronology.getInstance(dateTimeZone);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof IslamicChronology) {
            IslamicChronology islamicChronology = (IslamicChronology)object;
            return this.getLeapYearPatternType().index == islamicChronology.getLeapYearPatternType().index && super.equals(object);
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode() * 13 + this.getLeapYearPatternType().hashCode();
    }

    int getYear(long l) {
        long l2;
        long l3 = l - -42521587200000L;
        long l4 = l3 / 918518400000L;
        int n = (int)(l4 * 30L + 1L);
        long l5 = l2 = this.isLeapYear(n) ? 30672000000L : 30585600000L;
        for (long i = l3 % 918518400000L; i >= l2; i -= l2) {
            l2 = this.isLeapYear(++n) ? 30672000000L : 30585600000L;
        }
        return n;
    }

    long setYear(long l, int n) {
        int n2 = this.getYear(l);
        int n3 = this.getDayOfYear(l, n2);
        int n4 = this.getMillisOfDay(l);
        if (n3 > 354 && !this.isLeapYear(n)) {
            --n3;
        }
        l = this.getYearMonthDayMillis(n, 1, n3);
        return l += (long)n4;
    }

    long getYearDifference(long l, long l2) {
        int n = this.getYear(l);
        int n2 = this.getYear(l2);
        long l3 = l - this.getYearMillis(n);
        long l4 = l2 - this.getYearMillis(n2);
        int n3 = n - n2;
        if (l3 < l4) {
            --n3;
        }
        return n3;
    }

    long getTotalMillisByYearMonth(int n, int n2) {
        if (--n2 % 2 == 1) {
            return (long)(n2 /= 2) * 5097600000L + 2592000000L;
        }
        return (long)(n2 /= 2) * 5097600000L;
    }

    int getDayOfMonth(long l) {
        int n = this.getDayOfYear(l) - 1;
        if (n == 354) {
            return 30;
        }
        return n % 59 % 30 + 1;
    }

    boolean isLeapYear(int n) {
        return this.iLeapYears.isLeapYear(n);
    }

    int getDaysInYearMax() {
        return 355;
    }

    int getDaysInYear(int n) {
        return this.isLeapYear(n) ? 355 : 354;
    }

    int getDaysInYearMonth(int n, int n2) {
        if (n2 == 12 && this.isLeapYear(n)) {
            return 30;
        }
        return --n2 % 2 == 0 ? 30 : 29;
    }

    int getDaysInMonthMax() {
        return 30;
    }

    int getDaysInMonthMax(int n) {
        if (n == 12) {
            return 30;
        }
        return --n % 2 == 0 ? 30 : 29;
    }

    int getMonthOfYear(long l, int n) {
        int n2 = (int)((l - this.getYearMillis(n)) / 86400000L);
        if (n2 == 354) {
            return 12;
        }
        return n2 * 2 / 59 + 1;
    }

    long getAverageMillisPerYear() {
        return 30617280288L;
    }

    long getAverageMillisPerYearDividedByTwo() {
        return 15308640144L;
    }

    long getAverageMillisPerMonth() {
        return 2551440384L;
    }

    long calculateFirstDayOfYearMillis(int n) {
        if (n > 292271022) {
            throw new ArithmeticException("Year is too large: " + n + " > " + 292271022);
        }
        if (n < -292269337) {
            throw new ArithmeticException("Year is too small: " + n + " < " + -292269337);
        }
        long l = --n / 30;
        long l2 = -42521587200000L + l * 918518400000L;
        int n2 = n % 30 + 1;
        for (int i = 1; i < n2; ++i) {
            l2 += this.isLeapYear(i) ? 30672000000L : 30585600000L;
        }
        return l2;
    }

    int getMinYear() {
        return 1;
    }

    int getMaxYear() {
        return 292271022;
    }

    long getApproxMillisAtEpochDividedByTwo() {
        return 21260793600000L;
    }

    protected void assemble(AssembledChronology.Fields fields) {
        if (this.getBase() == null) {
            super.assemble(fields);
            fields.era = ERA_FIELD;
            fields.monthOfYear = new BasicMonthOfYearDateTimeField(this, 12);
            fields.months = fields.monthOfYear.getDurationField();
        }
    }

    public static class LeapYearPatternType
    implements Serializable {
        private static final long serialVersionUID = 26581275372698L;
        final byte index;
        final int pattern;

        LeapYearPatternType(int n, int n2) {
            this.index = (byte)n;
            this.pattern = n2;
        }

        boolean isLeapYear(int n) {
            int n2 = 1 << n % 30;
            return (this.pattern & n2) > 0;
        }

        private Object readResolve() {
            switch (this.index) {
                case 0: {
                    return LEAP_YEAR_15_BASED;
                }
                case 1: {
                    return LEAP_YEAR_16_BASED;
                }
                case 2: {
                    return LEAP_YEAR_INDIAN;
                }
                case 3: {
                    return LEAP_YEAR_HABASH_AL_HASIB;
                }
            }
            return this;
        }

        public boolean equals(Object object) {
            if (object instanceof LeapYearPatternType) {
                return this.index == ((LeapYearPatternType)object).index;
            }
            return false;
        }

        public int hashCode() {
            return this.index;
        }
    }
}

