/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.ToString
 */
package org.joda.time.base;

import org.joda.convert.ToString;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.format.FormatUtils;

public abstract class AbstractDuration
implements ReadableDuration {
    protected AbstractDuration() {
    }

    public Duration toDuration() {
        return new Duration(this.getMillis());
    }

    public Period toPeriod() {
        return new Period(this.getMillis());
    }

    public int compareTo(ReadableDuration readableDuration) {
        long l;
        long l2 = this.getMillis();
        if (l2 < (l = readableDuration.getMillis())) {
            return -1;
        }
        if (l2 > l) {
            return 1;
        }
        return 0;
    }

    public boolean isEqual(ReadableDuration readableDuration) {
        if (readableDuration == null) {
            readableDuration = Duration.ZERO;
        }
        return this.compareTo(readableDuration) == 0;
    }

    public boolean isLongerThan(ReadableDuration readableDuration) {
        if (readableDuration == null) {
            readableDuration = Duration.ZERO;
        }
        return this.compareTo(readableDuration) > 0;
    }

    public boolean isShorterThan(ReadableDuration readableDuration) {
        if (readableDuration == null) {
            readableDuration = Duration.ZERO;
        }
        return this.compareTo(readableDuration) < 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ReadableDuration)) {
            return false;
        }
        ReadableDuration readableDuration = (ReadableDuration)object;
        return this.getMillis() == readableDuration.getMillis();
    }

    public int hashCode() {
        long l = this.getMillis();
        return (int)(l ^ l >>> 32);
    }

    @ToString
    public String toString() {
        long l = this.getMillis();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("PT");
        boolean bl = l < 0L;
        FormatUtils.appendUnpaddedInteger(stringBuffer, l);
        while (stringBuffer.length() < (bl ? 7 : 6)) {
            stringBuffer.insert(bl ? 3 : 2, "0");
        }
        if (l / 1000L * 1000L == l) {
            stringBuffer.setLength(stringBuffer.length() - 3);
        } else {
            stringBuffer.insert(stringBuffer.length() - 3, ".");
        }
        stringBuffer.append('S');
        return stringBuffer.toString();
    }
}

