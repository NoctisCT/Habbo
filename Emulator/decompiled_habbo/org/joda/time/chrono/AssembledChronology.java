/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.chrono.BaseChronology;

public abstract class AssembledChronology
extends BaseChronology {
    private static final long serialVersionUID = -6728465968995518215L;
    private final Chronology iBase;
    private final Object iParam;
    private transient DurationField iMillis;
    private transient DurationField iSeconds;
    private transient DurationField iMinutes;
    private transient DurationField iHours;
    private transient DurationField iHalfdays;
    private transient DurationField iDays;
    private transient DurationField iWeeks;
    private transient DurationField iWeekyears;
    private transient DurationField iMonths;
    private transient DurationField iYears;
    private transient DurationField iCenturies;
    private transient DurationField iEras;
    private transient DateTimeField iMillisOfSecond;
    private transient DateTimeField iMillisOfDay;
    private transient DateTimeField iSecondOfMinute;
    private transient DateTimeField iSecondOfDay;
    private transient DateTimeField iMinuteOfHour;
    private transient DateTimeField iMinuteOfDay;
    private transient DateTimeField iHourOfDay;
    private transient DateTimeField iClockhourOfDay;
    private transient DateTimeField iHourOfHalfday;
    private transient DateTimeField iClockhourOfHalfday;
    private transient DateTimeField iHalfdayOfDay;
    private transient DateTimeField iDayOfWeek;
    private transient DateTimeField iDayOfMonth;
    private transient DateTimeField iDayOfYear;
    private transient DateTimeField iWeekOfWeekyear;
    private transient DateTimeField iWeekyear;
    private transient DateTimeField iWeekyearOfCentury;
    private transient DateTimeField iMonthOfYear;
    private transient DateTimeField iYear;
    private transient DateTimeField iYearOfEra;
    private transient DateTimeField iYearOfCentury;
    private transient DateTimeField iCenturyOfEra;
    private transient DateTimeField iEra;
    private transient int iBaseFlags;

    protected AssembledChronology(Chronology chronology, Object object) {
        this.iBase = chronology;
        this.iParam = object;
        this.setFields();
    }

    public DateTimeZone getZone() {
        Chronology chronology = this.iBase;
        if (chronology != null) {
            return chronology.getZone();
        }
        return null;
    }

    public long getDateTimeMillis(int n, int n2, int n3, int n4) throws IllegalArgumentException {
        Chronology chronology = this.iBase;
        if (chronology != null && (this.iBaseFlags & 6) == 6) {
            return chronology.getDateTimeMillis(n, n2, n3, n4);
        }
        return super.getDateTimeMillis(n, n2, n3, n4);
    }

    public long getDateTimeMillis(int n, int n2, int n3, int n4, int n5, int n6, int n7) throws IllegalArgumentException {
        Chronology chronology = this.iBase;
        if (chronology != null && (this.iBaseFlags & 5) == 5) {
            return chronology.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
        }
        return super.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
    }

    public long getDateTimeMillis(long l, int n, int n2, int n3, int n4) throws IllegalArgumentException {
        Chronology chronology = this.iBase;
        if (chronology != null && (this.iBaseFlags & 1) == 1) {
            return chronology.getDateTimeMillis(l, n, n2, n3, n4);
        }
        return super.getDateTimeMillis(l, n, n2, n3, n4);
    }

    public final DurationField millis() {
        return this.iMillis;
    }

    public final DateTimeField millisOfSecond() {
        return this.iMillisOfSecond;
    }

    public final DateTimeField millisOfDay() {
        return this.iMillisOfDay;
    }

    public final DurationField seconds() {
        return this.iSeconds;
    }

    public final DateTimeField secondOfMinute() {
        return this.iSecondOfMinute;
    }

    public final DateTimeField secondOfDay() {
        return this.iSecondOfDay;
    }

    public final DurationField minutes() {
        return this.iMinutes;
    }

    public final DateTimeField minuteOfHour() {
        return this.iMinuteOfHour;
    }

    public final DateTimeField minuteOfDay() {
        return this.iMinuteOfDay;
    }

    public final DurationField hours() {
        return this.iHours;
    }

    public final DateTimeField hourOfDay() {
        return this.iHourOfDay;
    }

    public final DateTimeField clockhourOfDay() {
        return this.iClockhourOfDay;
    }

    public final DurationField halfdays() {
        return this.iHalfdays;
    }

    public final DateTimeField hourOfHalfday() {
        return this.iHourOfHalfday;
    }

    public final DateTimeField clockhourOfHalfday() {
        return this.iClockhourOfHalfday;
    }

    public final DateTimeField halfdayOfDay() {
        return this.iHalfdayOfDay;
    }

    public final DurationField days() {
        return this.iDays;
    }

    public final DateTimeField dayOfWeek() {
        return this.iDayOfWeek;
    }

    public final DateTimeField dayOfMonth() {
        return this.iDayOfMonth;
    }

    public final DateTimeField dayOfYear() {
        return this.iDayOfYear;
    }

    public final DurationField weeks() {
        return this.iWeeks;
    }

    public final DateTimeField weekOfWeekyear() {
        return this.iWeekOfWeekyear;
    }

    public final DurationField weekyears() {
        return this.iWeekyears;
    }

    public final DateTimeField weekyear() {
        return this.iWeekyear;
    }

    public final DateTimeField weekyearOfCentury() {
        return this.iWeekyearOfCentury;
    }

    public final DurationField months() {
        return this.iMonths;
    }

    public final DateTimeField monthOfYear() {
        return this.iMonthOfYear;
    }

    public final DurationField years() {
        return this.iYears;
    }

    public final DateTimeField year() {
        return this.iYear;
    }

    public final DateTimeField yearOfEra() {
        return this.iYearOfEra;
    }

    public final DateTimeField yearOfCentury() {
        return this.iYearOfCentury;
    }

    public final DurationField centuries() {
        return this.iCenturies;
    }

    public final DateTimeField centuryOfEra() {
        return this.iCenturyOfEra;
    }

    public final DurationField eras() {
        return this.iEras;
    }

    public final DateTimeField era() {
        return this.iEra;
    }

    protected abstract void assemble(Fields var1);

    protected final Chronology getBase() {
        return this.iBase;
    }

    protected final Object getParam() {
        return this.iParam;
    }

    private void setFields() {
        Fields fields = new Fields();
        if (this.iBase != null) {
            fields.copyFieldsFrom(this.iBase);
        }
        this.assemble(fields);
        Object object = fields.millis;
        this.iMillis = object != null ? object : super.millis();
        object = fields.seconds;
        this.iSeconds = object != null ? object : super.seconds();
        object = fields.minutes;
        this.iMinutes = object != null ? object : super.minutes();
        object = fields.hours;
        this.iHours = object != null ? object : super.hours();
        object = fields.halfdays;
        this.iHalfdays = object != null ? object : super.halfdays();
        object = fields.days;
        this.iDays = object != null ? object : super.days();
        object = fields.weeks;
        this.iWeeks = object != null ? object : super.weeks();
        object = fields.weekyears;
        this.iWeekyears = object != null ? object : super.weekyears();
        object = fields.months;
        this.iMonths = object != null ? object : super.months();
        object = fields.years;
        this.iYears = object != null ? object : super.years();
        object = fields.centuries;
        this.iCenturies = object != null ? object : super.centuries();
        object = fields.eras;
        this.iEras = object != null ? object : super.eras();
        object = fields.millisOfSecond;
        this.iMillisOfSecond = object != null ? object : super.millisOfSecond();
        object = fields.millisOfDay;
        this.iMillisOfDay = object != null ? object : super.millisOfDay();
        object = fields.secondOfMinute;
        this.iSecondOfMinute = object != null ? object : super.secondOfMinute();
        object = fields.secondOfDay;
        this.iSecondOfDay = object != null ? object : super.secondOfDay();
        object = fields.minuteOfHour;
        this.iMinuteOfHour = object != null ? object : super.minuteOfHour();
        object = fields.minuteOfDay;
        this.iMinuteOfDay = object != null ? object : super.minuteOfDay();
        object = fields.hourOfDay;
        this.iHourOfDay = object != null ? object : super.hourOfDay();
        object = fields.clockhourOfDay;
        this.iClockhourOfDay = object != null ? object : super.clockhourOfDay();
        object = fields.hourOfHalfday;
        this.iHourOfHalfday = object != null ? object : super.hourOfHalfday();
        object = fields.clockhourOfHalfday;
        this.iClockhourOfHalfday = object != null ? object : super.clockhourOfHalfday();
        object = fields.halfdayOfDay;
        this.iHalfdayOfDay = object != null ? object : super.halfdayOfDay();
        object = fields.dayOfWeek;
        this.iDayOfWeek = object != null ? object : super.dayOfWeek();
        object = fields.dayOfMonth;
        this.iDayOfMonth = object != null ? object : super.dayOfMonth();
        object = fields.dayOfYear;
        this.iDayOfYear = object != null ? object : super.dayOfYear();
        object = fields.weekOfWeekyear;
        this.iWeekOfWeekyear = object != null ? object : super.weekOfWeekyear();
        object = fields.weekyear;
        this.iWeekyear = object != null ? object : super.weekyear();
        object = fields.weekyearOfCentury;
        this.iWeekyearOfCentury = object != null ? object : super.weekyearOfCentury();
        object = fields.monthOfYear;
        this.iMonthOfYear = object != null ? object : super.monthOfYear();
        object = fields.year;
        this.iYear = object != null ? object : super.year();
        object = fields.yearOfEra;
        this.iYearOfEra = object != null ? object : super.yearOfEra();
        object = fields.yearOfCentury;
        this.iYearOfCentury = object != null ? object : super.yearOfCentury();
        object = fields.centuryOfEra;
        this.iCenturyOfEra = object != null ? object : super.centuryOfEra();
        object = fields.era;
        Object object2 = this.iEra = object != null ? object : super.era();
        int n = this.iBase == null ? 0 : (this.iHourOfDay == this.iBase.hourOfDay() && this.iMinuteOfHour == this.iBase.minuteOfHour() && this.iSecondOfMinute == this.iBase.secondOfMinute() && this.iMillisOfSecond == this.iBase.millisOfSecond() ? 1 : 0) | (this.iMillisOfDay == this.iBase.millisOfDay() ? 2 : 0) | (this.iYear == this.iBase.year() && this.iMonthOfYear == this.iBase.monthOfYear() && this.iDayOfMonth == this.iBase.dayOfMonth() ? 4 : 0);
        this.iBaseFlags = n;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.setFields();
    }

    public static final class Fields {
        public DurationField millis;
        public DurationField seconds;
        public DurationField minutes;
        public DurationField hours;
        public DurationField halfdays;
        public DurationField days;
        public DurationField weeks;
        public DurationField weekyears;
        public DurationField months;
        public DurationField years;
        public DurationField centuries;
        public DurationField eras;
        public DateTimeField millisOfSecond;
        public DateTimeField millisOfDay;
        public DateTimeField secondOfMinute;
        public DateTimeField secondOfDay;
        public DateTimeField minuteOfHour;
        public DateTimeField minuteOfDay;
        public DateTimeField hourOfDay;
        public DateTimeField clockhourOfDay;
        public DateTimeField hourOfHalfday;
        public DateTimeField clockhourOfHalfday;
        public DateTimeField halfdayOfDay;
        public DateTimeField dayOfWeek;
        public DateTimeField dayOfMonth;
        public DateTimeField dayOfYear;
        public DateTimeField weekOfWeekyear;
        public DateTimeField weekyear;
        public DateTimeField weekyearOfCentury;
        public DateTimeField monthOfYear;
        public DateTimeField year;
        public DateTimeField yearOfEra;
        public DateTimeField yearOfCentury;
        public DateTimeField centuryOfEra;
        public DateTimeField era;

        Fields() {
        }

        public void copyFieldsFrom(Chronology chronology) {
            Object object = chronology.millis();
            if (Fields.isSupported((DurationField)object)) {
                this.millis = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.seconds()))) {
                this.seconds = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.minutes()))) {
                this.minutes = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.hours()))) {
                this.hours = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.halfdays()))) {
                this.halfdays = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.days()))) {
                this.days = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.weeks()))) {
                this.weeks = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.weekyears()))) {
                this.weekyears = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.months()))) {
                this.months = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.years()))) {
                this.years = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.centuries()))) {
                this.centuries = object;
            }
            if (Fields.isSupported((DurationField)(object = chronology.eras()))) {
                this.eras = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.millisOfSecond()))) {
                this.millisOfSecond = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.millisOfDay()))) {
                this.millisOfDay = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.secondOfMinute()))) {
                this.secondOfMinute = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.secondOfDay()))) {
                this.secondOfDay = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.minuteOfHour()))) {
                this.minuteOfHour = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.minuteOfDay()))) {
                this.minuteOfDay = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.hourOfDay()))) {
                this.hourOfDay = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.clockhourOfDay()))) {
                this.clockhourOfDay = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.hourOfHalfday()))) {
                this.hourOfHalfday = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.clockhourOfHalfday()))) {
                this.clockhourOfHalfday = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.halfdayOfDay()))) {
                this.halfdayOfDay = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.dayOfWeek()))) {
                this.dayOfWeek = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.dayOfMonth()))) {
                this.dayOfMonth = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.dayOfYear()))) {
                this.dayOfYear = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.weekOfWeekyear()))) {
                this.weekOfWeekyear = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.weekyear()))) {
                this.weekyear = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.weekyearOfCentury()))) {
                this.weekyearOfCentury = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.monthOfYear()))) {
                this.monthOfYear = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.year()))) {
                this.year = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.yearOfEra()))) {
                this.yearOfEra = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.yearOfCentury()))) {
                this.yearOfCentury = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.centuryOfEra()))) {
                this.centuryOfEra = object;
            }
            if (Fields.isSupported((DateTimeField)(object = chronology.era()))) {
                this.era = object;
            }
        }

        private static boolean isSupported(DurationField durationField) {
            return durationField == null ? false : durationField.isSupported();
        }

        private static boolean isSupported(DateTimeField dateTimeField) {
            return dateTimeField == null ? false : dateTimeField.isSupported();
        }
    }
}

