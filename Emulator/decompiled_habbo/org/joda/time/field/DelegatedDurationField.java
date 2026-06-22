/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.io.Serializable;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;

public class DelegatedDurationField
extends DurationField
implements Serializable {
    private static final long serialVersionUID = -5576443481242007829L;
    private final DurationField iField;
    private final DurationFieldType iType;

    protected DelegatedDurationField(DurationField durationField) {
        this(durationField, null);
    }

    protected DelegatedDurationField(DurationField durationField, DurationFieldType durationFieldType) {
        if (durationField == null) {
            throw new IllegalArgumentException("The field must not be null");
        }
        this.iField = durationField;
        this.iType = durationFieldType == null ? durationField.getType() : durationFieldType;
    }

    public final DurationField getWrappedField() {
        return this.iField;
    }

    public DurationFieldType getType() {
        return this.iType;
    }

    public String getName() {
        return this.iType.getName();
    }

    public boolean isSupported() {
        return this.iField.isSupported();
    }

    public boolean isPrecise() {
        return this.iField.isPrecise();
    }

    public int getValue(long l) {
        return this.iField.getValue(l);
    }

    public long getValueAsLong(long l) {
        return this.iField.getValueAsLong(l);
    }

    public int getValue(long l, long l2) {
        return this.iField.getValue(l, l2);
    }

    public long getValueAsLong(long l, long l2) {
        return this.iField.getValueAsLong(l, l2);
    }

    public long getMillis(int n) {
        return this.iField.getMillis(n);
    }

    public long getMillis(long l) {
        return this.iField.getMillis(l);
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

    public int getDifference(long l, long l2) {
        return this.iField.getDifference(l, l2);
    }

    public long getDifferenceAsLong(long l, long l2) {
        return this.iField.getDifferenceAsLong(l, l2);
    }

    public long getUnitMillis() {
        return this.iField.getUnitMillis();
    }

    public int compareTo(DurationField durationField) {
        return this.iField.compareTo(durationField);
    }

    public boolean equals(Object object) {
        if (object instanceof DelegatedDurationField) {
            return this.iField.equals(((DelegatedDurationField)object).iField);
        }
        return false;
    }

    public int hashCode() {
        return this.iField.hashCode() ^ this.iType.hashCode();
    }

    public String toString() {
        return this.iType == null ? this.iField.toString() : "DurationField[" + this.iType + ']';
    }
}

