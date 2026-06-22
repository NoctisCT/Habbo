/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.Locale;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.field.BaseDateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.UnsupportedDurationField;

final class BasicSingleEraDateTimeField
extends BaseDateTimeField {
    private static final int ERA_VALUE = 1;
    private final String iEraText;

    BasicSingleEraDateTimeField(String string) {
        super(DateTimeFieldType.era());
        this.iEraText = string;
    }

    public boolean isLenient() {
        return false;
    }

    public int get(long l) {
        return 1;
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, 1, 1);
        return l;
    }

    public long set(long l, String string, Locale locale) {
        if (!this.iEraText.equals(string) && !"1".equals(string)) {
            throw new IllegalFieldValueException(DateTimeFieldType.era(), string);
        }
        return l;
    }

    public long roundFloor(long l) {
        return Long.MIN_VALUE;
    }

    public long roundCeiling(long l) {
        return Long.MAX_VALUE;
    }

    public long roundHalfFloor(long l) {
        return Long.MIN_VALUE;
    }

    public long roundHalfCeiling(long l) {
        return Long.MIN_VALUE;
    }

    public long roundHalfEven(long l) {
        return Long.MIN_VALUE;
    }

    public DurationField getDurationField() {
        return UnsupportedDurationField.getInstance(DurationFieldType.eras());
    }

    public DurationField getRangeDurationField() {
        return null;
    }

    public int getMinimumValue() {
        return 1;
    }

    public int getMaximumValue() {
        return 1;
    }

    public String getAsText(int n, Locale locale) {
        return this.iEraText;
    }

    public int getMaximumTextLength(Locale locale) {
        return this.iEraText.length();
    }
}

