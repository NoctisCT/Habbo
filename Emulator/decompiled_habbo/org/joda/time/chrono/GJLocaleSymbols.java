/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.IllegalFieldValueException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class GJLocaleSymbols {
    private static ConcurrentMap<Locale, GJLocaleSymbols> cCache = new ConcurrentHashMap<Locale, GJLocaleSymbols>();
    private final String[] iEras;
    private final String[] iDaysOfWeek;
    private final String[] iShortDaysOfWeek;
    private final String[] iMonths;
    private final String[] iShortMonths;
    private final String[] iHalfday;
    private final TreeMap<String, Integer> iParseEras;
    private final TreeMap<String, Integer> iParseDaysOfWeek;
    private final TreeMap<String, Integer> iParseMonths;
    private final int iMaxEraLength;
    private final int iMaxDayOfWeekLength;
    private final int iMaxShortDayOfWeekLength;
    private final int iMaxMonthLength;
    private final int iMaxShortMonthLength;
    private final int iMaxHalfdayLength;

    static GJLocaleSymbols forLocale(Locale locale) {
        GJLocaleSymbols gJLocaleSymbols;
        GJLocaleSymbols gJLocaleSymbols2;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if ((gJLocaleSymbols2 = (GJLocaleSymbols)cCache.get(locale)) == null && (gJLocaleSymbols = cCache.putIfAbsent(locale, gJLocaleSymbols2 = new GJLocaleSymbols(locale))) != null) {
            gJLocaleSymbols2 = gJLocaleSymbols;
        }
        return gJLocaleSymbols2;
    }

    private static String[] realignMonths(String[] stringArray) {
        String[] stringArray2 = new String[13];
        for (int i = 1; i < 13; ++i) {
            stringArray2[i] = stringArray[i - 1];
        }
        return stringArray2;
    }

    private static String[] realignDaysOfWeek(String[] stringArray) {
        String[] stringArray2 = new String[8];
        for (int i = 1; i < 8; ++i) {
            stringArray2[i] = stringArray[i < 7 ? i + 1 : 1];
        }
        return stringArray2;
    }

    private static void addSymbols(TreeMap<String, Integer> treeMap, String[] stringArray, Integer[] integerArray) {
        int n = stringArray.length;
        while (--n >= 0) {
            String string = stringArray[n];
            if (string == null) continue;
            treeMap.put(string, integerArray[n]);
        }
    }

    private static void addNumerals(TreeMap<String, Integer> treeMap, int n, int n2, Integer[] integerArray) {
        for (int i = n; i <= n2; ++i) {
            treeMap.put(String.valueOf(i).intern(), integerArray[i]);
        }
    }

    private static int maxLength(String[] stringArray) {
        int n = 0;
        int n2 = stringArray.length;
        while (--n2 >= 0) {
            int n3;
            String string = stringArray[n2];
            if (string == null || (n3 = string.length()) <= n) continue;
            n = n3;
        }
        return n;
    }

    private GJLocaleSymbols(Locale locale) {
        DateFormatSymbols dateFormatSymbols = DateTimeUtils.getDateFormatSymbols(locale);
        this.iEras = dateFormatSymbols.getEras();
        this.iDaysOfWeek = GJLocaleSymbols.realignDaysOfWeek(dateFormatSymbols.getWeekdays());
        this.iShortDaysOfWeek = GJLocaleSymbols.realignDaysOfWeek(dateFormatSymbols.getShortWeekdays());
        this.iMonths = GJLocaleSymbols.realignMonths(dateFormatSymbols.getMonths());
        this.iShortMonths = GJLocaleSymbols.realignMonths(dateFormatSymbols.getShortMonths());
        this.iHalfday = dateFormatSymbols.getAmPmStrings();
        Integer[] integerArray = new Integer[13];
        for (int i = 0; i < 13; ++i) {
            integerArray[i] = i;
        }
        this.iParseEras = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        GJLocaleSymbols.addSymbols(this.iParseEras, this.iEras, integerArray);
        if ("en".equals(locale.getLanguage())) {
            this.iParseEras.put("BCE", integerArray[0]);
            this.iParseEras.put("CE", integerArray[1]);
        }
        this.iParseDaysOfWeek = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        GJLocaleSymbols.addSymbols(this.iParseDaysOfWeek, this.iDaysOfWeek, integerArray);
        GJLocaleSymbols.addSymbols(this.iParseDaysOfWeek, this.iShortDaysOfWeek, integerArray);
        GJLocaleSymbols.addNumerals(this.iParseDaysOfWeek, 1, 7, integerArray);
        this.iParseMonths = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        GJLocaleSymbols.addSymbols(this.iParseMonths, this.iMonths, integerArray);
        GJLocaleSymbols.addSymbols(this.iParseMonths, this.iShortMonths, integerArray);
        GJLocaleSymbols.addNumerals(this.iParseMonths, 1, 12, integerArray);
        this.iMaxEraLength = GJLocaleSymbols.maxLength(this.iEras);
        this.iMaxDayOfWeekLength = GJLocaleSymbols.maxLength(this.iDaysOfWeek);
        this.iMaxShortDayOfWeekLength = GJLocaleSymbols.maxLength(this.iShortDaysOfWeek);
        this.iMaxMonthLength = GJLocaleSymbols.maxLength(this.iMonths);
        this.iMaxShortMonthLength = GJLocaleSymbols.maxLength(this.iShortMonths);
        this.iMaxHalfdayLength = GJLocaleSymbols.maxLength(this.iHalfday);
    }

    public String eraValueToText(int n) {
        return this.iEras[n];
    }

    public int eraTextToValue(String string) {
        Integer n = this.iParseEras.get(string);
        if (n != null) {
            return n;
        }
        throw new IllegalFieldValueException(DateTimeFieldType.era(), string);
    }

    public int getEraMaxTextLength() {
        return this.iMaxEraLength;
    }

    public String monthOfYearValueToText(int n) {
        return this.iMonths[n];
    }

    public String monthOfYearValueToShortText(int n) {
        return this.iShortMonths[n];
    }

    public int monthOfYearTextToValue(String string) {
        Integer n = this.iParseMonths.get(string);
        if (n != null) {
            return n;
        }
        throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), string);
    }

    public int getMonthMaxTextLength() {
        return this.iMaxMonthLength;
    }

    public int getMonthMaxShortTextLength() {
        return this.iMaxShortMonthLength;
    }

    public String dayOfWeekValueToText(int n) {
        return this.iDaysOfWeek[n];
    }

    public String dayOfWeekValueToShortText(int n) {
        return this.iShortDaysOfWeek[n];
    }

    public int dayOfWeekTextToValue(String string) {
        Integer n = this.iParseDaysOfWeek.get(string);
        if (n != null) {
            return n;
        }
        throw new IllegalFieldValueException(DateTimeFieldType.dayOfWeek(), string);
    }

    public int getDayOfWeekMaxTextLength() {
        return this.iMaxDayOfWeekLength;
    }

    public int getDayOfWeekMaxShortTextLength() {
        return this.iMaxShortDayOfWeekLength;
    }

    public String halfdayValueToText(int n) {
        return this.iHalfday[n];
    }

    public int halfdayTextToValue(String string) {
        String[] stringArray = this.iHalfday;
        int n = stringArray.length;
        while (--n >= 0) {
            if (!stringArray[n].equalsIgnoreCase(string)) continue;
            return n;
        }
        throw new IllegalFieldValueException(DateTimeFieldType.halfdayOfDay(), string);
    }

    public int getHalfdayMaxTextLength() {
        return this.iMaxHalfdayLength;
    }
}

