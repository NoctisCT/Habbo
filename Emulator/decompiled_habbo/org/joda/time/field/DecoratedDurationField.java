/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.field.BaseDurationField;

public class DecoratedDurationField
extends BaseDurationField {
    private static final long serialVersionUID = 8019982251647420015L;
    private final DurationField iField;

    public DecoratedDurationField(DurationField durationField, DurationFieldType durationFieldType) {
        super(durationFieldType);
        if (durationField == null) {
            throw new IllegalArgumentException("The field must not be null");
        }
        if (!durationField.isSupported()) {
            throw new IllegalArgumentException("The field must be supported");
        }
        this.iField = durationField;
    }

    public final DurationField getWrappedField() {
        return this.iField;
    }

    public boolean isPrecise() {
        return this.iField.isPrecise();
    }

    public long getValueAsLong(long l, long l2) {
        return this.iField.getValueAsLong(l, l2);
    }

    public long getMillis(int n, long l) {
        return this.iField.getMillis(n, l);
    }

    public long getMillis(long l, long l2) {
        return this.iField.getMillis(l, l2);
    }

    public long add(long l, int n) {
        return this.iField.add(l, n);
    }

    public long add(long l, long l2) {
        return this.iField.add(l, l2);
    }

    public long getDifferenceAsLong(long l, long l2) {
        return this.iField.getDifferenceAsLong(l, l2);
    }

    public long getUnitMillis() {
        return this.iField.getUnitMillis();
    }
}

