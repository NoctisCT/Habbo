/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;

public final class UnsupportedDateTimeField
extends DateTimeField
implements Serializable {
    private static final long serialVersionUID = -1934618396111902255L;
    private static HashMap<DateTimeFieldType, UnsupportedDateTimeField> cCache;
    private final DateTimeFieldType iType;
    private final DurationField iDurationField;

    public static synchronized UnsupportedDateTimeField getInstance(DateTimeFieldType dateTimeFieldType, DurationField durationField) {
        UnsupportedDateTimeField unsupportedDateTimeField;
        if (cCache == null) {
            cCache = new HashMap(7);
            unsupportedDateTimeField = null;
        } else {
            unsupportedDateTimeField = cCache.get(dateTimeFieldType);
            if (unsupportedDateTimeField != null && unsupportedDateTimeField.getDurationField() != durationField) {
                unsupportedDateTimeField = null;
            }
        }
        if (unsupportedDateTimeField == null) {
            unsupportedDateTimeField = new UnsupportedDateTimeField(dateTimeFieldType, durationField);
            cCache.put(dateTimeFieldType, unsupportedDateTimeField);
        }
        return unsupportedDateTimeField;
    }

    private UnsupportedDateTimeField(DateTimeFieldType dateTimeFieldType, DurationField durationField) {
        if (dateTimeFieldType == null || durationField == null) {
            throw new IllegalArgumentException();
        }
        this.iType = dateTimeFieldType;
        this.iDurationField = durationField;
    }

    public DateTimeFieldType getType() {
        return this.iType;
    }

    public String getName() {
        return this.iType.getName();
    }

    public boolean isSupported() {
        return false;
    }

    public boolean isLenient() {
        return false;
    }

    public int get(long l) {
        throw this.unsupported();
    }

    public String getAsText(long l, Locale locale) {
        throw this.unsupported();
    }

    public String getAsText(long l) {
        throw this.unsupported();
    }

    public String getAsText(ReadablePartial readablePartial, int n, Locale locale) {
        throw this.unsupported();
    }

    public String getAsText(ReadablePartial readablePartial, Locale locale) {
        throw this.unsupported();
    }

    public String getAsText(int n, Locale locale) {
        throw this.unsupported();
    }

    public String getAsShortText(long l, Locale locale) {
        throw this.unsupported();
    }

    public String getAsShortText(long l) {
        throw this.unsupported();
    }

    public String getAsShortText(ReadablePartial readablePartial, int n, Locale locale) {
        throw this.unsupported();
    }

    public String getAsShortText(ReadablePartial readablePartial, Locale locale) {
        throw this.unsupported();
    }

    public String getAsShortText(int n, Locale locale) {
        throw this.unsupported();
    }

    public long add(long l, int n) {
        return this.getDurationField().add(l, n);
    }

    public long add(long l, long l2) {
        return this.getDurationField().add(l, l2);
    }

    public int[] add(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        throw this.unsupported();
    }

    public int[] addWrapPartial(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        throw this.unsupported();
    }

    public long addWrapField(long l, int n) {
        throw this.unsupported();
    }

    public int[] addWrapField(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        throw this.unsupported();
    }

    public int getDifference(long l, long l2) {
        return this.getDurationField().getDifference(l, l2);
    }

    public long getDifferenceAsLong(long l, long l2) {
        return this.getDurationField().getDifferenceAsLong(l, l2);
    }

    public long set(long l, int n) {
        throw this.unsupported();
    }

    public int[] set(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        throw this.unsupported();
    }

    public long set(long l, String string, Locale locale) {
        throw this.unsupported();
    }

    public long set(long l, String string) {
        throw this.unsupported();
    }

    public int[] set(ReadablePartial readablePartial, int n, int[] nArray, String string, Locale locale) {
        throw this.unsupported();
    }

    public DurationField getDurationField() {
        return this.iDurationField;
    }

    public DurationField getRangeDurationField() {
        return null;
    }

    public boolean isLeap(long l) {
        throw this.unsupported();
    }

    public int getLeapAmount(long l) {
        throw this.unsupported();
    }

    public DurationField getLeapDurationField() {
        return null;
    }

    public int getMinimumValue() {
        throw this.unsupported();
    }

    public int getMinimumValue(long l) {
        throw this.unsupported();
    }

    public int getMinimumValue(ReadablePartial readablePartial) {
        throw this.unsupported();
    }

    public int getMinimumValue(ReadablePartial readablePartial, int[] nArray) {
        throw this.unsupported();
    }

    public int getMaximumValue() {
        throw this.unsupported();
    }

    public int getMaximumValue(long l) {
        throw this.unsupported();
    }

    public int getMaximumValue(ReadablePartial readablePartial) {
        throw this.unsupported();
    }

    public int getMaximumValue(ReadablePartial readablePartial, int[] nArray) {
        throw this.unsupported();
    }

    public int getMaximumTextLength(Locale locale) {
        throw this.unsupported();
    }

    public int getMaximumShortTextLength(Locale locale) {
        throw this.unsupported();
    }

    public long roundFloor(long l) {
        throw this.unsupported();
    }

    public long roundCeiling(long l) {
        throw this.unsupported();
    }

    public long roundHalfFloor(long l) {
        throw this.unsupported();
    }

    public long roundHalfCeiling(long l) {
        throw this.unsupported();
    }

    public long roundHalfEven(long l) {
        throw this.unsupported();
    }

    public long remainder(long l) {
        throw this.unsupported();
    }

    public String toString() {
        return "UnsupportedDateTimeField";
    }

    private Object readResolve() {
        return UnsupportedDateTimeField.getInstance(this.iType, this.iDurationField);
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(this.iType + " field is unsupported");
    }
}

