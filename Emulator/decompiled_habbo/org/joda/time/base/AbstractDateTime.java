/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.ToString
 */
package org.joda.time.base;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.joda.convert.ToString;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableDateTime;
import org.joda.time.base.AbstractInstant;
import org.joda.time.format.DateTimeFormat;

public abstract class AbstractDateTime
extends AbstractInstant
implements ReadableDateTime {
    protected AbstractDateTime() {
    }

    public int get(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        return dateTimeFieldType.getField(this.getChronology()).get(this.getMillis());
    }

    public int getEra() {
        return this.getChronology().era().get(this.getMillis());
    }

    public int getCenturyOfEra() {
        return this.getChronology().centuryOfEra().get(this.getMillis());
    }

    public int getYearOfEra() {
        return this.getChronology().yearOfEra().get(this.getMillis());
    }

    public int getYearOfCentury() {
        return this.getChronology().yearOfCentury().get(this.getMillis());
    }

    public int getYear() {
        return this.getChronology().year().get(this.getMillis());
    }

    public int getWeekyear() {
        return this.getChronology().weekyear().get(this.getMillis());
    }

    public int getMonthOfYear() {
        return this.getChronology().monthOfYear().get(this.getMillis());
    }

    public int getWeekOfWeekyear() {
        return this.getChronology().weekOfWeekyear().get(this.getMillis());
    }

    public int getDayOfYear() {
        return this.getChronology().dayOfYear().get(this.getMillis());
    }

    public int getDayOfMonth() {
        return this.getChronology().dayOfMonth().get(this.getMillis());
    }

    public int getDayOfWeek() {
        return this.getChronology().dayOfWeek().get(this.getMillis());
    }

    public int getHourOfDay() {
        return this.getChronology().hourOfDay().get(this.getMillis());
    }

    public int getMinuteOfDay() {
        return this.getChronology().minuteOfDay().get(this.getMillis());
    }

    public int getMinuteOfHour() {
        return this.getChronology().minuteOfHour().get(this.getMillis());
    }

    public int getSecondOfDay() {
        return this.getChronology().secondOfDay().get(this.getMillis());
    }

    public int getSecondOfMinute() {
        return this.getChronology().secondOfMinute().get(this.getMillis());
    }

    public int getMillisOfDay() {
        return this.getChronology().millisOfDay().get(this.getMillis());
    }

    public int getMillisOfSecond() {
        return this.getChronology().millisOfSecond().get(this.getMillis());
    }

    public Calendar toCalendar(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        DateTimeZone dateTimeZone = this.getZone();
        Calendar calendar = Calendar.getInstance(dateTimeZone.toTimeZone(), locale);
        calendar.setTime(this.toDate());
        return calendar;
    }

    public GregorianCalendar toGregorianCalendar() {
        DateTimeZone dateTimeZone = this.getZone();
        GregorianCalendar gregorianCalendar = new GregorianCalendar(dateTimeZone.toTimeZone());
        gregorianCalendar.setTime(this.toDate());
        return gregorianCalendar;
    }

    @ToString
    public String toString() {
        return super.toString();
    }

    public String toString(String string) {
        if (string == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(string).print(this);
    }

    public String toString(String string, Locale locale) throws IllegalArgumentException {
        if (string == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(string).withLocale(locale).print(this);
    }
}

