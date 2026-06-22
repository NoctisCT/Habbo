/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadableDuration;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.DurationConverter;
import org.joda.time.convert.PeriodConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ReadableDurationConverter
extends AbstractConverter
implements DurationConverter,
PeriodConverter {
    static final ReadableDurationConverter INSTANCE = new ReadableDurationConverter();

    protected ReadableDurationConverter() {
    }

    @Override
    public long getDurationMillis(Object object) {
        return ((ReadableDuration)object).getMillis();
    }

    @Override
    public void setInto(ReadWritablePeriod readWritablePeriod, Object object, Chronology chronology) {
        ReadableDuration readableDuration = (ReadableDuration)object;
        chronology = DateTimeUtils.getChronology(chronology);
        long l = readableDuration.getMillis();
        int[] nArray = chronology.get(readWritablePeriod, l);
        for (int i = 0; i < nArray.length; ++i) {
            readWritablePeriod.setValue(i, nArray[i]);
        }
    }

    @Override
    public Class<?> getSupportedType() {
        return ReadableDuration.class;
    }
}

