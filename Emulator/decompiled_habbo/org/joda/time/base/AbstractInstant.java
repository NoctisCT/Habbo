/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.ToString
 */
package org.joda.time.base;

import java.util.Date;
import org.joda.convert.ToString;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public abstract class AbstractInstant
implements ReadableInstant {
    protected AbstractInstant() {
    }

    public DateTimeZone getZone() {
        return this.getChronology().getZone();
    }

    public int get(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        return dateTimeFieldType.getField(this.getChronology()).get(this.getMillis());
    }

    public boolean isSupported(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            return false;
        }
        return dateTimeFieldType.getField(this.getChronology()).isSupported();
    }

    public int get(DateTimeField dateTimeField) {
        if (dateTimeField == null) {
            throw new IllegalArgumentException("The DateTimeField must not be null");
        }
        return dateTimeField.get(this.getMillis());
    }

    public Instant toInstant() {
        return new Instant(this.getMillis());
    }

    public DateTime toDateTime() {
        return new DateTime(this.getMillis(), this.getZone());
    }

    public DateTime toDateTimeISO() {
        return new DateTime(this.getMillis(), (Chronology)ISOChronology.getInstance(this.getZone()));
    }

    public DateTime toDateTime(DateTimeZone dateTimeZone) {
        Chronology chronology = DateTimeUtils.getChronology(this.getChronology());
        chronology = chronology.withZone(dateTimeZone);
        return new DateTime(this.getMillis(), chronology);
    }

    public DateTime toDateTime(Chronology chronology) {
        return new DateTime(this.getMillis(), chronology);
    }

    public MutableDateTime toMutableDateTime() {
        return new MutableDateTime(this.getMillis(), this.getZone());
    }

    public MutableDateTime toMutableDateTimeISO() {
        return new MutableDateTime(this.getMillis(), (Chronology)ISOChronology.getInstance(this.getZone()));
    }

    public MutableDateTime toMutableDateTime(DateTimeZone dateTimeZone) {
        Chronology chronology = DateTimeUtils.getChronology(this.getChronology());
        chronology = chronology.withZone(dateTimeZone);
        return new MutableDateTime(this.getMillis(), chronology);
    }

    public MutableDateTime toMutableDateTime(Chronology chronology) {
        return new MutableDateTime(this.getMillis(), chronology);
    }

    public Date toDate() {
        return new Date(this.getMillis());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ReadableInstant)) {
            return false;
        }
        ReadableInstant readableInstant = (ReadableInstant)object;
        return this.getMillis() == readableInstant.getMillis() && FieldUtils.equals(this.getChronology(), readableInstant.getChronology());
    }

    public int hashCode() {
        return (int)(this.getMillis() ^ this.getMillis() >>> 32) + this.getChronology().hashCode();
    }

    public int compareTo(ReadableInstant readableInstant) {
        if (this == readableInstant) {
            return 0;
        }
        long l = readableInstant.getMillis();
        long l2 = this.getMillis();
        if (l2 == l) {
            return 0;
        }
        if (l2 < l) {
            return -1;
        }
        return 1;
    }

    public boolean isAfter(long l) {
        return this.getMillis() > l;
    }

    public boolean isAfterNow() {
        return this.isAfter(DateTimeUtils.currentTimeMillis());
    }

    public boolean isAfter(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        return this.isAfter(l);
    }

    public boolean isBefore(long l) {
        return this.getMillis() < l;
    }

    public boolean isBeforeNow() {
        return this.isBefore(DateTimeUtils.currentTimeMillis());
    }

    public boolean isBefore(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        return this.isBefore(l);
    }

    public boolean isEqual(long l) {
        return this.getMillis() == l;
    }

    public boolean isEqualNow() {
        return this.isEqual(DateTimeUtils.currentTimeMillis());
    }

    public boolean isEqual(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        return this.isEqual(l);
    }

    @ToString
    public String toString() {
        return ISODateTimeFormat.dateTime().print(this);
    }

    public String toString(DateTimeFormatter dateTimeFormatter) {
        if (dateTimeFormatter == null) {
            return this.toString();
        }
        return dateTimeFormatter.print(this);
    }
}

