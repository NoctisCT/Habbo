/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.PeriodType;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.convert.Converter;
import org.joda.time.format.DateTimeFormatter;

public abstract class AbstractConverter
implements Converter {
    protected AbstractConverter() {
    }

    public long getInstantMillis(Object object, Chronology chronology) {
        return DateTimeUtils.currentTimeMillis();
    }

    public Chronology getChronology(Object object, DateTimeZone dateTimeZone) {
        return ISOChronology.getInstance(dateTimeZone);
    }

    public Chronology getChronology(Object object, Chronology chronology) {
        return DateTimeUtils.getChronology(chronology);
    }

    public int[] getPartialValues(ReadablePartial readablePartial, Object object, Chronology chronology) {
        long l = this.getInstantMillis(object, chronology);
        return chronology.get(readablePartial, l);
    }

    public int[] getPartialValues(ReadablePartial readablePartial, Object object, Chronology chronology, DateTimeFormatter dateTimeFormatter) {
        return this.getPartialValues(readablePartial, object, chronology);
    }

    public PeriodType getPeriodType(Object object) {
        return PeriodType.standard();
    }

    public boolean isReadableInterval(Object object, Chronology chronology) {
        return false;
    }

    public String toString() {
        return "Converter[" + (this.getSupportedType() == null ? "null" : this.getSupportedType().getName()) + "]";
    }
}

