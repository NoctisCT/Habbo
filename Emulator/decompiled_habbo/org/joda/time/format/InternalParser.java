/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import org.joda.time.format.DateTimeParserBucket;

interface InternalParser {
    public int estimateParsedLength();

    public int parseInto(DateTimeParserBucket var1, CharSequence var2, int var3);
}

