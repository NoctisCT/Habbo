/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodParser;
import org.joda.time.format.PeriodPrinter;

public class PeriodFormat {
    private static final String BUNDLE_NAME = "org.joda.time.format.messages";
    private static final ConcurrentMap<Locale, PeriodFormatter> FORMATTERS = new ConcurrentHashMap<Locale, PeriodFormatter>();

    protected PeriodFormat() {
    }

    public static PeriodFormatter getDefault() {
        return PeriodFormat.wordBased(Locale.ENGLISH);
    }

    public static PeriodFormatter wordBased() {
        return PeriodFormat.wordBased(Locale.getDefault());
    }

    public static PeriodFormatter wordBased(Locale locale) {
        DynamicWordBased dynamicWordBased;
        PeriodFormatter periodFormatter;
        PeriodFormatter periodFormatter2 = (PeriodFormatter)FORMATTERS.get(locale);
        if (periodFormatter2 == null && (periodFormatter = FORMATTERS.putIfAbsent(locale, periodFormatter2 = new PeriodFormatter(dynamicWordBased = new DynamicWordBased(PeriodFormat.buildWordBased(locale)), dynamicWordBased, locale, null))) != null) {
            periodFormatter2 = periodFormatter;
        }
        return periodFormatter2;
    }

    private static PeriodFormatter buildWordBased(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        if (PeriodFormat.containsKey(resourceBundle, "PeriodFormat.regex.separator")) {
            return PeriodFormat.buildRegExFormatter(resourceBundle, locale);
        }
        return PeriodFormat.buildNonRegExFormatter(resourceBundle, locale);
    }

