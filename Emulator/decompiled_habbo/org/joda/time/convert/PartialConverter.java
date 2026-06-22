/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.joda.time.convert.Converter;
import org.joda.time.format.DateTimeFormatter;

public interface PartialConverter
extends Converter {
    public Chronology getChronology(Object var1, DateTimeZone var2);

    public Chronology getChronology(Object var1, Chronology var2);

    public int[] getPartialValues(ReadablePartial var1, Object var2, Chronology var3);

    public int[] getPartialValues(ReadablePartial var1, Object var2, Chronology var3, DateTimeFormatter var4);
}

