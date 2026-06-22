/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.base;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationFieldType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractPartial
implements ReadablePartial,
Comparable<ReadablePartial> {
    protected AbstractPartial() {
    }

    protected abstract DateTimeField getField(int var1, Chronology var2);

    @Override
    public DateTimeFieldType getFieldType(int n) {
        return this.getField(n, this.getChronology()).getType();
    }

    public DateTimeFieldType[] getFieldTypes() {
        DateTimeFieldType[] dateTimeFieldTypeArray = new DateTimeFieldType[this.size()];
        for (int i = 0; i < dateTimeFieldTypeArray.length; ++i) {
            dateTimeFieldTypeArray[i] = this.getFieldType(i);
        }
        return dateTimeFieldTypeArray;
    }

    @Override
    public DateTimeField getField(int n) {
        return this.getField(n, this.getChronology());
    }

    public DateTimeField[] getFields() {
        DateTimeField[] dateTimeFieldArray = new DateTimeField[this.size()];
        for (int i = 0; i < dateTimeFieldArray.length; ++i) {
            dateTimeFieldArray[i] = this.getField(i);
        }
        return dateTimeFieldArray;
    }

    public int[] getValues() {
        int[] nArray = new int[this.size()];
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = this.getValue(i);
        }
        return nArray;
    }

    @Override
    public int get(DateTimeFieldType dateTimeFieldType) {
        return this.getValue(this.indexOfSupported(dateTimeFieldType));
    }

    @Override
    public boolean isSupported(DateTimeFieldType dateTimeFieldType) {
        return this.indexOf(dateTimeFieldType) != -1;
    }

    public int indexOf(DateTimeFieldType dateTimeFieldType) {
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            if (this.getFieldType(i) != dateTimeFieldType) continue;
            return i;
        }
        return -1;
    }

    protected int indexOfSupported(DateTimeFieldType dateTimeFieldType) {
        int n = this.indexOf(dateTimeFieldType);
        if (n == -1) {
            throw new IllegalArgumentException("Field '" + dateTimeFieldType + "' is not supported");
        }
        return n;
    }

    protected int indexOf(DurationFieldType durationFieldType) {
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            if (this.getFieldType(i).getDurationType() != durationFieldType) continue;
            return i;
        }
        return -1;
    }

    protected int indexOfSupported(DurationFieldType durationFieldType) {
        int n = this.indexOf(durationFieldType);
        if (n == -1) {
            throw new IllegalArgumentException("Field '" + durationFieldType + "' is not supported");
        }
        return n;
    }

    @Override
    public DateTime toDateTime(ReadableInstant readableInstant) {
        Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        long l2 = chronology.set(this, l);
        return new DateTime(l2, chronology);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ReadablePartial)) {
            return false;
        }
        ReadablePartial readablePartial = (ReadablePartial)object;
        if (this.size() != readablePartial.size()) {
            return false;
        }
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            if (this.getValue(i) == readablePartial.getValue(i) && this.getFieldType(i) == readablePartial.getFieldType(i)) continue;
            return false;
        }
        return FieldUtils.equals(this.getChronology(), readablePartial.getChronology());
    }

    @Override
    public int hashCode() {
        int n = 157;
        int n2 = this.size();
        for (int i = 0; i < n2; ++i) {
            n = 23 * n + this.getValue(i);
            n = 23 * n + this.getFieldType(i).hashCode();
        }
        return n += this.getChronology().hashCode();
    }

    @Override
    public int compareTo(ReadablePartial readablePartial) {
        int n;
        if (this == readablePartial) {
            return 0;
        }
        if (this.size() != readablePartial.size()) {
            throw new ClassCastException("ReadablePartial objects must have matching field types");
        }
        int n2 = this.size();
        for (n = 0; n < n2; ++n) {
            if (this.getFieldType(n) == readablePartial.getFieldType(n)) continue;
            throw new ClassCastException("ReadablePartial objects must have matching field types");
        }
        n2 = this.size();
        for (n = 0; n < n2; ++n) {
            if (this.getValue(n) > readablePartial.getValue(n)) {
                return 1;
            }
            if (this.getValue(n) >= readablePartial.getValue(n)) continue;
            return -1;
        }
        return 0;
    }

    public boolean isAfter(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("Partial cannot be null");
        }
        return this.compareTo(readablePartial) > 0;
    }

    public boolean isBefore(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("Partial cannot be null");
        }
        return this.compareTo(readablePartial) < 0;
    }

    public boolean isEqual(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("Partial cannot be null");
        }
        return this.compareTo(readablePartial) == 0;
    }

    public String toString(DateTimeFormatter dateTimeFormatter) {
        if (dateTimeFormatter == null) {
            return this.toString();
        }
        return dateTimeFormatter.print(this);
    }
}