    private static PeriodFormatter buildRegExFormatter(ResourceBundle resourceBundle, Locale locale) {
        String[] stringArray = PeriodFormat.retrieveVariants(resourceBundle);
        String string = resourceBundle.getString("PeriodFormat.regex.separator");
        PeriodFormatterBuilder periodFormatterBuilder = new PeriodFormatterBuilder();
        periodFormatterBuilder.appendYears();
        if (PeriodFormat.containsKey(resourceBundle, "PeriodFormat.years.regex")) {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.years.regex").split(string), resourceBundle.getString("PeriodFormat.years.list").split(string));
        } else {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.year"), resourceBundle.getString("PeriodFormat.years"));
        }
        periodFormatterBuilder.appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray);
        periodFormatterBuilder.appendMonths();
        if (PeriodFormat.containsKey(resourceBundle, "PeriodFormat.months.regex")) {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.months.regex").split(string), resourceBundle.getString("PeriodFormat.months.list").split(string));
        } else {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.month"), resourceBundle.getString("PeriodFormat.months"));
        }
        periodFormatterBuilder.appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray);
        periodFormatterBuilder.appendWeeks();
        if (PeriodFormat.containsKey(resourceBundle, "PeriodFormat.weeks.regex")) {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.weeks.regex").split(string), resourceBundle.getString("PeriodFormat.weeks.list").split(string));
        } else {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.week"), resourceBundle.getString("PeriodFormat.weeks"));
        }
        periodFormatterBuilder.appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray);
        periodFormatterBuilder.appendDays();
        if (PeriodFormat.containsKey(resourceBundle, "PeriodFormat.days.regex")) {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.days.regex").split(string), resourceBundle.getString("PeriodFormat.days.list").split(string));
        } else {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.day"), resourceBundle.getString("PeriodFormat.days"));
        }
        periodFormatterBuilder.appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray);
        periodFormatterBuilder.appendHours();
        if (PeriodFormat.containsKey(resourceBundle, "PeriodFormat.hours.regex")) {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.hours.regex").split(string), resourceBundle.getString("PeriodFormat.hours.list").split(string));
        } else {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.hour"), resourceBundle.getString("PeriodFormat.hours"));
        }
        periodFormatterBuilder.appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray);
        periodFormatterBuilder.appendMinutes();
        if (PeriodFormat.containsKey(resourceBundle, "PeriodFormat.minutes.regex")) {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.minutes.regex").split(string), resourceBundle.getString("PeriodFormat.minutes.list").split(string));
        } else {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.minute"), resourceBundle.getString("PeriodFormat.minutes"));
        }
        periodFormatterBuilder.appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray);
        periodFormatterBuilder.appendSeconds();
        if (PeriodFormat.containsKey(resourceBundle, "PeriodFormat.seconds.regex")) {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.seconds.regex").split(string), resourceBundle.getString("PeriodFormat.seconds.list").split(string));
        } else {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.second"), resourceBundle.getString("PeriodFormat.seconds"));
        }
        periodFormatterBuilder.appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray);
        periodFormatterBuilder.appendMillis();
        if (PeriodFormat.containsKey(resourceBundle, "PeriodFormat.milliseconds.regex")) {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.milliseconds.regex").split(string), resourceBundle.getString("PeriodFormat.milliseconds.list").split(string));
        } else {
            periodFormatterBuilder.appendSuffix(resourceBundle.getString("PeriodFormat.millisecond"), resourceBundle.getString("PeriodFormat.milliseconds"));
        }
        return periodFormatterBuilder.toFormatter().withLocale(locale);
    }

    private static PeriodFormatter buildNonRegExFormatter(ResourceBundle resourceBundle, Locale locale) {
        String[] stringArray = PeriodFormat.retrieveVariants(resourceBundle);
        return new PeriodFormatterBuilder().appendYears().appendSuffix(resourceBundle.getString("PeriodFormat.year"), resourceBundle.getString("PeriodFormat.years")).appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray).appendMonths().appendSuffix(resourceBundle.getString("PeriodFormat.month"), resourceBundle.getString("PeriodFormat.months")).appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray).appendWeeks().appendSuffix(resourceBundle.getString("PeriodFormat.week"), resourceBundle.getString("PeriodFormat.weeks")).appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray).appendDays().appendSuffix(resourceBundle.getString("PeriodFormat.day"), resourceBundle.getString("PeriodFormat.days")).appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray).appendHours().appendSuffix(resourceBundle.getString("PeriodFormat.hour"), resourceBundle.getString("PeriodFormat.hours")).appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray).appendMinutes().appendSuffix(resourceBundle.getString("PeriodFormat.minute"), resourceBundle.getString("PeriodFormat.minutes")).appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray).appendSeconds().appendSuffix(resourceBundle.getString("PeriodFormat.second"), resourceBundle.getString("PeriodFormat.seconds")).appendSeparator(resourceBundle.getString("PeriodFormat.commaspace"), resourceBundle.getString("PeriodFormat.spaceandspace"), stringArray).appendMillis().appendSuffix(resourceBundle.getString("PeriodFormat.millisecond"), resourceBundle.getString("PeriodFormat.milliseconds")).toFormatter().withLocale(locale);
    }

    private static String[] retrieveVariants(ResourceBundle resourceBundle) {
        return new String[]{resourceBundle.getString("PeriodFormat.space"), resourceBundle.getString("PeriodFormat.comma"), resourceBundle.getString("PeriodFormat.commandand"), resourceBundle.getString("PeriodFormat.commaspaceand")};
    }

    private static boolean containsKey(ResourceBundle resourceBundle, String string) {
        Enumeration<String> enumeration = resourceBundle.getKeys();
        while (enumeration.hasMoreElements()) {
            if (!enumeration.nextElement().equals(string)) continue;
            return true;
        }
        return false;
    }

    static class DynamicWordBased
    implements PeriodPrinter,
    PeriodParser {
        private final PeriodFormatter iFormatter;

        DynamicWordBased(PeriodFormatter periodFormatter) {
            this.iFormatter = periodFormatter;
        }

        public int countFieldsToPrint(ReadablePeriod readablePeriod, int n, Locale locale) {
            return this.getPrinter(locale).countFieldsToPrint(readablePeriod, n, locale);
        }

        public int calculatePrintedLength(ReadablePeriod readablePeriod, Locale locale) {
            return this.getPrinter(locale).calculatePrintedLength(readablePeriod, locale);
        }

        public void printTo(StringBuffer stringBuffer, ReadablePeriod readablePeriod, Locale locale) {
            this.getPrinter(locale).printTo(stringBuffer, readablePeriod, locale);
        }

        public void printTo(Writer writer, ReadablePeriod readablePeriod, Locale locale) throws IOException {
            this.getPrinter(locale).printTo(writer, readablePeriod, locale);
        }

        private PeriodPrinter getPrinter(Locale locale) {
            if (locale != null && !locale.equals(this.iFormatter.getLocale())) {
                return PeriodFormat.wordBased(locale).getPrinter();
            }
            return this.iFormatter.getPrinter();
        }

        public int parseInto(ReadWritablePeriod readWritablePeriod, String string, int n, Locale locale) {
            return this.getParser(locale).parseInto(readWritablePeriod, string, n, locale);
        }

        private PeriodParser getParser(Locale locale) {
            if (locale != null && !locale.equals(this.iFormatter.getLocale())) {
                return PeriodFormat.wordBased(locale).getParser();
            }
            return this.iFormatter.getParser();
        }
    }
}

