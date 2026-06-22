/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.Locale;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.chrono.GJLocaleSymbols;
import org.joda.time.field.BaseDateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.UnsupportedDurationField;

final class GJEraDateTimeField
extends BaseDateTimeField {
    private static final long serialVersionUID = 4240986525305515528L;
    private final BasicChronology iChronology;

    GJEraDateTimeField(BasicChronology basicChronology) {
        super(DateTimeFieldType.era());
        this.iChronology = basicChronology;
    }

    public boolean isLenient() {
        return false;
    }

    public int get(long l) {
        if (this.iChronology.getYear(l) <= 0) {
            return 0;
        }
        return 1;
    }

    public String getAsText(int n, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).eraValueToText(n);
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, 0, 1);
        int n2 = this.get(l);
        if (n2 != n) {
            int n3 = this.iChronology.getYear(l);
            return this.iChronology.setYear(l, -n3);
        }
        return l;
    }

    public long set(long l, String string, Locale locale) {
        return this.set(l, GJLocaleSymbols.forLocale(locale).eraTextToValue(string));
    }

    public long roundFloor(long l) {
        if (this.get(l) == 1) {
            return this.iChronology.setYear(0L, 1);
        }
        return Long.MIN_VALUE;
    }

    public long roundCeiling(long l) {
        if (this.get(l) == 0) {
            return this.iChronology.setYear(0L, 1);
        }
        return Long.MAX_VALUE;
    }

    public long roundHalfFloor(long l) {
        return this.roundFloor(l);
    }

    public long roundHalfCeiling(long l) {
        return this.roundFloor(l);
    }

    public long roundHalfEven(long l) {
        return this.roundFloor(l);
    }

    public DurationField getDurationField() {
        return UnsupportedDurationField.getInstance(DurationFieldType.eras());
    }

    public DurationField getRangeDurationField() {
        return null;
    }

    public int getMinimumValue() {
        return 0;
    }

    public int getMaximumValue() {
        return 1;
    }

    public int getMaximumTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getEraMaxTextLength();
    }

    private Object readResolve() {
        return this.iChronology.era();
    }
}

