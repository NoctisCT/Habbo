/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.lang.reflect.Method;
import java.text.DateFormatSymbols;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.JodaTimePermission;
import org.joda.time.PeriodType;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.ISOChronology;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DateTimeUtils {
    public static final MillisProvider SYSTEM_MILLIS_PROVIDER;
    private static volatile MillisProvider cMillisProvider;
    private static final AtomicReference<Map<String, DateTimeZone>> cZoneNames;

    protected DateTimeUtils() {
    }

    public static final long currentTimeMillis() {
        return cMillisProvider.getMillis();
    }

    public static final void setCurrentMillisSystem() throws SecurityException {
        DateTimeUtils.checkPermission();
        cMillisProvider = SYSTEM_MILLIS_PROVIDER;
    }

    public static final void setCurrentMillisFixed(long l) throws SecurityException {
        DateTimeUtils.checkPermission();
        cMillisProvider = new FixedMillisProvider(l);
    }

    public static final void setCurrentMillisOffset(long l) throws SecurityException {
        DateTimeUtils.checkPermission();
        cMillisProvider = l == 0L ? SYSTEM_MILLIS_PROVIDER : new OffsetMillisProvider(l);
    }

    public static final void setCurrentMillisProvider(MillisProvider millisProvider) throws SecurityException {
        if (millisProvider == null) {
            throw new IllegalArgumentException("The MillisProvider must not be null");
        }
        DateTimeUtils.checkPermission();
        cMillisProvider = millisProvider;
    }

    private static void checkPermission() throws SecurityException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("CurrentTime.setProvider"));
        }
    }

    public static final long getInstantMillis(ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return DateTimeUtils.currentTimeMillis();
        }
        return readableInstant.getMillis();
    }

    public static final Chronology getInstantChronology(ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return ISOChronology.getInstance();
        }
        Chronology chronology = readableInstant.getChronology();
        if (chronology == null) {
            return ISOChronology.getInstance();
        }
        return chronology;
    }

    public static final Chronology getIntervalChronology(ReadableInstant readableInstant, ReadableInstant readableInstant2) {
        Chronology chronology = null;
        if (readableInstant != null) {
            chronology = readableInstant.getChronology();
        } else if (readableInstant2 != null) {
            chronology = readableInstant2.getChronology();
        }
        if (chronology == null) {
            chronology = ISOChronology.getInstance();
        }
        return chronology;
    }

    public static final Chronology getIntervalChronology(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return ISOChronology.getInstance();
        }
        Chronology chronology = readableInterval.getChronology();
        if (chronology == null) {
            return ISOChronology.getInstance();
        }
        return chronology;
    }

    public static final ReadableInterval getReadableInterval(ReadableInterval readableInterval) {
        if (readableInterval == null) {
            long l = DateTimeUtils.currentTimeMillis();
            readableInterval = new Interval(l, l);
        }
        return readableInterval;
    }

    public static final Chronology getChronology(Chronology chronology) {
        if (chronology == null) {
            return ISOChronology.getInstance();
        }
        return chronology;
    }

    public static final DateTimeZone getZone(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            return DateTimeZone.getDefault();
        }
        return dateTimeZone;
    }

    public static final PeriodType getPeriodType(PeriodType periodType) {
        if (periodType == null) {
            return PeriodType.standard();
        }
        return periodType;
    }

    public static final long getDurationMillis(ReadableDuration readableDuration) {
        if (readableDuration == null) {
            return 0L;
        }
        return readableDuration.getMillis();
    }

    public static final boolean isContiguous(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("Partial must not be null");
        }
        DurationFieldType durationFieldType = null;
        for (int i = 0; i < readablePartial.size(); ++i) {
            DateTimeField dateTimeField = readablePartial.getField(i);
            if (i > 0 && (dateTimeField.getRangeDurationField() == null || dateTimeField.getRangeDurationField().getType() != durationFieldType)) {
                return false;
            }
            durationFieldType = dateTimeField.getDurationField().getType();
        }
        return true;
    }

    public static final DateFormatSymbols getDateFormatSymbols(Locale locale) {
        try {
            Method method = DateFormatSymbols.class.getMethod("getInstance", Locale.class);
            return (DateFormatSymbols)method.invoke(null, locale);
        }
        catch (Exception exception) {
            return new DateFormatSymbols(locale);
        }
    }

    public static final Map<String, DateTimeZone> getDefaultTimeZoneNames() {
        Map<String, DateTimeZone> map = cZoneNames.get();
        if (map == null && !cZoneNames.compareAndSet(null, map = DateTimeUtils.buildDefaultTimeZoneNames())) {
            map = cZoneNames.get();
        }
        return map;
    }

    public static final void setDefaultTimeZoneNames(Map<String, DateTimeZone> map) {
        cZoneNames.set(Collections.unmodifiableMap(new HashMap<String, DateTimeZone>(map)));
    }

    private static Map<String, DateTimeZone> buildDefaultTimeZoneNames() {
        LinkedHashMap<String, DateTimeZone> linkedHashMap = new LinkedHashMap<String, DateTimeZone>();
        linkedHashMap.put("UT", DateTimeZone.UTC);
        linkedHashMap.put("UTC", DateTimeZone.UTC);
        linkedHashMap.put("GMT", DateTimeZone.UTC);
        DateTimeUtils.put(linkedHashMap, "EST", "America/New_York");
        DateTimeUtils.put(linkedHashMap, "EDT", "America/New_York");
        DateTimeUtils.put(linkedHashMap, "CST", "America/Chicago");
        DateTimeUtils.put(linkedHashMap, "CDT", "America/Chicago");
        DateTimeUtils.put(linkedHashMap, "MST", "America/Denver");
        DateTimeUtils.put(linkedHashMap, "MDT", "America/Denver");
        DateTimeUtils.put(linkedHashMap, "PST", "America/Los_Angeles");
        DateTimeUtils.put(linkedHashMap, "PDT", "America/Los_Angeles");
        return Collections.unmodifiableMap(linkedHashMap);
    }

    private static void put(Map<String, DateTimeZone> map, String string, String string2) {
        try {
            map.put(string, DateTimeZone.forID(string2));
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
    }

    public static final double toJulianDay(long l) {
        double d = (double)l / 8.64E7;
        return d + 2440587.5;
    }

    public static final long toJulianDayNumber(long l) {
        return (long)Math.floor(DateTimeUtils.toJulianDay(l) + 0.5);
    }

    public static final long fromJulianDay(double d) {
        double d2 = d - 2440587.5;
        return (long)(d2 * 8.64E7);
    }

    static {
        cMillisProvider = SYSTEM_MILLIS_PROVIDER = new SystemMillisProvider();
        cZoneNames = new AtomicReference();
    }

    static class OffsetMillisProvider
    implements MillisProvider {
        private final long iMillis;

        OffsetMillisProvider(long l) {
            this.iMillis = l;
        }

        public long getMillis() {
            return System.currentTimeMillis() + this.iMillis;
        }
    }

    static class FixedMillisProvider
    implements MillisProvider {
        private final long iMillis;

        FixedMillisProvider(long l) {
            this.iMillis = l;
        }

        public long getMillis() {
            return this.iMillis;
        }
    }

    static class SystemMillisProvider
    implements MillisProvider {
        SystemMillisProvider() {
        }

        public long getMillis() {
            return System.currentTimeMillis();
        }
    }

    public static interface MillisProvider {
        public long getMillis();
    }
}

