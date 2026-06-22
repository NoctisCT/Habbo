/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.base;

import java.io.Serializable;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadablePartial;
import org.joda.time.base.AbstractPartial;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.PartialConverter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public abstract class BasePartial
extends AbstractPartial
implements ReadablePartial,
Serializable {
    private static final long serialVersionUID = 2353678632973660L;
    private final Chronology iChronology;
    private final int[] iValues;

    protected BasePartial() {
        this(DateTimeUtils.currentTimeMillis(), (Chronology)null);
    }

    protected BasePartial(Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }

    protected BasePartial(long l) {
        this(l, (Chronology)null);
    }

    protected BasePartial(long l, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        this.iValues = chronology.get(this, l);
    }

    protected BasePartial(Object object, Chronology chronology) {
        PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(object);
        chronology = partialConverter.getChronology(object, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        this.iValues = partialConverter.getPartialValues(this, object, chronology);
    }

    protected BasePartial(Object object, Chronology chronology, DateTimeFormatter dateTimeFormatter) {
        PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(object);
        chronology = partialConverter.getChronology(object, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        this.iValues = partialConverter.getPartialValues(this, object, chronology, dateTimeFormatter);
    }

    protected BasePartial(int[] nArray, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        chronology.validate(this, nArray);
        this.iValues = nArray;
    }

    protected BasePartial(BasePartial basePartial, int[] nArray) {
        this.iChronology = basePartial.iChronology;
        this.iValues = nArray;
    }

    protected BasePartial(BasePartial basePartial, Chronology chronology) {
        this.iChronology = chronology.withUTC();
        this.iValues = basePartial.iValues;
    }

    public int getValue(int n) {
        return this.iValues[n];
    }

    public int[] getValues() {
        return (int[])this.iValues.clone();
    }

    public Chronology getChronology() {
        return this.iChronology;
    }

    protected void setValue(int n, int n2) {
        DateTimeField dateTimeField = this.getField(n);
        int[] nArray = dateTimeField.set(this, n, this.iValues, n2);
        System.arraycopy(nArray, 0, this.iValues, 0, this.iValues.length);
    }

    protected void setValues(int[] nArray) {
        this.getChronology().validate(this, nArray);
        System.arraycopy(nArray, 0, this.iValues, 0, this.iValues.length);
    }

    public String toString(String string) {
        if (string == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(string).print(this);
    }

    public String toString(String string, Locale locale) throws IllegalArgumentException {
        if (string == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(string).withLocale(locale).print(this);
    }
}

