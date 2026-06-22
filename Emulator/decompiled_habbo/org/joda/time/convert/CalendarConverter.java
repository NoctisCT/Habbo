/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.InstantConverter;
import org.joda.time.convert.PartialConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class CalendarConverter
extends AbstractConverter
implements InstantConverter,
PartialConverter {
    static final CalendarConverter INSTANCE = new CalendarConverter();

    protected CalendarConverter() {
    }

    @Override
    public Chronology getChronology(Object object, Chronology chronology) {
        if (chronology != null) {
            return chronology;
        }
        Calendar calendar = (Calendar)object;
        DateTimeZone dateTimeZone = null;
        try {
            dateTimeZone = DateTimeZone.forTimeZone(calendar.getTimeZone());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        return this.getChronology((Object)calendar, dateTimeZone);
    }

    @Override
    public Chronology getChronology(Object object, DateTimeZone dateTimeZone) {
        if (object.getClass().getName().endsWith(".BuddhistCalendar")) {
            return BuddhistChronology.getInstance(dateTimeZone);
        }
        if (object instanceof GregorianCalendar) {
            GregorianCalendar gregorianCalendar = (GregorianCalendar)object;
            long l = gregorianCalendar.getGregorianChange().getTime();
            if (l == Long.MIN_VALUE) {
                return GregorianChronology.getInstance(dateTimeZone);
            }
            if (l == Long.MAX_VALUE) {
                return JulianChronology.getInstance(dateTimeZone);
            }
            return GJChronology.getInstance(dateTimeZone, l, 4);
        }
        return ISOChronology.getInstance(dateTimeZone);
    }

    @Override
    public long getInstantMillis(Object object, Chronology chronology) {
        Calendar calendar = (Calendar)object;
        return calendar.getTime().getTime();
    }

    @Override
    public Class<?> getSupportedType() {
        return Calendar.class;
    }
}

