/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimeParserBucket;
import org.joda.time.format.InternalParser;
import org.joda.time.format.InternalParserDateTimeParser;

class DateTimeParserInternalParser
implements InternalParser {
    private final DateTimeParser underlying;

    static InternalParser of(DateTimeParser dateTimeParser) {
        if (dateTimeParser instanceof InternalParserDateTimeParser) {
            return (InternalParser)((Object)dateTimeParser);
        }
        if (dateTimeParser == null) {
            return null;
        }
        return new DateTimeParserInternalParser(dateTimeParser);
    }

    private DateTimeParserInternalParser(DateTimeParser dateTimeParser) {
        this.underlying = dateTimeParser;
    }

    DateTimeParser getUnderlying() {
        return this.underlying;
    }

    public int estimateParsedLength() {
        return this.underlying.estimateParsedLength();
    }

    public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
        return this.underlying.parseInto(dateTimeParserBucket, charSequence.toString(), n);
    }
}

