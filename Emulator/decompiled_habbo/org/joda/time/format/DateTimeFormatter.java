/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadWritableInstant;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
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

public class DateTimeFormatter {
    private final InternalPrinter iPrinter;
    private final InternalParser iParser;
    private final Locale iLocale;
    private final boolean iOffsetParsed;
    private final Chronology iChrono;
    private final DateTimeZone iZone;
    private final Integer iPivotYear;
    private final int iDefaultYear;

    public DateTimeFormatter(DateTimePrinter dateTimePrinter, DateTimeParser dateTimeParser) {
        this(DateTimePrinterInternalPrinter.of(dateTimePrinter), DateTimeParserInternalParser.of(dateTimeParser));
    }

    DateTimeFormatter(InternalPrinter internalPrinter, InternalParser internalParser) {
        this.iPrinter = internalPrinter;
        this.iParser = internalParser;
        this.iLocale = null;
        this.iOffsetParsed = false;
        this.iChrono = null;
        this.iZone = null;
        this.iPivotYear = null;
        this.iDefaultYear = 2000;
    }

    private DateTimeFormatter(InternalPrinter internalPrinter, InternalParser internalParser, Locale locale, boolean bl, Chronology chronology, DateTimeZone dateTimeZone, Integer n, int n2) {
        this.iPrinter = internalPrinter;
        this.iParser = internalParser;
        this.iLocale = locale;
        this.iOffsetParsed = bl;
        this.iChrono = chronology;
        this.iZone = dateTimeZone;
        this.iPivotYear = n;
        this.iDefaultYear = n2;
    }

    public boolean isPrinter() {
        return this.iPrinter != null;
    }

    public DateTimePrinter getPrinter() {
        return InternalPrinterDateTimePrinter.of(this.iPrinter);
    }

    InternalPrinter getPrinter0() {
        return this.iPrinter;
    }

    public boolean isParser() {
        return this.iParser != null;
    }

    public DateTimeParser getParser() {
        return InternalParserDateTimeParser.of(this.iParser);
    }

    InternalParser getParser0() {
        return this.iParser;
    }

