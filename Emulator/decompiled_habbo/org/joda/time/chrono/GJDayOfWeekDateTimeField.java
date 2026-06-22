/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.Locale;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.chrono.GJLocaleSymbols;
import org.joda.time.field.PreciseDurationDateTimeField;

final class GJDayOfWeekDateTimeField
extends PreciseDurationDateTimeField {
    private static final long serialVersionUID = -3857947176719041436L;
    private final BasicChronology iChronology;

    GJDayOfWeekDateTimeField(BasicChronology basicChronology, DurationField durationField) {
        super(DateTimeFieldType.dayOfWeek(), durationField);
        this.iChronology = basicChronology;
    }

    public int get(long l) {
        return this.iChronology.getDayOfWeek(l);
    }

    public String getAsText(int n, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).dayOfWeekValueToText(n);
    }

    public String getAsShortText(int n, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).dayOfWeekValueToShortText(n);
    }

    protected int convertText(String string, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).dayOfWeekTextToValue(string);
    }

    public DurationField getRangeDurationField() {
        return this.iChronology.weeks();
    }

    public int getMinimumValue() {
        return 1;
    }

    public int getMaximumValue() {
        return 7;
    }

    public int getMaximumTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getDayOfWeekMaxTextLength();
    }

    public int getMaximumShortTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getDayOfWeekMaxShortTextLength();
    }

    private Object readResolve() {
        return this.iChronology.dayOfWeek();
    }
}

