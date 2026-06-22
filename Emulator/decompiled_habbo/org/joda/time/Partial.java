/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.AbstractPartial;
import org.joda.time.field.AbstractPartialFieldProperty;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class Partial
extends AbstractPartial
implements ReadablePartial,
Serializable {
    private static final long serialVersionUID = 12324121189002L;
    private final Chronology iChronology;
    private final DateTimeFieldType[] iTypes;
    private final int[] iValues;
    private transient DateTimeFormatter[] iFormatter;

    public Partial() {
        this((Chronology)null);
    }

    public Partial(Chronology chronology) {
        this.iChronology = DateTimeUtils.getChronology(chronology).withUTC();
        this.iTypes = new DateTimeFieldType[0];
        this.iValues = new int[0];
    }

    public Partial(DateTimeFieldType dateTimeFieldType, int n) {
        this(dateTimeFieldType, n, null);
    }

    public Partial(DateTimeFieldType dateTimeFieldType, int n, Chronology chronology) {
        this.iChronology = chronology = DateTimeUtils.getChronology(chronology).withUTC();
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The field type must not be null");
        }
        this.iTypes = new DateTimeFieldType[]{dateTimeFieldType};
        this.iValues = new int[]{n};
        chronology.validate(this, this.iValues);
    }

    public Partial(DateTimeFieldType[] dateTimeFieldTypeArray, int[] nArray) {
        this(dateTimeFieldTypeArray, nArray, null);
    }

    public Partial(DateTimeFieldType[] dateTimeFieldTypeArray, int[] nArray, Chronology chronology) {
        this.iChronology = chronology = DateTimeUtils.getChronology(chronology).withUTC();
        if (dateTimeFieldTypeArray == null) {
            throw new IllegalArgumentException("Types array must not be null");
        }
        if (nArray == null) {
            throw new IllegalArgumentException("Values array must not be null");
        }
        if (nArray.length != dateTimeFieldTypeArray.length) {
            throw new IllegalArgumentException("Values array must be the same length as the types array");
        }
        if (dateTimeFieldTypeArray.length == 0) {
            this.iTypes = dateTimeFieldTypeArray;
            this.iValues = nArray;
            return;
        }
        for (int i = 0; i < dateTimeFieldTypeArray.length; ++i) {
            if (dateTimeFieldTypeArray[i] != null) continue;
            throw new IllegalArgumentException("Types array must not contain null: index " + i);
        }
        DurationField durationField = null;
        for (int i = 0; i < dateTimeFieldTypeArray.length; ++i) {
            DateTimeFieldType dateTimeFieldType = dateTimeFieldTypeArray[i];
            DurationField durationField2 = dateTimeFieldType.getDurationType().getField(this.iChronology);
            if (i > 0) {
                if (!durationField2.isSupported()) {
                    if (durationField.isSupported()) {
                        throw new IllegalArgumentException("Types array must be in order largest-smallest: " + dateTimeFieldTypeArray[i - 1].getName() + " < " + dateTimeFieldType.getName());
                    }
                    throw new IllegalArgumentException("Types array must not contain duplicate unsupported: " + dateTimeFieldTypeArray[i - 1].getName() + " and " + dateTimeFieldType.getName());
                }
                int n = durationField.compareTo(durationField2);
                if (n < 0) {
                    throw new IllegalArgumentException("Types array must be in order largest-smallest: " + dateTimeFieldTypeArray[i - 1].getName() + " < " + dateTimeFieldType.getName());
                }
                if (n == 0) {
                    if (durationField.equals(durationField2)) {
                        DurationFieldType durationFieldType = dateTimeFieldTypeArray[i - 1].getRangeDurationType();
                        DurationFieldType durationFieldType2 = dateTimeFieldType.getRangeDurationType();
                        if (durationFieldType == null) {
                            if (durationFieldType2 == null) {
                                throw new IllegalArgumentException("Types array must not contain duplicate: " + dateTimeFieldTypeArray[i - 1].getName() + " and " + dateTimeFieldType.getName());
                            }
                        } else {
                            DurationField durationField3;
                            if (durationFieldType2 == null) {
                                throw new IllegalArgumentException("Types array must be in order largest-smallest: " + dateTimeFieldTypeArray[i - 1].getName() + " < " + dateTimeFieldType.getName());
                            }
                            DurationField durationField4 = durationFieldType.getField(this.iChronology);
                            if (durationField4.compareTo(durationField3 = durationFieldType2.getField(this.iChronology)) < 0) {
                                throw new IllegalArgumentException("Types array must be in order largest-smallest: " + dateTimeFieldTypeArray[i - 1].getName() + " < " + dateTimeFieldType.getName());
                            }
                            if (durationField4.compareTo(durationField3) == 0) {
                                throw new IllegalArgumentException("Types array must not contain duplicate: " + dateTimeFieldTypeArray[i - 1].getName() + " and " + dateTimeFieldType.getName());
                            }
                        }
                    } else if (durationField.isSupported() && durationField.getType() != DurationFieldType.YEARS_TYPE) {
                        throw new IllegalArgumentException("Types array must be in order largest-smallest, for year-based fields, years is defined as being largest: " + dateTimeFieldTypeArray[i - 1].getName() + " < " + dateTimeFieldType.getName());
                    }
                }
            }
            durationField = durationField2;
        }
        this.iTypes = (DateTimeFieldType[])dateTimeFieldTypeArray.clone();
        chronology.validate(this, nArray);
        this.iValues = (int[])nArray.clone();
    }

    public Partial(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("The partial must not be null");
        }
        this.iChronology = DateTimeUtils.getChronology(readablePartial.getChronology()).withUTC();
        this.iTypes = new DateTimeFieldType[readablePartial.size()];
        this.iValues = new int[readablePartial.size()];
        for (int i = 0; i < readablePartial.size(); ++i) {
            this.iTypes[i] = readablePartial.getFieldType(i);
            this.iValues[i] = readablePartial.getValue(i);
        }
    }

    Partial(Partial partial, int[] nArray) {
        this.iChronology = partial.iChronology;
        this.iTypes = partial.iTypes;
        this.iValues = nArray;
    }

    Partial(Chronology chronology, DateTimeFieldType[] dateTimeFieldTypeArray, int[] nArray) {
        this.iChronology = chronology;
        this.iTypes = dateTimeFieldTypeArray;
        this.iValues = nArray;
    }

    public int size() {
        return this.iTypes.length;
    }

    public Chronology getChronology() {
        return this.iChronology;
    }

    protected DateTimeField getField(int n, Chronology chronology) {
        return this.iTypes[n].getField(chronology);
    }

    public DateTimeFieldType getFieldType(int n) {
        return this.iTypes[n];
    }

    public DateTimeFieldType[] getFieldTypes() {
        return (DateTimeFieldType[])this.iTypes.clone();
    }

    public int getValue(int n) {
        return this.iValues[n];
    }

    public int[] getValues() {
        return (int[])this.iValues.clone();
    }

    public Partial withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        if ((chronology = chronology.withUTC()) == this.getChronology()) {
            return this;
        }
        Partial partial = new Partial(chronology, this.iTypes, this.iValues);
        chronology.validate(partial, this.iValues);
        return partial;
    }

    public Partial with(DateTimeFieldType dateTimeFieldType, int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The field type must not be null");
        }
        int n2 = this.indexOf(dateTimeFieldType);
        if (n2 == -1) {
            Serializable serializable;
            int n3;
            DateTimeFieldType[] dateTimeFieldTypeArray = new DateTimeFieldType[this.iTypes.length + 1];
            int[] nArray = new int[dateTimeFieldTypeArray.length];
            DurationField durationField = dateTimeFieldType.getDurationType().getField(this.iChronology);
            if (durationField.isSupported()) {
                DurationField durationField2;
                DurationField durationField3;
                int n4;
                DurationField durationField4;
                for (n3 = 0; !(n3 >= this.iTypes.length || (durationField4 = (serializable = this.iTypes[n3]).getDurationType().getField(this.iChronology)).isSupported() && ((n4 = durationField.compareTo(durationField4)) > 0 || n4 == 0 && (dateTimeFieldType.getRangeDurationType() == null || serializable.getRangeDurationType() != null && (durationField3 = dateTimeFieldType.getRangeDurationType().getField(this.iChronology)).compareTo(durationField2 = serializable.getRangeDurationType().getField(this.iChronology)) > 0))); ++n3) {
                }
            }
            System.arraycopy(this.iTypes, 0, dateTimeFieldTypeArray, 0, n3);
            System.arraycopy(this.iValues, 0, nArray, 0, n3);
            dateTimeFieldTypeArray[n3] = dateTimeFieldType;
            nArray[n3] = n;
            System.arraycopy(this.iTypes, n3, dateTimeFieldTypeArray, n3 + 1, dateTimeFieldTypeArray.length - n3 - 1);
            System.arraycopy(this.iValues, n3, nArray, n3 + 1, nArray.length - n3 - 1);
            serializable = new Partial(dateTimeFieldTypeArray, nArray, this.iChronology);
            this.iChronology.validate((ReadablePartial)((Object)serializable), nArray);
            return serializable;
        }
        if (n == this.getValue(n2)) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).set(this, n2, nArray, n);
        return new Partial(this, nArray);
    }

    public Partial without(DateTimeFieldType dateTimeFieldType) {
        int n = this.indexOf(dateTimeFieldType);
        if (n != -1) {
            DateTimeFieldType[] dateTimeFieldTypeArray = new DateTimeFieldType[this.size() - 1];
            int[] nArray = new int[this.size() - 1];
            System.arraycopy(this.iTypes, 0, dateTimeFieldTypeArray, 0, n);
            System.arraycopy(this.iTypes, n + 1, dateTimeFieldTypeArray, n, dateTimeFieldTypeArray.length - n);
            System.arraycopy(this.iValues, 0, nArray, 0, n);
            System.arraycopy(this.iValues, n + 1, nArray, n, nArray.length - n);
            Partial partial = new Partial(this.iChronology, dateTimeFieldTypeArray, nArray);
            this.iChronology.validate(partial, nArray);
            return partial;
        }
        return this;
    }

    public Partial withField(DateTimeFieldType dateTimeFieldType, int n) {
        int n2 = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(n2)) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).set(this, n2, nArray, n);
        return new Partial(this, nArray);
    }

    public Partial withFieldAdded(DurationFieldType durationFieldType, int n) {
        int n2 = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).add(this, n2, nArray, n);
        return new Partial(this, nArray);
    }

    public Partial withFieldAddWrapped(DurationFieldType durationFieldType, int n) {
        int n2 = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).addWrapPartial(this, n2, nArray, n);
        return new Partial(this, nArray);
    }

    public Partial withPeriodAdded(ReadablePeriod readablePeriod, int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        for (int i = 0; i < readablePeriod.size(); ++i) {
            DurationFieldType durationFieldType = readablePeriod.getFieldType(i);
            int n2 = this.indexOf(durationFieldType);
            if (n2 < 0) continue;
            nArray = this.getField(n2).add(this, n2, nArray, FieldUtils.safeMultiply(readablePeriod.getValue(i), n));
        }
        return new Partial(this, nArray);
    }

    public Partial plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public Partial minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public Property property(DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }

    public boolean isMatch(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
        for (int i = 0; i < this.iTypes.length; ++i) {
            int n = this.iTypes[i].getField(chronology).get(l);
            if (n == this.iValues[i]) continue;
            return false;
        }
        return true;
    }

    public boolean isMatch(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("The partial must not be null");
        }
        for (int i = 0; i < this.iTypes.length; ++i) {
            int n = readablePartial.get(this.iTypes[i]);
            if (n == this.iValues[i]) continue;
            return false;
        }
        return true;
    }

    public DateTimeFormatter getFormatter() {
        DateTimeFormatter[] dateTimeFormatterArray = this.iFormatter;
        if (dateTimeFormatterArray == null) {
            if (this.size() == 0) {
                return null;
            }
            dateTimeFormatterArray = new DateTimeFormatter[2];
            try {
                ArrayList<DateTimeFieldType> arrayList = new ArrayList<DateTimeFieldType>(Arrays.asList(this.iTypes));
                dateTimeFormatterArray[0] = ISODateTimeFormat.forFields(arrayList, true, false);
                if (arrayList.size() == 0) {
                    dateTimeFormatterArray[1] = dateTimeFormatterArray[0];
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            this.iFormatter = dateTimeFormatterArray;
        }
        return dateTimeFormatterArray[0];
    }

    public String toString() {
        DateTimeFormatter dateTimeFormatter;
        DateTimeFormatter[] dateTimeFormatterArray = this.iFormatter;
        if (dateTimeFormatterArray == null) {
            this.getFormatter();
            dateTimeFormatterArray = this.iFormatter;
            if (dateTimeFormatterArray == null) {
                return this.toStringList();
            }
        }
        if ((dateTimeFormatter = dateTimeFormatterArray[1]) == null) {
            return this.toStringList();
        }
        return dateTimeFormatter.print(this);
    }

    public String toStringList() {
        int n = this.size();
        StringBuilder stringBuilder = new StringBuilder(20 * n);
        stringBuilder.append('[');
        for (int i = 0; i < n; ++i) {
            if (i > 0) {
                stringBuilder.append(',').append(' ');
            }
            stringBuilder.append(this.iTypes[i].getName());
            stringBuilder.append('=');
            stringBuilder.append(this.iValues[i]);
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public String toString(String string) {
        if (string == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(string).print(this);
    }

    public String toString(String string, Locale locale) {
        if (string == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(string).withLocale(locale).print(this);
    }

    public static class Property
    extends AbstractPartialFieldProperty
    implements Serializable {
        private static final long serialVersionUID = 53278362873888L;
        private final Partial iPartial;
        private final int iFieldIndex;

        Property(Partial partial, int n) {
            this.iPartial = partial;
            this.iFieldIndex = n;
        }

        public DateTimeField getField() {
            return this.iPartial.getField(this.iFieldIndex);
        }

        protected ReadablePartial getReadablePartial() {
            return this.iPartial;
        }

        public Partial getPartial() {
            return this.iPartial;
        }

        public int get() {
            return this.iPartial.getValue(this.iFieldIndex);
        }

        public Partial addToCopy(int n) {
            int[] nArray = this.iPartial.getValues();
            nArray = this.getField().add(this.iPartial, this.iFieldIndex, nArray, n);
            return new Partial(this.iPartial, nArray);
        }

        public Partial addWrapFieldToCopy(int n) {
            int[] nArray = this.iPartial.getValues();
            nArray = this.getField().addWrapField(this.iPartial, this.iFieldIndex, nArray, n);
            return new Partial(this.iPartial, nArray);
        }

        public Partial setCopy(int n) {
            int[] nArray = this.iPartial.getValues();
            nArray = this.getField().set(this.iPartial, this.iFieldIndex, nArray, n);
            return new Partial(this.iPartial, nArray);
        }

        public Partial setCopy(String string, Locale locale) {
            int[] nArray = this.iPartial.getValues();
            nArray = this.getField().set(this.iPartial, this.iFieldIndex, nArray, string, locale);
            return new Partial(this.iPartial, nArray);
        }

        public Partial setCopy(String string) {
            return this.setCopy(string, null);
        }

        public Partial withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }

        public Partial withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
    }
}

