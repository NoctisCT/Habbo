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
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.PeriodType;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.base.BasePeriod;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

public final class Period
extends BasePeriod
implements ReadablePeriod,
Serializable {
    public static final Period ZERO = new Period();
    private static final long serialVersionUID = 741052353876488155L;

    @FromString
    public static Period parse(String string) {
        return Period.parse(string, ISOPeriodFormat.standard());
    }

    public static Period parse(String string, PeriodFormatter periodFormatter) {
        return periodFormatter.parsePeriod(string);
    }

    public static Period years(int n) {
        return new Period(new int[]{n, 0, 0, 0, 0, 0, 0, 0}, PeriodType.standard());
    }

    public static Period months(int n) {
        return new Period(new int[]{0, n, 0, 0, 0, 0, 0, 0}, PeriodType.standard());
    }

    public static Period weeks(int n) {
        return new Period(new int[]{0, 0, n, 0, 0, 0, 0, 0}, PeriodType.standard());
    }

    public static Period days(int n) {
        return new Period(new int[]{0, 0, 0, n, 0, 0, 0, 0}, PeriodType.standard());
    }

    public static Period hours(int n) {
        return new Period(new int[]{0, 0, 0, 0, n, 0, 0, 0}, PeriodType.standard());
    }

    public static Period minutes(int n) {
        return new Period(new int[]{0, 0, 0, 0, 0, n, 0, 0}, PeriodType.standard());
    }

    public static Period seconds(int n) {
        return new Period(new int[]{0, 0, 0, 0, 0, 0, n, 0}, PeriodType.standard());
    }

    public static Period millis(int n) {
        return new Period(new int[]{0, 0, 0, 0, 0, 0, 0, n}, PeriodType.standard());
    }

    public static Period fieldDifference(ReadablePartial readablePartial, ReadablePartial readablePartial2) {
        if (readablePartial == null || readablePartial2 == null) {
            throw new IllegalArgumentException("ReadablePartial objects must not be null");
        }
        if (readablePartial.size() != readablePartial2.size()) {
            throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
        }
        DurationFieldType[] durationFieldTypeArray = new DurationFieldType[readablePartial.size()];
        int[] nArray = new int[readablePartial.size()];
        int n = readablePartial.size();
        for (int i = 0; i < n; ++i) {
            if (readablePartial.getFieldType(i) != readablePartial2.getFieldType(i)) {
                throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
            }
            durationFieldTypeArray[i] = readablePartial.getFieldType(i).getDurationType();
            if (i > 0 && durationFieldTypeArray[i - 1] == durationFieldTypeArray[i]) {
                throw new IllegalArgumentException("ReadablePartial objects must not have overlapping fields");
            }
            nArray[i] = readablePartial2.getValue(i) - readablePartial.getValue(i);
        }
        return new Period(nArray, PeriodType.forFields(durationFieldTypeArray));
    }

    public Period() {
        super(0L, (PeriodType)null, (Chronology)null);
    }

    public Period(int n, int n2, int n3, int n4) {
        super(0, 0, 0, 0, n, n2, n3, n4, PeriodType.standard());
    }

    public Period(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        super(n, n2, n3, n4, n5, n6, n7, n8, PeriodType.standard());
    }

    public Period(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, PeriodType periodType) {
        super(n, n2, n3, n4, n5, n6, n7, n8, periodType);
    }

    public Period(long l) {
        super(l);
    }

    public Period(long l, PeriodType periodType) {
        super(l, periodType, (Chronology)null);
    }

    public Period(long l, Chronology chronology) {
        super(l, (PeriodType)null, chronology);
    }

    public Period(long l, PeriodType periodType, Chronology chronology) {
        super(l, periodType, chronology);
    }

    public Period(long l, long l2) {
        super(l, l2, null, null);
    }

    public Period(long l, long l2, PeriodType periodType) {
        super(l, l2, periodType, null);
    }

    public Period(long l, long l2, Chronology chronology) {
        super(l, l2, null, chronology);
    }

    public Period(long l, long l2, PeriodType periodType, Chronology chronology) {
        super(l, l2, periodType, chronology);
    }

    public Period(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        super(readableInstant, readableInstant2, null);
    }

    public Period(ReadableInstant readableInstant, ReadableInstant readableInstant2, PeriodType periodType) {
        super(readableInstant, readableInstant2, periodType);
    }

    public Period(ReadablePartial readablePartial, ReadablePartial readablePartial2) {
        super(readablePartial, readablePartial2, null);
    }

    public Period(ReadablePartial readablePartial, ReadablePartial readablePartial2, PeriodType periodType) {
        super(readablePartial, readablePartial2, periodType);
    }

    public Period(ReadableInstant readableInstant, ReadableDuration readableDuration) {
        super(readableInstant, readableDuration, null);
    }

    public Period(ReadableInstant readableInstant, ReadableDuration readableDuration, PeriodType periodType) {
        super(readableInstant, readableDuration, periodType);
    }

    public Period(ReadableDuration readableDuration, ReadableInstant readableInstant) {
        super(readableDuration, readableInstant, null);
    }

    public Period(ReadableDuration readableDuration, ReadableInstant readableInstant, PeriodType periodType) {
        super(readableDuration, readableInstant, periodType);
    }

    public Period(Object object) {
        super(object, null, null);
    }

    public Period(Object object, PeriodType periodType) {
        super(object, periodType, null);
    }

    public Period(Object object, Chronology chronology) {
        super(object, null, chronology);
    }

    public Period(Object object, PeriodType periodType, Chronology chronology) {
        super(object, periodType, chronology);
    }

    private Period(int[] nArray, PeriodType periodType) {
        super(nArray, periodType);
    }

    public Period toPeriod() {
        return this;
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

    public Period withPeriodType(PeriodType periodType) {
        if ((periodType = DateTimeUtils.getPeriodType(periodType)).equals(this.getPeriodType())) {
            return this;
        }
        return new Period((Object)this, periodType);
    }

    public Period withFields(ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = super.mergePeriodInto(nArray, readablePeriod);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withField(DurationFieldType durationFieldType, int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        int[] nArray = this.getValues();
        super.setFieldInto(nArray, durationFieldType, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withFieldAdded(DurationFieldType durationFieldType, int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        super.addFieldInto(nArray, durationFieldType, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withYears(int n) {
        int[] nArray = this.getValues();
        this.getPeriodType().setIndexedField(this, PeriodType.YEAR_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withMonths(int n) {
        int[] nArray = this.getValues();
        this.getPeriodType().setIndexedField(this, PeriodType.MONTH_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withWeeks(int n) {
        int[] nArray = this.getValues();
        this.getPeriodType().setIndexedField(this, PeriodType.WEEK_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withDays(int n) {
        int[] nArray = this.getValues();
        this.getPeriodType().setIndexedField(this, PeriodType.DAY_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withHours(int n) {
        int[] nArray = this.getValues();
        this.getPeriodType().setIndexedField(this, PeriodType.HOUR_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withMinutes(int n) {
        int[] nArray = this.getValues();
        this.getPeriodType().setIndexedField(this, PeriodType.MINUTE_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withSeconds(int n) {
        int[] nArray = this.getValues();
        this.getPeriodType().setIndexedField(this, PeriodType.SECOND_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period withMillis(int n) {
        int[] nArray = this.getValues();
        this.getPeriodType().setIndexedField(this, PeriodType.MILLI_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period plus(ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.YEAR_INDEX, nArray, readablePeriod.get(DurationFieldType.YEARS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.MONTH_INDEX, nArray, readablePeriod.get(DurationFieldType.MONTHS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.WEEK_INDEX, nArray, readablePeriod.get(DurationFieldType.WEEKS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.DAY_INDEX, nArray, readablePeriod.get(DurationFieldType.DAYS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.HOUR_INDEX, nArray, readablePeriod.get(DurationFieldType.HOURS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.MINUTE_INDEX, nArray, readablePeriod.get(DurationFieldType.MINUTES_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.SECOND_INDEX, nArray, readablePeriod.get(DurationFieldType.SECONDS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.MILLI_INDEX, nArray, readablePeriod.get(DurationFieldType.MILLIS_TYPE));
        return new Period(nArray, this.getPeriodType());
    }

    public Period plusYears(int n) {
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.YEAR_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period plusMonths(int n) {
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.MONTH_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period plusWeeks(int n) {
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.WEEK_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period plusDays(int n) {
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.DAY_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period plusHours(int n) {
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.HOUR_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period plusMinutes(int n) {
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.MINUTE_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period plusSeconds(int n) {
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.SECOND_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period plusMillis(int n) {
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.MILLI_INDEX, nArray, n);
        return new Period(nArray, this.getPeriodType());
    }

    public Period minus(ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            return this;
        }
        int[] nArray = this.getValues();
        this.getPeriodType().addIndexedField(this, PeriodType.YEAR_INDEX, nArray, -readablePeriod.get(DurationFieldType.YEARS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.MONTH_INDEX, nArray, -readablePeriod.get(DurationFieldType.MONTHS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.WEEK_INDEX, nArray, -readablePeriod.get(DurationFieldType.WEEKS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.DAY_INDEX, nArray, -readablePeriod.get(DurationFieldType.DAYS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.HOUR_INDEX, nArray, -readablePeriod.get(DurationFieldType.HOURS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.MINUTE_INDEX, nArray, -readablePeriod.get(DurationFieldType.MINUTES_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.SECOND_INDEX, nArray, -readablePeriod.get(DurationFieldType.SECONDS_TYPE));
        this.getPeriodType().addIndexedField(this, PeriodType.MILLI_INDEX, nArray, -readablePeriod.get(DurationFieldType.MILLIS_TYPE));
        return new Period(nArray, this.getPeriodType());
    }

    public Period minusYears(int n) {
        return this.plusYears(-n);
    }

    public Period minusMonths(int n) {
        return this.plusMonths(-n);
    }

    public Period minusWeeks(int n) {
        return this.plusWeeks(-n);
    }

    public Period minusDays(int n) {
        return this.plusDays(-n);
    }

    public Period minusHours(int n) {
        return this.plusHours(-n);
    }

    public Period minusMinutes(int n) {
        return this.plusMinutes(-n);
    }

    public Period minusSeconds(int n) {
        return this.plusSeconds(-n);
    }

    public Period minusMillis(int n) {
        return this.plusMillis(-n);
    }

    public Period multipliedBy(int n) {
        if (this == ZERO || n == 1) {
            return this;
        }
        int[] nArray = this.getValues();
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = FieldUtils.safeMultiply(nArray[i], n);
        }
        return new Period(nArray, this.getPeriodType());
    }

    public Period negated() {
        return this.multipliedBy(-1);
    }

    public Weeks toStandardWeeks() {
        this.checkYearsAndMonths("Weeks");
        long l = this.getMillis();
        l += (long)this.getSeconds() * 1000L;
        l += (long)this.getMinutes() * 60000L;
        l += (long)this.getHours() * 3600000L;
        long l2 = (long)this.getWeeks() + (l += (long)this.getDays() * 86400000L) / 604800000L;
        return Weeks.weeks(FieldUtils.safeToInt(l2));
    }

    public Days toStandardDays() {
        this.checkYearsAndMonths("Days");
        long l = this.getMillis();
        l += (long)this.getSeconds() * 1000L;
        l += (long)this.getMinutes() * 60000L;
        long l2 = (l += (long)this.getHours() * 3600000L) / 86400000L;
        l2 = FieldUtils.safeAdd(l2, (long)this.getDays());
        l2 = FieldUtils.safeAdd(l2, (long)this.getWeeks() * 7L);
        return Days.days(FieldUtils.safeToInt(l2));
    }

    public Hours toStandardHours() {
        this.checkYearsAndMonths("Hours");
        long l = this.getMillis();
        l += (long)this.getSeconds() * 1000L;
        long l2 = (l += (long)this.getMinutes() * 60000L) / 3600000L;
        l2 = FieldUtils.safeAdd(l2, (long)this.getHours());
        l2 = FieldUtils.safeAdd(l2, (long)this.getDays() * 24L);
        l2 = FieldUtils.safeAdd(l2, (long)this.getWeeks() * 168L);
        return Hours.hours(FieldUtils.safeToInt(l2));
    }

    public Minutes toStandardMinutes() {
        this.checkYearsAndMonths("Minutes");
        long l = this.getMillis();
        long l2 = (l += (long)this.getSeconds() * 1000L) / 60000L;
        l2 = FieldUtils.safeAdd(l2, (long)this.getMinutes());
        l2 = FieldUtils.safeAdd(l2, (long)this.getHours() * 60L);
        l2 = FieldUtils.safeAdd(l2, (long)this.getDays() * 1440L);
        l2 = FieldUtils.safeAdd(l2, (long)this.getWeeks() * 10080L);
        return Minutes.minutes(FieldUtils.safeToInt(l2));
    }

    public Seconds toStandardSeconds() {
        this.checkYearsAndMonths("Seconds");
        long l = this.getMillis() / 1000;
        l = FieldUtils.safeAdd(l, (long)this.getSeconds());
        l = FieldUtils.safeAdd(l, (long)this.getMinutes() * 60L);
        l = FieldUtils.safeAdd(l, (long)this.getHours() * 3600L);
        l = FieldUtils.safeAdd(l, (long)this.getDays() * 86400L);
        l = FieldUtils.safeAdd(l, (long)this.getWeeks() * 604800L);
        return Seconds.seconds(FieldUtils.safeToInt(l));
    }

    public Duration toStandardDuration() {
        this.checkYearsAndMonths("Duration");
        long l = this.getMillis();
        l += (long)this.getSeconds() * 1000L;
        l += (long)this.getMinutes() * 60000L;
        l += (long)this.getHours() * 3600000L;
        l += (long)this.getDays() * 86400000L;
        return new Duration(l += (long)this.getWeeks() * 604800000L);
    }

    private void checkYearsAndMonths(String string) {
        if (this.getMonths() != 0) {
            throw new UnsupportedOperationException("Cannot convert to " + string + " as this period contains months and months vary in length");
        }
        if (this.getYears() != 0) {
            throw new UnsupportedOperationException("Cannot convert to " + string + " as this period contains years and years vary in length");
        }
    }

    public Period normalizedStandard() {
        return this.normalizedStandard(PeriodType.standard());
    }

    public Period normalizedStandard(PeriodType periodType) {
        periodType = DateTimeUtils.getPeriodType(periodType);
        long l = this.getMillis();
        l += (long)this.getSeconds() * 1000L;
        l += (long)this.getMinutes() * 60000L;
        l += (long)this.getHours() * 3600000L;
        l += (long)this.getDays() * 86400000L;
        Period period = new Period(l += (long)this.getWeeks() * 604800000L, periodType, (Chronology)ISOChronology.getInstanceUTC());
        int n = this.getYears();
        int n2 = this.getMonths();
        if (n != 0 || n2 != 0) {
            int n3;
            long l2 = (long)n * 12L + (long)n2;
            if (periodType.isSupported(DurationFieldType.YEARS_TYPE)) {
                n3 = FieldUtils.safeToInt(l2 / 12L);
                period = period.withYears(n3);
                l2 -= (long)(n3 * 12);
            }
            if (periodType.isSupported(DurationFieldType.MONTHS_TYPE)) {
                n3 = FieldUtils.safeToInt(l2);
                period = period.withMonths(n3);
                l2 -= (long)n3;
            }
            if (l2 != 0L) {
                throw new UnsupportedOperationException("Unable to normalize as PeriodType is missing either years or months but period has a month/year amount: " + this.toString());
            }
        }
        return period;
    }
}

