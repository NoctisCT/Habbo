/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.field.DelegatedDateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.StrictDateTimeField;

public class LenientDateTimeField
extends DelegatedDateTimeField {
    private static final long serialVersionUID = 8714085824173290599L;
    private final Chronology iBase;

    public static DateTimeField getInstance(DateTimeField dateTimeField, Chronology chronology) {
        if (dateTimeField == null) {
            return null;
        }
        if (dateTimeField instanceof StrictDateTimeField) {
            dateTimeField = ((StrictDateTimeField)dateTimeField).getWrappedField();
        }
        if (dateTimeField.isLenient()) {
            return dateTimeField;
        }
        return new LenientDateTimeField(dateTimeField, chronology);
    }

    protected LenientDateTimeField(DateTimeField dateTimeField, Chronology chronology) {
        super(dateTimeField);
        this.iBase = chronology;
    }

    public final boolean isLenient() {
        return true;
    }

    public long set(long l, int n) {
        long l2 = this.iBase.getZone().convertUTCToLocal(l);
        long l3 = FieldUtils.safeSubtract(n, this.get(l));
        l2 = this.getType().getField(this.iBase.withUTC()).add(l2, l3);
        return this.iBase.getZone().convertLocalToUTC(l2, false, l);
    }
}

