/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.joda.time.DurationFieldType;
import org.joda.time.PeriodType;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.FormatUtils;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodParser;
import org.joda.time.format.PeriodPrinter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PeriodFormatterBuilder {
    private static final int PRINT_ZERO_RARELY_FIRST = 1;
    private static final int PRINT_ZERO_RARELY_LAST = 2;
    private static final int PRINT_ZERO_IF_SUPPORTED = 3;
    private static final int PRINT_ZERO_ALWAYS = 4;
    private static final int PRINT_ZERO_NEVER = 5;
    private static final int YEARS = 0;
    private static final int MONTHS = 1;
    private static final int WEEKS = 2;
    private static final int DAYS = 3;
    private static final int HOURS = 4;
    private static final int MINUTES = 5;
    private static final int SECONDS = 6;
    private static final int MILLIS = 7;
    private static final int SECONDS_MILLIS = 8;
    private static final int SECONDS_OPTIONAL_MILLIS = 9;
    private static final int MAX_FIELD = 9;
    private static final ConcurrentMap<String, Pattern> PATTERNS = new ConcurrentHashMap<String, Pattern>();
    private int iMinPrintedDigits;
    private int iPrintZeroSetting;
    private int iMaxParsedDigits;
    private boolean iRejectSignedValues;
    private PeriodFieldAffix iPrefix;
    private List<Object> iElementPairs;
    private boolean iNotPrinter;
    private boolean iNotParser;
    private FieldFormatter[] iFieldFormatters;

    public PeriodFormatterBuilder() {
        this.clear();
    }

    public PeriodFormatter toFormatter() {
        PeriodFormatter periodFormatter = PeriodFormatterBuilder.toFormatter(this.iElementPairs, this.iNotPrinter, this.iNotParser);
        for (FieldFormatter fieldFormatter : this.iFieldFormatters) {
            if (fieldFormatter == null) continue;
            fieldFormatter.finish(this.iFieldFormatters);
        }
        this.iFieldFormatters = (FieldFormatter[])this.iFieldFormatters.clone();
        return periodFormatter;
    }

    public PeriodPrinter toPrinter() {
        if (this.iNotPrinter) {
            return null;
        }
        return this.toFormatter().getPrinter();
    }

    public PeriodParser toParser() {
        if (this.iNotParser) {
            return null;
        }
        return this.toFormatter().getParser();
    }

    public void clear() {
        this.iMinPrintedDigits = 1;
        this.iPrintZeroSetting = 2;
        this.iMaxParsedDigits = 10;
        this.iRejectSignedValues = false;
        this.iPrefix = null;
        if (this.iElementPairs == null) {
            this.iElementPairs = new ArrayList<Object>();
        } else {
            this.iElementPairs.clear();
        }
        this.iNotPrinter = false;
        this.iNotParser = false;
        this.iFieldFormatters = new FieldFormatter[10];
    }

    public PeriodFormatterBuilder append(PeriodFormatter periodFormatter) {
        if (periodFormatter == null) {
            throw new IllegalArgumentException("No formatter supplied");
        }
        this.clearPrefix();
        this.append0(periodFormatter.getPrinter(), periodFormatter.getParser());
        return this;
    }

    public PeriodFormatterBuilder append(PeriodPrinter periodPrinter, PeriodParser periodParser) {
        if (periodPrinter == null && periodParser == null) {
            throw new IllegalArgumentException("No printer or parser supplied");
        }
        this.clearPrefix();
        this.append0(periodPrinter, periodParser);
        return this;
    }

    public PeriodFormatterBuilder appendLiteral(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Literal must not be null");
        }
        this.clearPrefix();
        Literal literal = new Literal(string);
        this.append0(literal, literal);
        return this;
    }

    public PeriodFormatterBuilder minimumPrintedDigits(int n) {
        this.iMinPrintedDigits = n;
        return this;
    }

    public PeriodFormatterBuilder maximumParsedDigits(int n) {
        this.iMaxParsedDigits = n;
        return this;
    }

    public PeriodFormatterBuilder rejectSignedValues(boolean bl) {
        this.iRejectSignedValues = bl;
        return this;
    }

    public PeriodFormatterBuilder printZeroRarelyLast() {
        this.iPrintZeroSetting = 2;
        return this;
    }

    public PeriodFormatterBuilder printZeroRarelyFirst() {
        this.iPrintZeroSetting = 1;
        return this;
    }

    public PeriodFormatterBuilder printZeroIfSupported() {
        this.iPrintZeroSetting = 3;
        return this;
    }

    public PeriodFormatterBuilder printZeroAlways() {
        this.iPrintZeroSetting = 4;
        return this;
    }

    public PeriodFormatterBuilder printZeroNever() {
        this.iPrintZeroSetting = 5;
        return this;
    }

    public PeriodFormatterBuilder appendPrefix(String string) {
        if (string == null) {
            throw new IllegalArgumentException();
        }
        return this.appendPrefix(new SimpleAffix(string));
    }

    public PeriodFormatterBuilder appendPrefix(String string, String string2) {
        if (string == null || string2 == null) {
            throw new IllegalArgumentException();
        }
        return this.appendPrefix(new PluralAffix(string, string2));
    }

    public PeriodFormatterBuilder appendPrefix(String[] stringArray, String[] stringArray2) {
        if (stringArray == null || stringArray2 == null || stringArray.length < 1 || stringArray.length != stringArray2.length) {
            throw new IllegalArgumentException();
        }
        return this.appendPrefix(new RegExAffix(stringArray, stringArray2));
    }

    private PeriodFormatterBuilder appendPrefix(PeriodFieldAffix periodFieldAffix) {
        if (periodFieldAffix == null) {
            throw new IllegalArgumentException();
        }
        if (this.iPrefix != null) {
            periodFieldAffix = new CompositeAffix(this.iPrefix, periodFieldAffix);
        }
        this.iPrefix = periodFieldAffix;
        return this;
    }

    public PeriodFormatterBuilder appendYears() {
        this.appendField(0);
        return this;
    }

    public PeriodFormatterBuilder appendMonths() {
        this.appendField(1);
        return this;
    }

    public PeriodFormatterBuilder appendWeeks() {
        this.appendField(2);
        return this;
    }

    public PeriodFormatterBuilder appendDays() {
        this.appendField(3);
        return this;
    }

    public PeriodFormatterBuilder appendHours() {
        this.appendField(4);
        return this;
    }

    public PeriodFormatterBuilder appendMinutes() {
        this.appendField(5);
        return this;
    }

    public PeriodFormatterBuilder appendSeconds() {
        this.appendField(6);
        return this;
    }

    public PeriodFormatterBuilder appendSecondsWithMillis() {
        this.appendField(8);
        return this;
    }

    public PeriodFormatterBuilder appendSecondsWithOptionalMillis() {
        this.appendField(9);
        return this;
    }

    public PeriodFormatterBuilder appendMillis() {
        this.appendField(7);
        return this;
    }

    public PeriodFormatterBuilder appendMillis3Digit() {
        this.appendField(7, 3);
        return this;
    }

    private void appendField(int n) {
        this.appendField(n, this.iMinPrintedDigits);
    }

    private void appendField(int n, int n2) {
        FieldFormatter fieldFormatter = new FieldFormatter(n2, this.iPrintZeroSetting, this.iMaxParsedDigits, this.iRejectSignedValues, n, this.iFieldFormatters, this.iPrefix, null);
        this.append0(fieldFormatter, fieldFormatter);
        this.iFieldFormatters[n] = fieldFormatter;
        this.iPrefix = null;
    }

    public PeriodFormatterBuilder appendSuffix(String string) {
        if (string == null) {
            throw new IllegalArgumentException();
        }
        return this.appendSuffix(new SimpleAffix(string));
    }

    public PeriodFormatterBuilder appendSuffix(String string, String string2) {
        if (string == null || string2 == null) {
            throw new IllegalArgumentException();
        }
        return this.appendSuffix(new PluralAffix(string, string2));
    }

    public PeriodFormatterBuilder appendSuffix(String[] stringArray, String[] stringArray2) {
        if (stringArray == null || stringArray2 == null || stringArray.length < 1 || stringArray.length != stringArray2.length) {
            throw new IllegalArgumentException();
        }
        return this.appendSuffix(new RegExAffix(stringArray, stringArray2));
    }

    private PeriodFormatterBuilder appendSuffix(PeriodFieldAffix periodFieldAffix) {
        Object object;
        Object object2;
        if (this.iElementPairs.size() > 0) {
            object2 = this.iElementPairs.get(this.iElementPairs.size() - 2);
            object = this.iElementPairs.get(this.iElementPairs.size() - 1);
        } else {
            object2 = null;
            object = null;
        }
        if (object2 == null || object == null || object2 != object || !(object2 instanceof FieldFormatter)) {
            throw new IllegalStateException("No field to apply suffix to");
        }
        this.clearPrefix();
        FieldFormatter fieldFormatter = new FieldFormatter((FieldFormatter)object2, periodFieldAffix);
        this.iElementPairs.set(this.iElementPairs.size() - 2, fieldFormatter);
        this.iElementPairs.set(this.iElementPairs.size() - 1, fieldFormatter);
        this.iFieldFormatters[fieldFormatter.getFieldType()] = fieldFormatter;
        return this;
    }

    public PeriodFormatterBuilder appendSeparator(String string) {
        return this.appendSeparator(string, string, null, true, true);
    }

    public PeriodFormatterBuilder appendSeparatorIfFieldsAfter(String string) {
        return this.appendSeparator(string, string, null, false, true);
    }

    public PeriodFormatterBuilder appendSeparatorIfFieldsBefore(String string) {
        return this.appendSeparator(string, string, null, true, false);
    }

    public PeriodFormatterBuilder appendSeparator(String string, String string2) {
        return this.appendSeparator(string, string2, null, true, true);
    }

    public PeriodFormatterBuilder appendSeparator(String string, String string2, String[] stringArray) {
        return this.appendSeparator(string, string2, stringArray, true, true);
    }

    private PeriodFormatterBuilder appendSeparator(String string, String string2, String[] stringArray, boolean bl, boolean bl2) {
        if (string == null || string2 == null) {
            throw new IllegalArgumentException();
        }
        this.clearPrefix();
        List<Object> list = this.iElementPairs;
        if (list.size() == 0) {
            if (bl2 && !bl) {
                Separator separator = new Separator(string, string2, stringArray, Literal.EMPTY, Literal.EMPTY, bl, bl2);
                this.append0(separator, separator);
            }
            return this;
        }
        Separator separator = null;
        int n = list.size();
        while (--n >= 0) {
            if (list.get(n) instanceof Separator) {
                separator = (Separator)list.get(n);
                list = list.subList(n + 1, list.size());
                break;
            }
            --n;
        }
        if (separator != null && list.size() == 0) {
            throw new IllegalStateException("Cannot have two adjacent separators");
        }
        Object[] objectArray = PeriodFormatterBuilder.createComposite(list);
        list.clear();
        Separator separator2 = new Separator(string, string2, stringArray, (PeriodPrinter)objectArray[0], (PeriodParser)objectArray[1], bl, bl2);
        list.add(separator2);
        list.add(separator2);
        return this;
    }

    private void clearPrefix() throws IllegalStateException {
        if (this.iPrefix != null) {
            throw new IllegalStateException("Prefix not followed by field");
        }
        this.iPrefix = null;
    }

    private PeriodFormatterBuilder append0(PeriodPrinter periodPrinter, PeriodParser periodParser) {
        this.iElementPairs.add(periodPrinter);
        this.iElementPairs.add(periodParser);
        this.iNotPrinter |= periodPrinter == null;
        this.iNotParser |= periodParser == null;
        return this;
    }

    private static PeriodFormatter toFormatter(List<Object> list, boolean bl, boolean bl2) {
        Object object;
        if (bl && bl2) {
            throw new IllegalStateException("Builder has created neither a printer nor a parser");
        }
        int n = list.size();
        if (n >= 2 && list.get(0) instanceof Separator && ((Separator)(object = (Separator)list.get(0))).iAfterParser == null && ((Separator)object).iAfterPrinter == null) {
            PeriodFormatter periodFormatter = PeriodFormatterBuilder.toFormatter(list.subList(2, n), bl, bl2);
            object = ((Separator)object).finish(periodFormatter.getPrinter(), periodFormatter.getParser());
            return new PeriodFormatter((PeriodPrinter)object, (PeriodParser)object);
        }
        object = PeriodFormatterBuilder.createComposite(list);
        if (bl) {
            return new PeriodFormatter(null, (PeriodParser)object[1]);
        }
        if (bl2) {
            return new PeriodFormatter((PeriodPrinter)object[0], null);
        }
        return new PeriodFormatter((PeriodPrinter)object[0], (PeriodParser)object[1]);
    }

    private static Object[] createComposite(List<Object> list) {
        switch (list.size()) {
            case 0: {
                return new Object[]{Literal.EMPTY, Literal.EMPTY};
            }
            case 1: {
                return new Object[]{list.get(0), list.get(1)};
            }
        }
        Composite composite = new Composite(list);
        return new Object[]{composite, composite};
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class Composite
    implements PeriodPrinter,
    PeriodParser {
        private final PeriodPrinter[] iPrinters;
        private final PeriodParser[] iParsers;

        Composite(List<Object> list) {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            ArrayList<Object> arrayList2 = new ArrayList<Object>();
            this.decompose(list, arrayList, arrayList2);
            this.iPrinters = arrayList.size() <= 0 ? null : arrayList.toArray(new PeriodPrinter[arrayList.size()]);
            this.iParsers = arrayList2.size() <= 0 ? null : arrayList2.toArray(new PeriodParser[arrayList2.size()]);
        }

        @Override
        public int countFieldsToPrint(ReadablePeriod readablePeriod, int n, Locale locale) {
            int n2;
            PeriodPrinter[] periodPrinterArray = this.iPrinters;
            int n3 = periodPrinterArray.length;
            for (n2 = 0; n2 < n && --n3 >= 0; n2 += periodPrinterArray[n3].countFieldsToPrint(readablePeriod, Integer.MAX_VALUE, locale)) {
            }
            return n2;
        }

        @Override
        public int calculatePrintedLength(ReadablePeriod readablePeriod, Locale locale) {
            int n = 0;
            PeriodPrinter[] periodPrinterArray = this.iPrinters;
            int n2 = periodPrinterArray.length;
            while (--n2 >= 0) {
                n += periodPrinterArray[n2].calculatePrintedLength(readablePeriod, locale);
            }
            return n;
        }

        @Override
        public void printTo(StringBuffer stringBuffer, ReadablePeriod readablePeriod, Locale locale) {
            PeriodPrinter[] periodPrinterArray = this.iPrinters;
            int n = periodPrinterArray.length;
            for (int i = 0; i < n; ++i) {
                periodPrinterArray[i].printTo(stringBuffer, readablePeriod, locale);
            }
        }

        @Override
        public void printTo(Writer writer, ReadablePeriod readablePeriod, Locale locale) throws IOException {
            PeriodPrinter[] periodPrinterArray = this.iPrinters;
            int n = periodPrinterArray.length;
            for (int i = 0; i < n; ++i) {
                periodPrinterArray[i].printTo(writer, readablePeriod, locale);
            }
        }

        @Override
        public int parseInto(ReadWritablePeriod readWritablePeriod, String string, int n, Locale locale) {
            PeriodParser[] periodParserArray = this.iParsers;
            if (periodParserArray == null) {
                throw new UnsupportedOperationException();
            }
            int n2 = periodParserArray.length;
            for (int i = 0; i < n2 && n >= 0; ++i) {
                n = periodParserArray[i].parseInto(readWritablePeriod, string, n, locale);
            }
            return n;
        }

        private void decompose(List<Object> list, List<Object> list2, List<Object> list3) {
            int n = list.size();
            for (int i = 0; i < n; i += 2) {
                Object object = list.get(i);
                if (object instanceof PeriodPrinter) {
                    if (object instanceof Composite) {
                        this.addArrayToList(list2, ((Composite)object).iPrinters);
                    } else {
                        list2.add(object);
                    }
                }
                if (!((object = list.get(i + 1)) instanceof PeriodParser)) continue;
                if (object instanceof Composite) {
                    this.addArrayToList(list3, ((Composite)object).iParsers);
                    continue;
                }
                list3.add(object);
            }
        }

        private void addArrayToList(List<Object> list, Object[] objectArray) {
            if (objectArray != null) {
                for (int i = 0; i < objectArray.length; ++i) {
                    list.add(objectArray[i]);
                }
            }
        }
    }

    static class Separator
    implements PeriodPrinter,
    PeriodParser {
        private final String iText;
        private final String iFinalText;
        private final String[] iParsedForms;
        private final boolean iUseBefore;
        private final boolean iUseAfter;
        private final PeriodPrinter iBeforePrinter;
        private volatile PeriodPrinter iAfterPrinter;
        private final PeriodParser iBeforeParser;
        private volatile PeriodParser iAfterParser;

        Separator(String string, String string2, String[] stringArray, PeriodPrinter periodPrinter, PeriodParser periodParser, boolean bl, boolean bl2) {
            this.iText = string;
            this.iFinalText = string2;
            if (!(string2 != null && !string.equals(string2) || stringArray != null && stringArray.length != 0)) {
                this.iParsedForms = new String[]{string};
            } else {
                TreeSet<String> treeSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                treeSet.add(string);
                treeSet.add(string2);
                if (stringArray != null) {
                    int n = stringArray.length;
                    while (--n >= 0) {
                        treeSet.add(stringArray[n]);
                    }
                }
                ArrayList arrayList = new ArrayList(treeSet);
                Collections.reverse(arrayList);
                this.iParsedForms = arrayList.toArray(new String[arrayList.size()]);
            }
            this.iBeforePrinter = periodPrinter;
            this.iBeforeParser = periodParser;
            this.iUseBefore = bl;
            this.iUseAfter = bl2;
        }

        public int countFieldsToPrint(ReadablePeriod readablePeriod, int n, Locale locale) {
            int n2 = this.iBeforePrinter.countFieldsToPrint(readablePeriod, n, locale);
            if (n2 < n) {
                n2 += this.iAfterPrinter.countFieldsToPrint(readablePeriod, n, locale);
            }
            return n2;
        }

        public int calculatePrintedLength(ReadablePeriod readablePeriod, Locale locale) {
            PeriodPrinter periodPrinter = this.iBeforePrinter;
            PeriodPrinter periodPrinter2 = this.iAfterPrinter;
            int n = periodPrinter.calculatePrintedLength(readablePeriod, locale) + periodPrinter2.calculatePrintedLength(readablePeriod, locale);
            if (this.iUseBefore) {
                if (periodPrinter.countFieldsToPrint(readablePeriod, 1, locale) > 0) {
                    if (this.iUseAfter) {
                        int n2 = periodPrinter2.countFieldsToPrint(readablePeriod, 2, locale);
                        if (n2 > 0) {
                            n += (n2 > 1 ? this.iText : this.iFinalText).length();
                        }
                    } else {
                        n += this.iText.length();
                    }
                }
            } else if (this.iUseAfter && periodPrinter2.countFieldsToPrint(readablePeriod, 1, locale) > 0) {
                n += this.iText.length();
            }
            return n;
        }

        public void printTo(StringBuffer stringBuffer, ReadablePeriod readablePeriod, Locale locale) {
            PeriodPrinter periodPrinter = this.iBeforePrinter;
            PeriodPrinter periodPrinter2 = this.iAfterPrinter;
            periodPrinter.printTo(stringBuffer, readablePeriod, locale);
            if (this.iUseBefore) {
                if (periodPrinter.countFieldsToPrint(readablePeriod, 1, locale) > 0) {
                    if (this.iUseAfter) {
                        int n = periodPrinter2.countFieldsToPrint(readablePeriod, 2, locale);
                        if (n > 0) {
                            stringBuffer.append(n > 1 ? this.iText : this.iFinalText);
                        }
                    } else {
                        stringBuffer.append(this.iText);
                    }
                }
            } else if (this.iUseAfter && periodPrinter2.countFieldsToPrint(readablePeriod, 1, locale) > 0) {
                stringBuffer.append(this.iText);
            }
            periodPrinter2.printTo(stringBuffer, readablePeriod, locale);
        }

        public void printTo(Writer writer, ReadablePeriod readablePeriod, Locale locale) throws IOException {
            PeriodPrinter periodPrinter = this.iBeforePrinter;
            PeriodPrinter periodPrinter2 = this.iAfterPrinter;
            periodPrinter.printTo(writer, readablePeriod, locale);
            if (this.iUseBefore) {
                if (periodPrinter.countFieldsToPrint(readablePeriod, 1, locale) > 0) {
                    if (this.iUseAfter) {
                        int n = periodPrinter2.countFieldsToPrint(readablePeriod, 2, locale);
                        if (n > 0) {
                            writer.write(n > 1 ? this.iText : this.iFinalText);
                        }
                    } else {
                        writer.write(this.iText);
                    }
                }
            } else if (this.iUseAfter && periodPrinter2.countFieldsToPrint(readablePeriod, 1, locale) > 0) {
                writer.write(this.iText);
            }
            periodPrinter2.printTo(writer, readablePeriod, locale);
        }

        public int parseInto(ReadWritablePeriod readWritablePeriod, String string, int n, Locale locale) {
            int n2 = n;
            if ((n = this.iBeforeParser.parseInto(readWritablePeriod, string, n, locale)) < 0) {
                return n;
            }
            boolean bl = false;
            int n3 = -1;
            if (n > n2) {
                for (String string2 : this.iParsedForms) {
                    if (string2 != null && string2.length() != 0 && !string.regionMatches(true, n, string2, 0, string2.length())) continue;
                    n3 = string2 == null ? 0 : string2.length();
                    n += n3;
                    bl = true;
                    break;
                }
            }
            n2 = n;
            if ((n = this.iAfterParser.parseInto(readWritablePeriod, string, n, locale)) < 0) {
                return n;
            }
            if (bl && n == n2 && n3 > 0) {
                return ~n2;
            }
            if (n > n2 && !bl && !this.iUseBefore) {
                return ~n2;
            }
            return n;
        }

        Separator finish(PeriodPrinter periodPrinter, PeriodParser periodParser) {
            this.iAfterPrinter = periodPrinter;
            this.iAfterParser = periodParser;
            return this;
        }
    }

    static class Literal
    implements PeriodPrinter,
    PeriodParser {
        static final Literal EMPTY = new Literal("");
        private final String iText;

        Literal(String string) {
            this.iText = string;
        }

        public int countFieldsToPrint(ReadablePeriod readablePeriod, int n, Locale locale) {
            return 0;
        }

        public int calculatePrintedLength(ReadablePeriod readablePeriod, Locale locale) {
            return this.iText.length();
        }

        public void printTo(StringBuffer stringBuffer, ReadablePeriod readablePeriod, Locale locale) {
            stringBuffer.append(this.iText);
        }

        public void printTo(Writer writer, ReadablePeriod readablePeriod, Locale locale) throws IOException {
            writer.write(this.iText);
        }

        public int parseInto(ReadWritablePeriod readWritablePeriod, String string, int n, Locale locale) {
            if (string.regionMatches(true, n, this.iText, 0, this.iText.length())) {
                return n + this.iText.length();
            }
            return ~n;
        }
    }

    static class FieldFormatter
    implements PeriodPrinter,
    PeriodParser {
        private final int iMinPrintedDigits;
        private final int iPrintZeroSetting;
        private final int iMaxParsedDigits;
        private final boolean iRejectSignedValues;
        private final int iFieldType;
        private final FieldFormatter[] iFieldFormatters;
        private final PeriodFieldAffix iPrefix;
        private final PeriodFieldAffix iSuffix;

        FieldFormatter(int n, int n2, int n3, boolean bl, int n4, FieldFormatter[] fieldFormatterArray, PeriodFieldAffix periodFieldAffix, PeriodFieldAffix periodFieldAffix2) {
            this.iMinPrintedDigits = n;
            this.iPrintZeroSetting = n2;
            this.iMaxParsedDigits = n3;
            this.iRejectSignedValues = bl;
            this.iFieldType = n4;
            this.iFieldFormatters = fieldFormatterArray;
            this.iPrefix = periodFieldAffix;
            this.iSuffix = periodFieldAffix2;
        }

        FieldFormatter(FieldFormatter fieldFormatter, PeriodFieldAffix periodFieldAffix) {
            this.iMinPrintedDigits = fieldFormatter.iMinPrintedDigits;
            this.iPrintZeroSetting = fieldFormatter.iPrintZeroSetting;
            this.iMaxParsedDigits = fieldFormatter.iMaxParsedDigits;
            this.iRejectSignedValues = fieldFormatter.iRejectSignedValues;
            this.iFieldType = fieldFormatter.iFieldType;
            this.iFieldFormatters = fieldFormatter.iFieldFormatters;
            this.iPrefix = fieldFormatter.iPrefix;
            if (fieldFormatter.iSuffix != null) {
                periodFieldAffix = new CompositeAffix(fieldFormatter.iSuffix, periodFieldAffix);
            }
            this.iSuffix = periodFieldAffix;
        }

        public void finish(FieldFormatter[] fieldFormatterArray) {
            HashSet<PeriodFieldAffix> hashSet = new HashSet<PeriodFieldAffix>();
            HashSet<PeriodFieldAffix> hashSet2 = new HashSet<PeriodFieldAffix>();
            for (FieldFormatter fieldFormatter : fieldFormatterArray) {
                if (fieldFormatter == null || this.equals(fieldFormatter)) continue;
                hashSet.add(fieldFormatter.iPrefix);
                hashSet2.add(fieldFormatter.iSuffix);
            }
            if (this.iPrefix != null) {
                this.iPrefix.finish(hashSet);
            }
            if (this.iSuffix != null) {
                this.iSuffix.finish(hashSet2);
            }
        }

        public int countFieldsToPrint(ReadablePeriod readablePeriod, int n, Locale locale) {
            if (n <= 0) {
                return 0;
            }
            if (this.iPrintZeroSetting == 4 || this.getFieldValue(readablePeriod) != Long.MAX_VALUE) {
                return 1;
            }
            return 0;
        }

        public int calculatePrintedLength(ReadablePeriod readablePeriod, Locale locale) {
            long l = this.getFieldValue(readablePeriod);
            if (l == Long.MAX_VALUE) {
                return 0;
            }
            int n = Math.max(FormatUtils.calculateDigitCount(l), this.iMinPrintedDigits);
            if (this.iFieldType >= 8) {
                n = l < 0L ? Math.max(n, 5) : Math.max(n, 4);
                ++n;
                if (this.iFieldType == 9 && Math.abs(l) % 1000L == 0L) {
                    n -= 4;
                }
                l /= 1000L;
            }
            int n2 = (int)l;
            if (this.iPrefix != null) {
                n += this.iPrefix.calculatePrintedLength(n2);
            }
            if (this.iSuffix != null) {
                n += this.iSuffix.calculatePrintedLength(n2);
            }
            return n;
        }

        public void printTo(StringBuffer stringBuffer, ReadablePeriod readablePeriod, Locale locale) {
            long l = this.getFieldValue(readablePeriod);
            if (l == Long.MAX_VALUE) {
                return;
            }
            int n = (int)l;
            if (this.iFieldType >= 8) {
                n = (int)(l / 1000L);
            }
            if (this.iPrefix != null) {
                this.iPrefix.printTo(stringBuffer, n);
            }
            int n2 = stringBuffer.length();
            int n3 = this.iMinPrintedDigits;
            if (n3 <= 1) {
                FormatUtils.appendUnpaddedInteger(stringBuffer, n);
            } else {
                FormatUtils.appendPaddedInteger(stringBuffer, n, n3);
            }
            if (this.iFieldType >= 8) {
                int n4 = (int)(Math.abs(l) % 1000L);
                if (this.iFieldType == 8 || n4 > 0) {
                    if (l < 0L && l > -1000L) {
                        stringBuffer.insert(n2, '-');
                    }
                    stringBuffer.append('.');
                    FormatUtils.appendPaddedInteger(stringBuffer, n4, 3);
                }
            }
            if (this.iSuffix != null) {
                this.iSuffix.printTo(stringBuffer, n);
            }
        }

        public void printTo(Writer writer, ReadablePeriod readablePeriod, Locale locale) throws IOException {
            int n;
            long l = this.getFieldValue(readablePeriod);
            if (l == Long.MAX_VALUE) {
                return;
            }
            int n2 = (int)l;
            if (this.iFieldType >= 8) {
                n2 = (int)(l / 1000L);
            }
            if (this.iPrefix != null) {
                this.iPrefix.printTo(writer, n2);
            }
            if ((n = this.iMinPrintedDigits) <= 1) {
                FormatUtils.writeUnpaddedInteger(writer, n2);
            } else {
                FormatUtils.writePaddedInteger(writer, n2, n);
            }
            if (this.iFieldType >= 8) {
                int n3 = (int)(Math.abs(l) % 1000L);
                if (this.iFieldType == 8 || n3 > 0) {
                    writer.write(46);
                    FormatUtils.writePaddedInteger(writer, n3, 3);
                }
            }
            if (this.iSuffix != null) {
                this.iSuffix.printTo(writer, n2);
            }
        }

        public int parseInto(ReadWritablePeriod readWritablePeriod, String string, int n, Locale locale) {
            int n2;
            boolean bl;
            boolean bl2 = bl = this.iPrintZeroSetting == 4;
            if (n >= string.length()) {
                return bl ? ~n : n;
            }
            if (this.iPrefix != null) {
                if ((n = this.iPrefix.parse(string, n)) >= 0) {
                    bl = true;
                } else {
                    if (!bl) {
                        return ~n;
                    }
                    return n;
                }
            }
            int n3 = -1;
            if (this.iSuffix != null && !bl) {
                n3 = this.iSuffix.scan(string, n);
                if (n3 >= 0) {
                    bl = true;
                } else {
                    if (!bl) {
                        return ~n3;
                    }
                    return n3;
                }
            }
            if (!bl && !this.isSupported(readWritablePeriod.getPeriodType(), this.iFieldType)) {
                return n;
            }
            int n4 = n3 > 0 ? Math.min(this.iMaxParsedDigits, n3 - n) : Math.min(this.iMaxParsedDigits, string.length() - n);
            int n5 = 0;
            int n6 = -1;
            boolean bl3 = false;
            boolean bl4 = false;
            while (n5 < n4) {
                n2 = string.charAt(n + n5);
                if (!(n5 != 0 || n2 != 45 && n2 != 43 || this.iRejectSignedValues)) {
                    boolean bl5 = bl4 = n2 == 45;
                    if (n5 + 1 >= n4) break;
                    char c = string.charAt(n + n5 + 1);
                    n2 = c;
                    if (c < '0' || n2 > 57) break;
                    if (bl4) {
                        ++n5;
                    } else {
                        ++n;
                    }
                    n4 = Math.min(n4 + 1, string.length() - n);
                    continue;
                }
                if (n2 >= 48 && n2 <= 57) {
                    bl3 = true;
                } else {
                    if (n2 != 46 && n2 != 44 || this.iFieldType != 8 && this.iFieldType != 9 || n6 >= 0) break;
                    n6 = n + n5 + 1;
                    n4 = Math.min(n4 + 1, string.length() - n);
                }
                ++n5;
            }
            if (!bl3) {
                return ~n;
            }
            if (n3 >= 0 && n + n5 != n3) {
                return n;
            }
            if (this.iFieldType != 8 && this.iFieldType != 9) {
                this.setFieldValue(readWritablePeriod, this.iFieldType, this.parseInt(string, n, n5));
            } else if (n6 < 0) {
                this.setFieldValue(readWritablePeriod, 6, this.parseInt(string, n, n5));
                this.setFieldValue(readWritablePeriod, 7, 0);
            } else {
                int n7;
                n2 = this.parseInt(string, n, n6 - n - 1);
                this.setFieldValue(readWritablePeriod, 6, n2);
                int n8 = n + n5 - n6;
                if (n8 <= 0) {
                    n7 = 0;
                } else {
                    if (n8 >= 3) {
                        n7 = this.parseInt(string, n6, 3);
                    } else {
                        n7 = this.parseInt(string, n6, n8);
                        n7 = n8 == 1 ? (n7 *= 100) : (n7 *= 10);
                    }
                    if (bl4 || n2 < 0) {
                        n7 = -n7;
                    }
                }
                this.setFieldValue(readWritablePeriod, 7, n7);
            }
            if ((n += n5) >= 0 && this.iSuffix != null) {
                n = this.iSuffix.parse(string, n);
            }
            return n;
        }

        private int parseInt(String string, int n, int n2) {
            boolean bl;
            if (n2 >= 10) {
                return Integer.parseInt(string.substring(n, n + n2));
            }
            if (n2 <= 0) {
                return 0;
            }
            int n3 = string.charAt(n++);
            --n2;
            if (n3 == 45) {
                if (--n2 < 0) {
                    return 0;
                }
                bl = true;
                n3 = string.charAt(n++);
            } else {
                bl = false;
            }
            n3 -= 48;
            while (n2-- > 0) {
                n3 = (n3 << 3) + (n3 << 1) + string.charAt(n++) - 48;
            }
            return bl ? -n3 : n3;
        }

        long getFieldValue(ReadablePeriod readablePeriod) {
            int n;
            long l;
            PeriodType periodType = this.iPrintZeroSetting == 4 ? null : readablePeriod.getPeriodType();
            if (periodType != null && !this.isSupported(periodType, this.iFieldType)) {
                return Long.MAX_VALUE;
            }
            switch (this.iFieldType) {
                default: {
                    return Long.MAX_VALUE;
                }
                case 0: {
                    l = readablePeriod.get(DurationFieldType.years());
                    break;
                }
                case 1: {
                    l = readablePeriod.get(DurationFieldType.months());
                    break;
                }
                case 2: {
                    l = readablePeriod.get(DurationFieldType.weeks());
                    break;
                }
                case 3: {
                    l = readablePeriod.get(DurationFieldType.days());
                    break;
                }
                case 4: {
                    l = readablePeriod.get(DurationFieldType.hours());
                    break;
                }
                case 5: {
                    l = readablePeriod.get(DurationFieldType.minutes());
                    break;
                }
                case 6: {
                    l = readablePeriod.get(DurationFieldType.seconds());
                    break;
                }
                case 7: {
                    l = readablePeriod.get(DurationFieldType.millis());
                    break;
                }
                case 8: 
                case 9: {
                    n = readablePeriod.get(DurationFieldType.seconds());
                    int n2 = readablePeriod.get(DurationFieldType.millis());
                    l = (long)n * 1000L + (long)n2;
                }
            }
            if (l == 0L) {
                switch (this.iPrintZeroSetting) {
                    case 5: {
                        return Long.MAX_VALUE;
                    }
                    case 2: {
                        if (this.isZero(readablePeriod) && this.iFieldFormatters[this.iFieldType] == this) {
                            for (n = this.iFieldType + 1; n <= 9; ++n) {
                                if (!this.isSupported(periodType, n) || this.iFieldFormatters[n] == null) continue;
                                return Long.MAX_VALUE;
                            }
                            break;
                        }
                        return Long.MAX_VALUE;
                    }
                    case 1: {
                        if (this.isZero(readablePeriod) && this.iFieldFormatters[this.iFieldType] == this) {
                            n = Math.min(this.iFieldType, 8);
                            --n;
                            while (n >= 0 && n <= 9) {
                                if (this.isSupported(periodType, n) && this.iFieldFormatters[n] != null) {
                                    return Long.MAX_VALUE;
                                }
                                --n;
                            }
                            break;
                        }
                        return Long.MAX_VALUE;
                    }
                }
            }
            return l;
        }

        boolean isZero(ReadablePeriod readablePeriod) {
            int n = readablePeriod.size();
            for (int i = 0; i < n; ++i) {
                if (readablePeriod.getValue(i) == 0) continue;
                return false;
            }
            return true;
        }

        boolean isSupported(PeriodType periodType, int n) {
            switch (n) {
                default: {
                    return false;
                }
                case 0: {
                    return periodType.isSupported(DurationFieldType.years());
                }
                case 1: {
                    return periodType.isSupported(DurationFieldType.months());
                }
                case 2: {
                    return periodType.isSupported(DurationFieldType.weeks());
                }
                case 3: {
                    return periodType.isSupported(DurationFieldType.days());
                }
                case 4: {
                    return periodType.isSupported(DurationFieldType.hours());
                }
                case 5: {
                    return periodType.isSupported(DurationFieldType.minutes());
                }
                case 6: {
                    return periodType.isSupported(DurationFieldType.seconds());
                }
                case 7: {
                    return periodType.isSupported(DurationFieldType.millis());
                }
                case 8: 
                case 9: 
            }
            return periodType.isSupported(DurationFieldType.seconds()) || periodType.isSupported(DurationFieldType.millis());
        }

        void setFieldValue(ReadWritablePeriod readWritablePeriod, int n, int n2) {
            switch (n) {
                default: {
                    break;
                }
                case 0: {
                    readWritablePeriod.setYears(n2);
                    break;
                }
                case 1: {
                    readWritablePeriod.setMonths(n2);
                    break;
                }
                case 2: {
                    readWritablePeriod.setWeeks(n2);
                    break;
                }
                case 3: {
                    readWritablePeriod.setDays(n2);
                    break;
                }
                case 4: {
                    readWritablePeriod.setHours(n2);
                    break;
                }
                case 5: {
                    readWritablePeriod.setMinutes(n2);
                    break;
                }
                case 6: {
                    readWritablePeriod.setSeconds(n2);
                    break;
                }
                case 7: {
                    readWritablePeriod.setMillis(n2);
                }
            }
        }

        int getFieldType() {
            return this.iFieldType;
        }
    }

    static class CompositeAffix
    extends IgnorableAffix {
        private final PeriodFieldAffix iLeft;
        private final PeriodFieldAffix iRight;
        private final String[] iLeftRightCombinations;

        CompositeAffix(PeriodFieldAffix periodFieldAffix, PeriodFieldAffix periodFieldAffix2) {
            this.iLeft = periodFieldAffix;
            this.iRight = periodFieldAffix2;
            HashSet<String> hashSet = new HashSet<String>();
            for (String string : this.iLeft.getAffixes()) {
                for (String string2 : this.iRight.getAffixes()) {
                    hashSet.add(string + string2);
                }
            }
            this.iLeftRightCombinations = hashSet.toArray(new String[hashSet.size()]);
        }

        public int calculatePrintedLength(int n) {
            return this.iLeft.calculatePrintedLength(n) + this.iRight.calculatePrintedLength(n);
        }

        public void printTo(StringBuffer stringBuffer, int n) {
            this.iLeft.printTo(stringBuffer, n);
            this.iRight.printTo(stringBuffer, n);
        }

        public void printTo(Writer writer, int n) throws IOException {
            this.iLeft.printTo(writer, n);
            this.iRight.printTo(writer, n);
        }

        public int parse(String string, int n) {
            int n2 = this.iLeft.parse(string, n);
            if (n2 >= 0 && (n2 = this.iRight.parse(string, n2)) >= 0 && this.matchesOtherAffix(this.parse(string, n2) - n2, string, n)) {
                return ~n;
            }
            return n2;
        }

        public int scan(String string, int n) {
            int n2;
            int n3 = this.iLeft.scan(string, n);
            if (!(n3 < 0 || (n2 = this.iRight.scan(string, this.iLeft.parse(string, n3))) >= 0 && this.matchesOtherAffix(this.iRight.parse(string, n2) - n3, string, n))) {
                if (n3 > 0) {
                    return n3;
                }
                return n2;
            }
            return ~n;
        }

        public String[] getAffixes() {
            return (String[])this.iLeftRightCombinations.clone();
        }
    }

    static class RegExAffix
    extends IgnorableAffix {
        private static final Comparator<String> LENGTH_DESC_COMPARATOR = new Comparator<String>(){

            @Override
            public int compare(String string, String string2) {
                return string2.length() - string.length();
            }
        };
        private final String[] iSuffixes;
        private final Pattern[] iPatterns;
        private final String[] iSuffixesSortedDescByLength;

        RegExAffix(String[] stringArray, String[] stringArray2) {
            this.iSuffixes = (String[])stringArray2.clone();
            this.iPatterns = new Pattern[stringArray.length];
            for (int i = 0; i < stringArray.length; ++i) {
                Pattern pattern = (Pattern)PATTERNS.get(stringArray[i]);
                if (pattern == null) {
                    pattern = Pattern.compile(stringArray[i]);
                    PATTERNS.putIfAbsent(stringArray[i], pattern);
                }
                this.iPatterns[i] = pattern;
            }
            this.iSuffixesSortedDescByLength = (String[])this.iSuffixes.clone();
            Arrays.sort(this.iSuffixesSortedDescByLength, LENGTH_DESC_COMPARATOR);
        }

        private int selectSuffixIndex(int n) {
            String string = String.valueOf(n);
            for (int i = 0; i < this.iPatterns.length; ++i) {
                if (!this.iPatterns[i].matcher(string).matches()) continue;
                return i;
            }
            return this.iPatterns.length - 1;
        }

        public int calculatePrintedLength(int n) {
            return this.iSuffixes[this.selectSuffixIndex(n)].length();
        }

        public void printTo(StringBuffer stringBuffer, int n) {
            stringBuffer.append(this.iSuffixes[this.selectSuffixIndex(n)]);
        }

        public void printTo(Writer writer, int n) throws IOException {
            writer.write(this.iSuffixes[this.selectSuffixIndex(n)]);
        }

        public int parse(String string, int n) {
            for (String string2 : this.iSuffixesSortedDescByLength) {
                if (!string.regionMatches(true, n, string2, 0, string2.length()) || this.matchesOtherAffix(string2.length(), string, n)) continue;
                return n + string2.length();
            }
            return ~n;
        }

        public int scan(String string, int n) {
            int n2 = string.length();
            for (int i = n; i < n2; ++i) {
                for (String string2 : this.iSuffixesSortedDescByLength) {
                    if (!string.regionMatches(true, i, string2, 0, string2.length()) || this.matchesOtherAffix(string2.length(), string, i)) continue;
                    return i;
                }
            }
            return ~n;
        }

        public String[] getAffixes() {
            return (String[])this.iSuffixes.clone();
        }
    }

    static class PluralAffix
    extends IgnorableAffix {
        private final String iSingularText;
        private final String iPluralText;

        PluralAffix(String string, String string2) {
            this.iSingularText = string;
            this.iPluralText = string2;
        }

        public int calculatePrintedLength(int n) {
            return (n == 1 ? this.iSingularText : this.iPluralText).length();
        }

        public void printTo(StringBuffer stringBuffer, int n) {
            stringBuffer.append(n == 1 ? this.iSingularText : this.iPluralText);
        }

        public void printTo(Writer writer, int n) throws IOException {
            writer.write(n == 1 ? this.iSingularText : this.iPluralText);
        }

        public int parse(String string, int n) {
            String string2 = this.iPluralText;
            String string3 = this.iSingularText;
            if (string2.length() < string3.length()) {
                String string4 = string2;
                string2 = string3;
                string3 = string4;
            }
            if (string.regionMatches(true, n, string2, 0, string2.length()) && !this.matchesOtherAffix(string2.length(), string, n)) {
                return n + string2.length();
            }
            if (string.regionMatches(true, n, string3, 0, string3.length()) && !this.matchesOtherAffix(string3.length(), string, n)) {
                return n + string3.length();
            }
            return ~n;
        }

        public int scan(String string, int n) {
            String string2 = this.iPluralText;
            String string3 = this.iSingularText;
            if (string2.length() < string3.length()) {
                String string4 = string2;
                string2 = string3;
                string3 = string4;
            }
            int n2 = string2.length();
            int n3 = string3.length();
            int n4 = string.length();
            for (int i = n; i < n4; ++i) {
                if (string.regionMatches(true, i, string2, 0, n2) && !this.matchesOtherAffix(string2.length(), string, i)) {
                    return i;
                }
                if (!string.regionMatches(true, i, string3, 0, n3) || this.matchesOtherAffix(string3.length(), string, i)) continue;
                return i;
            }
            return ~n;
        }

        public String[] getAffixes() {
            return new String[]{this.iSingularText, this.iPluralText};
        }
    }

    static class SimpleAffix
    extends IgnorableAffix {
        private final String iText;

        SimpleAffix(String string) {
            this.iText = string;
        }

        public int calculatePrintedLength(int n) {
            return this.iText.length();
        }

        public void printTo(StringBuffer stringBuffer, int n) {
            stringBuffer.append(this.iText);
        }

        public void printTo(Writer writer, int n) throws IOException {
            writer.write(this.iText);
        }

        public int parse(String string, int n) {
            String string2 = this.iText;
            int n2 = string2.length();
            if (string.regionMatches(true, n, string2, 0, n2) && !this.matchesOtherAffix(n2, string, n)) {
                return n + n2;
            }
            return ~n;
        }

        public int scan(String string, int n) {
            String string2 = this.iText;
            int n2 = string2.length();
            int n3 = string.length();
            block3: for (int i = n; i < n3; ++i) {
                if (string.regionMatches(true, i, string2, 0, n2) && !this.matchesOtherAffix(n2, string, i)) {
                    return i;
                }
                switch (string.charAt(i)) {
                    case '+': 
                    case ',': 
                    case '-': 
                    case '.': 
                    case '0': 
                    case '1': 
                    case '2': 
                    case '3': 
                    case '4': 
                    case '5': 
                    case '6': 
                    case '7': 
                    case '8': 
                    case '9': {
                        continue block3;
                    }
                }
            }
            return ~n;
        }

        public String[] getAffixes() {
            return new String[]{this.iText};
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static abstract class IgnorableAffix
    implements PeriodFieldAffix {
        private volatile String[] iOtherAffixes;

        IgnorableAffix() {
        }

        @Override
        public void finish(Set<PeriodFieldAffix> set) {
            if (this.iOtherAffixes == null) {
                int n = Integer.MAX_VALUE;
                String stringArray = null;
                for (String stringArray2 : this.getAffixes()) {
                    if (stringArray2.length() >= n) continue;
                    n = stringArray2.length();
                    stringArray = stringArray2;
                }
                HashSet hashSet = new HashSet();
                for (PeriodFieldAffix periodFieldAffix : set) {
                    if (periodFieldAffix == null) continue;
                    for (String string : periodFieldAffix.getAffixes()) {
                        if (string.length() <= n && (!string.equalsIgnoreCase(stringArray) || string.equals(stringArray))) continue;
                        hashSet.add(string);
                    }
                }
                this.iOtherAffixes = hashSet.toArray(new String[hashSet.size()]);
            }
        }

        protected boolean matchesOtherAffix(int n, String string, int n2) {
            if (this.iOtherAffixes != null) {
                for (String string2 : this.iOtherAffixes) {
                    int n3 = string2.length();
                    if ((n >= n3 || !string.regionMatches(true, n2, string2, 0, n3)) && (n != n3 || !string.regionMatches(false, n2, string2, 0, n3))) continue;
                    return true;
                }
            }
            return false;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static interface PeriodFieldAffix {
        public int calculatePrintedLength(int var1);

        public void printTo(StringBuffer var1, int var2);

        public void printTo(Writer var1, int var2) throws IOException;

        public int parse(String var1, int var2);

        public int scan(String var1, int var2);

        public String[] getAffixes();

        public void finish(Set<PeriodFieldAffix> var1);
    }
}

