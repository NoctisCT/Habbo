/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableInstant;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ReadablePartial
extends Comparable<ReadablePartial> {
    public int size();

    public DateTimeFieldType getFieldType(int var1);

    public DateTimeField getField(int var1);

    public int getValue(int var1);

    public Chronology getChronology();

    public int get(DateTimeFieldType var1);

    public boolean isSupported(DateTimeFieldType var1);

    public DateTime toDateTime(ReadableInstant var1);

    public boolean equals(Object var1);

    public int hashCode();

    public String toString();
}

