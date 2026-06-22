/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadWritableInterval;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadablePeriod;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.DurationConverter;
import org.joda.time.convert.InstantConverter;
import org.joda.time.convert.IntervalConverter;
import org.joda.time.convert.PartialConverter;
import org.joda.time.convert.PeriodConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class NullConverter
extends AbstractConverter
implements InstantConverter,
PartialConverter,
DurationConverter,
PeriodConverter,
IntervalConverter {
    static final NullConverter INSTANCE = new NullConverter();

    protected NullConverter() {
    }

    @Override
    public long getDurationMillis(Object object) {
        return 0L;
    }

    @Override
    public void setInto(ReadWritablePeriod readWritablePeriod, Object object, Chronology chronology) {
        readWritablePeriod.setPeriod((ReadablePeriod)null);
    }

    @Override
    public void setInto(ReadWritableInterval readWritableInterval, Object object, Chronology chronology) {
        readWritableInterval.setChronology(chronology);
        long l = DateTimeUtils.currentTimeMillis();
        readWritableInterval.setInterval(l, l);
    }

    @Override
    public Class<?> getSupportedType() {
        return null;
    }
}

