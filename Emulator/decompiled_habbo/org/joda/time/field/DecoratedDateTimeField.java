/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.field.BaseDateTimeField;

public abstract class DecoratedDateTimeField
extends BaseDateTimeField {
    private static final long serialVersionUID = 203115783733757597L;
    private final DateTimeField iField;

    protected DecoratedDateTimeField(DateTimeField dateTimeField, DateTimeFieldType dateTimeFieldType) {
        super(dateTimeFieldType);
        if (dateTimeField == null) {
            throw new IllegalArgumentException("The field must not be null");
        }
        if (!dateTimeField.isSupported()) {
            throw new IllegalArgumentException("The field must be supported");
        }
        this.iField = dateTimeField;
    }

    public final DateTimeField getWrappedField() {
        return this.iField;
    }

    public boolean isLenient() {
        return this.iField.isLenient();
    }

    public int get(long l) {
        return this.iField.get(l);
    }

    public long set(long l, int n) {
        return this.iField.set(l, n);
    }

    public DurationField getDurationField() {
        return this.iField.getDurationField();
    }

    public DurationField getRangeDurationField() {
        return this.iField.getRangeDurationField();
    }

    public int getMinimumValue() {
        return this.iField.getMinimumValue();
    }

    public int getMaximumValue() {
        return this.iField.getMaximumValue();
    }

    public long roundFloor(long l) {
        return this.iField.roundFloor(l);
    }
}

