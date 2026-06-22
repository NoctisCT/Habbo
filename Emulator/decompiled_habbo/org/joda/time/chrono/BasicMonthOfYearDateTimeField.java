/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.ImpreciseDateTimeField;

class BasicMonthOfYearDateTimeField
extends ImpreciseDateTimeField {
    private static final long serialVersionUID = -8258715387168736L;
    private static final int MIN = 1;
    private final BasicChronology iChronology;
    private final int iMax;
    private final int iLeapMonth;

    BasicMonthOfYearDateTimeField(BasicChronology basicChronology, int n) {
        super(DateTimeFieldType.monthOfYear(), basicChronology.getAverageMillisPerMonth());
        this.iChronology = basicChronology;
        this.iMax = this.iChronology.getMaxMonth();
        this.iLeapMonth = n;
    }

    public boolean isLenient() {
        return false;
    }

    public int get(long l) {
        return this.iChronology.getMonthOfYear(l);
    }

    public long add(long l, int n) {
        int n2;
        if (n == 0) {
            return l;
        }
        long l2 = this.iChronology.getMillisOfDay(l);
        int n3 = this.iChronology.getYear(l);
        int n4 = this.iChronology.getMonthOfYear(l, n3);
        int n5 = n3;
        int n6 = n4 - 1 + n;
        if (n4 > 0 && n6 < 0) {
            if (Math.signum(n + this.iMax) == Math.signum(n)) {
                --n5;
                n += this.iMax;
            } else {
                ++n5;
                n -= this.iMax;
            }
            n6 = n4 - 1 + n;
        }
        if (n6 >= 0) {
            n5 += n6 / this.iMax;
            n6 = n6 % this.iMax + 1;
        } else {
            n5 = n5 + n6 / this.iMax - 1;
            n2 = (n6 = Math.abs(n6)) % this.iMax;
            if (n2 == 0) {
                n2 = this.iMax;
            }
            if ((n6 = this.iMax - n2 + 1) == 1) {
                ++n5;
            }
        }
        n2 = this.iChronology.getDayOfMonth(l, n3, n4);
        int n7 = this.iChronology.getDaysInYearMonth(n5, n6);
        if (n2 > n7) {
            n2 = n7;
        }
        long l3 = this.iChronology.getYearMonthDayMillis(n5, n6, n2);
        return l3 + l2;
    }

    public long add(long l, long l2) {
        int n;
        int n2;
        long l3;
        int n3 = (int)l2;
        if ((long)n3 == l2) {
            return this.add(l, n3);
        }
        long l4 = this.iChronology.getMillisOfDay(l);
        int n4 = this.iChronology.getYear(l);
        int n5 = this.iChronology.getMonthOfYear(l, n4);
        long l5 = (long)(n5 - 1) + l2;
        if (l5 >= 0L) {
            l3 = (long)n4 + l5 / (long)this.iMax;
            l5 = l5 % (long)this.iMax + 1L;
        } else {
            l3 = (long)n4 + l5 / (long)this.iMax - 1L;
            n2 = (int)((l5 = Math.abs(l5)) % (long)this.iMax);
            if (n2 == 0) {
                n2 = this.iMax;
            }
            if ((l5 = (long)(this.iMax - n2 + 1)) == 1L) {
                ++l3;
            }
        }
        if (l3 < (long)this.iChronology.getMinYear() || l3 > (long)this.iChronology.getMaxYear()) {
            throw new IllegalArgumentException("Magnitude of add amount is too large: " + l2);
        }
        n2 = (int)l3;
        int n6 = (int)l5;
        int n7 = this.iChronology.getDayOfMonth(l, n4, n5);
        if (n7 > (n = this.iChronology.getDaysInYearMonth(n2, n6))) {
            n7 = n;
        }
        long l6 = this.iChronology.getYearMonthDayMillis(n2, n6, n7);
        return l6 + l4;
    }

    public int[] add(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        if (n2 == 0) {
            return nArray;
        }
        if (readablePartial.size() > 0 && readablePartial.getFieldType(0).equals(DateTimeFieldType.monthOfYear()) && n == 0) {
            int n3 = nArray[0] - 1;
            int n4 = (n3 + n2 % 12 + 12) % 12 + 1;
            return this.set(readablePartial, 0, nArray, n4);
        }
        if (DateTimeUtils.isContiguous(readablePartial)) {
            long l = 0L;
            int n5 = readablePartial.size();
            for (int i = 0; i < n5; ++i) {
                l = readablePartial.getFieldType(i).getField(this.iChronology).set(l, nArray[i]);
            }
            l = this.add(l, n2);
            return this.iChronology.get(readablePartial, l);
        }
        return super.add(readablePartial, n, nArray, n2);
    }

    public long addWrapField(long l, int n) {
        return this.set(l, FieldUtils.getWrappedValue(this.get(l), n, 1, this.iMax));
    }

    public long getDifferenceAsLong(long l, long l2) {
        long l3;
        long l4;
        int n;
        if (l < l2) {
            return -this.getDifference(l2, l);
        }
        int n2 = this.iChronology.getYear(l);
        int n3 = this.iChronology.getMonthOfYear(l, n2);
        int n4 = this.iChronology.getYear(l2);
        int n5 = this.iChronology.getMonthOfYear(l2, n4);
        long l5 = (long)(n2 - n4) * (long)this.iMax + (long)n3 - (long)n5;
        int n6 = this.iChronology.getDayOfMonth(l, n2, n3);
        if (n6 == this.iChronology.getDaysInYearMonth(n2, n3) && (n = this.iChronology.getDayOfMonth(l2, n4, n5)) > n6) {
            l2 = this.iChronology.dayOfMonth().set(l2, n6);
        }
        if ((l4 = l - this.iChronology.getYearMonthMillis(n2, n3)) < (l3 = l2 - this.iChronology.getYearMonthMillis(n4, n5))) {
            --l5;
        }
        return l5;
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, 1, this.iMax);
        int n2 = this.iChronology.getYear(l);
        int n3 = this.iChronology.getDayOfMonth(l, n2);
        int n4 = this.iChronology.getDaysInYearMonth(n2, n);
        if (n3 > n4) {
            n3 = n4;
        }
        return this.iChronology.getYearMonthDayMillis(n2, n, n3) + (long)this.iChronology.getMillisOfDay(l);
    }

    public DurationField getRangeDurationField() {
        return this.iChronology.years();
    }

    public boolean isLeap(long l) {
        int n = this.iChronology.getYear(l);
        if (this.iChronology.isLeapYear(n)) {
            return this.iChronology.getMonthOfYear(l, n) == this.iLeapMonth;
        }
        return false;
    }

    public int getLeapAmount(long l) {
        return this.isLeap(l) ? 1 : 0;
    }

    public DurationField getLeapDurationField() {
        return this.iChronology.days();
    }

    public int getMinimumValue() {
        return 1;
    }

    public int getMaximumValue() {
        return this.iMax;
    }

    public long roundFloor(long l) {
        int n = this.iChronology.getYear(l);
        int n2 = this.iChronology.getMonthOfYear(l, n);
        return this.iChronology.getYearMonthMillis(n, n2);
    }

    public long remainder(long l) {
        return l - this.roundFloor(l);
    }

    private Object readResolve() {
        return this.iChronology.monthOfYear();
    }
}

