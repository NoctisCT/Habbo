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

public final class EthiopicChronology
extends BasicFixedMonthChronology {
    private static final long serialVersionUID = -5972804258688333942L;
    public static final int EE = 1;
    private static final DateTimeField ERA_FIELD = new BasicSingleEraDateTimeField("EE");
    private static final int MIN_YEAR = -292269337;
    private static final int MAX_YEAR = 292272984;
    private static final ConcurrentHashMap<DateTimeZone, EthiopicChronology[]> cCache = new ConcurrentHashMap();
    private static final EthiopicChronology INSTANCE_UTC = EthiopicChronology.getInstance(DateTimeZone.UTC);

    public static EthiopicChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }

    public static EthiopicChronology getInstance() {
        return EthiopicChronology.getInstance(DateTimeZone.getDefault(), 4);
    }

    public static EthiopicChronology getInstance(DateTimeZone dateTimeZone) {
        return EthiopicChronology.getInstance(dateTimeZone, 4);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static EthiopicChronology getInstance(DateTimeZone dateTimeZone, int n) {
        EthiopicChronology ethiopicChronology;
        EthiopicChronology[] ethiopicChronologyArray;
        EthiopicChronology[] ethiopicChronologyArray2;
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if ((ethiopicChronologyArray2 = cCache.get(dateTimeZone)) == null && (ethiopicChronologyArray = cCache.putIfAbsent(dateTimeZone, ethiopicChronologyArray2 = new EthiopicChronology[7])) != null) {
            ethiopicChronologyArray2 = ethiopicChronologyArray;
        }
        try {
            ethiopicChronology = ethiopicChronologyArray2[n - 1];
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            throw new IllegalArgumentException("Invalid min days in first week: " + n);
        }
        if (ethiopicChronology != null) return ethiopicChronology;
        ethiopicChronologyArray = ethiopicChronologyArray2;
        synchronized (ethiopicChronologyArray2) {
            ethiopicChronology = ethiopicChronologyArray2[n - 1];
            if (ethiopicChronology != null) return ethiopicChronology;
            if (dateTimeZone == DateTimeZone.UTC) {
                ethiopicChronology = new EthiopicChronology(null, null, n);
                DateTime dateTime = new DateTime(1, 1, 1, 0, 0, 0, 0, ethiopicChronology);
                ethiopicChronology = new EthiopicChronology(LimitChronology.getInstance(ethiopicChronology, dateTime, null), null, n);
            } else {
                ethiopicChronology = EthiopicChronology.getInstance(DateTimeZone.UTC, n);
                ethiopicChronology = new EthiopicChronology(ZonedChronology.getInstance(ethiopicChronology, dateTimeZone), null, n);
            }
            ethiopicChronologyArray2[n - 1] = ethiopicChronology;
            // ** MonitorExit[var4_3] (shouldn't be in output)
            return ethiopicChronology;
        }
    }

    EthiopicChronology(Chronology chronology, Object object, int n) {
        super(chronology, object, n);
    }

    private Object readResolve() {
        Chronology chronology = this.getBase();
        return chronology == null ? EthiopicChronology.getInstance(DateTimeZone.UTC, this.getMinimumDaysInFirstWeek()) : EthiopicChronology.getInstance(chronology.getZone(), this.getMinimumDaysInFirstWeek());
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
        return EthiopicChronology.getInstance(dateTimeZone);
    }

    boolean isLeapDay(long l) {
        return this.dayOfMonth().get(l) == 6 && this.monthOfYear().isLeap(l);
    }

    long calculateFirstDayOfYearMillis(int n) {
        int n2;
        int n3 = n - 1963;
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
        return 292272984;
    }

    long getApproxMillisAtEpochDividedByTwo() {
        return 30962844000000L;
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

