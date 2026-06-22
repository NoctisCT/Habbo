/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.PreciseDurationDateTimeField;

public class PreciseDateTimeField
extends PreciseDurationDateTimeField {
    private static final long serialVersionUID = -5586801265774496376L;
    private final int iRange;
    private final DurationField iRangeField;

    public PreciseDateTimeField(DateTimeFieldType dateTimeFieldType, DurationField durationField, DurationField durationField2) {
        super(dateTimeFieldType, durationField);
        if (!durationField2.isPrecise()) {
            throw new IllegalArgumentException("Range duration field must be precise");
        }
        long l = durationField2.getUnitMillis();
        this.iRange = (int)(l / this.getUnitMillis());
        if (this.iRange < 2) {
            throw new IllegalArgumentException("The effective range must be at least 2");
        }
        this.iRangeField = durationField2;
    }

    public int get(long l) {
        if (l >= 0L) {
            return (int)(l / this.getUnitMillis() % (long)this.iRange);
        }
        return this.iRange - 1 + (int)((l + 1L) / this.getUnitMillis() % (long)this.iRange);
    }

    public long addWrapField(long l, int n) {
        int n2 = this.get(l);
        int n3 = FieldUtils.getWrappedValue(n2, n, this.getMinimumValue(), this.getMaximumValue());
        return l + (long)(n3 - n2) * this.getUnitMillis();
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, this.getMinimumValue(), this.getMaximumValue());
        return l + (long)(n - this.get(l)) * this.iUnitMillis;
    }

    public DurationField getRangeDurationField() {
        return this.iRangeField;
    }

    public int getMaximumValue() {
        return this.iRange - 1;
    }

    public int getRange() {
        return this.iRange;
    }
}

