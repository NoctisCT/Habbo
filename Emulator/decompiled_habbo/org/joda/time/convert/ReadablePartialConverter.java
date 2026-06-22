/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.joda.time.convert.AbstractConverter;
import org.joda.time.convert.PartialConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ReadablePartialConverter
extends AbstractConverter
implements PartialConverter {
    static final ReadablePartialConverter INSTANCE = new ReadablePartialConverter();

    protected ReadablePartialConverter() {
    }

    @Override
    public Chronology getChronology(Object object, DateTimeZone dateTimeZone) {
        return this.getChronology(object, (Chronology)null).withZone(dateTimeZone);
    }

    @Override
    public Chronology getChronology(Object object, Chronology chronology) {
        if (chronology == null) {
            chronology = ((ReadablePartial)object).getChronology();
            chronology = DateTimeUtils.getChronology(chronology);
        }
        return chronology;
    }

    @Override
    public int[] getPartialValues(ReadablePartial readablePartial, Object object, Chronology chronology) {
        ReadablePartial readablePartial2 = (ReadablePartial)object;
        int n = readablePartial.size();
        int[] nArray = new int[n];
        for (int i = 0; i < n; ++i) {
            nArray[i] = readablePartial2.get(readablePartial.getFieldType(i));
        }
        chronology.validate(readablePartial, nArray);
        return nArray;
    }

    @Override
    public Class<?> getSupportedType() {
        return ReadablePartial.class;
    }
}

