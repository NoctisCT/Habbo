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
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.base.AbstractInstant;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.InstantConverter;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class Instant
extends AbstractInstant
implements ReadableInstant,
Serializable {
    public static final Instant EPOCH = new Instant(0L);
    private static final long serialVersionUID = 3299096530934209741L;
    private final long iMillis;

    public static Instant now() {
        return new Instant();
    }

    public static Instant ofEpochMilli(long l) {
        return new Instant(l);
    }

    public static Instant ofEpochSecond(long l) {
        return new Instant(FieldUtils.safeMultiply(l, 1000));
    }

    @FromString
    public static Instant parse(String string) {
        return Instant.parse(string, ISODateTimeFormat.dateTimeParser());
    }

    public static Instant parse(String string, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseDateTime(string).toInstant();
    }

    public Instant() {
        this.iMillis = DateTimeUtils.currentTimeMillis();
    }

    public Instant(long l) {
        this.iMillis = l;
    }

    public Instant(Object object) {
        InstantConverter instantConverter = ConverterManager.getInstance().getInstantConverter(object);
        this.iMillis = instantConverter.getInstantMillis(object, ISOChronology.getInstanceUTC());
    }

    public Instant toInstant() {
        return this;
    }

    public Instant withMillis(long l) {
        return l == this.iMillis ? this : new Instant(l);
    }

    public Instant withDurationAdded(long l, int n) {
        if (l == 0L || n == 0) {
            return this;
        }
        long l2 = this.getChronology().add(this.getMillis(), l, n);
        return this.withMillis(l2);
    }

    public Instant withDurationAdded(ReadableDuration readableDuration, int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), n);
    }

    public Instant plus(long l) {
        return this.withDurationAdded(l, 1);
    }

    public Instant plus(ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, 1);
    }

    public Instant minus(long l) {
        return this.withDurationAdded(l, -1);
    }

    public Instant minus(ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, -1);
    }

    public long getMillis() {
        return this.iMillis;
    }

    public Chronology getChronology() {
        return ISOChronology.getInstanceUTC();
    }

    public DateTime toDateTime() {
        return new DateTime(this.getMillis(), (Chronology)ISOChronology.getInstance());
    }

    @Deprecated
    public DateTime toDateTimeISO() {
        return this.toDateTime();
    }

    public MutableDateTime toMutableDateTime() {
        return new MutableDateTime(this.getMillis(), (Chronology)ISOChronology.getInstance());
    }

    @Deprecated
    public MutableDateTime toMutableDateTimeISO() {
        return this.toMutableDateTime();
    }
}

