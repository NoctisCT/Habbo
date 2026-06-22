/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import org.joda.time.format.DateTimeParserBucket;

public interface DateTimeParser {
    public int estimateParsedLength();

    public int parseInto(DateTimeParserBucket var1, String var2, int var3);
}

