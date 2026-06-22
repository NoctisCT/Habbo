/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.Chronology;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.BasicGJChronology;
import org.joda.time.chrono.ZonedChronology;
import org.joda.time.field.SkipDateTimeField;

public final class JulianChronology
extends BasicGJChronology {
    private static final long serialVersionUID = -8731039522547897247L;
    private static final long MILLIS_PER_YEAR = 31557600000L;
    private static final long MILLIS_PER_MONTH = 2629800000L;
    private static final int MIN_YEAR = -292269054;
    private static final int MAX_YEAR = 292272992;
    private static final JulianChronology INSTANCE_UTC;
    private static final ConcurrentHashMap<DateTimeZone, JulianChronology[]> cCache;

    static int adjustYearForSet(int n) {
        if (n <= 0) {
            if (n == 0) {
                throw new IllegalFieldValueException(DateTimeFieldType.year(), (Number)n, null, null);
            }
            ++n;
        }
        return n;
    }

    public static JulianChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }

    public static JulianChronology getInstance() {
        return JulianChronology.getInstance(DateTimeZone.getDefault(), 4);
    }

    public static JulianChronology getInstance(DateTimeZone dateTimeZone) {
        return JulianChronology.getInstance(dateTimeZone, 4);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static JulianChronology getInstance(DateTimeZone dateTimeZone, int n) {
        JulianChronology julianChronology;
        JulianChronology[] julianChronologyArray;
        JulianChronology[] julianChronologyArray2;
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if ((julianChronologyArray2 = cCache.get(dateTimeZone)) == null && (julianChronologyArray = cCache.putIfAbsent(dateTimeZone, julianChronologyArray2 = new JulianChronology[7])) != null) {
            julianChronologyArray2 = julianChronologyArray;
        }
        try {
            julianChronology = julianChronologyArray2[n - 1];
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            throw new IllegalArgumentException("Invalid min days in first week: " + n);
        }
        if (julianChronology != null) return julianChronology;
        julianChronologyArray = julianChronologyArray2;
        synchronized (julianChronologyArray2) {
            julianChronology = julianChronologyArray2[n - 1];
            if (julianChronology != null) return julianChronology;
            if (dateTimeZone == DateTimeZone.UTC) {
                julianChronology = new JulianChronology(null, null, n);
            } else {
                julianChronology = JulianChronology.getInstance(DateTimeZone.UTC, n);
                julianChronology = new JulianChronology(ZonedChronology.getInstance(julianChronology, dateTimeZone), null, n);
            }
            julianChronologyArray2[n - 1] = julianChronology;
            // ** MonitorExit[var4_3] (shouldn't be in output)
            return julianChronology;
        }
    }

    JulianChronology(Chronology chronology, Object object, int n) {
        super(chronology, object, n);
    }

    private Object readResolve() {
        Chronology chronology = this.getBase();
        int n = this.getMinimumDaysInFirstWeek();
        n = n == 0 ? 4 : n;
        return chronology == null ? JulianChronology.getInstance(DateTimeZone.UTC, n) : JulianChronology.getInstance(chronology.getZone(), n);
    }

    public Chronology withUTC() {
        return INSTANCE_UTC;
    }

    public Chronology withZone(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if (dateTimeZone == this.getZone()) {
            return this;
        }
        return JulianChronology.getInstance(dateTimeZone);
    }

    long getDateMidnightMillis(int n, int n2, int n3) throws IllegalArgumentException {
        return super.getDateMidnightMillis(JulianChronology.adjustYearForSet(n), n2, n3);
    }

    boolean isLeapYear(int n) {
        return (n & 3) == 0;
    }

    long calculateFirstDayOfYearMillis(int n) {
        int n2;
        int n3 = n - 1968;
        if (n3 <= 0) {
            n2 = n3 + 3 >> 2;
        } else {
            n2 = n3 >> 2;
            if (!this.isLeapYear(n)) {
                ++n2;
            }
        }
        long l = ((long)n3 * 365L + (long)n2) * 86400000L;
        return l - 62035200000L;
    }

    int getMinYear() {
        return -292269054;
    }

    int getMaxYear() {
        return 292272992;
    }

    long getAverageMillisPerYear() {
        return 31557600000L;
    }

    long getAverageMillisPerYearDividedByTwo() {
        return 15778800000L;
    }

    long getAverageMillisPerMonth() {
        return 2629800000L;
    }

    long getApproxMillisAtEpochDividedByTwo() {
        return 31083663600000L;
    }

    protected void assemble(AssembledChronology.Fields fields) {
        if (this.getBase() == null) {
            super.assemble(fields);
            fields.year = new SkipDateTimeField(this, fields.year);
            fields.weekyear = new SkipDateTimeField(this, fields.weekyear);
        }
    }

    static {
        cCache = new ConcurrentHashMap();
        INSTANCE_UTC = JulianChronology.getInstance(DateTimeZone.UTC);
    }
}

