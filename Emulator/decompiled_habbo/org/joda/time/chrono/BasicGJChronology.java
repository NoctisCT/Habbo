/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.Chronology;
import org.joda.time.chrono.BasicChronology;

abstract class BasicGJChronology
extends BasicChronology {
    private static final long serialVersionUID = 538276888268L;
    private static final int[] MIN_DAYS_PER_MONTH_ARRAY = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int[] MAX_DAYS_PER_MONTH_ARRAY = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final long[] MIN_TOTAL_MILLIS_BY_MONTH_ARRAY = new long[12];
    private static final long[] MAX_TOTAL_MILLIS_BY_MONTH_ARRAY = new long[12];
    private static final long FEB_29 = 5097600000L;

    BasicGJChronology(Chronology chronology, Object object, int n) {
        super(chronology, object, n);
    }

    boolean isLeapDay(long l) {
        return this.dayOfMonth().get(l) == 29 && this.monthOfYear().isLeap(l);
    }

    int getMonthOfYear(long l, int n) {
        int n2 = (int)(l - this.getYearMillis(n) >> 10);
        return this.isLeapYear(n) ? (n2 < 15356250 ? (n2 < 7678125 ? (n2 < 2615625 ? 1 : (n2 < 5062500 ? 2 : 3)) : (n2 < 10209375 ? 4 : (n2 < 12825000 ? 5 : 6))) : (n2 < 23118750 ? (n2 < 17971875 ? 7 : (n2 < 20587500 ? 8 : 9)) : (n2 < 25734375 ? 10 : (n2 < 28265625 ? 11 : 12)))) : (n2 < 15271875 ? (n2 < 7593750 ? (n2 < 2615625 ? 1 : (n2 < 4978125 ? 2 : 3)) : (n2 < 10125000 ? 4 : (n2 < 12740625 ? 5 : 6))) : (n2 < 23034375 ? (n2 < 17887500 ? 7 : (n2 < 20503125 ? 8 : 9)) : (n2 < 25650000 ? 10 : (n2 < 28181250 ? 11 : 12))));
    }

    int getDaysInYearMonth(int n, int n2) {
        if (this.isLeapYear(n)) {
            return MAX_DAYS_PER_MONTH_ARRAY[n2 - 1];
        }
        return MIN_DAYS_PER_MONTH_ARRAY[n2 - 1];
    }

    int getDaysInMonthMax(int n) {
        return MAX_DAYS_PER_MONTH_ARRAY[n - 1];
    }

    int getDaysInMonthMaxForSet(long l, int n) {
        return n > 28 || n < 1 ? this.getDaysInMonthMax(l) : 28;
    }

    long getTotalMillisByYearMonth(int n, int n2) {
        if (this.isLeapYear(n)) {
            return MAX_TOTAL_MILLIS_BY_MONTH_ARRAY[n2 - 1];
        }
        return MIN_TOTAL_MILLIS_BY_MONTH_ARRAY[n2 - 1];
    }

    long getYearDifference(long l, long l2) {
        int n = this.getYear(l);
        int n2 = this.getYear(l2);
        long l3 = l - this.getYearMillis(n);
        long l4 = l2 - this.getYearMillis(n2);
        if (l4 >= 5097600000L) {
            if (this.isLeapYear(n2)) {
                if (!this.isLeapYear(n)) {
                    l4 -= 86400000L;
                }
            } else if (l3 >= 5097600000L && this.isLeapYear(n)) {
                l3 -= 86400000L;
            }
        }
        int n3 = n - n2;
        if (l3 < l4) {
            --n3;
        }
        return n3;
    }

    long setYear(long l, int n) {
        int n2 = this.getYear(l);
        int n3 = this.getDayOfYear(l, n2);
        int n4 = this.getMillisOfDay(l);
        if (n3 > 59) {
            if (this.isLeapYear(n2)) {
                if (!this.isLeapYear(n)) {
                    --n3;
                }
            } else if (this.isLeapYear(n)) {
                ++n3;
            }
        }
        l = this.getYearMonthDayMillis(n, 1, n3);
        return l += (long)n4;
    }

    static {
        long l = 0L;
        long l2 = 0L;
        for (int i = 0; i < 11; ++i) {
            long l3 = (long)MIN_DAYS_PER_MONTH_ARRAY[i] * 86400000L;
            BasicGJChronology.MIN_TOTAL_MILLIS_BY_MONTH_ARRAY[i + 1] = l += l3;
            l3 = (long)MAX_DAYS_PER_MONTH_ARRAY[i] * 86400000L;
            BasicGJChronology.MAX_TOTAL_MILLIS_BY_MONTH_ARRAY[i + 1] = l2 += l3;
        }
    }
}

