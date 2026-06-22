/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.FromString
 */
package org.joda.time;

import java.io.Serializable;
import java.math.RoundingMode;
import org.joda.convert.FromString;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.Seconds;
import org.joda.time.base.BaseDuration;
import org.joda.time.field.FieldUtils;

public final class Duration
extends BaseDuration
implements ReadableDuration,
Serializable {
    public static final Duration ZERO = new Duration(0L);
    private static final long serialVersionUID = 2471658376918L;

    @FromString
    public static Duration parse(String string) {
        return new Duration(string);
    }

    public static Duration standardDays(long l) {
        if (l == 0L) {
            return ZERO;
        }
        return new Duration(FieldUtils.safeMultiply(l, 86400000));
    }

    public static Duration standardHours(long l) {
        if (l == 0L) {
            return ZERO;
        }
        return new Duration(FieldUtils.safeMultiply(l, 3600000));
    }

    public static Duration standardMinutes(long l) {
        if (l == 0L) {
            return ZERO;
        }
        return new Duration(FieldUtils.safeMultiply(l, 60000));
    }

    public static Duration standardSeconds(long l) {
        if (l == 0L) {
            return ZERO;
        }
        return new Duration(FieldUtils.safeMultiply(l, 1000));
    }

    public static Duration millis(long l) {
        if (l == 0L) {
            return ZERO;
        }
        return new Duration(l);
    }

    public Duration(long l) {
        super(l);
    }

    public Duration(long l, long l2) {
        super(l, l2);
    }

    public Duration(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        super(readableInstant, readableInstant2);
    }

    public Duration(Object object) {
        super(object);
    }

    public long getStandardDays() {
        return this.getMillis() / 86400000L;
    }

    public long getStandardHours() {
        return this.getMillis() / 3600000L;
    }

    public long getStandardMinutes() {
        return this.getMillis() / 60000L;
    }

    public long getStandardSeconds() {
        return this.getMillis() / 1000L;
    }

    public Duration toDuration() {
        return this;
    }

    public Days toStandardDays() {
        long l = this.getStandardDays();
        return Days.days(FieldUtils.safeToInt(l));
    }

    public Hours toStandardHours() {
        long l = this.getStandardHours();
        return Hours.hours(FieldUtils.safeToInt(l));
    }

    public Minutes toStandardMinutes() {
        long l = this.getStandardMinutes();
        return Minutes.minutes(FieldUtils.safeToInt(l));
    }

    public Seconds toStandardSeconds() {
        long l = this.getStandardSeconds();
        return Seconds.seconds(FieldUtils.safeToInt(l));
    }

    public Duration withMillis(long l) {
        if (l == this.getMillis()) {
            return this;
        }
        return new Duration(l);
    }

    public Duration withDurationAdded(long l, int n) {
        if (l == 0L || n == 0) {
            return this;
        }
        long l2 = FieldUtils.safeMultiply(l, n);
        long l3 = FieldUtils.safeAdd(this.getMillis(), l2);
        return new Duration(l3);
    }

    public Duration withDurationAdded(ReadableDuration readableDuration, int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), n);
    }

    public Duration plus(long l) {
        return this.withDurationAdded(l, 1);
    }

    public Duration plus(ReadableDuration readableDuration) {
        if (readableDuration == null) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), 1);
    }

    public Duration minus(long l) {
        return this.withDurationAdded(l, -1);
    }

    public Duration minus(ReadableDuration readableDuration) {
        if (readableDuration == null) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), -1);
    }

    public Duration multipliedBy(long l) {
        if (l == 1L) {
            return this;
        }
        return new Duration(FieldUtils.safeMultiply(this.getMillis(), l));
    }

    public Duration dividedBy(long l) {
        if (l == 1L) {
            return this;
        }
        return new Duration(FieldUtils.safeDivide(this.getMillis(), l));
    }

    public Duration dividedBy(long l, RoundingMode roundingMode) {
        if (l == 1L) {
            return this;
        }
        return new Duration(FieldUtils.safeDivide(this.getMillis(), l, roundingMode));
    }

    public Duration negated() {
        if (this.getMillis() == Long.MIN_VALUE) {
            throw new ArithmeticException("Negation of this duration would overflow");
        }
        return new Duration(-this.getMillis());
    }

    public Duration abs() {
        if (this.getMillis() < 0L) {
            return this.negated();
        }
        return this;
    }
}

