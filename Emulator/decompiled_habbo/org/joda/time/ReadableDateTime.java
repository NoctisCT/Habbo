/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableInstant;

public interface ReadableDateTime
extends ReadableInstant {
    public int getDayOfWeek();

    public int getDayOfMonth();

    public int getDayOfYear();

    public int getWeekOfWeekyear();

    public int getWeekyear();

    public int getMonthOfYear();

    public int getYear();

    public int getYearOfEra();

    public int getYearOfCentury();

    public int getCenturyOfEra();

    public int getEra();

    public int getMillisOfSecond();

    public int getMillisOfDay();

    public int getSecondOfMinute();

    public int getSecondOfDay();

    public int getMinuteOfHour();

    public int getMinuteOfDay();

    public int getHourOfDay();

    public DateTime toDateTime();

    public MutableDateTime toMutableDateTime();

    public String toString(String var1) throws IllegalArgumentException;

    public String toString(String var1, Locale var2) throws IllegalArgumentException;
}

