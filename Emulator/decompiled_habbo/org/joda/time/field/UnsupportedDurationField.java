/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.io.Serializable;
import java.util.HashMap;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;

public final class UnsupportedDurationField
extends DurationField
implements Serializable {
    private static final long serialVersionUID = -6390301302770925357L;
    private static HashMap<DurationFieldType, UnsupportedDurationField> cCache;
    private final DurationFieldType iType;

    public static synchronized UnsupportedDurationField getInstance(DurationFieldType durationFieldType) {
        UnsupportedDurationField unsupportedDurationField;
        if (cCache == null) {
            cCache = new HashMap(7);
            unsupportedDurationField = null;
        } else {
            unsupportedDurationField = cCache.get(durationFieldType);
        }
        if (unsupportedDurationField == null) {
            unsupportedDurationField = new UnsupportedDurationField(durationFieldType);
            cCache.put(durationFieldType, unsupportedDurationField);
        }
        return unsupportedDurationField;
    }

    private UnsupportedDurationField(DurationFieldType durationFieldType) {
        this.iType = durationFieldType;
    }

    public final DurationFieldType getType() {
        return this.iType;
    }

    public String getName() {
        return this.iType.getName();
    }

    public boolean isSupported() {
        return false;
    }

    public boolean isPrecise() {
        return true;
    }

    public int getValue(long l) {
        throw this.unsupported();
    }

    public long getValueAsLong(long l) {
        throw this.unsupported();
    }

    public int getValue(long l, long l2) {
        throw this.unsupported();
    }

    public long getValueAsLong(long l, long l2) {
        throw this.unsupported();
    }

    public long getMillis(int n) {
        throw this.unsupported();
    }

    public long getMillis(long l) {
        throw this.unsupported();
    }

    public long getMillis(int n, long l) {
        throw this.unsupported();
    }

    public long getMillis(long l, long l2) {
        throw this.unsupported();
    }

    public long add(long l, int n) {
        throw this.unsupported();
    }

    public long add(long l, long l2) {
        throw this.unsupported();
    }

    public int getDifference(long l, long l2) {
        throw this.unsupported();
    }

    public long getDifferenceAsLong(long l, long l2) {
        throw this.unsupported();
    }

    public long getUnitMillis() {
        return 0L;
    }

    public int compareTo(DurationField durationField) {
        return 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof UnsupportedDurationField) {
            UnsupportedDurationField unsupportedDurationField = (UnsupportedDurationField)object;
            if (unsupportedDurationField.getName() == null) {
                return this.getName() == null;
            }
            return unsupportedDurationField.getName().equals(this.getName());
        }
        return false;
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public String toString() {
        return "UnsupportedDurationField[" + this.getName() + ']';
    }

    private Object readResolve() {
        return UnsupportedDurationField.getInstance(this.iType);
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(this.iType + " field is unsupported");
    }
}

