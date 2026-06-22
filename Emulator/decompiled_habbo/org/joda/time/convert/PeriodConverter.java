/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.Chronology;
import org.joda.time.PeriodType;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.convert.Converter;

public interface PeriodConverter
extends Converter {
    public void setInto(ReadWritablePeriod var1, Object var2, Chronology var3);

    public PeriodType getPeriodType(Object var1);
}

