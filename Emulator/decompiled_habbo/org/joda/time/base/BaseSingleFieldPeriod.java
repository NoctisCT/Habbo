/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.base;

import java.io.Serializable;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.FieldUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BaseSingleFieldPeriod
implements ReadablePeriod,
Comparable<BaseSingleFieldPeriod>,
Serializable {
    private static final long serialVersionUID = 9386874258972L;
    private static final long START_1972 = 63072000000L;
    private volatile int iPeriod;

    protected static int between(ReadableInstant readableInstant, ReadableInstant readableInstant2, DurationFieldType durationFieldType) {
        if (readableInstant == null || readableInstant2 == null) {
            throw new IllegalArgumentException("ReadableInstant objects must not be null");
        }
        Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
        int n = durationFieldType.getField(chronology).getDifference(readableInstant2.getMillis(), readableInstant.getMillis());
        return n;
    }

    protected static int between(ReadablePartial readablePartial, ReadablePartial readablePartial2, ReadablePeriod readablePeriod) {
        if (readablePartial == null || readablePartial2 == null) {
            throw new IllegalArgumentException("ReadablePartial objects must not be null");
        }
        if (readablePartial.size() != readablePartial2.size()) {
            throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
        }
        int n = readablePartial.size();
        for (int i = 0; i < n; ++i) {
            if (readablePartial.getFieldType(i) == readablePartial2.getFieldType(i)) continue;
            throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
        }
        if (!DateTimeUtils.isContiguous(readablePartial)) {
            throw new IllegalArgumentException("ReadablePartial objects must be contiguous");
        }
        Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology()).withUTC();
        int[] nArray = chronology.get(readablePeriod, chronology.set(readablePartial, 63072000000L), chronology.set(readablePartial2, 63072000000L));
        return nArray[0];
    }

    protected static int standardPeriodIn(ReadablePeriod readablePeriod, long l) {
        if (readablePeriod == null) {
            return 0;
        }
        ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
        long l2 = 0L;
        for (int i = 0; i < readablePeriod.size(); ++i) {
            int n = readablePeriod.getValue(i);
            if (n == 0) continue;
            DurationField durationField = readablePeriod.getFieldType(i).getField(iSOChronology);
            if (!durationField.isPrecise()) {
                throw new IllegalArgumentException("Cannot convert period to duration as " + durationField.getName() + " is not precise in the period " + readablePeriod);
            }
            l2 = FieldUtils.safeAdd(l2, FieldUtils.safeMultiply(durationField.getUnitMillis(), n));
        }
        return FieldUtils.safeToInt(l2 / l);
    }

    protected BaseSingleFieldPeriod(int n) {
        this.iPeriod = n;
    }

    protected int getValue() {
        return this.iPeriod;
    }

    protected void setValue(int n) {
        this.iPeriod = n;
    }

    public abstract DurationFieldType getFieldType();

    @Override
    public abstract PeriodType getPeriodType();

    @Override
    public int size() {
        return 1;
    }

    @Override
    public DurationFieldType getFieldType(int n) {
        if (n != 0) {
            throw new IndexOutOfBoundsException(String.valueOf(n));
        }
        return this.getFieldType();
    }

    @Override
    public int getValue(int n) {
        if (n != 0) {
            throw new IndexOutOfBoundsException(String.valueOf(n));
        }
        return this.getValue();
    }

    @Override
    public int get(DurationFieldType durationFieldType) {
        if (durationFieldType == this.getFieldType()) {
            return this.getValue();
        }
        return 0;
    }

    @Override
    public boolean isSupported(DurationFieldType durationFieldType) {
        return durationFieldType == this.getFieldType();
    }

    @Override
    public Period toPeriod() {
        return Period.ZERO.withFields(this);
    }

    @Override
    public MutablePeriod toMutablePeriod() {
        MutablePeriod mutablePeriod = new MutablePeriod();
        mutablePeriod.add(this);
        return mutablePeriod;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ReadablePeriod)) {
            return false;
        }
        ReadablePeriod readablePeriod = (ReadablePeriod)object;
        return readablePeriod.getPeriodType() == this.getPeriodType() && readablePeriod.getValue(0) == this.getValue();
    }

    @Override
    public int hashCode() {
        int n = 17;
        n = 27 * n + this.getValue();
        n = 27 * n + this.getFieldType().hashCode();
        return n;
    }

    @Override
    public int compareTo(BaseSingleFieldPeriod baseSingleFieldPeriod) {
        if (baseSingleFieldPeriod.getClass() != this.getClass()) {
            throw new ClassCastException(this.getClass() + " cannot be compared to " + baseSingleFieldPeriod.getClass());
        }
        int n = baseSingleFieldPeriod.getValue();
        int n2 = this.getValue();
        if (n2 > n) {
            return 1;
        }
        if (n2 < n) {
            return -1;
        }
        return 0;
    }
}

