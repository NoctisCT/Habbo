/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.GJCacheKey;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.JulianChronology;
import org.joda.time.chrono.ZonedChronology;
import org.joda.time.field.BaseDateTimeField;
import org.joda.time.field.DecoratedDurationField;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class GJChronology
extends AssembledChronology {
    private static final long serialVersionUID = -2545574827706931671L;
    static final Instant DEFAULT_CUTOVER = new Instant(-12219292800000L);
    private static final ConcurrentHashMap<GJCacheKey, GJChronology> cCache = new ConcurrentHashMap();
    private JulianChronology iJulianChronology;
    private GregorianChronology iGregorianChronology;
    private Instant iCutoverInstant;
    private long iCutoverMillis;
    private long iGapDuration;

    private static long convertByYear(long l, Chronology chronology, Chronology chronology2) {
        return chronology2.getDateTimeMillis(chronology.year().get(l), chronology.monthOfYear().get(l), chronology.dayOfMonth().get(l), chronology.millisOfDay().get(l));
    }

    private static long convertByWeekyear(long l, Chronology chronology, Chronology chronology2) {
        long l2 = chronology2.weekyear().set(0L, chronology.weekyear().get(l));
        l2 = chronology2.weekOfWeekyear().set(l2, chronology.weekOfWeekyear().get(l));
        l2 = chronology2.dayOfWeek().set(l2, chronology.dayOfWeek().get(l));
        l2 = chronology2.millisOfDay().set(l2, chronology.millisOfDay().get(l));
        return l2;
    }

    public static GJChronology getInstanceUTC() {
        return GJChronology.getInstance(DateTimeZone.UTC, DEFAULT_CUTOVER, 4);
    }

    public static GJChronology getInstance() {
        return GJChronology.getInstance(DateTimeZone.getDefault(), DEFAULT_CUTOVER, 4);
    }

    public static GJChronology getInstance(DateTimeZone dateTimeZone) {
        return GJChronology.getInstance(dateTimeZone, DEFAULT_CUTOVER, 4);
    }

    public static GJChronology getInstance(DateTimeZone dateTimeZone, ReadableInstant readableInstant) {
        return GJChronology.getInstance(dateTimeZone, readableInstant, 4);
    }

    public static GJChronology getInstance(DateTimeZone dateTimeZone, ReadableInstant readableInstant, int n) {
        Object object;
        Instant instant;
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        if (readableInstant == null) {
            instant = DEFAULT_CUTOVER;
        } else {
            instant = readableInstant.toInstant();
            object = new LocalDate(instant.getMillis(), (Chronology)GregorianChronology.getInstance(dateTimeZone));
            if (((LocalDate)object).getYear() <= 0) {
                throw new IllegalArgumentException("Cutover too early. Must be on or after 0001-01-01.");
            }
        }
        object = new GJCacheKey(dateTimeZone, instant, n);
        GJChronology gJChronology = cCache.get(object);
        if (gJChronology == null) {
            if (dateTimeZone == DateTimeZone.UTC) {
                gJChronology = new GJChronology(JulianChronology.getInstance(dateTimeZone, n), GregorianChronology.getInstance(dateTimeZone, n), instant);
            } else {
                gJChronology = GJChronology.getInstance(DateTimeZone.UTC, instant, n);
                gJChronology = new GJChronology(ZonedChronology.getInstance(gJChronology, dateTimeZone), gJChronology.iJulianChronology, gJChronology.iGregorianChronology, gJChronology.iCutoverInstant);
            }
            GJChronology gJChronology2 = cCache.putIfAbsent((GJCacheKey)object, gJChronology);
            if (gJChronology2 != null) {
                gJChronology = gJChronology2;
            }
        }
        return gJChronology;
    }

    public static GJChronology getInstance(DateTimeZone dateTimeZone, long l, int n) {
        Instant instant = l == DEFAULT_CUTOVER.getMillis() ? null : new Instant(l);
        return GJChronology.getInstance(dateTimeZone, instant, n);
    }

    private GJChronology(JulianChronology julianChronology, GregorianChronology gregorianChronology, Instant instant) {
        super(null, new Object[]{julianChronology, gregorianChronology, instant});
    }

    private GJChronology(Chronology chronology, JulianChronology julianChronology, GregorianChronology gregorianChronology, Instant instant) {
        super(chronology, new Object[]{julianChronology, gregorianChronology, instant});
    }

    private Object readResolve() {
        return GJChronology.getInstance(this.getZone(), this.iCutoverInstant, this.getMinimumDaysInFirstWeek());
    }

    public DateTimeZone getZone() {
        Chronology chronology = this.getBase();
        if (chronology != null) {
            return chronology.getZone();
        }
        return DateTimeZone.UTC;
    }

    public Chronology withUTC() {
        return this.withZone(DateTimeZone.UTC);
    }

    public Chronology withZone(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if (dateTimeZone == this.getZone()) {
            return this;
        }
        return GJChronology.getInstance(dateTimeZone, this.iCutoverInstant, this.getMinimumDaysInFirstWeek());
    }

    public long getDateTimeMillis(int n, int n2, int n3, int n4) throws IllegalArgumentException {
        Chronology chronology = this.getBase();
        if (chronology != null) {
            return chronology.getDateTimeMillis(n, n2, n3, n4);
        }
        long l = this.iGregorianChronology.getDateTimeMillis(n, n2, n3, n4);
        if (l < this.iCutoverMillis && (l = this.iJulianChronology.getDateTimeMillis(n, n2, n3, n4)) >= this.iCutoverMillis) {
            throw new IllegalArgumentException("Specified date does not exist");
        }
        return l;
    }

    public long getDateTimeMillis(int n, int n2, int n3, int n4, int n5, int n6, int n7) throws IllegalArgumentException {
        long l;
        block5: {
            Chronology chronology = this.getBase();
            if (chronology != null) {
                return chronology.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
            }
            try {
                l = this.iGregorianChronology.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
            }
            catch (IllegalFieldValueException illegalFieldValueException) {
                if (n2 != 2 || n3 != 29) {
                    throw illegalFieldValueException;
                }
                l = this.iGregorianChronology.getDateTimeMillis(n, n2, 28, n4, n5, n6, n7);
                if (l < this.iCutoverMillis) break block5;
                throw illegalFieldValueException;
            }
        }
        if (l < this.iCutoverMillis && (l = this.iJulianChronology.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7)) >= this.iCutoverMillis) {
            throw new IllegalArgumentException("Specified date does not exist");
        }
        return l;
    }

    public Instant getGregorianCutover() {
        return this.iCutoverInstant;
    }

    public int getMinimumDaysInFirstWeek() {
        return this.iGregorianChronology.getMinimumDaysInFirstWeek();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof GJChronology) {
            GJChronology gJChronology = (GJChronology)object;
            return this.iCutoverMillis == gJChronology.iCutoverMillis && this.getMinimumDaysInFirstWeek() == gJChronology.getMinimumDaysInFirstWeek() && this.getZone().equals(gJChronology.getZone());
        }
        return false;
    }

    public int hashCode() {
        return "GJ".hashCode() * 11 + this.getZone().hashCode() + this.getMinimumDaysInFirstWeek() + this.iCutoverInstant.hashCode();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(60);
        stringBuffer.append("GJChronology");
        stringBuffer.append('[');
        stringBuffer.append(this.getZone().getID());
        if (this.iCutoverMillis != DEFAULT_CUTOVER.getMillis()) {
            stringBuffer.append(",cutover=");
            DateTimeFormatter dateTimeFormatter = this.withUTC().dayOfYear().remainder(this.iCutoverMillis) == 0L ? ISODateTimeFormat.date() : ISODateTimeFormat.dateTime();
            dateTimeFormatter.withChronology(this.withUTC()).printTo(stringBuffer, this.iCutoverMillis);
        }
        if (this.getMinimumDaysInFirstWeek() != 4) {
            stringBuffer.append(",mdfw=");
            stringBuffer.append(this.getMinimumDaysInFirstWeek());
        }
        stringBuffer.append(']');
        return stringBuffer.toString();
    }

    protected void assemble(AssembledChronology.Fields fields) {
        Object[] objectArray = (Object[])this.getParam();
        JulianChronology julianChronology = (JulianChronology)objectArray[0];
        GregorianChronology gregorianChronology = (GregorianChronology)objectArray[1];
        Instant instant = (Instant)objectArray[2];
        this.iCutoverMillis = instant.getMillis();
        this.iJulianChronology = julianChronology;
        this.iGregorianChronology = gregorianChronology;
        this.iCutoverInstant = instant;
        if (this.getBase() != null) {
            return;
        }
        if (julianChronology.getMinimumDaysInFirstWeek() != gregorianChronology.getMinimumDaysInFirstWeek()) {
            throw new IllegalArgumentException();
        }
        this.iGapDuration = this.iCutoverMillis - this.julianToGregorianByYear(this.iCutoverMillis);
        fields.copyFieldsFrom(gregorianChronology);
        if (gregorianChronology.millisOfDay().get(this.iCutoverMillis) == 0) {
            fields.millisOfSecond = new CutoverField(julianChronology.millisOfSecond(), fields.millisOfSecond, this.iCutoverMillis);
            fields.millisOfDay = new CutoverField(julianChronology.millisOfDay(), fields.millisOfDay, this.iCutoverMillis);
            fields.secondOfMinute = new CutoverField(julianChronology.secondOfMinute(), fields.secondOfMinute, this.iCutoverMillis);
            fields.secondOfDay = new CutoverField(julianChronology.secondOfDay(), fields.secondOfDay, this.iCutoverMillis);
            fields.minuteOfHour = new CutoverField(julianChronology.minuteOfHour(), fields.minuteOfHour, this.iCutoverMillis);
            fields.minuteOfDay = new CutoverField(julianChronology.minuteOfDay(), fields.minuteOfDay, this.iCutoverMillis);
            fields.hourOfDay = new CutoverField(julianChronology.hourOfDay(), fields.hourOfDay, this.iCutoverMillis);
            fields.hourOfHalfday = new CutoverField(julianChronology.hourOfHalfday(), fields.hourOfHalfday, this.iCutoverMillis);
            fields.clockhourOfDay = new CutoverField(julianChronology.clockhourOfDay(), fields.clockhourOfDay, this.iCutoverMillis);
            fields.clockhourOfHalfday = new CutoverField(julianChronology.clockhourOfHalfday(), fields.clockhourOfHalfday, this.iCutoverMillis);
            fields.halfdayOfDay = new CutoverField(julianChronology.halfdayOfDay(), fields.halfdayOfDay, this.iCutoverMillis);
        }
        fields.era = new CutoverField(julianChronology.era(), fields.era, this.iCutoverMillis);
        fields.year = new ImpreciseCutoverField(julianChronology.year(), fields.year, this.iCutoverMillis);
        fields.years = fields.year.getDurationField();
        fields.yearOfEra = new ImpreciseCutoverField(julianChronology.yearOfEra(), fields.yearOfEra, fields.years, this.iCutoverMillis);
        fields.centuryOfEra = new ImpreciseCutoverField(julianChronology.centuryOfEra(), fields.centuryOfEra, this.iCutoverMillis);
        fields.centuries = fields.centuryOfEra.getDurationField();
        fields.yearOfCentury = new ImpreciseCutoverField(julianChronology.yearOfCentury(), fields.yearOfCentury, fields.years, fields.centuries, this.iCutoverMillis);
        fields.monthOfYear = new ImpreciseCutoverField(julianChronology.monthOfYear(), fields.monthOfYear, null, fields.years, this.iCutoverMillis);
        fields.months = fields.monthOfYear.getDurationField();
        fields.weekyear = new ImpreciseCutoverField(julianChronology.weekyear(), fields.weekyear, null, this.iCutoverMillis, true);
        fields.weekyears = fields.weekyear.getDurationField();
        fields.weekyearOfCentury = new ImpreciseCutoverField(julianChronology.weekyearOfCentury(), fields.weekyearOfCentury, fields.weekyears, fields.centuries, this.iCutoverMillis);
        long l = gregorianChronology.year().roundCeiling(this.iCutoverMillis);
        fields.dayOfYear = new CutoverField(julianChronology.dayOfYear(), fields.dayOfYear, fields.years, l, false);
        l = gregorianChronology.weekyear().roundCeiling(this.iCutoverMillis);
        fields.weekOfWeekyear = new CutoverField(julianChronology.weekOfWeekyear(), fields.weekOfWeekyear, fields.weekyears, l, true);
        CutoverField cutoverField = new CutoverField(julianChronology.dayOfMonth(), fields.dayOfMonth, this.iCutoverMillis);
        cutoverField.iRangeDurationField = fields.months;
        fields.dayOfMonth = cutoverField;
    }

    long julianToGregorianByYear(long l) {
        return GJChronology.convertByYear(l, this.iJulianChronology, this.iGregorianChronology);
    }

    long gregorianToJulianByYear(long l) {
        return GJChronology.convertByYear(l, this.iGregorianChronology, this.iJulianChronology);
    }

    long julianToGregorianByWeekyear(long l) {
        return GJChronology.convertByWeekyear(l, this.iJulianChronology, this.iGregorianChronology);
    }

    long gregorianToJulianByWeekyear(long l) {
        return GJChronology.convertByWeekyear(l, this.iGregorianChronology, this.iJulianChronology);
    }

    private static class LinkedDurationField
    extends DecoratedDurationField {
        private static final long serialVersionUID = 4097975388007713084L;
        private final ImpreciseCutoverField iField;

        LinkedDurationField(DurationField durationField, ImpreciseCutoverField impreciseCutoverField) {
            super(durationField, durationField.getType());
            this.iField = impreciseCutoverField;
        }

        public long add(long l, int n) {
            return this.iField.add(l, n);
        }

        public long add(long l, long l2) {
            return this.iField.add(l, l2);
        }

        public int getDifference(long l, long l2) {
            return this.iField.getDifference(l, l2);
        }

        public long getDifferenceAsLong(long l, long l2) {
            return this.iField.getDifferenceAsLong(l, l2);
        }
    }

    private final class ImpreciseCutoverField
    extends CutoverField {
        private static final long serialVersionUID = 3410248757173576441L;

        ImpreciseCutoverField(DateTimeField dateTimeField, DateTimeField dateTimeField2, long l) {
            this(dateTimeField, dateTimeField2, null, l, false);
        }

        ImpreciseCutoverField(DateTimeField dateTimeField, DateTimeField dateTimeField2, DurationField durationField, long l) {
            this(dateTimeField, dateTimeField2, durationField, l, false);
        }

        ImpreciseCutoverField(DateTimeField dateTimeField, DateTimeField dateTimeField2, DurationField durationField, DurationField durationField2, long l) {
            this(dateTimeField, dateTimeField2, durationField, l, false);
            this.iRangeDurationField = durationField2;
        }

        ImpreciseCutoverField(DateTimeField dateTimeField, DateTimeField dateTimeField2, DurationField durationField, long l, boolean bl) {
            super(dateTimeField, dateTimeField2, l, bl);
            if (durationField == null) {
                durationField = new LinkedDurationField(this.iDurationField, this);
            }
            this.iDurationField = durationField;
        }

        public long add(long l, int n) {
            if (l >= this.iCutover) {
                if ((l = this.iGregorianField.add(l, n)) < this.iCutover && l + GJChronology.this.iGapDuration < this.iCutover) {
                    if (this.iConvertByWeekyear) {
                        int n2 = GJChronology.this.iGregorianChronology.weekyear().get(l);
                        if (n2 <= 0) {
                            l = GJChronology.this.iGregorianChronology.weekyear().add(l, -1);
                        }
                    } else {
                        int n3 = GJChronology.this.iGregorianChronology.year().get(l);
                        if (n3 <= 0) {
                            l = GJChronology.this.iGregorianChronology.year().add(l, -1);
                        }
                    }
                    l = this.gregorianToJulian(l);
                }
            } else if ((l = this.iJulianField.add(l, n)) >= this.iCutover && l - GJChronology.this.iGapDuration >= this.iCutover) {
                l = this.julianToGregorian(l);
            }
            return l;
        }

        public long add(long l, long l2) {
            if (l >= this.iCutover) {
                if ((l = this.iGregorianField.add(l, l2)) < this.iCutover && l + GJChronology.this.iGapDuration < this.iCutover) {
                    if (this.iConvertByWeekyear) {
                        int n = GJChronology.this.iGregorianChronology.weekyear().get(l);
                        if (n <= 0) {
                            l = GJChronology.this.iGregorianChronology.weekyear().add(l, -1);
                        }
                    } else {
                        int n = GJChronology.this.iGregorianChronology.year().get(l);
                        if (n <= 0) {
                            l = GJChronology.this.iGregorianChronology.year().add(l, -1);
                        }
                    }
                    l = this.gregorianToJulian(l);
                }
            } else if ((l = this.iJulianField.add(l, l2)) >= this.iCutover && l - GJChronology.this.iGapDuration >= this.iCutover) {
                l = this.julianToGregorian(l);
            }
            return l;
        }

        public int getDifference(long l, long l2) {
            if (l >= this.iCutover) {
                if (l2 >= this.iCutover) {
                    return this.iGregorianField.getDifference(l, l2);
                }
                l = this.gregorianToJulian(l);
                return this.iJulianField.getDifference(l, l2);
            }
            if (l2 < this.iCutover) {
                return this.iJulianField.getDifference(l, l2);
            }
            l = this.julianToGregorian(l);
            return this.iGregorianField.getDifference(l, l2);
        }

        public long getDifferenceAsLong(long l, long l2) {
            if (l >= this.iCutover) {
                if (l2 >= this.iCutover) {
                    return this.iGregorianField.getDifferenceAsLong(l, l2);
                }
                l = this.gregorianToJulian(l);
                return this.iJulianField.getDifferenceAsLong(l, l2);
            }
            if (l2 < this.iCutover) {
                return this.iJulianField.getDifferenceAsLong(l, l2);
            }
            l = this.julianToGregorian(l);
            return this.iGregorianField.getDifferenceAsLong(l, l2);
        }

        public int getMinimumValue(long l) {
            if (l >= this.iCutover) {
                return this.iGregorianField.getMinimumValue(l);
            }
            return this.iJulianField.getMinimumValue(l);
        }

        public int getMaximumValue(long l) {
            if (l >= this.iCutover) {
                return this.iGregorianField.getMaximumValue(l);
            }
            return this.iJulianField.getMaximumValue(l);
        }
    }

    private class CutoverField
    extends BaseDateTimeField {
        private static final long serialVersionUID = 3528501219481026402L;
        final DateTimeField iJulianField;
        final DateTimeField iGregorianField;
        final long iCutover;
        final boolean iConvertByWeekyear;
        protected DurationField iDurationField;
        protected DurationField iRangeDurationField;

        CutoverField(DateTimeField dateTimeField, DateTimeField dateTimeField2, long l) {
            this(dateTimeField, dateTimeField2, l, false);
        }

        CutoverField(DateTimeField dateTimeField, DateTimeField dateTimeField2, long l, boolean bl) {
            this(dateTimeField, dateTimeField2, null, l, bl);
        }

        CutoverField(DateTimeField dateTimeField, DateTimeField dateTimeField2, DurationField durationField, long l, boolean bl) {
            super(dateTimeField2.getType());
            this.iJulianField = dateTimeField;
            this.iGregorianField = dateTimeField2;
            this.iCutover = l;
            this.iConvertByWeekyear = bl;
            this.iDurationField = dateTimeField2.getDurationField();
            if (durationField == null && (durationField = dateTimeField2.getRangeDurationField()) == null) {
                durationField = dateTimeField.getRangeDurationField();
            }
            this.iRangeDurationField = durationField;
        }

        public boolean isLenient() {
            return false;
        }

        public int get(long l) {
            if (l >= this.iCutover) {
                return this.iGregorianField.get(l);
            }
            return this.iJulianField.get(l);
        }

        public String getAsText(long l, Locale locale) {
            if (l >= this.iCutover) {
                return this.iGregorianField.getAsText(l, locale);
            }
            return this.iJulianField.getAsText(l, locale);
        }

        public String getAsText(int n, Locale locale) {
            return this.iGregorianField.getAsText(n, locale);
        }

        public String getAsShortText(long l, Locale locale) {
            if (l >= this.iCutover) {
                return this.iGregorianField.getAsShortText(l, locale);
            }
            return this.iJulianField.getAsShortText(l, locale);
        }

        public String getAsShortText(int n, Locale locale) {
            return this.iGregorianField.getAsShortText(n, locale);
        }

        public long add(long l, int n) {
            return this.iGregorianField.add(l, n);
        }

        public long add(long l, long l2) {
            return this.iGregorianField.add(l, l2);
        }

        public int[] add(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
            if (n2 == 0) {
                return nArray;
            }
            if (DateTimeUtils.isContiguous(readablePartial)) {
                long l = 0L;
                int n3 = readablePartial.size();
                for (int i = 0; i < n3; ++i) {
                    l = readablePartial.getFieldType(i).getField(GJChronology.this).set(l, nArray[i]);
                }
                l = this.add(l, n2);
                return GJChronology.this.get(readablePartial, l);
            }
            return super.add(readablePartial, n, nArray, n2);
        }

        public int getDifference(long l, long l2) {
            return this.iGregorianField.getDifference(l, l2);
        }

        public long getDifferenceAsLong(long l, long l2) {
            return this.iGregorianField.getDifferenceAsLong(l, l2);
        }

        public long set(long l, int n) {
            if (l >= this.iCutover) {
                if ((l = this.iGregorianField.set(l, n)) < this.iCutover) {
                    if (l + GJChronology.this.iGapDuration < this.iCutover) {
                        l = this.gregorianToJulian(l);
                    }
                    if (this.get(l) != n) {
                        throw new IllegalFieldValueException(this.iGregorianField.getType(), (Number)n, null, null);
                    }
                }
            } else if ((l = this.iJulianField.set(l, n)) >= this.iCutover) {
                if (l - GJChronology.this.iGapDuration >= this.iCutover) {
                    l = this.julianToGregorian(l);
                }
                if (this.get(l) != n) {
                    throw new IllegalFieldValueException(this.iJulianField.getType(), (Number)n, null, null);
                }
            }
            return l;
        }

        public long set(long l, String string, Locale locale) {
            if (l >= this.iCutover) {
                if ((l = this.iGregorianField.set(l, string, locale)) < this.iCutover && l + GJChronology.this.iGapDuration < this.iCutover) {
                    l = this.gregorianToJulian(l);
                }
            } else if ((l = this.iJulianField.set(l, string, locale)) >= this.iCutover && l - GJChronology.this.iGapDuration >= this.iCutover) {
                l = this.julianToGregorian(l);
            }
            return l;
        }

        public DurationField getDurationField() {
            return this.iDurationField;
        }

        public DurationField getRangeDurationField() {
            return this.iRangeDurationField;
        }

        public boolean isLeap(long l) {
            if (l >= this.iCutover) {
                return this.iGregorianField.isLeap(l);
            }
            return this.iJulianField.isLeap(l);
        }

        public int getLeapAmount(long l) {
            if (l >= this.iCutover) {
                return this.iGregorianField.getLeapAmount(l);
            }
            return this.iJulianField.getLeapAmount(l);
        }

        public DurationField getLeapDurationField() {
            return this.iGregorianField.getLeapDurationField();
        }

        public int getMinimumValue() {
            return this.iJulianField.getMinimumValue();
        }

        public int getMinimumValue(ReadablePartial readablePartial) {
            return this.iJulianField.getMinimumValue(readablePartial);
        }

        public int getMinimumValue(ReadablePartial readablePartial, int[] nArray) {
            return this.iJulianField.getMinimumValue(readablePartial, nArray);
        }

        public int getMinimumValue(long l) {
            if (l < this.iCutover) {
                return this.iJulianField.getMinimumValue(l);
            }
            int n = this.iGregorianField.getMinimumValue(l);
            if ((l = this.iGregorianField.set(l, n)) < this.iCutover) {
                n = this.iGregorianField.get(this.iCutover);
            }
            return n;
        }

        public int getMaximumValue() {
            return this.iGregorianField.getMaximumValue();
        }

        public int getMaximumValue(long l) {
            if (l >= this.iCutover) {
                return this.iGregorianField.getMaximumValue(l);
            }
            int n = this.iJulianField.getMaximumValue(l);
            if ((l = this.iJulianField.set(l, n)) >= this.iCutover) {
                n = this.iJulianField.get(this.iJulianField.add(this.iCutover, -1));
            }
            return n;
        }

        public int getMaximumValue(ReadablePartial readablePartial) {
            long l = GJChronology.getInstanceUTC().set(readablePartial, 0L);
            return this.getMaximumValue(l);
        }

        public int getMaximumValue(ReadablePartial readablePartial, int[] nArray) {
            GJChronology gJChronology = GJChronology.getInstanceUTC();
            long l = 0L;
            int n = readablePartial.size();
            for (int i = 0; i < n; ++i) {
                DateTimeField dateTimeField = readablePartial.getFieldType(i).getField(gJChronology);
                if (nArray[i] > dateTimeField.getMaximumValue(l)) continue;
                l = dateTimeField.set(l, nArray[i]);
            }
            return this.getMaximumValue(l);
        }

        public long roundFloor(long l) {
            if (l >= this.iCutover) {
                if ((l = this.iGregorianField.roundFloor(l)) < this.iCutover && l + GJChronology.this.iGapDuration < this.iCutover) {
                    l = this.gregorianToJulian(l);
                }
            } else {
                l = this.iJulianField.roundFloor(l);
            }
            return l;
        }

        public long roundCeiling(long l) {
            if (l >= this.iCutover) {
                l = this.iGregorianField.roundCeiling(l);
            } else if ((l = this.iJulianField.roundCeiling(l)) >= this.iCutover && l - GJChronology.this.iGapDuration >= this.iCutover) {
                l = this.julianToGregorian(l);
            }
            return l;
        }

        public int getMaximumTextLength(Locale locale) {
            return Math.max(this.iJulianField.getMaximumTextLength(locale), this.iGregorianField.getMaximumTextLength(locale));
        }

        public int getMaximumShortTextLength(Locale locale) {
            return Math.max(this.iJulianField.getMaximumShortTextLength(locale), this.iGregorianField.getMaximumShortTextLength(locale));
        }

        protected long julianToGregorian(long l) {
            if (this.iConvertByWeekyear) {
                return GJChronology.this.julianToGregorianByWeekyear(l);
            }
            return GJChronology.this.julianToGregorianByYear(l);
        }

        protected long gregorianToJulian(long l) {
            if (this.iConvertByWeekyear) {
                return GJChronology.this.gregorianToJulianByWeekyear(l);
            }
            return GJChronology.this.gregorianToJulianByYear(l);
        }
    }
}