    public DateTimeFormatter withLocale(Locale locale) {
        if (locale == this.getLocale() || locale != null && locale.equals(this.getLocale())) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, locale, this.iOffsetParsed, this.iChrono, this.iZone, this.iPivotYear, this.iDefaultYear);
    }

    public Locale getLocale() {
        return this.iLocale;
    }

    public DateTimeFormatter withOffsetParsed() {
        if (this.iOffsetParsed) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, true, this.iChrono, null, this.iPivotYear, this.iDefaultYear);
    }

    public boolean isOffsetParsed() {
        return this.iOffsetParsed;
    }

    public DateTimeFormatter withChronology(Chronology chronology) {
        if (this.iChrono == chronology) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, this.iOffsetParsed, chronology, this.iZone, this.iPivotYear, this.iDefaultYear);
    }

    public Chronology getChronology() {
        return this.iChrono;
    }

    @Deprecated
    public Chronology getChronolgy() {
        return this.iChrono;
    }

    public DateTimeFormatter withZoneUTC() {
        return this.withZone(DateTimeZone.UTC);
    }

    public DateTimeFormatter withZone(DateTimeZone dateTimeZone) {
        if (this.iZone == dateTimeZone) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, false, this.iChrono, dateTimeZone, this.iPivotYear, this.iDefaultYear);
    }

    public DateTimeZone getZone() {
        return this.iZone;
    }

    public DateTimeFormatter withPivotYear(Integer n) {
        if (this.iPivotYear == n || this.iPivotYear != null && this.iPivotYear.equals(n)) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, this.iOffsetParsed, this.iChrono, this.iZone, n, this.iDefaultYear);
    }

    public DateTimeFormatter withPivotYear(int n) {
        return this.withPivotYear((Integer)n);
    }

    public Integer getPivotYear() {
        return this.iPivotYear;
    }

    public DateTimeFormatter withDefaultYear(int n) {
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, this.iOffsetParsed, this.iChrono, this.iZone, this.iPivotYear, n);
    }

    public int getDefaultYear() {
        return this.iDefaultYear;
    }

    public void printTo(StringBuffer stringBuffer, ReadableInstant readableInstant) {
        try {
            this.printTo((Appendable)stringBuffer, readableInstant);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void printTo(StringBuilder stringBuilder, ReadableInstant readableInstant) {
        try {
            this.printTo((Appendable)stringBuilder, readableInstant);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void printTo(Writer writer, ReadableInstant readableInstant) throws IOException {
        this.printTo((Appendable)writer, readableInstant);
    }

    public void printTo(Appendable appendable, ReadableInstant readableInstant) throws IOException {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.printTo(appendable, l, chronology);
    }

    public void printTo(StringBuffer stringBuffer, long l) {
        try {
            this.printTo((Appendable)stringBuffer, l);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void printTo(StringBuilder stringBuilder, long l) {
        try {
            this.printTo((Appendable)stringBuilder, l);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void printTo(Writer writer, long l) throws IOException {
        this.printTo((Appendable)writer, l);
    }

    public void printTo(Appendable appendable, long l) throws IOException {
        this.printTo(appendable, l, null);
    }

    public void printTo(StringBuffer stringBuffer, ReadablePartial readablePartial) {
        try {
            this.printTo((Appendable)stringBuffer, readablePartial);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void printTo(StringBuilder stringBuilder, ReadablePartial readablePartial) {
        try {
            this.printTo((Appendable)stringBuilder, readablePartial);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void printTo(Writer writer, ReadablePartial readablePartial) throws IOException {
        this.printTo((Appendable)writer, readablePartial);
    }

    public void printTo(Appendable appendable, ReadablePartial readablePartial) throws IOException {
        InternalPrinter internalPrinter = this.requirePrinter();
        if (readablePartial == null) {
            throw new IllegalArgumentException("The partial must not be null");
        }
        internalPrinter.printTo(appendable, readablePartial, this.iLocale);
    }

    public String print(ReadableInstant readableInstant) {
        StringBuilder stringBuilder = new StringBuilder(this.requirePrinter().estimatePrintedLength());
        try {
            this.printTo((Appendable)stringBuilder, readableInstant);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return stringBuilder.toString();
    }

    public String print(long l) {
        StringBuilder stringBuilder = new StringBuilder(this.requirePrinter().estimatePrintedLength());
        try {
            this.printTo((Appendable)stringBuilder, l);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return stringBuilder.toString();
    }

    public String print(ReadablePartial readablePartial) {
        StringBuilder stringBuilder = new StringBuilder(this.requirePrinter().estimatePrintedLength());
        try {
            this.printTo((Appendable)stringBuilder, readablePartial);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return stringBuilder.toString();
    }

    private void printTo(Appendable appendable, long l, Chronology chronology) throws IOException {
        InternalPrinter internalPrinter = this.requirePrinter();
        DateTimeZone dateTimeZone = (chronology = this.selectChronology(chronology)).getZone();
        int n = dateTimeZone.getOffset(l);
        long l2 = l + (long)n;
        if ((l ^ l2) < 0L && (l ^ (long)n) >= 0L) {
            dateTimeZone = DateTimeZone.UTC;
            n = 0;
            l2 = l;
        }
        internalPrinter.printTo(appendable, l2, chronology.withUTC(), n, dateTimeZone, this.iLocale);
    }

    private InternalPrinter requirePrinter() {
        InternalPrinter internalPrinter = this.iPrinter;
        if (internalPrinter == null) {
            throw new UnsupportedOperationException("Printing not supported");
        }
        return internalPrinter;
    }

    public int parseInto(ReadWritableInstant readWritableInstant, String string, int n) {
        InternalParser internalParser = this.requireParser();
        if (readWritableInstant == null) {
            throw new IllegalArgumentException("Instant must not be null");
        }
        long l = readWritableInstant.getMillis();
        Chronology chronology = readWritableInstant.getChronology();
        int n2 = DateTimeUtils.getChronology(chronology).year().get(l);
        long l2 = l + (long)chronology.getZone().getOffset(l);
        chronology = this.selectChronology(chronology);
        DateTimeParserBucket dateTimeParserBucket = new DateTimeParserBucket(l2, chronology, this.iLocale, this.iPivotYear, n2);
        int n3 = internalParser.parseInto(dateTimeParserBucket, string, n);
        readWritableInstant.setMillis(dateTimeParserBucket.computeMillis(false, string));
        if (this.iOffsetParsed && dateTimeParserBucket.getOffsetInteger() != null) {
            int n4 = dateTimeParserBucket.getOffsetInteger();
            DateTimeZone dateTimeZone = DateTimeZone.forOffsetMillis(n4);
            chronology = chronology.withZone(dateTimeZone);
        } else if (dateTimeParserBucket.getZone() != null) {
            chronology = chronology.withZone(dateTimeParserBucket.getZone());
        }
        readWritableInstant.setChronology(chronology);
        if (this.iZone != null) {
            readWritableInstant.setZone(this.iZone);
        }
        return n3;
    }

    public long parseMillis(String string) {
        InternalParser internalParser = this.requireParser();
        Chronology chronology = this.selectChronology(this.iChrono);
        DateTimeParserBucket dateTimeParserBucket = new DateTimeParserBucket(0L, chronology, this.iLocale, this.iPivotYear, this.iDefaultYear);
        return dateTimeParserBucket.doParseMillis(internalParser, string);
    }

    public LocalDate parseLocalDate(String string) {
        return this.parseLocalDateTime(string).toLocalDate();
    }

    public LocalTime parseLocalTime(String string) {
        return this.parseLocalDateTime(string).toLocalTime();
    }

    public LocalDateTime parseLocalDateTime(String string) {
        Chronology chronology;
        DateTimeParserBucket dateTimeParserBucket;
        InternalParser internalParser = this.requireParser();
        int n = internalParser.parseInto(dateTimeParserBucket = new DateTimeParserBucket(0L, chronology = this.selectChronology(null).withUTC(), this.iLocale, this.iPivotYear, this.iDefaultYear), string, 0);
        if (n >= 0) {
            if (n >= string.length()) {
                long l = dateTimeParserBucket.computeMillis(true, string);
                if (dateTimeParserBucket.getOffsetInteger() != null) {
                    int n2 = dateTimeParserBucket.getOffsetInteger();
                    DateTimeZone dateTimeZone = DateTimeZone.forOffsetMillis(n2);
                    chronology = chronology.withZone(dateTimeZone);
                } else if (dateTimeParserBucket.getZone() != null) {
                    chronology = chronology.withZone(dateTimeParserBucket.getZone());
                }
                return new LocalDateTime(l, chronology);
            }
        } else {
            n ^= 0xFFFFFFFF;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(string, n));
    }

    public DateTime parseDateTime(String string) {
        Chronology chronology;
        DateTimeParserBucket dateTimeParserBucket;
        InternalParser internalParser = this.requireParser();
        int n = internalParser.parseInto(dateTimeParserBucket = new DateTimeParserBucket(0L, chronology = this.selectChronology(null), this.iLocale, this.iPivotYear, this.iDefaultYear), string, 0);
        if (n >= 0) {
            if (n >= string.length()) {
                long l = dateTimeParserBucket.computeMillis(true, string);
                if (this.iOffsetParsed && dateTimeParserBucket.getOffsetInteger() != null) {
                    int n2 = dateTimeParserBucket.getOffsetInteger();
                    DateTimeZone dateTimeZone = DateTimeZone.forOffsetMillis(n2);
                    chronology = chronology.withZone(dateTimeZone);
                } else if (dateTimeParserBucket.getZone() != null) {
                    chronology = chronology.withZone(dateTimeParserBucket.getZone());
                }
                DateTime dateTime = new DateTime(l, chronology);
                if (this.iZone != null) {
                    dateTime = dateTime.withZone(this.iZone);
                }
                return dateTime;
            }
        } else {
            n ^= 0xFFFFFFFF;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(string, n));
    }

    public MutableDateTime parseMutableDateTime(String string) {
        Chronology chronology;
        DateTimeParserBucket dateTimeParserBucket;
        InternalParser internalParser = this.requireParser();
        int n = internalParser.parseInto(dateTimeParserBucket = new DateTimeParserBucket(0L, chronology = this.selectChronology(null), this.iLocale, this.iPivotYear, this.iDefaultYear), string, 0);
        if (n >= 0) {
            if (n >= string.length()) {
                long l = dateTimeParserBucket.computeMillis(true, string);
                if (this.iOffsetParsed && dateTimeParserBucket.getOffsetInteger() != null) {
                    int n2 = dateTimeParserBucket.getOffsetInteger();
                    DateTimeZone dateTimeZone = DateTimeZone.forOffsetMillis(n2);
                    chronology = chronology.withZone(dateTimeZone);
                } else if (dateTimeParserBucket.getZone() != null) {
                    chronology = chronology.withZone(dateTimeParserBucket.getZone());
                }
                MutableDateTime mutableDateTime = new MutableDateTime(l, chronology);
                if (this.iZone != null) {
                    mutableDateTime.setZone(this.iZone);
                }
                return mutableDateTime;
            }
        } else {
            n ^= 0xFFFFFFFF;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(string, n));
    }

    private InternalParser requireParser() {
        InternalParser internalParser = this.iParser;
        if (internalParser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        return internalParser;
    }

    private Chronology selectChronology(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        if (this.iChrono != null) {
            chronology = this.iChrono;
        }
        if (this.iZone != null) {
            chronology = chronology.withZone(this.iZone);
        }
        return chronology;
    }
}

