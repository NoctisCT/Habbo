/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.DurationFieldType;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public interface ReadablePeriod {
    public PeriodType getPeriodType();

    public int size();

    public DurationFieldType getFieldType(int var1);

    public int getValue(int var1);

    public int get(DurationFieldType var1);

    public boolean isSupported(DurationFieldType var1);

    public Period toPeriod();

    public MutablePeriod toMutablePeriod();

    public boolean equals(Object var1);

    public int hashCode();

    public String toString();
}

