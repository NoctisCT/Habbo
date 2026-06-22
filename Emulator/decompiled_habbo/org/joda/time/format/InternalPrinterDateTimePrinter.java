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
import org.joda.time.format.DateTimePrinterInternalPrinter;
import org.joda.time.format.InternalPrinter;

class InternalPrinterDateTimePrinter
implements DateTimePrinter,
InternalPrinter {
    private final InternalPrinter underlying;

    static DateTimePrinter of(InternalPrinter internalPrinter) {
        if (internalPrinter instanceof DateTimePrinterInternalPrinter) {
            return ((DateTimePrinterInternalPrinter)internalPrinter).getUnderlying();
        }
        if (internalPrinter instanceof DateTimePrinter) {
            return (DateTimePrinter)((Object)internalPrinter);
        }
        if (internalPrinter == null) {
            return null;
        }
        return new InternalPrinterDateTimePrinter(internalPrinter);
    }

    private InternalPrinterDateTimePrinter(InternalPrinter internalPrinter) {
        this.underlying = internalPrinter;
    }

    public int estimatePrintedLength() {
        return this.underlying.estimatePrintedLength();
    }

    public void printTo(StringBuffer stringBuffer, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) {
        try {
            this.underlying.printTo(stringBuffer, l, chronology, n, dateTimeZone, locale);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void printTo(Writer writer, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
        this.underlying.printTo(writer, l, chronology, n, dateTimeZone, locale);
    }

    public void printTo(Appendable appendable, long l, Chronology chronology, int n, DateTimeZone dateTimeZone, Locale locale) throws IOException {
        this.underlying.printTo(appendable, l, chronology, n, dateTimeZone, locale);
    }

    public void printTo(StringBuffer stringBuffer, ReadablePartial readablePartial, Locale locale) {
        try {
            this.underlying.printTo(stringBuffer, readablePartial, locale);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void printTo(Writer writer, ReadablePartial readablePartial, Locale locale) throws IOException {
        this.underlying.printTo(writer, readablePartial, locale);
    }

    public void printTo(Appendable appendable, ReadablePartial readablePartial, Locale locale) throws IOException {
        this.underlying.printTo(appendable, readablePartial, locale);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof InternalPrinterDateTimePrinter) {
            InternalPrinterDateTimePrinter internalPrinterDateTimePrinter = (InternalPrinterDateTimePrinter)object;
            return this.underlying.equals(internalPrinterDateTimePrinter.underlying);
        }
        return false;
    }
}

