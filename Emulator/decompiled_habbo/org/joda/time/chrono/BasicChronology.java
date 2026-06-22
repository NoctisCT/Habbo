/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.BasicDayOfMonthDateTimeField;
import org.joda.time.chrono.BasicDayOfYearDateTimeField;
import org.joda.time.chrono.BasicWeekOfWeekyearDateTimeField;
import org.joda.time.chrono.BasicWeekyearDateTimeField;
import org.joda.time.chrono.BasicYearDateTimeField;
import org.joda.time.chrono.GJDayOfWeekDateTimeField;
import org.joda.time.chrono.GJEraDateTimeField;
import org.joda.time.chrono.GJLocaleSymbols;
import org.joda.time.chrono.GJMonthOfYearDateTimeField;
import org.joda.time.chrono.GJYearOfEraDateTimeField;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.DividedDateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.MillisDurationField;
import org.joda.time.field.OffsetDateTimeField;
import org.joda.time.field.PreciseDateTimeField;
import org.joda.time.field.PreciseDurationField;
import org.joda.time.field.RemainderDateTimeField;
import org.joda.time.field.ZeroIsMaxDateTimeField;

abstract class BasicChronology
extends AssembledChronology {
    private static final long serialVersionUID = 8283225332206808863L;
    private static final DurationField cMillisField = MillisDurationField.INSTANCE;
    private static final DurationField cSecondsField = new PreciseDurationField(DurationFieldType.seconds(), 1000L);
    private static final DurationField cMinutesField = new PreciseDurationField(DurationFieldType.minutes(), 60000L);
    private static final DurationField cHoursField = new PreciseDurationField(DurationFieldType.hours(), 3600000L);
    private static final DurationField cHalfdaysField = new PreciseDurationField(DurationFieldType.halfdays(), 43200000L);
    private static final DurationField cDaysField = new PreciseDurationField(DurationFieldType.days(), 86400000L);
    private static final DurationField cWeeksField = new PreciseDurationField(DurationFieldType.weeks(), 604800000L);
    private static final DateTimeField cMillisOfSecondField = new PreciseDateTimeField(DateTimeFieldType.millisOfSecond(), cMillisField, cSecondsField);
    private static final DateTimeField cMillisOfDayField = new PreciseDateTimeField(DateTimeFieldType.millisOfDay(), cMillisField, cDaysField);
    private static final DateTimeField cSecondOfMinuteField = new PreciseDateTimeField(DateTimeFieldType.secondOfMinute(), cSecondsField, cMinutesField);
    private static final DateTimeField cSecondOfDayField = new PreciseDateTimeField(DateTimeFieldType.secondOfDay(), cSecondsField, cDaysField);
    private static final DateTimeField cMinuteOfHourField = new PreciseDateTimeField(DateTimeFieldType.minuteOfHour(), cMinutesField, cHoursField);
    private static final DateTimeField cMinuteOfDayField = new PreciseDateTimeField(DateTimeFieldType.minuteOfDay(), cMinutesField, cDaysField);
    private static final DateTimeField cHourOfDayField = new PreciseDateTimeField(DateTimeFieldType.hourOfDay(), cHoursField, cDaysField);
    private static final DateTimeField cHourOfHalfdayField = new PreciseDateTimeField(DateTimeFieldType.hourOfHalfday(), cHoursField, cHalfdaysField);
    private static final DateTimeField cClockhourOfDayField = new ZeroIsMaxDateTimeField(cHourOfDayField, DateTimeFieldType.clockhourOfDay());
    private static final DateTimeField cClockhourOfHalfdayField = new ZeroIsMaxDateTimeField(cHourOfHalfdayField, DateTimeFieldType.clockhourOfHalfday());
    private static final DateTimeField cHalfdayOfDayField = new HalfdayField();
    private static final int CACHE_SIZE = 1024;
    private static final int CACHE_MASK = 1023;
    private final transient YearInfo[] iYearInfoCache = new YearInfo[1024];
    private final int iMinDaysInFirstWeek;

    BasicChronology(Chronology chronology, Object object, int n) {
        super(chronology, object);
        if (n < 1 || n > 7) {
            throw new IllegalArgumentException("Invalid min days in first week: " + n);
        }
        this.iMinDaysInFirstWeek = n;
    }

    public DateTimeZone getZone() {
        Chronology chronology = this.getBase();
        if (chronology != null) {
            return chronology.getZone();
        }
        return DateTimeZone.UTC;
    }

    public long getDateTimeMillis(int n, int n2, int n3, int n4) throws IllegalArgumentException {
        Chronology chronology = this.getBase();
        if (chronology != null) {
            return chronology.getDateTimeMillis(n, n2, n3, n4);
        }
        FieldUtils.verifyValueBounds(DateTimeFieldType.millisOfDay(), n4, 0, 86399999);
        return this.getDateTimeMillis0(n, n2, n3, n4);
    }

    public long getDateTimeMillis(int n, int n2, int n3, int n4, int n5, int n6, int n7) throws IllegalArgumentException {
        Chronology chronology = this.getBase();
        if (chronology != null) {
            return chronology.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
        }
        FieldUtils.verifyValueBounds(DateTimeFieldType.hourOfDay(), n4, 0, 23);
        FieldUtils.verifyValueBounds(DateTimeFieldType.minuteOfHour(), n5, 0, 59);
        FieldUtils.verifyValueBounds(DateTimeFieldType.secondOfMinute(), n6, 0, 59);
        FieldUtils.verifyValueBounds(DateTimeFieldType.millisOfSecond(), n7, 0, 999);
        long l = n4 * 3600000 + n5 * 60000 + n6 * 1000 + n7;
        return this.getDateTimeMillis0(n, n2, n3, (int)l);
    }

    private long getDateTimeMillis0(int n, int n2, int n3, int n4) {
        long l;
        long l2 = this.getDateMidnightMillis(n, n2, n3);
        if (l2 == Long.MIN_VALUE) {
            l2 = this.getDateMidnightMillis(n, n2, n3 + 1);
            n4 -= 86400000;
        }
        if ((l = l2 + (long)n4) < 0L && l2 > 0L) {
            return Long.MAX_VALUE;
        }
        if (l > 0L && l2 < 0L) {
            return Long.MIN_VALUE;
        }
        return l;
    }

    public int getMinimumDaysInFirstWeek() {
        return this.iMinDaysInFirstWeek;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object != null && this.getClass() == object.getClass()) {
            BasicChronology basicChronology = (BasicChronology)object;
            return this.getMinimumDaysInFirstWeek() == basicChronology.getMinimumDaysInFirstWeek() && this.getZone().equals(basicChronology.getZone());
        }
        return false;
    }

    public int hashCode() {
        return this.getClass().getName().hashCode() * 11 + this.getZone().hashCode() + this.getMinimumDaysInFirstWeek();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(60);
        String string = this.getClass().getName();
        int n = string.lastIndexOf(46);
        if (n >= 0) {
            string = string.substring(n + 1);
        }
        stringBuilder.append(string);
        stringBuilder.append('[');
        DateTimeZone dateTimeZone = this.getZone();
        if (dateTimeZone != null) {
            stringBuilder.append(dateTimeZone.getID());
        }
        if (this.getMinimumDaysInFirstWeek() != 4) {
            stringBuilder.append(",mdfw=");
            stringBuilder.append(this.getMinimumDaysInFirstWeek());
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    protected void assemble(AssembledChronology.Fields fields) {
        fields.millis = cMillisField;
        fields.seconds = cSecondsField;
        fields.minutes = cMinutesField;
        fields.hours = cHoursField;
        fields.halfdays = cHalfdaysField;
        fields.days = cDaysField;
        fields.weeks = cWeeksField;
        fields.millisOfSecond = cMillisOfSecondField;
        fields.millisOfDay = cMillisOfDayField;
        fields.secondOfMinute = cSecondOfMinuteField;
        fields.secondOfDay = cSecondOfDayField;
        fields.minuteOfHour = cMinuteOfHourField;
        fields.minuteOfDay = cMinuteOfDayField;
        fields.hourOfDay = cHourOfDayField;
        fields.hourOfHalfday = cHourOfHalfdayField;
        fields.clockhourOfDay = cClockhourOfDayField;
        fields.clockhourOfHalfday = cClockhourOfHalfdayField;
        fields.halfdayOfDay = cHalfdayOfDayField;
        fields.year = new BasicYearDateTimeField(this);
        fields.yearOfEra = new GJYearOfEraDateTimeField(fields.year, this);
        DecoratedDateTimeField decoratedDateTimeField = new OffsetDateTimeField(fields.yearOfEra, 99);
        fields.centuryOfEra = new DividedDateTimeField(decoratedDateTimeField, DateTimeFieldType.centuryOfEra(), 100);
        fields.centuries = fields.centuryOfEra.getDurationField();
        decoratedDateTimeField = new RemainderDateTimeField((DividedDateTimeField)fields.centuryOfEra);
        fields.yearOfCentury = new OffsetDateTimeField(decoratedDateTimeField, DateTimeFieldType.yearOfCentury(), 1);
        fields.era = new GJEraDateTimeField(this);
        fields.dayOfWeek = new GJDayOfWeekDateTimeField(this, fields.days);
        fields.dayOfMonth = new BasicDayOfMonthDateTimeField(this, fields.days);
        fields.dayOfYear = new BasicDayOfYearDateTimeField(this, fields.days);
        fields.monthOfYear = new GJMonthOfYearDateTimeField(this);
        fields.weekyear = new BasicWeekyearDateTimeField(this);
        fields.weekOfWeekyear = new BasicWeekOfWeekyearDateTimeField(this, fields.weeks);
        decoratedDateTimeField = new RemainderDateTimeField(fields.weekyear, fields.centuries, DateTimeFieldType.weekyearOfCentury(), 100);
        fields.weekyearOfCentury = new OffsetDateTimeField(decoratedDateTimeField, DateTimeFieldType.weekyearOfCentury(), 1);
        fields.years = fields.year.getDurationField();
        fields.months = fields.monthOfYear.getDurationField();
        fields.weekyears = fields.weekyear.getDurationField();
    }

    int getDaysInYearMax() {
        return 366;
    }

    int getDaysInYear(int n) {
        return this.isLeapYear(n) ? 366 : 365;
    }

    int getWeeksInYear(int n) {
        long l = this.getFirstWeekOfYearMillis(n);
        long l2 = this.getFirstWeekOfYearMillis(n + 1);
        return (int)((l2 - l) / 604800000L);
    }

    long getFirstWeekOfYearMillis(int n) {
        long l = this.getYearMillis(n);
        int n2 = this.getDayOfWeek(l);
        if (n2 > 8 - this.iMinDaysInFirstWeek) {
            return l + (long)(8 - n2) * 86400000L;
        }
        return l - (long)(n2 - 1) * 86400000L;
    }

    long getYearMillis(int n) {
        return this.getYearInfo((int)n).iFirstDayMillis;
    }

    long getYearMonthMillis(int n, int n2) {
        long l = this.getYearMillis(n);
        return l += this.getTotalMillisByYearMonth(n, n2);
    }

    long getYearMonthDayMillis(int n, int n2, int n3) {
        long l = this.getYearMillis(n);
        return (l += this.getTotalMillisByYearMonth(n, n2)) + (long)(n3 - 1) * 86400000L;
    }

    int getYear(long l) {
        long l2;
        int n;
        long l3;
        long l4;
        long l5 = this.getAverageMillisPerYearDividedByTwo();
        long l6 = (l >> 1) + this.getApproxMillisAtEpochDividedByTwo();
        if (l6 < 0L) {
            l6 = l6 - l5 + 1L;
        }
        if ((l4 = l - (l3 = this.getYearMillis(n = (int)(l6 / l5)))) < 0L) {
            --n;
        } else if (l4 >= 31536000000L && (l3 += (l2 = this.isLeapYear(n) ? 31622400000L : 31536000000L)) <= l) {
            ++n;
        }
        return n;
    }

    int getMonthOfYear(long l) {
        return this.getMonthOfYear(l, this.getYear(l));
    }

    abstract int getMonthOfYear(long var1, int var3);

    int getDayOfMonth(long l) {
        int n = this.getYear(l);
        int n2 = this.getMonthOfYear(l, n);
        return this.getDayOfMonth(l, n, n2);
    }

    int getDayOfMonth(long l, int n) {
        int n2 = this.getMonthOfYear(l, n);
        return this.getDayOfMonth(l, n, n2);
    }

    int getDayOfMonth(long l, int n, int n2) {
        long l2 = this.getYearMillis(n);
        return (int)((l - (l2 += this.getTotalMillisByYearMonth(n, n2))) / 86400000L) + 1;
    }

    int getDayOfYear(long l) {
        return this.getDayOfYear(l, this.getYear(l));
    }

    int getDayOfYear(long l, int n) {
        long l2 = this.getYearMillis(n);
        return (int)((l - l2) / 86400000L) + 1;
    }

    int getWeekyear(long l) {
        int n = this.getYear(l);
        int n2 = this.getWeekOfWeekyear(l, n);
        if (n2 == 1) {
            return this.getYear(l + 604800000L);
        }
        if (n2 > 51) {
            return this.getYear(l - 1209600000L);
        }
        return n;
    }

    int getWeekOfWeekyear(long l) {
        return this.getWeekOfWeekyear(l, this.getYear(l));
    }

    int getWeekOfWeekyear(long l, int n) {
        long l2 = this.getFirstWeekOfYearMillis(n);
        if (l < l2) {
            return this.getWeeksInYear(n - 1);
        }
        long l3 = this.getFirstWeekOfYearMillis(n + 1);
        if (l >= l3) {
            return 1;
        }
        return (int)((l - l2) / 604800000L) + 1;
    }

    int getDayOfWeek(long l) {
        long l2;
        if (l >= 0L) {
            l2 = l / 86400000L;
        } else {
            l2 = (l - 86399999L) / 86400000L;
            if (l2 < -3L) {
                return 7 + (int)((l2 + 4L) % 7L);
            }
        }
        return 1 + (int)((l2 + 3L) % 7L);
    }

    int getMillisOfDay(long l) {
        if (l >= 0L) {
            return (int)(l % 86400000L);
        }
        return 86399999 + (int)((l + 1L) % 86400000L);
    }

    int getDaysInMonthMax() {
        return 31;
    }

    int getDaysInMonthMax(long l) {
        int n = this.getYear(l);
        int n2 = this.getMonthOfYear(l, n);
        return this.getDaysInYearMonth(n, n2);
    }

    int getDaysInMonthMaxForSet(long l, int n) {
        return this.getDaysInMonthMax(l);
    }

    long getDateMidnightMillis(int n, int n2, int n3) {
        FieldUtils.verifyValueBounds(DateTimeFieldType.year(), n, this.getMinYear() - 1, this.getMaxYear() + 1);
        FieldUtils.verifyValueBounds(DateTimeFieldType.monthOfYear(), n2, 1, this.getMaxMonth(n));
        FieldUtils.verifyValueBounds(DateTimeFieldType.dayOfMonth(), n3, 1, this.getDaysInYearMonth(n, n2));
        long l = this.getYearMonthDayMillis(n, n2, n3);
        if (l < 0L && n == this.getMaxYear() + 1) {
            return Long.MAX_VALUE;
        }
        if (l > 0L && n == this.getMinYear() - 1) {
            return Long.MIN_VALUE;
        }
        return l;
    }

    abstract long getYearDifference(long var1, long var3);

    abstract boolean isLeapYear(int var1);

    boolean isLeapDay(long l) {
        return false;
    }

    abstract int getDaysInYearMonth(int var1, int var2);

    abstract int getDaysInMonthMax(int var1);

    abstract long getTotalMillisByYearMonth(int var1, int var2);

    abstract long calculateFirstDayOfYearMillis(int var1);

    abstract int getMinYear();

    abstract int getMaxYear();

    int getMaxMonth(int n) {
        return this.getMaxMonth();
    }

    int getMaxMonth() {
        return 12;
    }

    abstract long getAverageMillisPerYear();

    abstract long getAverageMillisPerYearDividedByTwo();

    abstract long getAverageMillisPerMonth();

    abstract long getApproxMillisAtEpochDividedByTwo();

    abstract long setYear(long var1, int var3);

    private YearInfo getYearInfo(int n) {
        YearInfo yearInfo = this.iYearInfoCache[n & 0x3FF];
        if (yearInfo == null || yearInfo.iYear != n) {
            this.iYearInfoCache[n & 0x3FF] = yearInfo = new YearInfo(n, this.calculateFirstDayOfYearMillis(n));
        }
        return yearInfo;
    }

    private static class YearInfo {
        public final int iYear;
        public final long iFirstDayMillis;

        YearInfo(int n, long l) {
            this.iYear = n;
            this.iFirstDayMillis = l;
        }
    }

    private static class HalfdayField
    extends PreciseDateTimeField {
        private static final long serialVersionUID = 581601443656929254L;

        HalfdayField() {
            super(DateTimeFieldType.halfdayOfDay(), cHalfdaysField, cDaysField);
        }

        public String getAsText(int n, Locale locale) {
            return GJLocaleSymbols.forLocale(locale).halfdayValueToText(n);
        }

        public long set(long l, String string, Locale locale) {
            return this.set(l, GJLocaleSymbols.forLocale(locale).halfdayTextToValue(string));
        }

        public int getMaximumTextLength(Locale locale) {
            return GJLocaleSymbols.forLocale(locale).getHalfdayMaxTextLength();
        }
    }
}

