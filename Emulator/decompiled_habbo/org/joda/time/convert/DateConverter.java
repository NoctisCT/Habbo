/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import java.util.Date;
import org.joda.time.Chronology;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.InstantConverter;
import org.joda.time.convert.PartialConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DateConverter
extends AbstractConverter
implements InstantConverter,
PartialConverter {
    static final DateConverter INSTANCE = new DateConverter();

    protected DateConverter() {
    }

    @Override
    public long getInstantMillis(Object object, Chronology chronology) {
        Date date = (Date)object;
        return date.getTime();
    }

    @Override
    public Class<?> getSupportedType() {
        return Date.class;
    }
}

