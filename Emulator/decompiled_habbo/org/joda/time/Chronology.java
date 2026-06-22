/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;

public abstract class Chronology {
    public abstract DateTimeZone getZone();

    public abstract Chronology withUTC();

    public abstract Chronology withZone(DateTimeZone var1);

    public abstract long getDateTimeMillis(int var1, int var2, int var3, int var4);

    public abstract long getDateTimeMillis(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

    public abstract long getDateTimeMillis(long var1, int var3, int var4, int var5, int var6);

    public abstract void validate(ReadablePartial var1, int[] var2);

    public abstract int[] get(ReadablePartial var1, long var2);

    public abstract long set(ReadablePartial var1, long var2);

    public abstract int[] get(ReadablePeriod var1, long var2, long var4);

    public abstract int[] get(ReadablePeriod var1, long var2);

    public abstract long add(ReadablePeriod var1, long var2, int var4);

    public abstract long add(long var1, long var3, int var5);

    public abstract DurationField millis();

    public abstract DateTimeField millisOfSecond();

    public abstract DateTimeField millisOfDay();

    public abstract DurationField seconds();

    public abstract DateTimeField secondOfMinute();

    public abstract DateTimeField secondOfDay();

    public abstract DurationField minutes();

    public abstract DateTimeField minuteOfHour();

    public abstract DateTimeField minuteOfDay();

    public abstract DurationField hours();

    public abstract DateTimeField hourOfDay();

    public abstract DateTimeField clockhourOfDay();

    public abstract DurationField halfdays();

    public abstract DateTimeField hourOfHalfday();

    public abstract DateTimeField clockhourOfHalfday();

    public abstract DateTimeField halfdayOfDay();

    public abstract DurationField days();

    public abstract DateTimeField dayOfWeek();

    public abstract DateTimeField dayOfMonth();

    public abstract DateTimeField dayOfYear();

    public abstract DurationField weeks();

    public abstract DateTimeField weekOfWeekyear();

    public abstract DurationField weekyears();

    public abstract DateTimeField weekyear();

    public abstract DateTimeField weekyearOfCentury();

    public abstract DurationField months();

    public abstract DateTimeField monthOfYear();

    public abstract DurationField years();

    public abstract DateTimeField year();

    public abstract DateTimeField yearOfEra();

    public abstract DateTimeField yearOfCentury();

    public abstract DurationField centuries();

    public abstract DateTimeField centuryOfEra();

    public abstract DurationField eras();

    public abstract DateTimeField era();

    public abstract String toString();
}

