/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import org.joda.time.ReadablePeriod;

public interface PeriodPrinter {
    public int calculatePrintedLength(ReadablePeriod var1, Locale var2);

    public int countFieldsToPrint(ReadablePeriod var1, int var2, Locale var3);

    public void printTo(StringBuffer var1, ReadablePeriod var2, Locale var3);

    public void printTo(Writer var1, ReadablePeriod var2, Locale var3) throws IOException;
}

