/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.ImpreciseDateTimeField;

final class BasicWeekyearDateTimeField
extends ImpreciseDateTimeField {
    private static final long serialVersionUID = 6215066916806820644L;
    private static final long WEEK_53 = 31449600000L;
    private final BasicChronology iChronology;

    BasicWeekyearDateTimeField(BasicChronology basicChronology) {
        super(DateTimeFieldType.weekyear(), basicChronology.getAverageMillisPerYear());
        this.iChronology = basicChronology;
    }

    public boolean isLenient() {
        return false;
    }

    public int get(long l) {
        return this.iChronology.getWeekyear(l);
    }

    public long add(long l, int n) {
        if (n == 0) {
            return l;
        }
        return this.set(l, this.get(l) + n);
    }

    public long add(long l, long l2) {
        return this.add(l, FieldUtils.safeToInt(l2));
    }

    public long addWrapField(long l, int n) {
        return this.add(l, n);
    }

    public long getDifferenceAsLong(long l, long l2) {
        if (l < l2) {
            return -this.getDifference(l2, l);
        }
        int n = this.get(l);
        int n2 = this.get(l2);
        long l3 = this.remainder(l);
        long l4 = this.remainder(l2);
        if (l4 >= 31449600000L && this.iChronology.getWeeksInYear(n) <= 52) {
            l4 -= 604800000L;
        }
        int n3 = n - n2;
        if (l3 < l4) {
            --n3;
        }
        return n3;
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, Math.abs(n), this.iChronology.getMinYear(), this.iChronology.getMaxYear());
        int n2 = this.get(l);
        if (n2 == n) {
            return l;
        }
        int n3 = this.iChronology.getDayOfWeek(l);
        int n4 = this.iChronology.getWeeksInYear(n2);
        int n5 = this.iChronology.getWeeksInYear(n);
        int n6 = n5 < n4 ? n5 : n4;
        int n7 = this.iChronology.getWeekOfWeekyear(l);
        if (n7 > n6) {
            n7 = n6;
        }
        long l2 = l;
        int n8 = this.get(l2 = this.iChronology.setYear(l2, n));
        if (n8 < n) {
            l2 += 604800000L;
        } else if (n8 > n) {
            l2 -= 604800000L;
        }
        int n9 = this.iChronology.getWeekOfWeekyear(l2);
        l2 += (long)(n7 - n9) * 604800000L;
        l2 = this.iChronology.dayOfWeek().set(l2, n3);
        return l2;
    }

    public DurationField getRangeDurationField() {
        return null;
    }

    public boolean isLeap(long l) {
        return this.iChronology.getWeeksInYear(this.iChronology.getWeekyear(l)) > 52;
    }

    public int getLeapAmount(long l) {
        return this.iChronology.getWeeksInYear(this.iChronology.getWeekyear(l)) - 52;
    }

    public DurationField getLeapDurationField() {
        return this.iChronology.weeks();
    }

    public int getMinimumValue() {
        return this.iChronology.getMinYear();
    }

    public int getMaximumValue() {
        return this.iChronology.getMaxYear();
    }

    public long roundFloor(long l) {
        l = this.iChronology.weekOfWeekyear().roundFloor(l);
        int n = this.iChronology.getWeekOfWeekyear(l);
        if (n > 1) {
            l -= 604800000L * (long)(n - 1);
        }
        return l;
    }

    public long remainder(long l) {
        return l - this.roundFloor(l);
    }

    private Object readResolve() {
        return this.iChronology.weekyear();
    }
}

