/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimeParserBucket;
import org.joda.time.format.DateTimeParserInternalParser;
import org.joda.time.format.InternalParser;

class InternalParserDateTimeParser
implements DateTimeParser,
InternalParser {
    private final InternalParser underlying;

    static DateTimeParser of(InternalParser internalParser) {
        if (internalParser instanceof DateTimeParserInternalParser) {
            return ((DateTimeParserInternalParser)internalParser).getUnderlying();
        }
        if (internalParser instanceof DateTimeParser) {
            return (DateTimeParser)((Object)internalParser);
        }
        if (internalParser == null) {
            return null;
        }
        return new InternalParserDateTimeParser(internalParser);
    }

    private InternalParserDateTimeParser(InternalParser internalParser) {
        this.underlying = internalParser;
    }

    public int estimateParsedLength() {
        return this.underlying.estimateParsedLength();
    }

    public int parseInto(DateTimeParserBucket dateTimeParserBucket, CharSequence charSequence, int n) {
        return this.underlying.parseInto(dateTimeParserBucket, charSequence, n);
    }

    public int parseInto(DateTimeParserBucket dateTimeParserBucket, String string, int n) {
        return this.underlying.parseInto(dateTimeParserBucket, string, n);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof InternalParserDateTimeParser) {
            InternalParserDateTimeParser internalParserDateTimeParser = (InternalParserDateTimeParser)object;
            return this.underlying.equals(internalParserDateTimeParser.underlying);
        }
        return false;
    }
}

