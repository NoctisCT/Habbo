/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.Chronology;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ReadableInstant
extends Comparable<ReadableInstant> {
    public long getMillis();

    public Chronology getChronology();

    public DateTimeZone getZone();

    public int get(DateTimeFieldType var1);

    public boolean isSupported(DateTimeFieldType var1);

    public Instant toInstant();

    public boolean isEqual(ReadableInstant var1);

    public boolean isAfter(ReadableInstant var1);

    public boolean isBefore(ReadableInstant var1);

    public boolean equals(Object var1);

    public int hashCode();

    public String toString();
}

