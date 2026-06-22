/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.BasicGJChronology;
import org.joda.time.chrono.ZonedChronology;

public final class GregorianChronology
extends BasicGJChronology {
    private static final long serialVersionUID = -861407383323710522L;
    private static final long MILLIS_PER_YEAR = 31556952000L;
    private static final long MILLIS_PER_MONTH = 2629746000L;
    private static final int DAYS_0000_TO_1970 = 719527;
    private static final int MIN_YEAR = -292275054;
    private static final int MAX_YEAR = 292278993;
    private static final GregorianChronology INSTANCE_UTC;
    private static final ConcurrentHashMap<DateTimeZone, GregorianChronology[]> cCache;

    public static GregorianChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }

    public static GregorianChronology getInstance() {
        return GregorianChronology.getInstance(DateTimeZone.getDefault(), 4);
    }

    public static GregorianChronology getInstance(DateTimeZone dateTimeZone) {
        return GregorianChronology.getInstance(dateTimeZone, 4);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static GregorianChronology getInstance(DateTimeZone dateTimeZone, int n) {
        GregorianChronology gregorianChronology;
        GregorianChronology[] gregorianChronologyArray;
        GregorianChronology[] gregorianChronologyArray2;
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if ((gregorianChronologyArray2 = cCache.get(dateTimeZone)) == null && (gregorianChronologyArray = cCache.putIfAbsent(dateTimeZone, gregorianChronologyArray2 = new GregorianChronology[7])) != null) {
            gregorianChronologyArray2 = gregorianChronologyArray;
        }
        try {
            gregorianChronology = gregorianChronologyArray2[n - 1];
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            throw new IllegalArgumentException("Invalid min days in first week: " + n);
        }
        if (gregorianChronology != null) return gregorianChronology;
        gregorianChronologyArray = gregorianChronologyArray2;
        synchronized (gregorianChronologyArray2) {
            gregorianChronology = gregorianChronologyArray2[n - 1];
            if (gregorianChronology != null) return gregorianChronology;
            if (dateTimeZone == DateTimeZone.UTC) {
                gregorianChronology = new GregorianChronology(null, null, n);
            } else {
                gregorianChronology = GregorianChronology.getInstance(DateTimeZone.UTC, n);
                gregorianChronology = new GregorianChronology(ZonedChronology.getInstance(gregorianChronology, dateTimeZone), null, n);
            }
            gregorianChronologyArray2[n - 1] = gregorianChronology;
            // ** MonitorExit[var4_3] (shouldn't be in output)
            return gregorianChronology;
        }
    }

    private GregorianChronology(Chronology chronology, Object object, int n) {
        super(chronology, object, n);
    }

    private Object readResolve() {
        Chronology chronology = this.getBase();
        int n = this.getMinimumDaysInFirstWeek();
        n = n == 0 ? 4 : n;
        return chronology == null ? GregorianChronology.getInstance(DateTimeZone.UTC, n) : GregorianChronology.getInstance(chronology.getZone(), n);
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
        return GregorianChronology.getInstance(dateTimeZone);
    }

    protected void assemble(AssembledChronology.Fields fields) {
        if (this.getBase() == null) {
            super.assemble(fields);
        }
    }

    boolean isLeapYear(int n) {
        return (n & 3) == 0 && (n % 100 != 0 || n % 400 == 0);
    }

    long calculateFirstDayOfYearMillis(int n) {
        int n2 = n / 100;
        if (n < 0) {
            n2 = (n + 3 >> 2) - n2 + (n2 + 3 >> 2) - 1;
        } else {
            n2 = (n >> 2) - n2 + (n2 >> 2);
            if (this.isLeapYear(n)) {
                --n2;
            }
        }
        return ((long)n * 365L + (long)(n2 - 719527)) * 86400000L;
    }

    int getMinYear() {
        return -292275054;
    }

    int getMaxYear() {
        return 292278993;
    }

    long getAverageMillisPerYear() {
        return 31556952000L;
    }

    long getAverageMillisPerYearDividedByTwo() {
        return 15778476000L;
    }

    long getAverageMillisPerMonth() {
        return 2629746000L;
    }

    long getApproxMillisAtEpochDividedByTwo() {
        return 31083597720000L;
    }

    static {
        cCache = new ConcurrentHashMap();
        INSTANCE_UTC = GregorianChronology.getInstance(DateTimeZone.UTC);
    }
}

