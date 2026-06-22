/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.InstantConverter;
import org.joda.time.convert.PartialConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ReadableInstantConverter
extends AbstractConverter
implements InstantConverter,
PartialConverter {
    static final ReadableInstantConverter INSTANCE = new ReadableInstantConverter();

    protected ReadableInstantConverter() {
    }

    @Override
    public Chronology getChronology(Object object, DateTimeZone dateTimeZone) {
        Chronology chronology = ((ReadableInstant)object).getChronology();
        if (chronology == null) {
            return ISOChronology.getInstance(dateTimeZone);
        }
        DateTimeZone dateTimeZone2 = chronology.getZone();
        if (dateTimeZone2 != dateTimeZone && (chronology = chronology.withZone(dateTimeZone)) == null) {
            return ISOChronology.getInstance(dateTimeZone);
        }
        return chronology;
    }

    @Override
    public Chronology getChronology(Object object, Chronology chronology) {
        if (chronology == null) {
            chronology = ((ReadableInstant)object).getChronology();
            chronology = DateTimeUtils.getChronology(chronology);
        }
        return chronology;
    }

    @Override
    public long getInstantMillis(Object object, Chronology chronology) {
        return ((ReadableInstant)object).getMillis();
    }

    @Override
    public Class<?> getSupportedType() {
        return ReadableInstant.class;
    }
}

