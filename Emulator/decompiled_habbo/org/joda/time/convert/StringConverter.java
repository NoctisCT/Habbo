/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadWritableInterval;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadablePartial;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.DurationConverter;
import org.joda.time.convert.InstantConverter;
import org.joda.time.convert.IntervalConverter;
import org.joda.time.convert.PartialConverter;
import org.joda.time.convert.PeriodConverter;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class StringConverter
extends AbstractConverter
implements InstantConverter,
PartialConverter,
DurationConverter,
PeriodConverter,
IntervalConverter {
    static final StringConverter INSTANCE = new StringConverter();

    protected StringConverter() {
    }

    @Override
    public long getInstantMillis(Object object, Chronology chronology) {
        String string = (String)object;
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
        return dateTimeFormatter.withChronology(chronology).parseMillis(string);
    }

    @Override
    public int[] getPartialValues(ReadablePartial readablePartial, Object object, Chronology chronology, DateTimeFormatter dateTimeFormatter) {
        if (dateTimeFormatter.getZone() != null) {
            chronology = chronology.withZone(dateTimeFormatter.getZone());
        }
        long l = dateTimeFormatter.withChronology(chronology).parseMillis((String)object);
        return chronology.get(readablePartial, l);
    }

    @Override
    public long getDurationMillis(Object object) {
        int n;
        String string = (String)object;
        String string2 = string;
        int n2 = string2.length();
        if (n2 < 4 || string2.charAt(0) != 'P' && string2.charAt(0) != 'p' || string2.charAt(1) != 'T' && string2.charAt(1) != 't' || string2.charAt(n2 - 1) != 'S' && string2.charAt(n2 - 1) != 's') {
            throw new IllegalArgumentException("Invalid format: \"" + string + '\"');
        }
        string2 = string2.substring(2, n2 - 1);
        int n3 = -1;
        boolean bl = false;
        for (int i = 0; i < string2.length(); ++i) {
            if (string2.charAt(i) >= '0' && string2.charAt(i) <= '9') continue;
            if (i == 0 && string2.charAt(0) == '-') {
                bl = true;
                continue;
            }
            if (i > (bl ? 1 : 0) && string2.charAt(i) == '.' && n3 == -1) {
                n3 = i;
                continue;
            }
            throw new IllegalArgumentException("Invalid format: \"" + string + '\"');
        }
        long l = 0L;
        long l2 = 0L;
        int n4 = n = bl ? 1 : 0;
        if (n3 > 0) {
            l2 = Long.parseLong(string2.substring(n, n3));
            if ((string2 = string2.substring(n3 + 1)).length() != 3) {
                string2 = (string2 + "000").substring(0, 3);
            }
            l = Integer.parseInt(string2);
        } else {
            l2 = bl ? Long.parseLong(string2.substring(n, string2.length())) : Long.parseLong(string2);
        }
        if (bl) {
            return FieldUtils.safeAdd(FieldUtils.safeMultiply(-l2, 1000), -l);
        }
        return FieldUtils.safeAdd(FieldUtils.safeMultiply(l2, 1000), l);
    }

    @Override
    public void setInto(ReadWritablePeriod readWritablePeriod, Object object, Chronology chronology) {
        String string = (String)object;
        PeriodFormatter periodFormatter = ISOPeriodFormat.standard();
        readWritablePeriod.clear();
        int n = periodFormatter.parseInto(readWritablePeriod, string, 0);
        if (n < string.length()) {
            if (n < 0) {
                periodFormatter.withParseType(readWritablePeriod.getPeriodType()).parseMutablePeriod(string);
            }
            throw new IllegalArgumentException("Invalid format: \"" + string + '\"');
        }
    }

    @Override
    public void setInto(ReadWritableInterval readWritableInterval, Object object, Chronology chronology) {
        DateTime dateTime;
        String string = (String)object;
        int n = string.indexOf(47);
        if (n < 0) {
            throw new IllegalArgumentException("Format requires a '/' separator: " + string);
        }
        String string2 = string.substring(0, n);
        if (string2.length() <= 0) {
            throw new IllegalArgumentException("Format invalid: " + string);
        }
        String string3 = string.substring(n + 1);
        if (string3.length() <= 0) {
            throw new IllegalArgumentException("Format invalid: " + string);
        }
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
        dateTimeFormatter = dateTimeFormatter.withChronology(chronology);
        PeriodFormatter periodFormatter = ISOPeriodFormat.standard();
        long l = 0L;
        long l2 = 0L;
        Period period = null;
        Chronology chronology2 = null;
        char c = string2.charAt(0);
        if (c == 'P' || c == 'p') {
            period = periodFormatter.withParseType(this.getPeriodType(string2)).parsePeriod(string2);
        } else {
            dateTime = dateTimeFormatter.parseDateTime(string2);
            l = dateTime.getMillis();
            chronology2 = dateTime.getChronology();
        }
        c = string3.charAt(0);
        if (c == 'P' || c == 'p') {
            if (period != null) {
                throw new IllegalArgumentException("Interval composed of two durations: " + string);
            }
            period = periodFormatter.withParseType(this.getPeriodType(string3)).parsePeriod(string3);
            chronology = chronology != null ? chronology : chronology2;
            l2 = chronology.add(period, l, 1);
        } else {
            dateTime = dateTimeFormatter.parseDateTime(string3);
            l2 = dateTime.getMillis();
            chronology2 = chronology2 != null ? chronology2 : dateTime.getChronology();
            Chronology chronology3 = chronology = chronology != null ? chronology : chronology2;
            if (period != null) {
                l = chronology.add(period, l2, -1);
            }
        }
        readWritableInterval.setInterval(l, l2);
        readWritableInterval.setChronology(chronology);
    }

    @Override
    public Class<?> getSupportedType() {
        return String.class;
    }
}

