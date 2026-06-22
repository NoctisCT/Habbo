/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.FromString
 */
package org.joda.time;

import java.io.Serializable;
import org.joda.convert.FromString;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BasePeriod;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

public class MutablePeriod
extends BasePeriod
implements ReadWritablePeriod,
Cloneable,
Serializable {
    private static final long serialVersionUID = 3436451121567212165L;

    @FromString
    public static MutablePeriod parse(String string) {
        return MutablePeriod.parse(string, ISOPeriodFormat.standard());
    }

    public static MutablePeriod parse(String string, PeriodFormatter periodFormatter) {
        return periodFormatter.parsePeriod(string).toMutablePeriod();
    }

    public MutablePeriod() {
        super(0L, (PeriodType)null, (Chronology)null);
    }

    public MutablePeriod(PeriodType periodType) {
        super(0L, periodType, (Chronology)null);
    }

    public MutablePeriod(int n, int n2, int n3, int n4) {
        super(0, 0, 0, 0, n, n2, n3, n4, PeriodType.standard());
    }

    public MutablePeriod(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        super(n, n2, n3, n4, n5, n6, n7, n8, PeriodType.standard());
    }

    public MutablePeriod(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, PeriodType periodType) {
        super(n, n2, n3, n4, n5, n6, n7, n8, periodType);
    }

    public MutablePeriod(long l) {
        super(l);
    }

    public MutablePeriod(long l, PeriodType periodType) {
        super(l, periodType, (Chronology)null);
    }

    public MutablePeriod(long l, Chronology chronology) {
        super(l, (PeriodType)null, chronology);
    }

    public MutablePeriod(long l, PeriodType periodType, Chronology chronology) {
        super(l, periodType, chronology);
    }

    public MutablePeriod(long l, long l2) {
        super(l, l2, null, null);
    }

    public MutablePeriod(long l, long l2, PeriodType periodType) {
        super(l, l2, periodType, null);
    }

    public MutablePeriod(long l, long l2, Chronology chronology) {
        super(l, l2, null, chronology);
    }

    public MutablePeriod(long l, long l2, PeriodType periodType, Chronology chronology) {
        super(l, l2, periodType, chronology);
    }

    public MutablePeriod(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        super(readableInstant, readableInstant2, null);
    }

    public MutablePeriod(ReadableInstant readableInstant, ReadableInstant readableInstant2, PeriodType periodType) {
        super(readableInstant, readableInstant2, periodType);
    }

    public MutablePeriod(ReadableInstant readableInstant, ReadableDuration readableDuration) {
        super(readableInstant, readableDuration, null);
    }

    public MutablePeriod(ReadableInstant readableInstant, ReadableDuration readableDuration, PeriodType periodType) {
        super(readableInstant, readableDuration, periodType);
    }

    public MutablePeriod(ReadableDuration readableDuration, ReadableInstant readableInstant) {
        super(readableDuration, readableInstant, null);
    }

    public MutablePeriod(ReadableDuration readableDuration, ReadableInstant readableInstant, PeriodType periodType) {
        super(readableDuration, readableInstant, periodType);
    }

    public MutablePeriod(Object object) {
        super(object, null, null);
    }

    public MutablePeriod(Object object, PeriodType periodType) {
        super(object, periodType, null);
    }

    public MutablePeriod(Object object, Chronology chronology) {
        super(object, null, chronology);
    }

    public MutablePeriod(Object object, PeriodType periodType, Chronology chronology) {
        super(object, periodType, chronology);
    }

    public void clear() {
        super.setValues(new int[this.size()]);
    }

    public void setValue(int n, int n2) {
        super.setValue(n, n2);
    }

    public void set(DurationFieldType durationFieldType, int n) {
        super.setField(durationFieldType, n);
    }

    public void setPeriod(ReadablePeriod readablePeriod) {
        super.setPeriod(readablePeriod);
    }

    public void setPeriod(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        super.setPeriod(n, n2, n3, n4, n5, n6, n7, n8);
    }

    public void setPeriod(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            this.setPeriod(0L);
        } else {
            Chronology chronology = DateTimeUtils.getChronology(readableInterval.getChronology());
            this.setPeriod(readableInterval.getStartMillis(), readableInterval.getEndMillis(), chronology);
        }
    }

    public void setPeriod(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        if (readableInstant == readableInstant2) {
            this.setPeriod(0L);
        } else {
            long l = DateTimeUtils.getInstantMillis(readableInstant);
            long l2 = DateTimeUtils.getInstantMillis(readableInstant2);
            Chronology chronology = DateTimeUtils.getIntervalChronology(readableInstant, readableInstant2);
            this.setPeriod(l, l2, chronology);
        }
    }

    public void setPeriod(long l, long l2) {
        this.setPeriod(l, l2, null);
    }

    public void setPeriod(long l, long l2, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        this.setValues(chronology.get(this, l, l2));
    }

    public void setPeriod(ReadableDuration readableDuration) {
        this.setPeriod(readableDuration, null);
    }

    public void setPeriod(ReadableDuration readableDuration, Chronology chronology) {
        long l = DateTimeUtils.getDurationMillis(readableDuration);
        this.setPeriod(l, chronology);
    }

    public void setPeriod(long l) {
        this.setPeriod(l, null);
    }

    public void setPeriod(long l, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        this.setValues(chronology.get(this, l));
    }

    public void add(DurationFieldType durationFieldType, int n) {
        super.addField(durationFieldType, n);
    }

    public void add(ReadablePeriod readablePeriod) {
        super.addPeriod(readablePeriod);
    }

    public void add(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        this.setPeriod(FieldUtils.safeAdd(this.getYears(), n), FieldUtils.safeAdd(this.getMonths(), n2), FieldUtils.safeAdd(this.getWeeks(), n3), FieldUtils.safeAdd(this.getDays(), n4), FieldUtils.safeAdd(this.getHours(), n5), FieldUtils.safeAdd(this.getMinutes(), n6), FieldUtils.safeAdd(this.getSeconds(), n7), FieldUtils.safeAdd(this.getMillis(), n8));
    }

    public void add(ReadableInterval readableInterval) {
        if (readableInterval != null) {
            this.add(readableInterval.toPeriod(this.getPeriodType()));
        }
    }

    public void add(ReadableDuration readableDuration) {
        if (readableDuration != null) {
            this.add(new Period(readableDuration.getMillis(), this.getPeriodType()));
        }
    }

    public void add(long l) {
        this.add(new Period(l, this.getPeriodType()));
    }

    public void add(long l, Chronology chronology) {
        this.add(new Period(l, this.getPeriodType(), chronology));
    }

    public void mergePeriod(ReadablePeriod readablePeriod) {
        super.mergePeriod(readablePeriod);
    }

    public int getYears() {
        return this.getPeriodType().getIndexedField(this, PeriodType.YEAR_INDEX);
    }

    public int getMonths() {
        return this.getPeriodType().getIndexedField(this, PeriodType.MONTH_INDEX);
    }

    public int getWeeks() {
        return this.getPeriodType().getIndexedField(this, PeriodType.WEEK_INDEX);
    }

    public int getDays() {
        return this.getPeriodType().getIndexedField(this, PeriodType.DAY_INDEX);
    }

    public int getHours() {
        return this.getPeriodType().getIndexedField(this, PeriodType.HOUR_INDEX);
    }

    public int getMinutes() {
        return this.getPeriodType().getIndexedField(this, PeriodType.MINUTE_INDEX);
    }

    public int getSeconds() {
        return this.getPeriodType().getIndexedField(this, PeriodType.SECOND_INDEX);
    }

    public int getMillis() {
        return this.getPeriodType().getIndexedField(this, PeriodType.MILLI_INDEX);
    }

    public void setYears(int n) {
        super.setField(DurationFieldType.years(), n);
    }

    public void addYears(int n) {
        super.addField(DurationFieldType.years(), n);
    }

    public void setMonths(int n) {
        super.setField(DurationFieldType.months(), n);
    }

    public void addMonths(int n) {
        super.addField(DurationFieldType.months(), n);
    }

    public void setWeeks(int n) {
        super.setField(DurationFieldType.weeks(), n);
    }

    public void addWeeks(int n) {
        super.addField(DurationFieldType.weeks(), n);
    }

    public void setDays(int n) {
        super.setField(DurationFieldType.days(), n);
    }

    public void addDays(int n) {
        super.addField(DurationFieldType.days(), n);
    }

    public void setHours(int n) {
        super.setField(DurationFieldType.hours(), n);
    }

    public void addHours(int n) {
        super.addField(DurationFieldType.hours(), n);
    }

    public void setMinutes(int n) {
        super.setField(DurationFieldType.minutes(), n);
    }

    public void addMinutes(int n) {
        super.addField(DurationFieldType.minutes(), n);
    }

    public void setSeconds(int n) {
        super.setField(DurationFieldType.seconds(), n);
    }

    public void addSeconds(int n) {
        super.addField(DurationFieldType.seconds(), n);
    }

    public void setMillis(int n) {
        super.setField(DurationFieldType.millis(), n);
    }

    public void addMillis(int n) {
        super.addField(DurationFieldType.millis(), n);
    }

    public MutablePeriod copy() {
        return (MutablePeriod)this.clone();
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError("Clone error");
        }
    }
}

