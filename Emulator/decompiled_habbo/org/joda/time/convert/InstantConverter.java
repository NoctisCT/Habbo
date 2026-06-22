/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.convert.Converter;

public interface InstantConverter
extends Converter {
    public Chronology getChronology(Object var1, DateTimeZone var2);

    public Chronology getChronology(Object var1, Chronology var2);

    public long getInstantMillis(Object var1, Chronology var2);
}

