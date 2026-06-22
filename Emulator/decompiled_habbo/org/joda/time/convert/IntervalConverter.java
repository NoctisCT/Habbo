/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.ReadWritableInterval;
import org.joda.time.convert.Converter;

public interface IntervalConverter
extends Converter {
    public boolean isReadableInterval(Object var1, Chronology var2);

    public void setInto(ReadWritableInterval var1, Object var2, Chronology var3);
}

