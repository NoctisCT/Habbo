/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.Locale;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.chrono.BasicMonthOfYearDateTimeField;
import org.joda.time.chrono.GJLocaleSymbols;

final class GJMonthOfYearDateTimeField
extends BasicMonthOfYearDateTimeField {
    private static final long serialVersionUID = -4748157875845286249L;

    GJMonthOfYearDateTimeField(BasicChronology basicChronology) {
        super(basicChronology, 2);
    }

    public String getAsText(int n, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).monthOfYearValueToText(n);
    }

    public String getAsShortText(int n, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).monthOfYearValueToShortText(n);
    }

    protected int convertText(String string, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).monthOfYearTextToValue(string);
    }

    public int getMaximumTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getMonthMaxTextLength();
    }

    public int getMaximumShortTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getMonthMaxShortTextLength();
    }
}

