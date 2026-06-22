/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.util.Locale;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.field.FieldUtils;

public abstract class AbstractPartialFieldProperty {
    protected AbstractPartialFieldProperty() {
    }

    public abstract DateTimeField getField();

    public DateTimeFieldType getFieldType() {
        return this.getField().getType();
    }

    public String getName() {
        return this.getField().getName();
    }

    protected abstract ReadablePartial getReadablePartial();

    public abstract int get();

    public String getAsString() {
        return Integer.toString(this.get());
    }

    public String getAsText() {
        return this.getAsText(null);
    }

    public String getAsText(Locale locale) {
        return this.getField().getAsText(this.getReadablePartial(), this.get(), locale);
    }

    public String getAsShortText() {
        return this.getAsShortText(null);
    }

    public String getAsShortText(Locale locale) {
        return this.getField().getAsShortText(this.getReadablePartial(), this.get(), locale);
    }

    public DurationField getDurationField() {
        return this.getField().getDurationField();
    }

    public DurationField getRangeDurationField() {
        return this.getField().getRangeDurationField();
    }

    public int getMinimumValueOverall() {
        return this.getField().getMinimumValue();
    }

    public int getMinimumValue() {
        return this.getField().getMinimumValue(this.getReadablePartial());
    }

    public int getMaximumValueOverall() {
        return this.getField().getMaximumValue();
    }

    public int getMaximumValue() {
        return this.getField().getMaximumValue(this.getReadablePartial());
    }

    public int getMaximumTextLength(Locale locale) {
        return this.getField().getMaximumTextLength(locale);
    }

    public int getMaximumShortTextLength(Locale locale) {
        return this.getField().getMaximumShortTextLength(locale);
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
            throw new IllegalArgumentException("The instant must not be null");
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
        if (!(object instanceof AbstractPartialFieldProperty)) {
            return false;
        }
        AbstractPartialFieldProperty abstractPartialFieldProperty = (AbstractPartialFieldProperty)object;
        return this.get() == abstractPartialFieldProperty.get() && this.getFieldType() == abstractPartialFieldProperty.getFieldType() && FieldUtils.equals(this.getReadablePartial().getChronology(), abstractPartialFieldProperty.getReadablePartial().getChronology());
    }

    public int hashCode() {
        int n = 19;
        n = 13 * n + this.get();
        n = 13 * n + this.getFieldType().hashCode();
        n = 13 * n + this.getReadablePartial().getChronology().hashCode();
        return n;
    }

    public String toString() {
        return "Property[" + this.getName() + "]";
    }
}

