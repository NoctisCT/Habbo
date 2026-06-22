/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.io.Serializable;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationField;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.field.FieldUtils;

public abstract class AbstractReadableInstantFieldProperty
implements Serializable {
    private static final long serialVersionUID = 1971226328211649661L;

    public abstract DateTimeField getField();

    public DateTimeFieldType getFieldType() {
        return this.getField().getType();
    }

    public String getName() {
        return this.getField().getName();
    }

    protected abstract long getMillis();

    protected Chronology getChronology() {
        throw new UnsupportedOperationException("The method getChronology() was added in v1.4 and needs to be implemented by subclasses of AbstractReadableInstantFieldProperty");
    }

    public int get() {
        return this.getField().get(this.getMillis());
    }

    public String getAsString() {
        return Integer.toString(this.get());
    }

    public String getAsText() {
        return this.getAsText(null);
    }

    public String getAsText(Locale locale) {
        return this.getField().getAsText(this.getMillis(), locale);
    }

    public String getAsShortText() {
        return this.getAsShortText(null);
    }

    public String getAsShortText(Locale locale) {
        return this.getField().getAsShortText(this.getMillis(), locale);
    }

    public int getDifference(ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.getField().getDifference(this.getMillis(), DateTimeUtils.currentTimeMillis());
        }
        return this.getField().getDifference(this.getMillis(), readableInstant.getMillis());
    }

    public long getDifferenceAsLong(ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.getField().getDifferenceAsLong(this.getMillis(), DateTimeUtils.currentTimeMillis());
        }
        return this.getField().getDifferenceAsLong(this.getMillis(), readableInstant.getMillis());
    }

    public DurationField getDurationField() {
        return this.getField().getDurationField();
    }

    public DurationField getRangeDurationField() {
        return this.getField().getRangeDurationField();
    }

    public boolean isLeap() {
        return this.getField().isLeap(this.getMillis());
    }

    public int getLeapAmount() {
        return this.getField().getLeapAmount(this.getMillis());
    }

    public DurationField getLeapDurationField() {
        return this.getField().getLeapDurationField();
    }

    public int getMinimumValueOverall() {
        return this.getField().getMinimumValue();
    }

    public int getMinimumValue() {
        return this.getField().getMinimumValue(this.getMillis());
    }

    public int getMaximumValueOverall() {
        return this.getField().getMaximumValue();
    }

    public int getMaximumValue() {
        return this.getField().getMaximumValue(this.getMillis());
    }

    public int getMaximumTextLength(Locale locale) {
        return this.getField().getMaximumTextLength(locale);
    }

    public int getMaximumShortTextLength(Locale locale) {
        return this.getField().getMaximumShortTextLength(locale);
    }

    public long remainder() {
        return this.getField().remainder(this.getMillis());
    }

    public Interval toInterval() {
        DateTimeField dateTimeField = this.getField();
        long l = dateTimeField.roundFloor(this.getMillis());
        long l2 = dateTimeField.add(l, 1);
        Interval interval = new Interval(l, l2, this.getChronology());
        return interval;
    }

    public int compareTo(ReadableInstant readableInstant) {
        int n;
        if (readableInstant == null) {
            throw new IllegalArgumentException("The instant must not be null");
        }
        int n2 = this.get();
        if (n2 < (n = readableInstant.get(this.getFieldType()))) {
            return -1;
        }
        if (n2 > n) {
            return 1;
        }
        return 0;
    }

    public int compareTo(ReadablePartial readablePartial) {
        int n;
        if (readablePartial == null) {
            throw new IllegalArgumentException("The partial must not be null");
        }
        int n2 = this.get();
        if (n2 < (n = readablePartial.get(this.getFieldType()))) {
            return -1;
        }
        if (n2 > n) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AbstractReadableInstantFieldProperty)) {
            return false;
        }
        AbstractReadableInstantFieldProperty abstractReadableInstantFieldProperty = (AbstractReadableInstantFieldProperty)object;
        return this.get() == abstractReadableInstantFieldProperty.get() && this.getFieldType().equals(abstractReadableInstantFieldProperty.getFieldType()) && FieldUtils.equals(this.getChronology(), abstractReadableInstantFieldProperty.getChronology());
    }

    public int hashCode() {
        return this.get() * 17 + this.getFieldType().hashCode() + this.getChronology().hashCode();
    }

    public String toString() {
        return "Property[" + this.getName() + "]";
    }
}

