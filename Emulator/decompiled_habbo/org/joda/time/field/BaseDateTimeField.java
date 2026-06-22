/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.util.Locale;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.ReadablePartial;
import org.joda.time.field.FieldUtils;

public abstract class BaseDateTimeField
extends DateTimeField {
    private final DateTimeFieldType iType;

    protected BaseDateTimeField(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        this.iType = dateTimeFieldType;
    }

    public final DateTimeFieldType getType() {
        return this.iType;
    }

    public final String getName() {
        return this.iType.getName();
    }

    public final boolean isSupported() {
        return true;
    }

    public abstract int get(long var1);

    public String getAsText(long l, Locale locale) {
        return this.getAsText(this.get(l), locale);
    }

    public final String getAsText(long l) {
        return this.getAsText(l, null);
    }

    public String getAsText(ReadablePartial readablePartial, int n, Locale locale) {
        return this.getAsText(n, locale);
    }

    public final String getAsText(ReadablePartial readablePartial, Locale locale) {
        return this.getAsText(readablePartial, readablePartial.get(this.getType()), locale);
    }

    public String getAsText(int n, Locale locale) {
        return Integer.toString(n);
    }

    public String getAsShortText(long l, Locale locale) {
        return this.getAsShortText(this.get(l), locale);
    }

    public final String getAsShortText(long l) {
        return this.getAsShortText(l, null);
    }

    public String getAsShortText(ReadablePartial readablePartial, int n, Locale locale) {
        return this.getAsShortText(n, locale);
    }

    public final String getAsShortText(ReadablePartial readablePartial, Locale locale) {
        return this.getAsShortText(readablePartial, readablePartial.get(this.getType()), locale);
    }

    public String getAsShortText(int n, Locale locale) {
        return this.getAsText(n, locale);
    }

    public long add(long l, int n) {
        return this.getDurationField().add(l, n);
    }

    public long add(long l, long l2) {
        return this.getDurationField().add(l, l2);
    }

    public int[] add(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        int n3;
        long l;
        if (n2 == 0) {
            return nArray;
        }
        DateTimeField dateTimeField = null;
        while (n2 > 0) {
            l = nArray[n] + n2;
            n3 = this.getMaximumValue(readablePartial, nArray);
            if (l <= (long)n3) {
                nArray[n] = (int)l;
                break;
            }
            if (dateTimeField == null) {
                if (n == 0) {
                    throw new IllegalArgumentException("Maximum value exceeded for add");
                }
                dateTimeField = readablePartial.getField(n - 1);
                if (this.getRangeDurationField().getType() != dateTimeField.getDurationField().getType()) {
                    throw new IllegalArgumentException("Fields invalid for add");
                }
            }
            n2 -= n3 + 1 - nArray[n];
            nArray = dateTimeField.add(readablePartial, n - 1, nArray, 1);
            nArray[n] = this.getMinimumValue(readablePartial, nArray);
        }
        while (n2 < 0) {
            l = nArray[n] + n2;
            n3 = this.getMinimumValue(readablePartial, nArray);
            if (l >= (long)n3) {
                nArray[n] = (int)l;
                break;
            }
            if (dateTimeField == null) {
                if (n == 0) {
                    throw new IllegalArgumentException("Maximum value exceeded for add");
                }
                dateTimeField = readablePartial.getField(n - 1);
                if (this.getRangeDurationField().getType() != dateTimeField.getDurationField().getType()) {
                    throw new IllegalArgumentException("Fields invalid for add");
                }
            }
            n2 -= n3 - 1 - nArray[n];
            nArray = dateTimeField.add(readablePartial, n - 1, nArray, -1);
            nArray[n] = this.getMaximumValue(readablePartial, nArray);
        }
        return this.set(readablePartial, n, nArray, nArray[n]);
    }

    public int[] addWrapPartial(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        int n3;
        long l;
        if (n2 == 0) {
            return nArray;
        }
        DateTimeField dateTimeField = null;
        while (n2 > 0) {
            l = nArray[n] + n2;
            n3 = this.getMaximumValue(readablePartial, nArray);
            if (l <= (long)n3) {
                nArray[n] = (int)l;
                break;
            }
            if (dateTimeField == null) {
                if (n == 0) {
                    n2 -= n3 + 1 - nArray[n];
                    nArray[n] = this.getMinimumValue(readablePartial, nArray);
                    continue;
                }
                dateTimeField = readablePartial.getField(n - 1);
                if (this.getRangeDurationField().getType() != dateTimeField.getDurationField().getType()) {
                    throw new IllegalArgumentException("Fields invalid for add");
                }
            }
            n2 -= n3 + 1 - nArray[n];
            nArray = dateTimeField.addWrapPartial(readablePartial, n - 1, nArray, 1);
            nArray[n] = this.getMinimumValue(readablePartial, nArray);
        }
        while (n2 < 0) {
            l = nArray[n] + n2;
            n3 = this.getMinimumValue(readablePartial, nArray);
            if (l >= (long)n3) {
                nArray[n] = (int)l;
                break;
            }
            if (dateTimeField == null) {
                if (n == 0) {
                    n2 -= n3 - 1 - nArray[n];
                    nArray[n] = this.getMaximumValue(readablePartial, nArray);
                    continue;
                }
                dateTimeField = readablePartial.getField(n - 1);
                if (this.getRangeDurationField().getType() != dateTimeField.getDurationField().getType()) {
                    throw new IllegalArgumentException("Fields invalid for add");
                }
            }
            n2 -= n3 - 1 - nArray[n];
            nArray = dateTimeField.addWrapPartial(readablePartial, n - 1, nArray, -1);
            nArray[n] = this.getMaximumValue(readablePartial, nArray);
        }
        return this.set(readablePartial, n, nArray, nArray[n]);
    }

    public long addWrapField(long l, int n) {
        int n2 = this.get(l);
        int n3 = FieldUtils.getWrappedValue(n2, n, this.getMinimumValue(l), this.getMaximumValue(l));
        return this.set(l, n3);
    }

    public int[] addWrapField(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        int n3 = nArray[n];
        int n4 = FieldUtils.getWrappedValue(n3, n2, this.getMinimumValue(readablePartial), this.getMaximumValue(readablePartial));
        return this.set(readablePartial, n, nArray, n4);
    }

    public int getDifference(long l, long l2) {
        return this.getDurationField().getDifference(l, l2);
    }

    public long getDifferenceAsLong(long l, long l2) {
        return this.getDurationField().getDifferenceAsLong(l, l2);
    }

    public abstract long set(long var1, int var3);

    public int[] set(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        FieldUtils.verifyValueBounds(this, n2, this.getMinimumValue(readablePartial, nArray), this.getMaximumValue(readablePartial, nArray));
        nArray[n] = n2;
        for (int i = n + 1; i < readablePartial.size(); ++i) {
            DateTimeField dateTimeField = readablePartial.getField(i);
            if (nArray[i] > dateTimeField.getMaximumValue(readablePartial, nArray)) {
                nArray[i] = dateTimeField.getMaximumValue(readablePartial, nArray);
            }
            if (nArray[i] >= dateTimeField.getMinimumValue(readablePartial, nArray)) continue;
            nArray[i] = dateTimeField.getMinimumValue(readablePartial, nArray);
        }
        return nArray;
    }

    public long set(long l, String string, Locale locale) {
        int n = this.convertText(string, locale);
        return this.set(l, n);
    }

    public final long set(long l, String string) {
        return this.set(l, string, null);
    }

    public int[] set(ReadablePartial readablePartial, int n, int[] nArray, String string, Locale locale) {
        int n2 = this.convertText(string, locale);
        return this.set(readablePartial, n, nArray, n2);
    }

    protected int convertText(String string, Locale locale) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            throw new IllegalFieldValueException(this.getType(), string);
        }
    }

    public abstract DurationField getDurationField();

    public abstract DurationField getRangeDurationField();

    public boolean isLeap(long l) {
        return false;
    }

    public int getLeapAmount(long l) {
        return 0;
    }

    public DurationField getLeapDurationField() {
        return null;
    }

    public abstract int getMinimumValue();

    public int getMinimumValue(long l) {
        return this.getMinimumValue();
    }

    public int getMinimumValue(ReadablePartial readablePartial) {
        return this.getMinimumValue();
    }

    public int getMinimumValue(ReadablePartial readablePartial, int[] nArray) {
        return this.getMinimumValue(readablePartial);
    }

    public abstract int getMaximumValue();

    public int getMaximumValue(long l) {
        return this.getMaximumValue();
    }

    public int getMaximumValue(ReadablePartial readablePartial) {
        return this.getMaximumValue();
    }

    public int getMaximumValue(ReadablePartial readablePartial, int[] nArray) {
        return this.getMaximumValue(readablePartial);
    }

    public int getMaximumTextLength(Locale locale) {
        int n = this.getMaximumValue();
        if (n >= 0) {
            if (n < 10) {
                return 1;
            }
            if (n < 100) {
                return 2;
            }
            if (n < 1000) {
                return 3;
            }
        }
        return Integer.toString(n).length();
    }

    public int getMaximumShortTextLength(Locale locale) {
        return this.getMaximumTextLength(locale);
    }

    public abstract long roundFloor(long var1);

    public long roundCeiling(long l) {
        long l2 = this.roundFloor(l);
        if (l2 != l) {
            l = this.add(l2, 1);
        }
        return l;
    }

    public long roundHalfFloor(long l) {
        long l2;
        long l3;
        long l4 = this.roundFloor(l);
        long l5 = l - l4;
        if (l5 <= (l3 = (l2 = this.roundCeiling(l)) - l)) {
            return l4;
        }
        return l2;
    }

    public long roundHalfCeiling(long l) {
        long l2;
        long l3 = this.roundFloor(l);
        long l4 = this.roundCeiling(l);
        long l5 = l4 - l;
        if (l5 <= (l2 = l - l3)) {
            return l4;
        }
        return l3;
    }

    public long roundHalfEven(long l) {
        long l2;
        long l3;
        long l4 = this.roundFloor(l);
        long l5 = l - l4;
        if (l5 < (l3 = (l2 = this.roundCeiling(l)) - l)) {
            return l4;
        }
        if (l3 < l5) {
            return l2;
        }
        if ((this.get(l2) & 1) == 0) {
            return l2;
        }
        return l4;
    }

    public long remainder(long l) {
        return l - this.roundFloor(l);
    }

    public String toString() {
        return "DateTimeField[" + this.getName() + ']';
    }
}

