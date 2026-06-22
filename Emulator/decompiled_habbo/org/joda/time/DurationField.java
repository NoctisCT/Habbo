/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.DurationFieldType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class DurationField
implements Comparable<DurationField> {
    public abstract DurationFieldType getType();

    public abstract String getName();

    public abstract boolean isSupported();

    public abstract boolean isPrecise();

    public abstract long getUnitMillis();

    public abstract int getValue(long var1);

    public abstract long getValueAsLong(long var1);

    public abstract int getValue(long var1, long var3);

    public abstract long getValueAsLong(long var1, long var3);

    public abstract long getMillis(int var1);

    public abstract long getMillis(long var1);

    public abstract long getMillis(int var1, long var2);

    public abstract long getMillis(long var1, long var3);

    public abstract long add(long var1, int var3);

    public abstract long add(long var1, long var3);

    public long subtract(long l, int n) {
        if (n == Integer.MIN_VALUE) {
            return this.subtract(l, (long)n);
        }
        return this.add(l, -n);
    }

    public long subtract(long l, long l2) {
        if (l2 == Long.MIN_VALUE) {
            throw new ArithmeticException("Long.MIN_VALUE cannot be negated");
        }
        return this.add(l, -l2);
    }

    public abstract int getDifference(long var1, long var3);

    public abstract long getDifferenceAsLong(long var1, long var3);

    public abstract String toString();
}

