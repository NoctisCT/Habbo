/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.FormatUtils;
import org.joda.time.format.PeriodParser;
import org.joda.time.format.PeriodPrinter;

public class PeriodFormatter {
    private final PeriodPrinter iPrinter;
    private final PeriodParser iParser;
    private final Locale iLocale;
    private final PeriodType iParseType;

    public PeriodFormatter(PeriodPrinter periodPrinter, PeriodParser periodParser) {
        this.iPrinter = periodPrinter;
        this.iParser = periodParser;
        this.iLocale = null;
        this.iParseType = null;
    }

    PeriodFormatter(PeriodPrinter periodPrinter, PeriodParser periodParser, Locale locale, PeriodType periodType) {
        this.iPrinter = periodPrinter;
        this.iParser = periodParser;
        this.iLocale = locale;
        this.iParseType = periodType;
    }

    public boolean isPrinter() {
        return this.iPrinter != null;
    }

    public PeriodPrinter getPrinter() {
        return this.iPrinter;
    }

    public boolean isParser() {
        return this.iParser != null;
    }

    public PeriodParser getParser() {
        return this.iParser;
    }

    public PeriodFormatter withLocale(Locale locale) {
        if (locale == this.getLocale() || locale != null && locale.equals(this.getLocale())) {
            return this;
        }
        return new PeriodFormatter(this.iPrinter, this.iParser, locale, this.iParseType);
    }

    public Locale getLocale() {
        return this.iLocale;
    }

    public PeriodFormatter withParseType(PeriodType periodType) {
        if (periodType == this.iParseType) {
            return this;
        }
        return new PeriodFormatter(this.iPrinter, this.iParser, this.iLocale, periodType);
    }

    public PeriodType getParseType() {
        return this.iParseType;
    }

    public void printTo(StringBuffer stringBuffer, ReadablePeriod readablePeriod) {
        this.checkPrinter();
        this.checkPeriod(readablePeriod);
        this.getPrinter().printTo(stringBuffer, readablePeriod, this.iLocale);
    }

    public void printTo(Writer writer, ReadablePeriod readablePeriod) throws IOException {
        this.checkPrinter();
        this.checkPeriod(readablePeriod);
        this.getPrinter().printTo(writer, readablePeriod, this.iLocale);
    }

    public String print(ReadablePeriod readablePeriod) {
        this.checkPrinter();
        this.checkPeriod(readablePeriod);
        PeriodPrinter periodPrinter = this.getPrinter();
        StringBuffer stringBuffer = new StringBuffer(periodPrinter.calculatePrintedLength(readablePeriod, this.iLocale));
        periodPrinter.printTo(stringBuffer, readablePeriod, this.iLocale);
        return stringBuffer.toString();
    }

    private void checkPrinter() {
        if (this.iPrinter == null) {
            throw new UnsupportedOperationException("Printing not supported");
        }
    }

    private void checkPeriod(ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            throw new IllegalArgumentException("Period must not be null");
        }
    }

    public int parseInto(ReadWritablePeriod readWritablePeriod, String string, int n) {
        this.checkParser();
        this.checkPeriod(readWritablePeriod);
        return this.getParser().parseInto(readWritablePeriod, string, n, this.iLocale);
    }

    public Period parsePeriod(String string) {
        this.checkParser();
        return this.parseMutablePeriod(string).toPeriod();
    }

    public MutablePeriod parseMutablePeriod(String string) {
        this.checkParser();
        MutablePeriod mutablePeriod = new MutablePeriod(0L, this.iParseType);
        int n = this.getParser().parseInto(mutablePeriod, string, 0, this.iLocale);
        if (n >= 0) {
            if (n >= string.length()) {
                return mutablePeriod;
            }
        } else {
            n ^= 0xFFFFFFFF;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(string, n));
    }

    private void checkParser() {
        if (this.iParser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
    }
}

