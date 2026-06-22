/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.field.BaseDateTimeField;
import org.joda.time.field.BaseDurationField;
import org.joda.time.field.FieldUtils;

public abstract class ImpreciseDateTimeField
extends BaseDateTimeField {
    private static final long serialVersionUID = 7190739608550251860L;
    final long iUnitMillis;
    private final DurationField iDurationField;

    public ImpreciseDateTimeField(DateTimeFieldType dateTimeFieldType, long l) {
        super(dateTimeFieldType);
        this.iUnitMillis = l;
        this.iDurationField = new LinkedDurationField(dateTimeFieldType.getDurationType());
    }

    public abstract int get(long var1);

    public abstract long set(long var1, int var3);

    public abstract long add(long var1, int var3);

    public abstract long add(long var1, long var3);

    public int getDifference(long l, long l2) {
        return FieldUtils.safeToInt(this.getDifferenceAsLong(l, l2));
    }

    public long getDifferenceAsLong(long l, long l2) {
        if (l < l2) {
            return -this.getDifferenceAsLong(l2, l);
        }
        long l3 = (l - l2) / this.iUnitMillis;
        if (this.add(l2, l3) < l) {
            while (this.add(l2, ++l3) <= l) {
            }
            --l3;
        } else if (this.add(l2, l3) > l) {
            while (this.add(l2, --l3) > l) {
            }
        }
        return l3;
    }

    public final DurationField getDurationField() {
        return this.iDurationField;
    }

    public abstract DurationField getRangeDurationField();

    public abstract long roundFloor(long var1);

    protected final long getDurationUnitMillis() {
        return this.iUnitMillis;
    }

    private final class LinkedDurationField
    extends BaseDurationField {
        private static final long serialVersionUID = -203813474600094134L;

        LinkedDurationField(DurationFieldType durationFieldType) {
            super(durationFieldType);
        }

        public boolean isPrecise() {
            return false;
        }

        public long getUnitMillis() {
            return ImpreciseDateTimeField.this.iUnitMillis;
        }

        public int getValue(long l, long l2) {
            return ImpreciseDateTimeField.this.getDifference(l2 + l, l2);
        }

        public long getValueAsLong(long l, long l2) {
            return ImpreciseDateTimeField.this.getDifferenceAsLong(l2 + l, l2);
        }

        public long getMillis(int n, long l) {
            return ImpreciseDateTimeField.this.add(l, n) - l;
        }

        public long getMillis(long l, long l2) {
            return ImpreciseDateTimeField.this.add(l2, l) - l2;
        }

        public long add(long l, int n) {
            return ImpreciseDateTimeField.this.add(l, n);
        }

        public long add(long l, long l2) {
            return ImpreciseDateTimeField.this.add(l, l2);
        }

        public int getDifference(long l, long l2) {
            return ImpreciseDateTimeField.this.getDifference(l, l2);
        }

        public long getDifferenceAsLong(long l, long l2) {
            return ImpreciseDateTimeField.this.getDifferenceAsLong(l, l2);
        }
    }
}

