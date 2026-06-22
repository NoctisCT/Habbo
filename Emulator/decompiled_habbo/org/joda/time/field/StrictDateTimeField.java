/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.field.DelegatedDateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.LenientDateTimeField;

public class StrictDateTimeField
extends DelegatedDateTimeField {
    private static final long serialVersionUID = 3154803964207950910L;

    public static DateTimeField getInstance(DateTimeField dateTimeField) {
        if (dateTimeField == null) {
            return null;
        }
        if (dateTimeField instanceof LenientDateTimeField) {
            dateTimeField = ((LenientDateTimeField)dateTimeField).getWrappedField();
        }
        if (!dateTimeField.isLenient()) {
            return dateTimeField;
        }
        return new StrictDateTimeField(dateTimeField);
    }

    protected StrictDateTimeField(DateTimeField dateTimeField) {
        super(dateTimeField);
    }

    public final boolean isLenient() {
        return false;
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, this.getMinimumValue(l), this.getMaximumValue(l));
        return super.set(l, n);
    }
}

