/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.base;

import java.io.Serializable;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.MutablePeriod;
import org.joda.time.PeriodType;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.AbstractPeriod;
import org.joda.time.base.BaseLocal;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.PeriodConverter;
import org.joda.time.field.FieldUtils;

public abstract class BasePeriod
extends AbstractPeriod
implements ReadablePeriod,
Serializable {
    private static final long serialVersionUID = -2110953284060001145L;
    private static final ReadablePeriod DUMMY_PERIOD = new AbstractPeriod(){

        public int getValue(int n) {
            return 0;
        }

        public PeriodType getPeriodType() {
            return PeriodType.time();
        }
    };
    private final PeriodType iType;
    private final int[] iValues;

    protected BasePeriod(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, PeriodType periodType) {
        this.iType = periodType = this.checkPeriodType(periodType);
        this.iValues = this.setPeriodInternal(n, n2, n3, n4, n5, n6, n7, n8);
    }

    protected BasePeriod(long l, long l2, PeriodType periodType, Chronology chronology) {
        periodType = this.checkPeriodType(periodType);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iType = periodType;
        this.iValues = chronology.get(this, l, l2);
    }

    protected BasePeriod(ReadableInstant readableInstant, ReadableInstant readableInstant2, PeriodType periodType) {
        periodType = this.checkPeriodType(periodType);
        if (readableInstant == null && readableInstant2 == null) {
            this.iType = periodType;
            this.iValues = new int[this.size()];
        } else {
            long l = DateTimeUtils.getInstantMillis(readableInstant);
            long l2 = DateTimeUtils.getInstantMillis(readableInstant2);
            Chronology chronology = DateTimeUtils.getIntervalChronology(readableInstant, readableInstant2);
            this.iType = periodType;
            this.iValues = chronology.get(this, l, l2);
        }
    }

    protected BasePeriod(ReadablePartial readablePartial, ReadablePartial readablePartial2, PeriodType periodType) {
        if (readablePartial == null || readablePartial2 == null) {
            throw new IllegalArgumentException("ReadablePartial objects must not be null");
        }
        if (readablePartial instanceof BaseLocal && readablePartial2 instanceof BaseLocal && readablePartial.getClass() == readablePartial2.getClass()) {
            periodType = this.checkPeriodType(periodType);
            long l = ((BaseLocal)readablePartial).getLocalMillis();
            long l2 = ((BaseLocal)readablePartial2).getLocalMillis();
            Chronology chronology = readablePartial.getChronology();
            chronology = DateTimeUtils.getChronology(chronology);
            this.iType = periodType;
            this.iValues = chronology.get(this, l, l2);
        } else {
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
            this.iType = this.checkPeriodType(periodType);
            Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology()).withUTC();
            this.iValues = chronology.get(this, chronology.set(readablePartial, 0L), chronology.set(readablePartial2, 0L));
        }
    }

    protected BasePeriod(ReadableInstant readableInstant, ReadableDuration readableDuration, PeriodType periodType) {
        periodType = this.checkPeriodType(periodType);
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        long l2 = DateTimeUtils.getDurationMillis(readableDuration);
        long l3 = FieldUtils.safeAdd(l, l2);
        Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iType = periodType;
        this.iValues = chronology.get(this, l, l3);
    }

    protected BasePeriod(ReadableDuration readableDuration, ReadableInstant readableInstant, PeriodType periodType) {
        periodType = this.checkPeriodType(periodType);
        long l = DateTimeUtils.getDurationMillis(readableDuration);
        long l2 = DateTimeUtils.getInstantMillis(readableInstant);
        long l3 = FieldUtils.safeSubtract(l2, l);
        Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iType = periodType;
        this.iValues = chronology.get(this, l3, l2);
    }

    protected BasePeriod(long l) {
        this.iType = PeriodType.standard();
        int[] nArray = ISOChronology.getInstanceUTC().get(DUMMY_PERIOD, l);
        this.iValues = new int[8];
        System.arraycopy(nArray, 0, this.iValues, 4, 4);
    }

    protected BasePeriod(long l, PeriodType periodType, Chronology chronology) {
        periodType = this.checkPeriodType(periodType);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iType = periodType;
        this.iValues = chronology.get(this, l);
    }

    protected BasePeriod(Object object, PeriodType periodType, Chronology chronology) {
        PeriodConverter periodConverter = ConverterManager.getInstance().getPeriodConverter(object);
        periodType = periodType == null ? periodConverter.getPeriodType(object) : periodType;
        this.iType = periodType = this.checkPeriodType(periodType);
        if (this instanceof ReadWritablePeriod) {
            this.iValues = new int[this.size()];
            chronology = DateTimeUtils.getChronology(chronology);
            periodConverter.setInto((ReadWritablePeriod)((Object)this), object, chronology);
        } else {
            this.iValues = new MutablePeriod(object, periodType, chronology).getValues();
        }
    }

    protected BasePeriod(int[] nArray, PeriodType periodType) {
        this.iType = periodType;
        this.iValues = nArray;
    }

    protected PeriodType checkPeriodType(PeriodType periodType) {
        return DateTimeUtils.getPeriodType(periodType);
    }

    public PeriodType getPeriodType() {
        return this.iType;
    }

    public int getValue(int n) {
        return this.iValues[n];
    }

    public Duration toDurationFrom(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
        long l2 = chronology.add(this, l, 1);
        return new Duration(l, l2);
    }

    public Duration toDurationTo(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
        long l2 = chronology.add(this, l, -1);
        return new Duration(l2, l);
    }

    private void checkAndUpdate(DurationFieldType durationFieldType, int[] nArray, int n) {
        int n2 = this.indexOf(durationFieldType);
        if (n2 == -1) {
            if (n != 0) {
                throw new IllegalArgumentException("Period does not support field '" + durationFieldType.getName() + "'");
            }
        } else {
            nArray[n2] = n;
        }
    }

    protected void setPeriod(ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            this.setValues(new int[this.size()]);
        } else {
            this.setPeriodInternal(readablePeriod);
        }
    }

    private void setPeriodInternal(ReadablePeriod readablePeriod) {
        int[] nArray = new int[this.size()];
        int n = readablePeriod.size();
        for (int i = 0; i < n; ++i) {
            DurationFieldType durationFieldType = readablePeriod.getFieldType(i);
            int n2 = readablePeriod.getValue(i);
            this.checkAndUpdate(durationFieldType, nArray, n2);
        }
        this.setValues(nArray);
    }

    protected void setPeriod(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        int[] nArray = this.setPeriodInternal(n, n2, n3, n4, n5, n6, n7, n8);
        this.setValues(nArray);
    }

    private int[] setPeriodInternal(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        int[] nArray = new int[this.size()];
        this.checkAndUpdate(DurationFieldType.years(), nArray, n);
        this.checkAndUpdate(DurationFieldType.months(), nArray, n2);
        this.checkAndUpdate(DurationFieldType.weeks(), nArray, n3);
        this.checkAndUpdate(DurationFieldType.days(), nArray, n4);
        this.checkAndUpdate(DurationFieldType.hours(), nArray, n5);
        this.checkAndUpdate(DurationFieldType.minutes(), nArray, n6);
        this.checkAndUpdate(DurationFieldType.seconds(), nArray, n7);
        this.checkAndUpdate(DurationFieldType.millis(), nArray, n8);
        return nArray;
    }

    protected void setField(DurationFieldType durationFieldType, int n) {
        this.setFieldInto(this.iValues, durationFieldType, n);
    }

    protected void setFieldInto(int[] nArray, DurationFieldType durationFieldType, int n) {
        int n2 = this.indexOf(durationFieldType);
        if (n2 == -1) {
            if (n != 0 || durationFieldType == null) {
                throw new IllegalArgumentException("Period does not support field '" + durationFieldType + "'");
            }
        } else {
            nArray[n2] = n;
        }
    }

    protected void addField(DurationFieldType durationFieldType, int n) {
        this.addFieldInto(this.iValues, durationFieldType, n);
    }

    protected void addFieldInto(int[] nArray, DurationFieldType durationFieldType, int n) {
        int n2 = this.indexOf(durationFieldType);
        if (n2 == -1) {
            if (n != 0 || durationFieldType == null) {
                throw new IllegalArgumentException("Period does not support field '" + durationFieldType + "'");
            }
        } else {
            nArray[n2] = FieldUtils.safeAdd(nArray[n2], n);
        }
    }

    protected void mergePeriod(ReadablePeriod readablePeriod) {
        if (readablePeriod != null) {
            this.setValues(this.mergePeriodInto(this.getValues(), readablePeriod));
        }
    }

    protected int[] mergePeriodInto(int[] nArray, ReadablePeriod readablePeriod) {
        int n = readablePeriod.size();
        for (int i = 0; i < n; ++i) {
            DurationFieldType durationFieldType = readablePeriod.getFieldType(i);
            int n2 = readablePeriod.getValue(i);
            this.checkAndUpdate(durationFieldType, nArray, n2);
        }
        return nArray;
    }

    protected void addPeriod(ReadablePeriod readablePeriod) {
        if (readablePeriod != null) {
            this.setValues(this.addPeriodInto(this.getValues(), readablePeriod));
        }
    }

    protected int[] addPeriodInto(int[] nArray, ReadablePeriod readablePeriod) {
        int n = readablePeriod.size();
        for (int i = 0; i < n; ++i) {
            DurationFieldType durationFieldType = readablePeriod.getFieldType(i);
            int n2 = readablePeriod.getValue(i);
            if (n2 == 0) continue;
            int n3 = this.indexOf(durationFieldType);
            if (n3 == -1) {
                throw new IllegalArgumentException("Period does not support field '" + durationFieldType.getName() + "'");
            }
            nArray[n3] = FieldUtils.safeAdd(this.getValue(n3), n2);
        }
        return nArray;
    }

    protected void setValue(int n, int n2) {
        this.iValues[n] = n2;
    }

    protected void setValues(int[] nArray) {
        System.arraycopy(nArray, 0, this.iValues, 0, this.iValues.length);
    }
}

