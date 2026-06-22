/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimePrinter;
import org.joda.time.format.InternalPrinter;
import org.joda.time.format.InternalPrinterDateTimePrinter;

class DateTimePrinterInternalPrinter
implements InternalPrinter {
    private final DateTimePrinter underlying;

    static InternalPrinter of(DateTimePrinter dateTimePrinter) {
        if (dateTimePrinter instanceof InternalPrinterDateTimePrinter) {
            return (InternalPrinter)((Object)dateTimePrinter);
        }
        if (dateTimePrinter == null) {
            return null;
        }
        return new DateTimePrinterInternalPrinter(dateTimePrinter);
    }

    private DateTimePrinterInternalPrinter(DateTimePrinter dateTimePrinter) {
        this.underlying = dateTimePrinter;
    }

    DateTimePrinter getUnderlying() {
        return this.underlying;
    }

    public int estimatePrintedLength() {
        return this.underlying.estimatePrintedLength();
    }

    public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
        if (appendable instanceof StringBuffer) {
            StringBuffer stringBuffer = (StringBuffer)appendable;
            this.underlying.printTo(stringBuffer, l, chronology, n, dateTimeZone, locale);
        } else if (appendable instanceof Writer) {
            Writer writer = (Writer)appendable;
            this.underlying.printTo(writer, l, chronology, n, dateTimeZone, locale);
        } else {
            StringBuffer stringBuffer = new StringBuffer(this.estimatePrintedLength());
            this.underlying.printTo(stringBuffer, l, chronology, n, dateTimeZone, locale);
            appendable.append(stringBuffer);
        }
    }

    public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
        if (appendable instanceof StringBuffer) {
            StringBuffer stringBuffer = (StringBuffer)appendable;
            this.underlying.printTo(stringBuffer, readablePartial, locale);
        } else if (appendable instanceof Writer) {
            Writer writer = (Writer)appendable;
            this.underlying.printTo(writer, readablePartial, locale);
        } else {
            StringBuffer stringBuffer = new StringBuffer(this.estimatePrintedLength());
            this.underlying.printTo(stringBuffer, readablePartial, locale);
            appendable.append(stringBuffer);
        }
    }
}

