/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.io.Serializable;
import java.util.Locale;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;

public class DelegatedDateTimeField
extends DateTimeField
implements Serializable {
    private static final long serialVersionUID = -4730164440214502503L;
    private final DateTimeField iField;
    private final DurationField iRangeDurationField;
    private final DateTimeFieldType iType;

    public DelegatedDateTimeField(DateTimeField dateTimeField) {
        this(dateTimeField, null);
    }

    public DelegatedDateTimeField(DateTimeField dateTimeField, DateTimeFieldType dateTimeFieldType) {
        this(dateTimeField, null, dateTimeFieldType);
    }

    public DelegatedDateTimeField(DateTimeField dateTimeField, DurationField durationField, DateTimeFieldType dateTimeFieldType) {
        if (dateTimeField == null) {
            throw new IllegalArgumentException("The field must not be null");
        }
        this.iField = dateTimeField;
        this.iRangeDurationField = durationField;
        this.iType = dateTimeFieldType == null ? dateTimeField.getType() : dateTimeFieldType;
    }

    public final DateTimeField getWrappedField() {
        return this.iField;
    }

    public DateTimeFieldType getType() {
        return this.iType;
    }

    public String getName() {
        return this.iType.getName();
    }

    public boolean isSupported() {
        return this.iField.isSupported();
    }

    public boolean isLenient() {
        return this.iField.isLenient();
    }

    public int get(long l) {
        return this.iField.get(l);
    }

    public String getAsText(long l, Locale locale) {
        return this.iField.getAsText(l, locale);
    }

    public String getAsText(long l) {
        return this.iField.getAsText(l);
    }

    public String getAsText(ReadablePartial readablePartial, int n, Locale locale) {
        return this.iField.getAsText(readablePartial, n, locale);
    }

    public String getAsText(ReadablePartial readablePartial, Locale locale) {
        return this.iField.getAsText(readablePartial, locale);
    }

    public String getAsText(int n, Locale locale) {
        return this.iField.getAsText(n, locale);
    }

    public String getAsShortText(long l, Locale locale) {
        return this.iField.getAsShortText(l, locale);
    }

    public String getAsShortText(long l) {
        return this.iField.getAsShortText(l);
    }

    public String getAsShortText(ReadablePartial readablePartial, int n, Locale locale) {
        return this.iField.getAsShortText(readablePartial, n, locale);
    }

    public String getAsShortText(ReadablePartial readablePartial, Locale locale) {
        return this.iField.getAsShortText(readablePartial, locale);
    }

    public String getAsShortText(int n, Locale locale) {
        return this.iField.getAsShortText(n, locale);
    }

    public long add(long l, int n) {
        return this.iField.add(l, n);
    }

    public long add(long l, long l2) {
        return this.iField.add(l, l2);
    }

    public int[] add(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        return this.iField.add(readablePartial, n, nArray, n2);
    }

    public int[] addWrapPartial(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        return this.iField.addWrapPartial(readablePartial, n, nArray, n2);
    }

    public long addWrapField(long l, int n) {
        return this.iField.addWrapField(l, n);
    }

    public int[] addWrapField(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        return this.iField.addWrapField(readablePartial, n, nArray, n2);
    }

    public int getDifference(long l, long l2) {
        return this.iField.getDifference(l, l2);
    }

    public long getDifferenceAsLong(long l, long l2) {
        return this.iField.getDifferenceAsLong(l, l2);
    }

    public long set(long l, int n) {
        return this.iField.set(l, n);
    }

    public long set(long l, String string, Locale locale) {
        return this.iField.set(l, string, locale);
    }

    public long set(long l, String string) {
        return this.iField.set(l, string);
    }

    public int[] set(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        return this.iField.set(readablePartial, n, nArray, n2);
    }

    public int[] set(ReadablePartial readablePartial, int n, int[] nArray, String string, Locale locale) {
        return this.iField.set(readablePartial, n, nArray, string, locale);
    }

    public DurationField getDurationField() {
        return this.iField.getDurationField();
    }

    public DurationField getRangeDurationField() {
        if (this.iRangeDurationField != null) {
            return this.iRangeDurationField;
        }
        return this.iField.getRangeDurationField();
    }

    public boolean isLeap(long l) {
        return this.iField.isLeap(l);
    }

    public int getLeapAmount(long l) {
        return this.iField.getLeapAmount(l);
    }

    public DurationField getLeapDurationField() {
        return this.iField.getLeapDurationField();
    }

    public int getMinimumValue() {
        return this.iField.getMinimumValue();
    }

    public int getMinimumValue(long l) {
        return this.iField.getMinimumValue(l);
    }

    public int getMinimumValue(ReadablePartial readablePartial) {
        return this.iField.getMinimumValue(readablePartial);
    }

    public int getMinimumValue(ReadablePartial readablePartial, int[] nArray) {
        return this.iField.getMinimumValue(readablePartial, nArray);
    }

    public int getMaximumValue() {
        return this.iField.getMaximumValue();
    }

    public int getMaximumValue(long l) {
        return this.iField.getMaximumValue(l);
    }

    public int getMaximumValue(ReadablePartial readablePartial) {
        return this.iField.getMaximumValue(readablePartial);
    }

    public int getMaximumValue(ReadablePartial readablePartial, int[] nArray) {
        return this.iField.getMaximumValue(readablePartial, nArray);
    }

    public int getMaximumTextLength(Locale locale) {
        return this.iField.getMaximumTextLength(locale);
    }

    public int getMaximumShortTextLength(Locale locale) {
        return this.iField.getMaximumShortTextLength(locale);
    }

    public long roundFloor(long l) {
        return this.iField.roundFloor(l);
    }

    public long roundCeiling(long l) {
        return this.iField.roundCeiling(l);
    }

    public long roundHalfFloor(long l) {
        return this.iField.roundHalfFloor(l);
    }

    public long roundHalfCeiling(long l) {
        return this.iField.roundHalfCeiling(l);
    }

    public long roundHalfEven(long l) {
        return this.iField.roundHalfEven(l);
    }

    public long remainder(long l) {
        return this.iField.remainder(l);
    }

    public String toString() {
        return "DateTimeField[" + this.getName() + ']';
    }
}

