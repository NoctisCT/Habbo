/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParserBucket;
import org.joda.time.format.InternalParser;
import org.joda.time.format.InternalPrinter;

public class DateTimeFormat {
    static final int FULL = 0;
    static final int LONG = 1;
    static final int MEDIUM = 2;
    static final int SHORT = 3;
    static final int NONE = 4;
    static final int DATE = 0;
    static final int TIME = 1;
    static final int DATETIME = 2;
    private static final int PATTERN_CACHE_SIZE = 500;
    private static final ConcurrentHashMap<String, DateTimeFormatter> cPatternCache = new ConcurrentHashMap();
    private static final AtomicReferenceArray<DateTimeFormatter> cStyleCache = new AtomicReferenceArray(25);

    public static DateTimeFormatter forPattern(String string) {
        return DateTimeFormat.createFormatterForPattern(string);
    }

    public static DateTimeFormatter forStyle(String string) {
        return DateTimeFormat.createFormatterForStyle(string);
    }

    public static String patternForStyle(String string, Locale locale) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.createFormatterForStyle(string);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return ((StyleFormatter)dateTimeFormatter.getPrinter0()).getPattern(locale);
    }

    public static DateTimeFormatter shortDate() {
        return DateTimeFormat.createFormatterForStyleIndex(3, 4);
    }

    public static DateTimeFormatter shortTime() {
        return DateTimeFormat.createFormatterForStyleIndex(4, 3);
    }

    public static DateTimeFormatter shortDateTime() {
        return DateTimeFormat.createFormatterForStyleIndex(3, 3);
    }

    public static DateTimeFormatter mediumDate() {
        return DateTimeFormat.createFormatterForStyleIndex(2, 4);
    }

    public static DateTimeFormatter mediumTime() {
        return DateTimeFormat.createFormatterForStyleIndex(4, 2);
    }

    public static DateTimeFormatter mediumDateTime() {
        return DateTimeFormat.createFormatterForStyleIndex(2, 2);
    }

    public static DateTimeFormatter longDate() {
        return DateTimeFormat.createFormatterForStyleIndex(1, 4);
    }

    public static DateTimeFormatter longTime() {
        return DateTimeFormat.createFormatterForStyleIndex(4, 1);
    }

    public static DateTimeFormatter longDateTime() {
        return DateTimeFormat.createFormatterForStyleIndex(1, 1);
    }

    public static DateTimeFormatter fullDate() {
        return DateTimeFormat.createFormatterForStyleIndex(0, 4);
    }

    public static DateTimeFormatter fullTime() {
        return DateTimeFormat.createFormatterForStyleIndex(4, 0);
    }

    public static DateTimeFormatter fullDateTime() {
        return DateTimeFormat.createFormatterForStyleIndex(0, 0);
    }

    static void appendPatternTo(DateTimeFormatterBuilder dateTimeFormatterBuilder, String string) {
        DateTimeFormat.parsePatternTo(dateTimeFormatterBuilder, string);
    }

    protected DateTimeFormat() {
    }

    private static void parsePatternTo(DateTimeFormatterBuilder dateTimeFormatterBuilder, String string) {
        int n = string.length();
        int[] nArray = new int[1];
        block30: for (int i = 0; i < n; ++i) {
            nArray[0] = i;
            String string2 = DateTimeFormat.parseToken(string, nArray);
            i = nArray[0];
            int n2 = string2.length();
            if (n2 == 0) break;
            char c = string2.charAt(0);
            switch (c) {
                case 'G': {
                    dateTimeFormatterBuilder.appendEraText();
                    continue block30;
                }
                case 'C': {
                    dateTimeFormatterBuilder.appendCenturyOfEra(n2, n2);
                    continue block30;
                }
                case 'Y': 
                case 'x': 
                case 'y': {
                    if (n2 == 2) {
                        boolean bl = true;
                        if (i + 1 < n) {
                            nArray[0] = nArray[0] + 1;
                            if (DateTimeFormat.isNumericToken(DateTimeFormat.parseToken(string, nArray))) {
                                bl = false;
                            }
                            nArray[0] = nArray[0] - 1;
                        }
                        switch (c) {
                            case 'x': {
                                dateTimeFormatterBuilder.appendTwoDigitWeekyear(new DateTime().getWeekyear() - 30, bl);
                                continue block30;
                            }
                        }
                        dateTimeFormatterBuilder.appendTwoDigitYear(new DateTime().getYear() - 30, bl);
                        continue block30;
                    }
                    int n3 = 9;
                    if (i + 1 < n) {
                        nArray[0] = nArray[0] + 1;
                        if (DateTimeFormat.isNumericToken(DateTimeFormat.parseToken(string, nArray))) {
                            n3 = n2;
                        }
                        nArray[0] = nArray[0] - 1;
                    }
                    switch (c) {
                        case 'x': {
                            dateTimeFormatterBuilder.appendWeekyear(n2, n3);
                            break;
                        }
                        case 'y': {
                            dateTimeFormatterBuilder.appendYear(n2, n3);
                            break;
                        }
                        case 'Y': {
                            dateTimeFormatterBuilder.appendYearOfEra(n2, n3);
                        }
                    }
                    continue block30;
                }
                case 'M': {
                    if (n2 >= 3) {
                        if (n2 >= 4) {
                            dateTimeFormatterBuilder.appendMonthOfYearText();
                            continue block30;
                        }
                        dateTimeFormatterBuilder.appendMonthOfYearShortText();
                        continue block30;
                    }
                    dateTimeFormatterBuilder.appendMonthOfYear(n2);
                    continue block30;
                }
                case 'd': {
                    dateTimeFormatterBuilder.appendDayOfMonth(n2);
                    continue block30;
                }
                case 'a': {
                    dateTimeFormatterBuilder.appendHalfdayOfDayText();
                    continue block30;
                }
                case 'h': {
                    dateTimeFormatterBuilder.appendClockhourOfHalfday(n2);
                    continue block30;
                }
                case 'H': {
                    dateTimeFormatterBuilder.appendHourOfDay(n2);
                    continue block30;
                }
                case 'k': {
                    dateTimeFormatterBuilder.appendClockhourOfDay(n2);
                    continue block30;
                }
                case 'K': {
                    dateTimeFormatterBuilder.appendHourOfHalfday(n2);
                    continue block30;
                }
                case 'm': {
                    dateTimeFormatterBuilder.appendMinuteOfHour(n2);
                    continue block30;
                }
                case 's': {
                    dateTimeFormatterBuilder.appendSecondOfMinute(n2);
                    continue block30;
                }
                case 'S': {
                    dateTimeFormatterBuilder.appendFractionOfSecond(n2, n2);
                    continue block30;
                }
                case 'e': {
                    dateTimeFormatterBuilder.appendDayOfWeek(n2);
                    continue block30;
                }
                case 'E': {
                    if (n2 >= 4) {
                        dateTimeFormatterBuilder.appendDayOfWeekText();
                        continue block30;
                    }
                    dateTimeFormatterBuilder.appendDayOfWeekShortText();
                    continue block30;
                }
                case 'D': {
                    dateTimeFormatterBuilder.appendDayOfYear(n2);
                    continue block30;
                }
                case 'w': {
                    dateTimeFormatterBuilder.appendWeekOfWeekyear(n2);
                    continue block30;
                }
                case 'z': {
                    if (n2 >= 4) {
                        dateTimeFormatterBuilder.appendTimeZoneName();
                        continue block30;
                    }
                    dateTimeFormatterBuilder.appendTimeZoneShortName(null);
                    continue block30;
                }
                case 'Z': {
                    if (n2 == 1) {
                        dateTimeFormatterBuilder.appendTimeZoneOffset(null, "Z", false, 2, 2);
                        continue block30;
                    }
                    if (n2 == 2) {
                        dateTimeFormatterBuilder.appendTimeZoneOffset(null, "Z", true, 2, 2);
                        continue block30;
                    }
                    dateTimeFormatterBuilder.appendTimeZoneId();
                    continue block30;
                }
                case '\'': {
                    String string3 = string2.substring(1);
                    if (string3.length() == 1) {
                        dateTimeFormatterBuilder.appendLiteral(string3.charAt(0));
                        continue block30;
                    }
                    dateTimeFormatterBuilder.appendLiteral(new String(string3));
                    continue block30;
                }
                default: {
                    throw new IllegalArgumentException("Illegal pattern component: " + string2);
                }
            }
        }
    }

    private static String parseToken(String string, int[] nArray) {
        int n;
        StringBuilder stringBuilder = new StringBuilder();
        int n2 = string.length();
        char c = string.charAt(n);
        if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
            char c2;
            stringBuilder.append(c);
            while (n + 1 < n2 && (c2 = string.charAt(n + 1)) == c) {
                stringBuilder.append(c);
                ++n;
            }
        } else {
            stringBuilder.append('\'');
            boolean bl = false;
            for (n = nArray[0]; n < n2; ++n) {
                c = string.charAt(n);
                if (c == '\'') {
                    if (n + 1 < n2 && string.charAt(n + 1) == '\'') {
                        ++n;
                        stringBuilder.append(c);
                        continue;
                    }
                    bl = !bl;
                    continue;
                }
                if (bl || (c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) {
                    stringBuilder.append(c);
                    continue;
                }
                break;
            }
        }
        nArray[0] = --n;
        return stringBuilder.toString();
    }

    private static boolean isNumericToken(String string) {
        int n = string.length();
        if (n > 0) {
            char c = string.charAt(0);
            switch (c) {
                case 'C': 
                case 'D': 
                case 'F': 
                case 'H': 
                case 'K': 
                case 'S': 
                case 'W': 
                case 'Y': 
                case 'c': 
                case 'd': 
                case 'e': 
                case 'h': 
                case 'k': 
                case 'm': 
                case 's': 
                case 'w': 
                case 'x': 
                case 'y': {
                    return true;
                }
                case 'M': {
                    if (n > 2) break;
                    return true;
                }
            }
        }
        return false;
    }

    private static DateTimeFormatter createFormatterForPattern(String string) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("Invalid pattern specification");
        }
        DateTimeFormatter dateTimeFormatter = cPatternCache.get(string);
        if (dateTimeFormatter == null) {
            DateTimeFormatter dateTimeFormatter2;
            DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder();
            DateTimeFormat.parsePatternTo(dateTimeFormatterBuilder, string);
            dateTimeFormatter = dateTimeFormatterBuilder.toFormatter();
            if (cPatternCache.size() < 500 && (dateTimeFormatter2 = cPatternCache.putIfAbsent(string, dateTimeFormatter)) != null) {
                dateTimeFormatter = dateTimeFormatter2;
            }
        }
        return dateTimeFormatter;
    }

    private static DateTimeFormatter createFormatterForStyle(String string) {
        if (string == null || string.length() != 2) {
            throw new IllegalArgumentException("Invalid style specification: " + string);
        }
        int n = DateTimeFormat.selectStyle(string.charAt(0));
        int n2 = DateTimeFormat.selectStyle(string.charAt(1));
        if (n == 4 && n2 == 4) {
            throw new IllegalArgumentException("Style '--' is invalid");
        }
        return DateTimeFormat.createFormatterForStyleIndex(n, n2);
    }

    private static DateTimeFormatter createFormatterForStyleIndex(int n, int n2) {
        int n3 = (n << 2) + n + n2;
        if (n3 >= cStyleCache.length()) {
            return DateTimeFormat.createDateTimeFormatter(n, n2);
        }
        DateTimeFormatter dateTimeFormatter = cStyleCache.get(n3);
        if (dateTimeFormatter == null && !cStyleCache.compareAndSet(n3, null, dateTimeFormatter = DateTimeFormat.createDateTimeFormatter(n, n2))) {
            dateTimeFormatter = cStyleCache.get(n3);
        }
        return dateTimeFormatter;
    }

    private static DateTimeFormatter createDateTimeFormatter(int n, int n2) {
        int n3 = 2;
        if (n == 4) {
            n3 = 1;
        } else if (n2 == 4) {
            n3 = 0;
        }
        StyleFormatter styleFormatter = new StyleFormatter(n, n2, n3);
        return new DateTimeFormatter(styleFormatter, styleFormatter);
    }

    private static int selectStyle(char c) {
        switch (c) {
            case 'S': {
                return 3;
            }
            case 'M': {
                return 2;
            }
            case 'L': {
                return 1;
            }
            case 'F': {
                return 0;
            }
            case '-': {
                return 4;
            }
        }
        throw new IllegalArgumentException("Invalid style character: " + c);
    }

    static class StyleFormatterCacheKey {
        private final int combinedTypeAndStyle;
        private final Locale locale;

        public StyleFormatterCacheKey(int n, int n2, int n3, Locale locale) {
            this.locale = locale;
            this.combinedTypeAndStyle = n + (n2 << 4) + (n3 << 8);
        }

        public int hashCode() {
            int n = 1;
            n = 31 * n + this.combinedTypeAndStyle;
            n = 31 * n + (this.locale == null ? 0 : this.locale.hashCode());
            return n;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (!(object instanceof StyleFormatterCacheKey)) {
                return false;
            }
            StyleFormatterCacheKey styleFormatterCacheKey = (StyleFormatterCacheKey)object;
            if (this.combinedTypeAndStyle != styleFormatterCacheKey.combinedTypeAndStyle) {
                return false;
            }
            return !(this.locale == null ? styleFormatterCacheKey.locale != null : !this.locale.equals(styleFormatterCacheKey.locale));
        }
    }

    static class StyleFormatter
    implements InternalPrinter,
    InternalParser {
        private static final ConcurrentHashMap<StyleFormatterCacheKey, DateTimeFormatter> cCache = new ConcurrentHashMap();
        private final int iDateStyle;
        private final int iTimeStyle;
        private final int iType;

        StyleFormatter(int n, int n2, int n3) {
            this.iDateStyle = n;
            this.iTimeStyle = n2;
            this.iType = n3;
        }

        public int estimatePrintedLength() {
            return 40;
        }

        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            InternalPrinter internalPrinter = this.getFormatter(locale).getPrinter0();
            internalPrinter.printTo(appendable, l, chronology, n, dateTimeZone, locale);
        }

        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
            InternalPrinter internalPrinter = this.getFormatter(locale).getPrinter0();
            internalPrinter.printTo(appendable, readablePartial, locale);
        }

        public int estimateParsedLength() {
            return 40;
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            InternalParser internalParser = this.getFormatter(dateTimeParserBucket.getLocale()).getParser0();
            return internalParser.parseInto(dateTimeParserBucket, charSequence, n);
        }

        private DateTimeFormatter getFormatter(Locale locale) {
            DateTimeFormatter dateTimeFormatter;
            locale = locale == null ? Locale.getDefault() : locale;
            StyleFormatterCacheKey styleFormatterCacheKey = new StyleFormatterCacheKey(this.iType, this.iDateStyle, this.iTimeStyle, locale);
            DateTimeFormatter dateTimeFormatter2 = cCache.get(styleFormatterCacheKey);
            if (dateTimeFormatter2 == null && (dateTimeFormatter = cCache.putIfAbsent(styleFormatterCacheKey, dateTimeFormatter2 = DateTimeFormat.forPattern(this.getPattern(locale)))) != null) {
                dateTimeFormatter2 = dateTimeFormatter;
            }
            return dateTimeFormatter2;
        }

        String getPattern(Locale locale) {
            DateFormat dateFormat = null;
            switch (this.iType) {
                case 0: {
                    dateFormat = DateFormat.getDateInstance(this.iDateStyle, locale);
                    break;
                }
                case 1: {
                    dateFormat = DateFormat.getTimeInstance(this.iTimeStyle, locale);
                    break;
                }
                case 2: {
                    dateFormat = DateFormat.getDateTimeInstance(this.iDateStyle, this.iTimeStyle, locale);
                }
            }
            if (!(dateFormat instanceof SimpleDateFormat)) {
                throw new IllegalArgumentException("No datetime pattern for locale: " + locale);
            }
            return ((SimpleDateFormat)dateFormat).toPattern();
        }
    }
}

