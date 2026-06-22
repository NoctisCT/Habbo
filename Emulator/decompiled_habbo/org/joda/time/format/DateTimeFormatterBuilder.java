/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadablePartial;
import org.joda.time.field.MillisDurationField;
import org.joda.time.field.PreciseDateTimeField;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimeParserBucket;
import org.joda.time.format.DateTimeParserInternalParser;
import org.joda.time.format.DateTimePrinter;
import org.joda.time.format.DateTimePrinterInternalPrinter;
import org.joda.time.format.FormatUtils;
import org.joda.time.format.InternalParser;
import org.joda.time.format.InternalParserDateTimeParser;
import org.joda.time.format.InternalPrinter;
import org.joda.time.format.InternalPrinterDateTimePrinter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DateTimeFormatterBuilder {
    private ArrayList<Object> iElementPairs = new ArrayList();
    private Object iFormatter;

    public DateTimeFormatter toFormatter() {
        Object object = this.getFormatter();
        InternalPrinter internalPrinter = null;
        if (this.isPrinter(object)) {
            internalPrinter = (InternalPrinter)object;
        }
        InternalParser internalParser = null;
        if (this.isParser(object)) {
            internalParser = (InternalParser)object;
        }
        if (internalPrinter != null || internalParser != null) {
            return new DateTimeFormatter(internalPrinter, internalParser);
        }
        throw new UnsupportedOperationException("Both printing and parsing not supported");
    }

    public DateTimePrinter toPrinter() {
        Object object = this.getFormatter();
        if (this.isPrinter(object)) {
            InternalPrinter internalPrinter = (InternalPrinter)object;
            return InternalPrinterDateTimePrinter.of(internalPrinter);
        }
        throw new UnsupportedOperationException("Printing is not supported");
    }

    public DateTimeParser toParser() {
        Object object = this.getFormatter();
        if (this.isParser(object)) {
            InternalParser internalParser = (InternalParser)object;
            return InternalParserDateTimeParser.of(internalParser);
        }
        throw new UnsupportedOperationException("Parsing is not supported");
    }

    public boolean canBuildFormatter() {
        return this.isFormatter(this.getFormatter());
    }

    public boolean canBuildPrinter() {
        return this.isPrinter(this.getFormatter());
    }

    public boolean canBuildParser() {
        return this.isParser(this.getFormatter());
    }

    public void clear() {
        this.iFormatter = null;
        this.iElementPairs.clear();
    }

    public DateTimeFormatterBuilder append(DateTimeFormatter dateTimeFormatter) {
        if (dateTimeFormatter == null) {
            throw new IllegalArgumentException("No formatter supplied");
        }
        return this.append0(dateTimeFormatter.getPrinter0(), dateTimeFormatter.getParser0());
    }

    public DateTimeFormatterBuilder append(DateTimePrinter dateTimePrinter) {
        this.checkPrinter(dateTimePrinter);
        return this.append0(DateTimePrinterInternalPrinter.of(dateTimePrinter), null);
    }

    public DateTimeFormatterBuilder append(DateTimeParser dateTimeParser) {
        this.checkParser(dateTimeParser);
        return this.append0(null, DateTimeParserInternalParser.of(dateTimeParser));
    }

    public DateTimeFormatterBuilder append(DateTimePrinter dateTimePrinter, DateTimeParser dateTimeParser) {
        this.checkPrinter(dateTimePrinter);
        this.checkParser(dateTimeParser);
        return this.append0(DateTimePrinterInternalPrinter.of(dateTimePrinter), DateTimeParserInternalParser.of(dateTimeParser));
    }

    public DateTimeFormatterBuilder append(DateTimePrinter dateTimePrinter, DateTimeParser[] dateTimeParserArray) {
        int n;
        if (dateTimePrinter != null) {
            this.checkPrinter(dateTimePrinter);
        }
        if (dateTimeParserArray == null) {
            throw new IllegalArgumentException("No parsers supplied");
        }
        int n2 = dateTimeParserArray.length;
        if (n2 == 1) {
            if (dateTimeParserArray[0] == null) {
                throw new IllegalArgumentException("No parser supplied");
            }
            return this.append0(DateTimePrinterInternalPrinter.of(dateTimePrinter), DateTimeParserInternalParser.of(dateTimeParserArray[0]));
        }
        InternalParser[] internalParserArray = new InternalParser[n2];
        for (n = 0; n < n2 - 1; ++n) {
            internalParserArray[n] = DateTimeParserInternalParser.of(dateTimeParserArray[n]);
            if (internalParserArray[n] != null) continue;
            throw new IllegalArgumentException("Incomplete parser array");
        }
        internalParserArray[n] = DateTimeParserInternalParser.of(dateTimeParserArray[n]);
        return this.append0(DateTimePrinterInternalPrinter.of(dateTimePrinter), new MatchingParser(internalParserArray));
    }

    public DateTimeFormatterBuilder appendOptional(DateTimeParser dateTimeParser) {
        this.checkParser(dateTimeParser);
        InternalParser[] internalParserArray = new InternalParser[]{DateTimeParserInternalParser.of(dateTimeParser), null};
        return this.append0(null, new MatchingParser(internalParserArray));
    }

    private void checkParser(DateTimeParser dateTimeParser) {
        if (dateTimeParser == null) {
            throw new IllegalArgumentException("No parser supplied");
        }
    }

    private void checkPrinter(DateTimePrinter dateTimePrinter) {
        if (dateTimePrinter == null) {
            throw new IllegalArgumentException("No printer supplied");
        }
    }

    private DateTimeFormatterBuilder append0(Object object) {
        this.iFormatter = null;
        this.iElementPairs.add(object);
        this.iElementPairs.add(object);
        return this;
    }

    private DateTimeFormatterBuilder append0(InternalPrinter internalPrinter, InternalParser internalParser) {
        this.iFormatter = null;
        this.iElementPairs.add(internalPrinter);
        this.iElementPairs.add(internalParser);
        return this;
    }

    public DateTimeFormatterBuilder appendLiteral(char c) {
        return this.append0(new CharacterLiteral(c));
    }

    public DateTimeFormatterBuilder appendLiteral(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Literal must not be null");
        }
        switch (string.length()) {
            case 0: {
                return this;
            }
            case 1: {
                return this.append0(new CharacterLiteral(string.charAt(0)));
            }
        }
        return this.append0(new StringLiteral(string));
    }

    public DateTimeFormatterBuilder appendDecimal(DateTimeFieldType dateTimeFieldType, int n, int n2) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (n2 < n) {
            n2 = n;
        }
        if (n < 0 || n2 <= 0) {
            throw new IllegalArgumentException();
        }
        if (n <= 1) {
            return this.append0(new UnpaddedNumber(dateTimeFieldType, n2, false));
        }
        return this.append0(new PaddedNumber(dateTimeFieldType, n2, false, n));
    }

    public DateTimeFormatterBuilder appendFixedDecimal(DateTimeFieldType dateTimeFieldType, int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Illegal number of digits: " + n);
        }
        return this.append0(new FixedNumber(dateTimeFieldType, n, false));
    }

    public DateTimeFormatterBuilder appendSignedDecimal(DateTimeFieldType dateTimeFieldType, int n, int n2) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (n2 < n) {
            n2 = n;
        }
        if (n < 0 || n2 <= 0) {
            throw new IllegalArgumentException();
        }
        if (n <= 1) {
            return this.append0(new UnpaddedNumber(dateTimeFieldType, n2, true));
        }
        return this.append0(new PaddedNumber(dateTimeFieldType, n2, true, n));
    }

    public DateTimeFormatterBuilder appendFixedSignedDecimal(DateTimeFieldType dateTimeFieldType, int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Illegal number of digits: " + n);
        }
        return this.append0(new FixedNumber(dateTimeFieldType, n, true));
    }

    public DateTimeFormatterBuilder appendText(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        return this.append0(new TextField(dateTimeFieldType, false));
    }

    public DateTimeFormatterBuilder appendShortText(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        return this.append0(new TextField(dateTimeFieldType, true));
    }

    public DateTimeFormatterBuilder appendFraction(DateTimeFieldType dateTimeFieldType, int n, int n2) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (n2 < n) {
            n2 = n;
        }
        if (n < 0 || n2 <= 0) {
            throw new IllegalArgumentException();
        }
        return this.append0(new Fraction(dateTimeFieldType, n, n2));
    }

    public DateTimeFormatterBuilder appendFractionOfSecond(int n, int n2) {
        return this.appendFraction(DateTimeFieldType.secondOfDay(), n, n2);
    }

    public DateTimeFormatterBuilder appendFractionOfMinute(int n, int n2) {
        return this.appendFraction(DateTimeFieldType.minuteOfDay(), n, n2);
    }

    public DateTimeFormatterBuilder appendFractionOfHour(int n, int n2) {
        return this.appendFraction(DateTimeFieldType.hourOfDay(), n, n2);
    }

    public DateTimeFormatterBuilder appendFractionOfDay(int n, int n2) {
        return this.appendFraction(DateTimeFieldType.dayOfYear(), n, n2);
    }

    public DateTimeFormatterBuilder appendMillisOfSecond(int n) {
        return this.appendDecimal(DateTimeFieldType.millisOfSecond(), n, 3);
    }

    public DateTimeFormatterBuilder appendMillisOfDay(int n) {
        return this.appendDecimal(DateTimeFieldType.millisOfDay(), n, 8);
    }

    public DateTimeFormatterBuilder appendSecondOfMinute(int n) {
        return this.appendDecimal(DateTimeFieldType.secondOfMinute(), n, 2);
    }

    public DateTimeFormatterBuilder appendSecondOfDay(int n) {
        return this.appendDecimal(DateTimeFieldType.secondOfDay(), n, 5);
    }

    public DateTimeFormatterBuilder appendMinuteOfHour(int n) {
        return this.appendDecimal(DateTimeFieldType.minuteOfHour(), n, 2);
    }

    public DateTimeFormatterBuilder appendMinuteOfDay(int n) {
        return this.appendDecimal(DateTimeFieldType.minuteOfDay(), n, 4);
    }

    public DateTimeFormatterBuilder appendHourOfDay(int n) {
        return this.appendDecimal(DateTimeFieldType.hourOfDay(), n, 2);
    }

    public DateTimeFormatterBuilder appendClockhourOfDay(int n) {
        return this.appendDecimal(DateTimeFieldType.clockhourOfDay(), n, 2);
    }

    public DateTimeFormatterBuilder appendHourOfHalfday(int n) {
        return this.appendDecimal(DateTimeFieldType.hourOfHalfday(), n, 2);
    }

    public DateTimeFormatterBuilder appendClockhourOfHalfday(int n) {
        return this.appendDecimal(DateTimeFieldType.clockhourOfHalfday(), n, 2);
    }

    public DateTimeFormatterBuilder appendDayOfWeek(int n) {
        return this.appendDecimal(DateTimeFieldType.dayOfWeek(), n, 1);
    }

    public DateTimeFormatterBuilder appendDayOfMonth(int n) {
        return this.appendDecimal(DateTimeFieldType.dayOfMonth(), n, 2);
    }

    public DateTimeFormatterBuilder appendDayOfYear(int n) {
        return this.appendDecimal(DateTimeFieldType.dayOfYear(), n, 3);
    }

    public DateTimeFormatterBuilder appendWeekOfWeekyear(int n) {
        return this.appendDecimal(DateTimeFieldType.weekOfWeekyear(), n, 2);
    }

    public DateTimeFormatterBuilder appendWeekyear(int n, int n2) {
        return this.appendSignedDecimal(DateTimeFieldType.weekyear(), n, n2);
    }

    public DateTimeFormatterBuilder appendMonthOfYear(int n) {
        return this.appendDecimal(DateTimeFieldType.monthOfYear(), n, 2);
    }

    public DateTimeFormatterBuilder appendYear(int n, int n2) {
        return this.appendSignedDecimal(DateTimeFieldType.year(), n, n2);
    }

    public DateTimeFormatterBuilder appendTwoDigitYear(int n) {
        return this.appendTwoDigitYear(n, false);
    }

    public DateTimeFormatterBuilder appendTwoDigitYear(int n, boolean bl) {
        return this.append0(new TwoDigitYear(DateTimeFieldType.year(), n, bl));
    }

    public DateTimeFormatterBuilder appendTwoDigitWeekyear(int n) {
        return this.appendTwoDigitWeekyear(n, false);
    }

    public DateTimeFormatterBuilder appendTwoDigitWeekyear(int n, boolean bl) {
        return this.append0(new TwoDigitYear(DateTimeFieldType.weekyear(), n, bl));
    }

    public DateTimeFormatterBuilder appendYearOfEra(int n, int n2) {
        return this.appendDecimal(DateTimeFieldType.yearOfEra(), n, n2);
    }

    public DateTimeFormatterBuilder appendYearOfCentury(int n, int n2) {
        return this.appendDecimal(DateTimeFieldType.yearOfCentury(), n, n2);
    }

    public DateTimeFormatterBuilder appendCenturyOfEra(int n, int n2) {
        return this.appendSignedDecimal(DateTimeFieldType.centuryOfEra(), n, n2);
    }

    public DateTimeFormatterBuilder appendHalfdayOfDayText() {
        return this.appendText(DateTimeFieldType.halfdayOfDay());
    }

    public DateTimeFormatterBuilder appendDayOfWeekText() {
        return this.appendText(DateTimeFieldType.dayOfWeek());
    }

    public DateTimeFormatterBuilder appendDayOfWeekShortText() {
        return this.appendShortText(DateTimeFieldType.dayOfWeek());
    }

    public DateTimeFormatterBuilder appendMonthOfYearText() {
        return this.appendText(DateTimeFieldType.monthOfYear());
    }

    public DateTimeFormatterBuilder appendMonthOfYearShortText() {
        return this.appendShortText(DateTimeFieldType.monthOfYear());
    }

    public DateTimeFormatterBuilder appendEraText() {
        return this.appendText(DateTimeFieldType.era());
    }

    public DateTimeFormatterBuilder appendTimeZoneName() {
        return this.append0(new TimeZoneName(0, null), null);
    }

    public DateTimeFormatterBuilder appendTimeZoneName(Map<String, DateTimeZone> map) {
        TimeZoneName timeZoneName = new TimeZoneName(0, map);
        return this.append0(timeZoneName, timeZoneName);
    }

    public DateTimeFormatterBuilder appendTimeZoneShortName() {
        return this.append0(new TimeZoneName(1, null), null);
    }

    public DateTimeFormatterBuilder appendTimeZoneShortName(Map<String, DateTimeZone> map) {
        TimeZoneName timeZoneName = new TimeZoneName(1, map);
        return this.append0(timeZoneName, timeZoneName);
    }

    public DateTimeFormatterBuilder appendTimeZoneId() {
        return this.append0(TimeZoneId.INSTANCE, TimeZoneId.INSTANCE);
    }

    public DateTimeFormatterBuilder appendTimeZoneOffset(String string, boolean bl, int n, int n2) {
        return this.append0(new TimeZoneOffset(string, string, bl, n, n2));
    }

    public DateTimeFormatterBuilder appendTimeZoneOffset(String string, String string2, boolean bl, int n, int n2) {
        return this.append0(new TimeZoneOffset(string, string2, bl, n, n2));
    }

    public DateTimeFormatterBuilder appendPattern(String string) {
        DateTimeFormat.appendPatternTo(this, string);
        return this;
    }

    private Object getFormatter() {
        Object object = this.iFormatter;
        if (object == null) {
            if (this.iElementPairs.size() == 2) {
                Object object2 = this.iElementPairs.get(0);
                Object object3 = this.iElementPairs.get(1);
                if (object2 != null) {
                    if (object2 == object3 || object3 == null) {
                        object = object2;
                    }
                } else {
                    object = object3;
                }
            }
            if (object == null) {
                object = new Composite(this.iElementPairs);
            }
            this.iFormatter = object;
        }
        return object;
    }

    private boolean isPrinter(Object object) {
        if (object instanceof InternalPrinter) {
            if (object instanceof Composite) {
                return ((Composite)object).isPrinter();
            }
            return true;
        }
        return false;
    }

    private boolean isParser(Object object) {
        if (object instanceof InternalParser) {
            if (object instanceof Composite) {
                return ((Composite)object).isParser();
            }
            return true;
        }
        return false;
    }

    private boolean isFormatter(Object object) {
        return this.isPrinter(object) || this.isParser(object);
    }

    static void appendUnknownString(Appendable appendable, int n) throws IOException {
        int n2 = n;
        while (--n2 >= 0) {
            appendable.append('\ufffd');
        }
    }

    static boolean csStartsWith(CharSequence charSequence, int n, String string) {
        int n2 = string.length();
        if (charSequence.length() - n < n2) {
            return false;
        }
        for (int i = 0; i < n2; ++i) {
            if (charSequence.charAt(n + i) == string.charAt(i)) continue;
            return false;
        }
        return true;
    }

    static boolean csStartsWithIgnoreCase(CharSequence charSequence, int n, String string) {
        int n2 = string.length();
        if (charSequence.length() - n < n2) {
            return false;
        }
        for (int i = 0; i < n2; ++i) {
            char c;
            char c2;
            char c3;
            char c4 = charSequence.charAt(n + i);
            if (c4 == (c3 = string.charAt(i)) || (c2 = Character.toUpperCase(c4)) == (c = Character.toUpperCase(c3)) || Character.toLowerCase(c2) == Character.toLowerCase(c)) continue;
            return false;
        }
        return true;
    }

    static class MatchingParser
    implements InternalParser {
        private final InternalParser[] iParsers;
        private final int iParsedLengthEstimate;

        MatchingParser(InternalParser[] internalParserArray) {
            this.iParsers = internalParserArray;
            int n = 0;
            int n2 = internalParserArray.length;
            while (--n2 >= 0) {
                int n3;
                InternalParser internalParser = internalParserArray[n2];
                if (internalParser == null || (n3 = internalParser.estimateParsedLength()) <= n) continue;
                n = n3;
            }
            this.iParsedLengthEstimate = n;
        }

        public int estimateParsedLength() {
            return this.iParsedLengthEstimate;
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            InternalParser[] internalParserArray = this.iParsers;
            int n2 = internalParserArray.length;
            Object object = dateTimeParserBucket.saveState();
            boolean bl = false;
            int n3 = n;
            Object object2 = null;
            int n4 = n;
            for (int i = 0; i < n2; ++i) {
                InternalParser internalParser = internalParserArray[i];
                if (internalParser == null) {
                    if (n3 <= n) {
                        return n;
                    }
                    bl = true;
                    break;
                }
                int n5 = internalParser.parseInto(dateTimeParserBucket, charSequence, n);
                if (n5 >= n) {
                    if (n5 > n3) {
                        if (n5 >= charSequence.length() || i + 1 >= n2 || internalParserArray[i + 1] == null) {
                            return n5;
                        }
                        n3 = n5;
                        object2 = dateTimeParserBucket.saveState();
                    }
                } else if (n5 < 0 && (n5 ^= 0xFFFFFFFF) > n4) {
                    n4 = n5;
                }
                dateTimeParserBucket.restoreState(object);
            }
            if (n3 > n || n3 == n && bl) {
                if (object2 != null) {
                    dateTimeParserBucket.restoreState(object2);
                }
                return n3;
            }
            return ~n4;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class Composite
    implements InternalPrinter,
    InternalParser {
        private final InternalPrinter[] iPrinters;
        private final InternalParser[] iParsers;
        private final int iPrintedLengthEstimate;
        private final int iParsedLengthEstimate;

        Composite(List<Object> list) {
            Object object;
            int n;
            int n2;
            int n3;
            ArrayList<Object> arrayList = new ArrayList<Object>();
            ArrayList<Object> arrayList2 = new ArrayList<Object>();
            this.decompose(list, arrayList, arrayList2);
            if (arrayList.contains(null) || arrayList.isEmpty()) {
                this.iPrinters = null;
                this.iPrintedLengthEstimate = 0;
            } else {
                n3 = arrayList.size();
                this.iPrinters = new InternalPrinter[n3];
                n2 = 0;
                for (n = 0; n < n3; ++n) {
                    object = (InternalPrinter)arrayList.get(n);
                    n2 += object.estimatePrintedLength();
                    this.iPrinters[n] = object;
                }
                this.iPrintedLengthEstimate = n2;
            }
            if (arrayList2.contains(null) || arrayList2.isEmpty()) {
                this.iParsers = null;
                this.iParsedLengthEstimate = 0;
            } else {
                n3 = arrayList2.size();
                this.iParsers = new InternalParser[n3];
                n2 = 0;
                for (n = 0; n < n3; ++n) {
                    object = (InternalParser)arrayList2.get(n);
                    n2 += object.estimateParsedLength();
                    this.iParsers[n] = object;
                }
                this.iParsedLengthEstimate = n2;
            }
        }

        @Override
        public int estimatePrintedLength() {
            return this.iPrintedLengthEstimate;
        }

        @Override
        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            InternalPrinter[] internalPrinterArray = this.iPrinters;
            if (internalPrinterArray == null) {
                throw new UnsupportedOperationException();
            }
            if (locale == null) {
                locale = Locale.getDefault();
            }
            int n2 = internalPrinterArray.length;
            for (int i = 0; i < n2; ++i) {
                internalPrinterArray[i].printTo(appendable, l, chronology, n, dateTimeZone, locale);
            }
        }

        @Override
        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
            InternalPrinter[] internalPrinterArray = this.iPrinters;
            if (internalPrinterArray == null) {
                throw new UnsupportedOperationException();
            }
            if (locale == null) {
                locale = Locale.getDefault();
            }
            int n = internalPrinterArray.length;
            for (int i = 0; i < n; ++i) {
                internalPrinterArray[i].printTo(appendable, readablePartial, locale);
            }
        }

        @Override
        public int estimateParsedLength() {
            return this.iParsedLengthEstimate;
        }

        @Override
        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            InternalParser[] internalParserArray = this.iParsers;
            if (internalParserArray == null) {
                throw new UnsupportedOperationException();
            }
            int n2 = internalParserArray.length;
            for (int i = 0; i < n2 && n >= 0; ++i) {
                n = internalParserArray[i].parseInto(dateTimeParserBucket, charSequence, n);
            }
            return n;
        }

        boolean isPrinter() {
            return this.iPrinters != null;
        }

        boolean isParser() {
            return this.iParsers != null;
        }

        private void decompose(List<Object> list, List<Object> list2, List<Object> list3) {
            int n = list.size();
            for (int i = 0; i < n; i += 2) {
                Object object = list.get(i);
                if (object instanceof Composite) {
                    this.addArrayToList(list2, ((Composite)object).iPrinters);
                } else {
                    list2.add(object);
                }
                object = list.get(i + 1);
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum TimeZoneId implements InternalPrinter,
    InternalParser
    {
        INSTANCE;

        private static final List<String> ALL_IDS;
        private static final Map<String, List<String>> GROUPED_IDS;
        private static final List<String> BASE_GROUPED_IDS;
        static final int MAX_LENGTH;
        static final int MAX_PREFIX_LENGTH;

        @Override
        public int estimatePrintedLength() {
            return MAX_LENGTH;
        }

        @Override
        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            appendable.append(dateTimeZone != null ? dateTimeZone.getID() : "");
        }

        @Override
        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
        }

        @Override
        public int estimateParsedLength() {
            return MAX_LENGTH;
        }

        @Override
        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            List<String> list = BASE_GROUPED_IDS;
            int n2 = charSequence.length();
            int n3 = Math.min(n2, n + MAX_PREFIX_LENGTH);
            int n4 = n;
            String string = "";
            for (int i = n4; i < n3; ++i) {
                if (charSequence.charAt(i) != '/') continue;
                string = charSequence.subSequence(n4, i + 1).toString();
                n4 += string.length();
                String string2 = string;
                if (i < n2) {
                    string2 = string2 + charSequence.charAt(i + 1);
                }
                if ((list = GROUPED_IDS.get(string2)) != null) break;
                return ~n;
            }
            String string3 = null;
            for (int i = 0; i < list.size(); ++i) {
                String string4 = list.get(i);
                if (!DateTimeFormatterBuilder.csStartsWith(charSequence, n4, string4) || string3 != null && string4.length() <= string3.length()) continue;
                string3 = string4;
            }
            if (string3 != null) {
                dateTimeParserBucket.setZone(DateTimeZone.forID(string + string3));
                return n4 + string3.length();
            }
            return ~n;
        }

        static {
            BASE_GROUPED_IDS = new ArrayList<String>();
            ALL_IDS = new ArrayList<String>(DateTimeZone.getAvailableIDs());
            Collections.sort(ALL_IDS);
            GROUPED_IDS = new HashMap<String, List<String>>();
            int n = 0;
            int n2 = 0;
            for (String string : ALL_IDS) {
                int n3 = string.indexOf(47);
                if (n3 >= 0) {
                    if (n3 < string.length()) {
                        ++n3;
                    }
                    n2 = Math.max(n2, n3);
                    String string2 = string.substring(0, n3 + 1);
                    String string3 = string.substring(n3);
                    if (!GROUPED_IDS.containsKey(string2)) {
                        GROUPED_IDS.put(string2, new ArrayList());
                    }
                    GROUPED_IDS.get(string2).add(string3);
                } else {
                    BASE_GROUPED_IDS.add(string);
                }
                n = Math.max(n, string.length());
            }
            MAX_LENGTH = n;
            MAX_PREFIX_LENGTH = n2;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class TimeZoneName
    implements InternalPrinter,
    InternalParser {
        static final int LONG_NAME = 0;
        static final int SHORT_NAME = 1;
        private final Map<String, DateTimeZone> iParseLookup;
        private final int iType;

        TimeZoneName(int n, Map<String, DateTimeZone> map) {
            this.iType = n;
            this.iParseLookup = map;
        }

        @Override
        public int estimatePrintedLength() {
            return this.iType == 1 ? 4 : 20;
        }

        @Override
        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            appendable.append(this.print(l - (long)n, dateTimeZone, locale));
        }

        private String print(long l, DateTimeZone dateTimeZone, Locale locale) {
            if (dateTimeZone == null) {
                return "";
            }
            switch (this.iType) {
                case 0: {
                    return dateTimeZone.getName(l, locale);
                }
                case 1: {
                    return dateTimeZone.getShortName(l, locale);
                }
            }
            return "";
        }

        @Override
        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
        }

        @Override
        public int estimateParsedLength() {
            return this.iType == 1 ? 4 : 20;
        }

        @Override
        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            Map<String, DateTimeZone> map = this.iParseLookup;
            map = map != null ? map : DateTimeUtils.getDefaultTimeZoneNames();
            String string = null;
            for (String string2 : map.keySet()) {
                if (!DateTimeFormatterBuilder.csStartsWith(charSequence, n, string2) || string != null && string2.length() <= string.length()) continue;
                string = string2;
            }
            if (string != null) {
                dateTimeParserBucket.setZone(map.get(string));
                return n + string.length();
            }
            return ~n;
        }
    }

    static class TimeZoneOffset
    implements InternalPrinter,
    InternalParser {
        private final String iZeroOffsetPrintText;
        private final String iZeroOffsetParseText;
        private final boolean iShowSeparators;
        private final int iMinFields;
        private final int iMaxFields;

        TimeZoneOffset(String string, String string2, boolean bl, int n, int n2) {
            this.iZeroOffsetPrintText = string;
            this.iZeroOffsetParseText = string2;
            this.iShowSeparators = bl;
            if (n <= 0 || n2 < n) {
                throw new IllegalArgumentException();
            }
            if (n > 4) {
                n = 4;
                n2 = 4;
            }
            this.iMinFields = n;
            this.iMaxFields = n2;
        }

        public int estimatePrintedLength() {
            int n = 1 + this.iMinFields << 1;
            if (this.iShowSeparators) {
                n += this.iMinFields - 1;
            }
            if (this.iZeroOffsetPrintText != null && this.iZeroOffsetPrintText.length() > n) {
                n = this.iZeroOffsetPrintText.length();
            }
            return n;
        }

        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            if (dateTimeZone == null) {
                return;
            }
            if (n == 0 && this.iZeroOffsetPrintText != null) {
                appendable.append(this.iZeroOffsetPrintText);
                return;
            }
            if (n >= 0) {
                appendable.append('+');
            } else {
                appendable.append('-');
                n = -n;
            }
            int n2 = n / 3600000;
            FormatUtils.appendPaddedInteger(appendable, n2, 2);
            if (this.iMaxFields == 1) {
                return;
            }
            if ((n -= n2 * 3600000) == 0 && this.iMinFields <= 1) {
                return;
            }
            int n3 = n / 60000;
            if (this.iShowSeparators) {
                appendable.append(':');
            }
            FormatUtils.appendPaddedInteger(appendable, n3, 2);
            if (this.iMaxFields == 2) {
                return;
            }
            if ((n -= n3 * 60000) == 0 && this.iMinFields <= 2) {
                return;
            }
            int n4 = n / 1000;
            if (this.iShowSeparators) {
                appendable.append(':');
            }
            FormatUtils.appendPaddedInteger(appendable, n4, 2);
            if (this.iMaxFields == 3) {
                return;
            }
            if ((n -= n4 * 1000) == 0 && this.iMinFields <= 3) {
                return;
            }
            if (this.iShowSeparators) {
                appendable.append('.');
            }
            FormatUtils.appendPaddedInteger(appendable, n, 3);
        }

        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
        }

        public int estimateParsedLength() {
            return this.estimatePrintedLength();
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            int n2;
            char c;
            block24: {
                int n3;
                boolean bl;
                block28: {
                    int n4;
                    block27: {
                        block26: {
                            char c2;
                            block25: {
                                n4 = charSequence.length() - n;
                                if (this.iZeroOffsetParseText != null) {
                                    if (this.iZeroOffsetParseText.length() == 0) {
                                        if (n4 <= 0 || (c = charSequence.charAt(n)) != '-' && c != '+') {
                                            dateTimeParserBucket.setOffset((Integer)0);
                                            return n;
                                        }
                                    } else if (DateTimeFormatterBuilder.csStartsWithIgnoreCase(charSequence, n, this.iZeroOffsetParseText)) {
                                        dateTimeParserBucket.setOffset((Integer)0);
                                        return n + this.iZeroOffsetParseText.length();
                                    }
                                }
                                if (n4 <= 1) {
                                    return ~n;
                                }
                                c2 = charSequence.charAt(n);
                                if (c2 == '-') {
                                    c = '\u0001';
                                } else if (c2 == '+') {
                                    c = '\u0000';
                                } else {
                                    return ~n;
                                }
                                --n4;
                                if (this.digitCount(charSequence, ++n, 2) < 2) {
                                    return ~n;
                                }
                                int n5 = FormatUtils.parseTwoDigits(charSequence, n);
                                if (n5 > 23) {
                                    return ~n;
                                }
                                n2 = n5 * 3600000;
                                n += 2;
                                if ((n4 -= 2) <= 0) break block24;
                                c2 = charSequence.charAt(n);
                                if (c2 != ':') break block25;
                                bl = true;
                                --n4;
                                ++n;
                                break block26;
                            }
                            if (c2 < '0' || c2 > '9') break block24;
                            bl = false;
                        }
                        n3 = this.digitCount(charSequence, n, 2);
                        if (n3 == 0 && !bl) break block24;
                        if (n3 < 2) {
                            return ~n;
                        }
                        int n6 = FormatUtils.parseTwoDigits(charSequence, n);
                        if (n6 > 59) {
                            return ~n;
                        }
                        n2 += n6 * 60000;
                        n += 2;
                        if ((n4 -= 2) <= 0) break block24;
                        if (!bl) break block27;
                        if (charSequence.charAt(n) != ':') break block24;
                        --n4;
                        ++n;
                    }
                    if ((n3 = this.digitCount(charSequence, n, 2)) == 0 && !bl) break block24;
                    if (n3 < 2) {
                        return ~n;
                    }
                    int n7 = FormatUtils.parseTwoDigits(charSequence, n);
                    if (n7 > 59) {
                        return ~n;
                    }
                    n2 += n7 * 1000;
                    n += 2;
                    if ((n4 -= 2) <= 0) break block24;
                    if (!bl) break block28;
                    if (charSequence.charAt(n) != '.' && charSequence.charAt(n) != ',') break block24;
                    --n4;
                    ++n;
                }
                if ((n3 = this.digitCount(charSequence, n, 3)) != 0 || bl) {
                    if (n3 < 1) {
                        return ~n;
                    }
                    n2 += (charSequence.charAt(n++) - 48) * 100;
                    if (n3 > 1) {
                        n2 += (charSequence.charAt(n++) - 48) * 10;
                        if (n3 > 2) {
                            n2 += charSequence.charAt(n++) - 48;
                        }
                    }
                }
            }
            dateTimeParserBucket.setOffset((Integer)(c != '\u0000' ? -n2 : n2));
            return n;
        }

        private int digitCount(CharSequence charSequence, int n, int n2) {
            char c;
            int n3 = Math.min(charSequence.length() - n, n2);
            n2 = 0;
            while (n3 > 0 && (c = charSequence.charAt(n + n2)) >= '0' && c <= '9') {
                ++n2;
                --n3;
            }
            return n2;
        }
    }

    static class Fraction
    implements InternalPrinter,
    InternalParser {
        private final DateTimeFieldType iFieldType;
        protected int iMinDigits;
        protected int iMaxDigits;

        protected Fraction(DateTimeFieldType dateTimeFieldType, int n, int n2) {
            this.iFieldType = dateTimeFieldType;
            if (n2 > 18) {
                n2 = 18;
            }
            this.iMinDigits = n;
            this.iMaxDigits = n2;
        }

        public int estimatePrintedLength() {
            return this.iMaxDigits;
        }

        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            this.printTo(appendable, l, chronology);
        }

        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
            long l = readablePartial.getChronology().set(readablePartial, 0L);
            this.printTo(appendable, l, readablePartial.getChronology());
        }

        protected void printTo(Appendable appendable, long l, Chronology chronology) throws IOException {
            int n;
            long l2;
            DateTimeField dateTimeField = this.iFieldType.getField(chronology);
            int n2 = this.iMinDigits;
            try {
                l2 = dateTimeField.remainder(l);
            }
            catch (RuntimeException runtimeException) {
                DateTimeFormatterBuilder.appendUnknownString(appendable, n2);
                return;
            }
            if (l2 == 0L) {
                while (--n2 >= 0) {
                    appendable.append('0');
                }
                return;
            }
            long[] lArray = this.getFractionData(l2, dateTimeField);
            long l3 = lArray[0];
            int n3 = (int)lArray[1];
            String string = (l3 & Integer.MAX_VALUE) == l3 ? Integer.toString((int)l3) : Long.toString(l3);
            int n4 = string.length();
            for (n = n3; n4 < n; --n) {
                appendable.append('0');
                --n2;
            }
            if (n2 < n) {
                while (n2 < n && n4 > 1 && string.charAt(n4 - 1) == '0') {
                    --n;
                    --n4;
                }
                if (n4 < string.length()) {
                    for (int i = 0; i < n4; ++i) {
                        appendable.append(string.charAt(i));
                    }
                    return;
                }
            }
            appendable.append(string);
        }

        private long[] getFractionData(long l, DateTimeField dateTimeField) {
            long l2;
            long l3 = dateTimeField.getDurationField().getUnitMillis();
            int n = this.iMaxDigits;
            while (true) {
                switch (n) {
                    default: {
                        l2 = 1L;
                        break;
                    }
                    case 1: {
                        l2 = 10L;
                        break;
                    }
                    case 2: {
                        l2 = 100L;
                        break;
                    }
                    case 3: {
                        l2 = 1000L;
                        break;
                    }
                    case 4: {
                        l2 = 10000L;
                        break;
                    }
                    case 5: {
                        l2 = 100000L;
                        break;
                    }
                    case 6: {
                        l2 = 1000000L;
                        break;
                    }
                    case 7: {
                        l2 = 10000000L;
                        break;
                    }
                    case 8: {
                        l2 = 100000000L;
                        break;
                    }
                    case 9: {
                        l2 = 1000000000L;
                        break;
                    }
                    case 10: {
                        l2 = 10000000000L;
                        break;
                    }
                    case 11: {
                        l2 = 100000000000L;
                        break;
                    }
                    case 12: {
                        l2 = 1000000000000L;
                        break;
                    }
                    case 13: {
                        l2 = 10000000000000L;
                        break;
                    }
                    case 14: {
                        l2 = 100000000000000L;
                        break;
                    }
                    case 15: {
                        l2 = 1000000000000000L;
                        break;
                    }
                    case 16: {
                        l2 = 10000000000000000L;
                        break;
                    }
                    case 17: {
                        l2 = 100000000000000000L;
                        break;
                    }
                    case 18: {
                        l2 = 1000000000000000000L;
                    }
                }
                if (l3 * l2 / l2 == l3) break;
                --n;
            }
            return new long[]{l * l2 / l3, n};
        }

        public int estimateParsedLength() {
            return this.iMaxDigits;
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            char c;
            DateTimeField dateTimeField = this.iFieldType.getField(dateTimeParserBucket.getChronology());
            int n2 = Math.min(this.iMaxDigits, charSequence.length() - n);
            long l = 0L;
            long l2 = dateTimeField.getDurationField().getUnitMillis() * 10L;
            int n3 = 0;
            while (n3 < n2 && (c = charSequence.charAt(n + n3)) >= '0' && c <= '9') {
                ++n3;
                long l3 = l2 / 10L;
                l += (long)(c - 48) * l3;
                l2 = l3;
            }
            l /= 10L;
            if (n3 == 0) {
                return ~n;
            }
            if (l > Integer.MAX_VALUE) {
                return ~n;
            }
            PreciseDateTimeField preciseDateTimeField = new PreciseDateTimeField(DateTimeFieldType.millisOfSecond(), MillisDurationField.INSTANCE, dateTimeField.getDurationField());
            dateTimeParserBucket.saveField(preciseDateTimeField, (int)l);
            return n + n3;
        }
    }

    static class TextField
    implements InternalPrinter,
    InternalParser {
        private static Map<Locale, Map<DateTimeFieldType, Object[]>> cParseCache = new ConcurrentHashMap<Locale, Map<DateTimeFieldType, Object[]>>();
        private final DateTimeFieldType iFieldType;
        private final boolean iShort;

        TextField(DateTimeFieldType dateTimeFieldType, boolean bl) {
            this.iFieldType = dateTimeFieldType;
            this.iShort = bl;
        }

        public int estimatePrintedLength() {
            return this.iShort ? 6 : 20;
        }

        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            try {
                appendable.append(this.print(l, chronology, locale));
            }
            catch (RuntimeException runtimeException) {
                appendable.append('\ufffd');
            }
        }

        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
            try {
                appendable.append(this.print(readablePartial, locale));
            }
            catch (RuntimeException runtimeException) {
                appendable.append('\ufffd');
            }
        }

        private String print(long l, Chronology chronology, Locale locale) {
            DateTimeField dateTimeField = this.iFieldType.getField(chronology);
            if (this.iShort) {
                return dateTimeField.getAsShortText(l, locale);
            }
            return dateTimeField.getAsText(l, locale);
        }

        private String print(ReadablePartial readablePartial, Locale locale) {
            if (readablePartial.isSupported(this.iFieldType)) {
                DateTimeField dateTimeField = this.iFieldType.getField(readablePartial.getChronology());
                if (this.iShort) {
                    return dateTimeField.getAsShortText(readablePartial, locale);
                }
                return dateTimeField.getAsText(readablePartial, locale);
            }
            return "\ufffd";
        }

        public int estimateParsedLength() {
            return this.estimatePrintedLength();
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            int n2;
            Object[] objectArray;
            Locale locale = dateTimeParserBucket.getLocale();
            Map<String, Boolean> map = null;
            int n3 = 0;
            Map<DateTimeFieldType, Object[]> map2 = cParseCache.get(locale);
            if (map2 == null) {
                map2 = new ConcurrentHashMap<DateTimeFieldType, Object[]>();
                cParseCache.put(locale, map2);
            }
            if ((objectArray = map2.get(this.iFieldType)) == null) {
                map = new ConcurrentHashMap(32);
                MutableDateTime mutableDateTime = new MutableDateTime(0L, DateTimeZone.UTC);
                MutableDateTime.Property property = mutableDateTime.property(this.iFieldType);
                int n4 = property.getMinimumValueOverall();
                int n5 = property.getMaximumValueOverall();
                if (n5 - n4 > 32) {
                    return ~n;
                }
                n3 = property.getMaximumTextLength(locale);
                for (int i = n4; i <= n5; ++i) {
                    property.set(i);
                    map.put(property.getAsShortText(locale), Boolean.TRUE);
                    map.put(property.getAsShortText(locale).toLowerCase(locale), Boolean.TRUE);
                    map.put(property.getAsShortText(locale).toUpperCase(locale), Boolean.TRUE);
                    map.put(property.getAsText(locale), Boolean.TRUE);
                    map.put(property.getAsText(locale).toLowerCase(locale), Boolean.TRUE);
                    map.put(property.getAsText(locale).toUpperCase(locale), Boolean.TRUE);
                }
                if ("en".equals(locale.getLanguage()) && this.iFieldType == DateTimeFieldType.era()) {
                    map.put("BCE", Boolean.TRUE);
                    map.put("bce", Boolean.TRUE);
                    map.put("CE", Boolean.TRUE);
                    map.put("ce", Boolean.TRUE);
                    n3 = 3;
                }
                objectArray = new Object[]{map, n3};
                map2.put(this.iFieldType, objectArray);
            } else {
                map = (Map)objectArray[0];
                n3 = (Integer)objectArray[1];
            }
            for (int i = n2 = Math.min(charSequence.length(), n + n3); i > n; --i) {
                String string = charSequence.subSequence(n, i).toString();
                if (!map.containsKey(string)) continue;
                dateTimeParserBucket.saveField(this.iFieldType, string, locale);
                return i;
            }
            return ~n;
        }
    }

    static class TwoDigitYear
    implements InternalPrinter,
    InternalParser {
        private final DateTimeFieldType iType;
        private final int iPivot;
        private final boolean iLenientParse;

        TwoDigitYear(DateTimeFieldType dateTimeFieldType, int n, boolean bl) {
            this.iType = dateTimeFieldType;
            this.iPivot = n;
            this.iLenientParse = bl;
        }

        public int estimateParsedLength() {
            return this.iLenientParse ? 4 : 2;
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            int n2;
            int n3;
            char c;
            int n4;
            int n5 = charSequence.length() - n;
            if (!this.iLenientParse) {
                if ((n5 = Math.min(2, n5)) < 2) {
                    return ~n;
                }
            } else {
                n4 = 0;
                c = '\u0000';
                n3 = 0;
                while (n3 < n5) {
                    n2 = charSequence.charAt(n + n3);
                    if (n3 == 0 && (n2 == 45 || n2 == 43)) {
                        n4 = 1;
                        char c2 = c = n2 == 45 ? (char)'\u0001' : '\u0000';
                        if (c != '\u0000') {
                            ++n3;
                            continue;
                        }
                        ++n;
                        --n5;
                        continue;
                    }
                    if (n2 < 48 || n2 > 57) break;
                    ++n3;
                }
                if (n3 == 0) {
                    return ~n;
                }
                if (n4 != 0 || n3 != 2) {
                    if (n3 >= 9) {
                        n2 = Integer.parseInt(charSequence.subSequence(n, n += n3).toString());
                    } else {
                        int n6 = n;
                        if (c != '\u0000') {
                            ++n6;
                        }
                        try {
                            n2 = charSequence.charAt(n6++) - 48;
                        }
                        catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
                            return ~n;
                        }
                        n += n3;
                        while (n6 < n) {
                            n2 = (n2 << 3) + (n2 << 1) + charSequence.charAt(n6++) - 48;
                        }
                        if (c != '\u0000') {
                            n2 = -n2;
                        }
                    }
                    dateTimeParserBucket.saveField(this.iType, n2);
                    return n;
                }
            }
            if ((c = charSequence.charAt(n)) < '0' || c > '9') {
                return ~n;
            }
            n4 = c - 48;
            c = charSequence.charAt(n + 1);
            if (c < '0' || c > '9') {
                return ~n;
            }
            n4 = (n4 << 3) + (n4 << 1) + c - 48;
            n3 = this.iPivot;
            if (dateTimeParserBucket.getPivotYear() != null) {
                n3 = dateTimeParserBucket.getPivotYear();
            }
            int n7 = (n2 = n3 - 50) >= 0 ? n2 % 100 : 99 + (n2 + 1) % 100;
            dateTimeParserBucket.saveField(this.iType, n4 += n2 + (n4 < n7 ? 100 : 0) - n7);
            return n + 2;
        }

        public int estimatePrintedLength() {
            return 2;
        }

        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            int n2 = this.getTwoDigitYear(l, chronology);
            if (n2 < 0) {
                appendable.append('\ufffd');
                appendable.append('\ufffd');
            } else {
                FormatUtils.appendPaddedInteger(appendable, n2, 2);
            }
        }

        private int getTwoDigitYear(long l, Chronology chronology) {
            try {
                int n = this.iType.getField(chronology).get(l);
                if (n < 0) {
                    n = -n;
                }
                return n % 100;
            }
            catch (RuntimeException runtimeException) {
                return -1;
            }
        }

        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
            int n = this.getTwoDigitYear(readablePartial);
            if (n < 0) {
                appendable.append('\ufffd');
                appendable.append('\ufffd');
            } else {
                FormatUtils.appendPaddedInteger(appendable, n, 2);
            }
        }

        private int getTwoDigitYear(ReadablePartial readablePartial) {
            if (readablePartial.isSupported(this.iType)) {
                try {
                    int n = readablePartial.get(this.iType);
                    if (n < 0) {
                        n = -n;
                    }
                    return n % 100;
                }
                catch (RuntimeException runtimeException) {
                    // empty catch block
                }
            }
            return -1;
        }
    }

    static class FixedNumber
    extends PaddedNumber {
        protected FixedNumber(DateTimeFieldType dateTimeFieldType, int n, boolean bl) {
            super(dateTimeFieldType, n, bl, n);
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            int n2 = super.parseInto(dateTimeParserBucket, charSequence, n);
            if (n2 < 0) {
                return n2;
            }
            int n3 = n + this.iMaxParsedDigits;
            if (n2 != n3) {
                char c;
                if (this.iSigned && ((c = charSequence.charAt(n)) == '-' || c == '+')) {
                    ++n3;
                }
                if (n2 > n3) {
                    return ~(n3 + 1);
                }
                if (n2 < n3) {
                    return ~n2;
                }
            }
            return n2;
        }
    }

    static class PaddedNumber
    extends NumberFormatter {
        protected final int iMinPrintedDigits;

        protected PaddedNumber(DateTimeFieldType dateTimeFieldType, int n, boolean bl, int n2) {
            super(dateTimeFieldType, n, bl);
            this.iMinPrintedDigits = n2;
        }

        public int estimatePrintedLength() {
            return this.iMaxParsedDigits;
        }

        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            try {
                DateTimeField dateTimeField = this.iFieldType.getField(chronology);
                FormatUtils.appendPaddedInteger(appendable, dateTimeField.get(l), this.iMinPrintedDigits);
            }
            catch (RuntimeException runtimeException) {
                DateTimeFormatterBuilder.appendUnknownString(appendable, this.iMinPrintedDigits);
            }
        }

        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
            if (readablePartial.isSupported(this.iFieldType)) {
                try {
                    FormatUtils.appendPaddedInteger(appendable, readablePartial.get(this.iFieldType), this.iMinPrintedDigits);
                }
                catch (RuntimeException runtimeException) {
                    DateTimeFormatterBuilder.appendUnknownString(appendable, this.iMinPrintedDigits);
                }
            } else {
                DateTimeFormatterBuilder.appendUnknownString(appendable, this.iMinPrintedDigits);
            }
        }
    }

    static class UnpaddedNumber
    extends NumberFormatter {
        protected UnpaddedNumber(DateTimeFieldType dateTimeFieldType, int n, boolean bl) {
            super(dateTimeFieldType, n, bl);
        }

        public int estimatePrintedLength() {
            return this.iMaxParsedDigits;
        }

        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            try {
                DateTimeField dateTimeField = this.iFieldType.getField(chronology);
                FormatUtils.appendUnpaddedInteger(appendable, dateTimeField.get(l));
            }
            catch (RuntimeException runtimeException) {
                appendable.append('\ufffd');
            }
        }

        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
            if (readablePartial.isSupported(this.iFieldType)) {
                try {
                    FormatUtils.appendUnpaddedInteger(appendable, readablePartial.get(this.iFieldType));
                }
                catch (RuntimeException runtimeException) {
                    appendable.append('\ufffd');
                }
            } else {
                appendable.append('\ufffd');
            }
        }
    }

    static abstract class NumberFormatter
    implements InternalPrinter,
    InternalParser {
        protected final DateTimeFieldType iFieldType;
        protected final int iMaxParsedDigits;
        protected final boolean iSigned;

        NumberFormatter(DateTimeFieldType dateTimeFieldType, int n, boolean bl) {
            this.iFieldType = dateTimeFieldType;
            this.iMaxParsedDigits = n;
            this.iSigned = bl;
        }

        public int estimateParsedLength() {
            return this.iMaxParsedDigits;
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            int n2;
            int n3 = Math.min(this.iMaxParsedDigits, charSequence.length() - n);
            boolean bl = false;
            boolean bl2 = false;
            int n4 = 0;
            while (n4 < n3) {
                n2 = charSequence.charAt(n + n4);
                if (n4 == 0 && (n2 == 45 || n2 == 43) && this.iSigned) {
                    bl = n2 == 45;
                    boolean bl3 = bl2 = n2 == 43;
                    if (n4 + 1 >= n3) break;
                    char c = charSequence.charAt(n + n4 + 1);
                    n2 = c;
                    if (c < '0' || n2 > 57) break;
                    ++n4;
                    n3 = Math.min(n3 + 1, charSequence.length() - n);
                    continue;
                }
                if (n2 < 48 || n2 > 57) break;
                ++n4;
            }
            if (n4 == 0) {
                return ~n;
            }
            if (n4 >= 9) {
                n2 = bl2 ? Integer.parseInt(charSequence.subSequence(n + 1, n += n4).toString()) : Integer.parseInt(charSequence.subSequence(n, n += n4).toString());
            } else {
                int n5 = n;
                if (bl || bl2) {
                    ++n5;
                }
                try {
                    n2 = charSequence.charAt(n5++) - 48;
                }
                catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
                    return ~n;
                }
                n += n4;
                while (n5 < n) {
                    n2 = (n2 << 3) + (n2 << 1) + charSequence.charAt(n5++) - 48;
                }
                if (bl) {
                    n2 = -n2;
                }
            }
            dateTimeParserBucket.saveField(this.iFieldType, n2);
            return n;
        }
    }

    static class StringLiteral
    implements InternalPrinter,
    InternalParser {
        private final String iValue;

        StringLiteral(String string) {
            this.iValue = string;
        }

        public int estimatePrintedLength() {
            return this.iValue.length();
        }

        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            appendable.append(this.iValue);
        }

        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
            appendable.append(this.iValue);
        }

        public int estimateParsedLength() {
            return this.iValue.length();
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            if (DateTimeFormatterBuilder.csStartsWithIgnoreCase(charSequence, n, this.iValue)) {
                return n + this.iValue.length();
            }
            return ~n;
        }
    }

    static class CharacterLiteral
    implements InternalPrinter,
    InternalParser {
        private final char iValue;

        CharacterLiteral(char c) {
            this.iValue = c;
        }

        public int estimatePrintedLength() {
            return 1;
        }

        public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
            appendable.append(this.iValue);
        }

        public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
            appendable.append(this.iValue);
        }

        public int estimateParsedLength() {
            return 1;
        }

        public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
            char c;
            if (n >= charSequence.length()) {
                return ~n;
            }
            char c2 = charSequence.charAt(n);
            if (c2 != (c = this.iValue) && (c2 = Character.toUpperCase(c2)) != (c = Character.toUpperCase(c)) && (c2 = Character.toLowerCase(c2)) != (c = Character.toLowerCase(c))) {
                return ~n;
            }
            return n + 1;
        }
    }
}

