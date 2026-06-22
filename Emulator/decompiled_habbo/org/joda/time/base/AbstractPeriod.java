/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.ToString
 */
package org.joda.time.base;

import org.joda.convert.ToString;
import org.joda.time.DurationFieldType;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

public abstract class AbstractPeriod
implements ReadablePeriod {
    protected AbstractPeriod() {
    }

    public int size() {
        return this.getPeriodType().size();
    }

    public DurationFieldType getFieldType(int n) {
        return this.getPeriodType().getFieldType(n);
    }

    public DurationFieldType[] getFieldTypes() {
        DurationFieldType[] durationFieldTypeArray = new DurationFieldType[this.size()];
        for (int i = 0; i < durationFieldTypeArray.length; ++i) {
            durationFieldTypeArray[i] = this.getFieldType(i);
        }
        return durationFieldTypeArray;
    }

    public int[] getValues() {
        int[] nArray = new int[this.size()];
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = this.getValue(i);
        }
        return nArray;
    }

    public int get(DurationFieldType durationFieldType) {
        int n = this.indexOf(durationFieldType);
        if (n == -1) {
            return 0;
        }
        return this.getValue(n);
    }

    public boolean isSupported(DurationFieldType durationFieldType) {
        return this.getPeriodType().isSupported(durationFieldType);
    }

    public int indexOf(DurationFieldType durationFieldType) {
        return this.getPeriodType().indexOf(durationFieldType);
    }

    public Period toPeriod() {
        return new Period(this);
    }

    public MutablePeriod toMutablePeriod() {
        return new MutablePeriod(this);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ReadablePeriod)) {
            return false;
        }
        ReadablePeriod readablePeriod = (ReadablePeriod)object;
        if (this.size() != readablePeriod.size()) {
            return false;
        }
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            if (this.getValue(i) == readablePeriod.getValue(i) && this.getFieldType(i) == readablePeriod.getFieldType(i)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int n = 17;
        int n2 = this.size();
        for (int i = 0; i < n2; ++i) {
            n = 27 * n + this.getValue(i);
            n = 27 * n + this.getFieldType(i).hashCode();
        }
        return n;
    }

    @ToString
    public String toString() {
        return ISOPeriodFormat.standard().print(this);
    }

    public String toString(PeriodFormatter periodFormatter) {
        if (periodFormatter == null) {
            return this.toString();
        }
        return periodFormatter.print(this);
    }
}

