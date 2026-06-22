/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadWritableInterval;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadableInterval;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.DurationConverter;
import org.joda.time.convert.IntervalConverter;
import org.joda.time.convert.PeriodConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ReadableIntervalConverter
extends AbstractConverter
implements IntervalConverter,
DurationConverter,
PeriodConverter {
    static final ReadableIntervalConverter INSTANCE = new ReadableIntervalConverter();

    protected ReadableIntervalConverter() {
    }

    @Override
    public long getDurationMillis(Object object) {
        return ((ReadableInterval)object).toDurationMillis();
    }

    @Override
    public void setInto(ReadWritablePeriod readWritablePeriod, Object object, Chronology chronology) {
        ReadableInterval readableInterval = (ReadableInterval)object;
        chronology = chronology != null ? chronology : DateTimeUtils.getIntervalChronology(readableInterval);
        long l = readableInterval.getStartMillis();
        long l2 = readableInterval.getEndMillis();
        int[] nArray = chronology.get(readWritablePeriod, l, l2);
        for (int i = 0; i < nArray.length; ++i) {
            readWritablePeriod.setValue(i, nArray[i]);
        }
    }

    @Override
    public boolean isReadableInterval(Object object, Chronology chronology) {
        return true;
    }

    @Override
    public void setInto(ReadWritableInterval readWritableInterval, Object object, Chronology chronology) {
        ReadableInterval readableInterval = (ReadableInterval)object;
        readWritableInterval.setInterval(readableInterval);
        if (chronology != null) {
            readWritableInterval.setChronology(chronology);
        } else {
            readWritableInterval.setChronology(readableInterval.getChronology());
        }
    }

    @Override
    public Class<?> getSupportedType() {
        return ReadableInterval.class;
    }
}

