/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.DurationConverter;
import org.joda.time.convert.InstantConverter;
import org.joda.time.convert.PartialConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class LongConverter
extends AbstractConverter
implements InstantConverter,
PartialConverter,
DurationConverter {
    static final LongConverter INSTANCE = new LongConverter();

    protected LongConverter() {
    }

    @Override
    public long getInstantMillis(Object object, Chronology chronology) {
        return (Long)object;
    }

    @Override
    public long getDurationMillis(Object object) {
        return (Long)object;
    }

    @Override
    public Class<?> getSupportedType() {
        return Long.class;
    }
}

