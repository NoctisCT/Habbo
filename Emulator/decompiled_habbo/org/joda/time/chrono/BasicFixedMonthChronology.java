/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.Chronology;
import org.joda.time.chrono.BasicChronology;

abstract class BasicFixedMonthChronology
extends BasicChronology {
    private static final long serialVersionUID = 261387371998L;
    static final int MONTH_LENGTH = 30;
    static final long MILLIS_PER_YEAR = 31557600000L;
    static final long MILLIS_PER_MONTH = 2592000000L;

    BasicFixedMonthChronology(Chronology chronology, Object object, int n) {
        super(chronology, object, n);
    }

    long setYear(long l, int n) {
        int n2 = this.getYear(l);
        int n3 = this.getDayOfYear(l, n2);
        int n4 = this.getMillisOfDay(l);
        if (n3 > 365 && !this.isLeapYear(n)) {
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
        return (long)(n2 - 1) * 2592000000L;
    }

    int getDayOfMonth(long l) {
        return (this.getDayOfYear(l) - 1) % 30 + 1;
    }

    boolean isLeapYear(int n) {
        return (n & 3) == 3;
    }

    int getDaysInYearMonth(int n, int n2) {
        return n2 != 13 ? 30 : (this.isLeapYear(n) ? 6 : 5);
    }

    int getDaysInMonthMax() {
        return 30;
    }

    int getDaysInMonthMax(int n) {
        return n != 13 ? 30 : 6;
    }

    int getMonthOfYear(long l) {
        return (this.getDayOfYear(l) - 1) / 30 + 1;
    }

    int getMonthOfYear(long l, int n) {
        long l2 = (l - this.getYearMillis(n)) / 2592000000L;
        return (int)l2 + 1;
    }

    int getMaxMonth() {
        return 13;
    }

    long getAverageMillisPerYear() {
        return 31557600000L;
    }

    long getAverageMillisPerYearDividedByTwo() {
        return 15778800000L;
    }

    long getAverageMillisPerMonth() {
        return 2592000000L;
    }
}

