/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.field.DelegatedDateTimeField;
import org.joda.time.field.FieldUtils;

public final class SkipUndoDateTimeField
extends DelegatedDateTimeField {
    private static final long serialVersionUID = -5875876968979L;
    private final Chronology iChronology;
    private final int iSkip;
    private transient int iMinValue;

    public SkipUndoDateTimeField(Chronology chronology, DateTimeField dateTimeField) {
        this(chronology, dateTimeField, 0);
    }

    public SkipUndoDateTimeField(Chronology chronology, DateTimeField dateTimeField, int n) {
        super(dateTimeField);
        this.iChronology = chronology;
        int n2 = super.getMinimumValue();
        this.iMinValue = n2 < n ? n2 + 1 : (n2 == n + 1 ? n : n2);
        this.iSkip = n;
    }

    public int get(long l) {
        int n = super.get(l);
        if (n < this.iSkip) {
            ++n;
        }
        return n;
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, this.iMinValue, this.getMaximumValue());
        if (n <= this.iSkip) {
            --n;
        }
        return super.set(l, n);
    }

    public int getMinimumValue() {
        return this.iMinValue;
    }

    private Object readResolve() {
        return this.getType().getField(this.iChronology);
    }
}

