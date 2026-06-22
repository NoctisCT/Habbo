/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.util.Locale;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;

public abstract class DateTimeField {
    public abstract DateTimeFieldType getType();

    public abstract String getName();

    public abstract boolean isSupported();

    public abstract boolean isLenient();

    public abstract int get(long var1);

    public abstract String getAsText(long var1, Locale var3);

    public abstract String getAsText(long var1);

    public abstract String getAsText(ReadablePartial var1, int var2, Locale var3);

    public abstract String getAsText(ReadablePartial var1, Locale var2);

    public abstract String getAsText(int var1, Locale var2);

    public abstract String getAsShortText(long var1, Locale var3);

    public abstract String getAsShortText(long var1);

    public abstract String getAsShortText(ReadablePartial var1, int var2, Locale var3);

    public abstract String getAsShortText(ReadablePartial var1, Locale var2);

    public abstract String getAsShortText(int var1, Locale var2);

    public abstract long add(long var1, int var3);

    public abstract long add(long var1, long var3);

    public abstract int[] add(ReadablePartial var1, int var2, int[] var3, int var4);

    public abstract int[] addWrapPartial(ReadablePartial var1, int var2, int[] var3, int var4);

    public abstract long addWrapField(long var1, int var3);

    public abstract int[] addWrapField(ReadablePartial var1, int var2, int[] var3, int var4);

    public abstract int getDifference(long var1, long var3);

    public abstract long getDifferenceAsLong(long var1, long var3);

    public abstract long set(long var1, int var3);

    public long setExtended(long l, int n) {
        return this.set(l, n);
    }

    public abstract int[] set(ReadablePartial var1, int var2, int[] var3, int var4);

    public abstract long set(long var1, String var3, Locale var4);

    public abstract long set(long var1, String var3);

    public abstract int[] set(ReadablePartial var1, int var2, int[] var3, String var4, Locale var5);

    public abstract DurationField getDurationField();

    public abstract DurationField getRangeDurationField();

    public abstract boolean isLeap(long var1);

    public abstract int getLeapAmount(long var1);

    public abstract DurationField getLeapDurationField();

    public abstract int getMinimumValue();

    public abstract int getMinimumValue(long var1);

    public abstract int getMinimumValue(ReadablePartial var1);

    public abstract int getMinimumValue(ReadablePartial var1, int[] var2);

    public abstract int getMaximumValue();

    public abstract int getMaximumValue(long var1);

    public abstract int getMaximumValue(ReadablePartial var1);

    public abstract int getMaximumValue(ReadablePartial var1, int[] var2);

    public abstract int getMaximumTextLength(Locale var1);

    public abstract int getMaximumShortTextLength(Locale var1);

    public abstract long roundFloor(long var1);

    public abstract long roundCeiling(long var1);

    public abstract long roundHalfFloor(long var1);

    public abstract long roundHalfCeiling(long var1);

    public abstract long roundHalfEven(long var1);

    public abstract long remainder(long var1);

    public abstract String toString();
}

