/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.BasicFixedMonthChronology;
import org.joda.time.chrono.BasicMonthOfYearDateTimeField;
import org.joda.time.chrono.BasicSingleEraDateTimeField;
import org.joda.time.chrono.LimitChronology;
import org.joda.time.chrono.ZonedChronology;
import org.joda.time.field.SkipDateTimeField;

public final class CopticChronology
extends BasicFixedMonthChronology {
    private static final long serialVersionUID = -5972804258688333942L;
    public static final int AM = 1;
    private static final DateTimeField ERA_FIELD = new BasicSingleEraDateTimeField("AM");
    private static final int MIN_YEAR = -292269337;
    private static final int MAX_YEAR = 292272708;
    private static final ConcurrentHashMap<DateTimeZone, CopticChronology[]> cCache = new ConcurrentHashMap();
    private static final CopticChronology INSTANCE_UTC = CopticChronology.getInstance(DateTimeZone.UTC);

    public static CopticChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }

    public static CopticChronology getInstance() {
        return CopticChronology.getInstance(DateTimeZone.getDefault(), 4);
    }

    public static CopticChronology getInstance(DateTimeZone dateTimeZone) {
        return CopticChronology.getInstance(dateTimeZone, 4);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static CopticChronology getInstance(DateTimeZone dateTimeZone, int n) {
        CopticChronology copticChronology;
        CopticChronology[] copticChronologyArray;
        CopticChronology[] copticChronologyArray2;
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if ((copticChronologyArray2 = cCache.get(dateTimeZone)) == null && (copticChronologyArray = cCache.putIfAbsent(dateTimeZone, copticChronologyArray2 = new CopticChronology[7])) != null) {
            copticChronologyArray2 = copticChronologyArray;
        }
        try {
            copticChronology = copticChronologyArray2[n - 1];
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            throw new IllegalArgumentException("Invalid min days in first week: " + n);
        }
        if (copticChronology != null) return copticChronology;
        copticChronologyArray = copticChronologyArray2;
        synchronized (copticChronologyArray2) {
            copticChronology = copticChronologyArray2[n - 1];
            if (copticChronology != null) return copticChronology;
            if (dateTimeZone == DateTimeZone.UTC) {
                copticChronology = new CopticChronology(null, null, n);
                DateTime dateTime = new DateTime(1, 1, 1, 0, 0, 0, 0, copticChronology);
                copticChronology = new CopticChronology(LimitChronology.getInstance(copticChronology, dateTime, null), null, n);
            } else {
                copticChronology = CopticChronology.getInstance(DateTimeZone.UTC, n);
                copticChronology = new CopticChronology(ZonedChronology.getInstance(copticChronology, dateTimeZone), null, n);
            }
            copticChronologyArray2[n - 1] = copticChronology;
            // ** MonitorExit[var4_3] (shouldn't be in output)
            return copticChronology;
        }
    }

    CopticChronology(Chronology chronology, Object object, int n) {
        super(chronology, object, n);
    }

    private Object readResolve() {
        Chronology chronology = this.getBase();
        int n = this.getMinimumDaysInFirstWeek();
        n = n == 0 ? 4 : n;
        return chronology == null ? CopticChronology.getInstance(DateTimeZone.UTC, n) : CopticChronology.getInstance(chronology.getZone(), n);
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
        return CopticChronology.getInstance(dateTimeZone);
    }

    boolean isLeapDay(long l) {
        return this.dayOfMonth().get(l) == 6 && this.monthOfYear().isLeap(l);
    }

    long calculateFirstDayOfYearMillis(int n) {
        int n2;
        int n3 = n - 1687;
        if (n3 <= 0) {
            n2 = n3 + 3 >> 2;
        } else {
            n2 = n3 >> 2;
            if (!this.isLeapYear(n)) {
                ++n2;
            }
        }
        long l = ((long)n3 * 365L + (long)n2) * 86400000L;
        return l + 21859200000L;
    }

    int getMinYear() {
        return -292269337;
    }

    int getMaxYear() {
        return 292272708;
    }

    long getApproxMillisAtEpochDividedByTwo() {
        return 26607895200000L;
    }

    protected void assemble(AssembledChronology.Fields fields) {
        if (this.getBase() == null) {
            super.assemble(fields);
            fields.year = new SkipDateTimeField(this, fields.year);
            fields.weekyear = new SkipDateTimeField(this, fields.weekyear);
            fields.era = ERA_FIELD;
            fields.monthOfYear = new BasicMonthOfYearDateTimeField(this, 13);
            fields.months = fields.monthOfYear.getDurationField();
        }
    }
}

