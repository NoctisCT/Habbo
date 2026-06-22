/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.PeriodType;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadablePeriod;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.PeriodConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ReadablePeriodConverter
extends AbstractConverter
implements PeriodConverter {
    static final ReadablePeriodConverter INSTANCE = new ReadablePeriodConverter();

    protected ReadablePeriodConverter() {
    }

    @Override
    public void setInto(ReadWritablePeriod readWritablePeriod, Object object, Chronology chronology) {
        readWritablePeriod.setPeriod((ReadablePeriod)object);
    }

    @Override
    public PeriodType getPeriodType(Object object) {
        ReadablePeriod readablePeriod = (ReadablePeriod)object;
        return readablePeriod.getPeriodType();
    }

    @Override
    public Class<?> getSupportedType() {
        return ReadablePeriod.class;
    }
}

