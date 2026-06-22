/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.HashMap;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.IllegalInstantException;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.field.BaseDateTimeField;
import org.joda.time.field.BaseDurationField;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ZonedChronology
extends AssembledChronology {
    private static final long serialVersionUID = -1079258847191166848L;
    private static final long NEAR_ZERO = 604800000L;

    public static ZonedChronology getInstance(Chronology chronology, DateTimeZone dateTimeZone) {
        if (chronology == null) {
            throw new IllegalArgumentException("Must supply a chronology");
        }
        if ((chronology = chronology.withUTC()) == null) {
            throw new IllegalArgumentException("UTC chronology must not be null");
        }
        if (dateTimeZone == null) {
            throw new IllegalArgumentException("DateTimeZone must not be null");
        }
        return new ZonedChronology(chronology, dateTimeZone);
    }

    static boolean useTimeArithmetic(DurationField durationField) {
        return durationField != null && durationField.getUnitMillis() < 43200000L;
    }

    private ZonedChronology(Chronology chronology, DateTimeZone dateTimeZone) {
        super(chronology, dateTimeZone);
    }

    @Override
    public DateTimeZone getZone() {
        return (DateTimeZone)this.getParam();
    }

    @Override
    public Chronology withUTC() {
        return this.getBase();
    }

    @Override
    public Chronology withZone(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if (dateTimeZone == this.getParam()) {
            return this;
        }
        if (dateTimeZone == DateTimeZone.UTC) {
            return this.getBase();
        }
        return new ZonedChronology(this.getBase(), dateTimeZone);
    }

    @Override
    public long getDateTimeMillis(int n, int n2, int n3, int n4) throws IllegalArgumentException {
        return this.localToUTC(this.getBase().getDateTimeMillis(n, n2, n3, n4));
    }

    @Override
    public long getDateTimeMillis(int n, int n2, int n3, int n4, int n5, int n6, int n7) throws IllegalArgumentException {
        return this.localToUTC(this.getBase().getDateTimeMillis(n, n2, n3, n4, n5, n6, n7));
    }

    @Override
    public long getDateTimeMillis(long l, int n, int n2, int n3, int n4) throws IllegalArgumentException {
        return this.localToUTC(this.getBase().getDateTimeMillis(l + (long)this.getZone().getOffset(l), n, n2, n3, n4));
    }

    private long localToUTC(long l) {
        if (l == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        if (l == Long.MIN_VALUE) {
            return Long.MIN_VALUE;
        }
        DateTimeZone dateTimeZone = this.getZone();
        int n = dateTimeZone.getOffsetFromLocal(l);
        long l2 = l - (long)n;
        if (l > 604800000L && l2 < 0L) {
            return Long.MAX_VALUE;
        }
        if (l < -604800000L && l2 > 0L) {
            return Long.MIN_VALUE;
        }
        int n2 = dateTimeZone.getOffset(l2);
        if (n != n2) {
            throw new IllegalInstantException(l, dateTimeZone.getID());
        }
        return l2;
    }

    @Override
    protected void assemble(AssembledChronology.Fields fields) {
        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
        fields.eras = this.convertField(fields.eras, hashMap);
        fields.centuries = this.convertField(fields.centuries, hashMap);
        fields.years = this.convertField(fields.years, hashMap);
        fields.months = this.convertField(fields.months, hashMap);
        fields.weekyears = this.convertField(fields.weekyears, hashMap);
        fields.weeks = this.convertField(fields.weeks, hashMap);
        fields.days = this.convertField(fields.days, hashMap);
        fields.halfdays = this.convertField(fields.halfdays, hashMap);
        fields.hours = this.convertField(fields.hours, hashMap);
        fields.minutes = this.convertField(fields.minutes, hashMap);
        fields.seconds = this.convertField(fields.seconds, hashMap);
        fields.millis = this.convertField(fields.millis, hashMap);
        fields.year = this.convertField(fields.year, hashMap);
        fields.yearOfEra = this.convertField(fields.yearOfEra, hashMap);
        fields.yearOfCentury = this.convertField(fields.yearOfCentury, hashMap);
        fields.centuryOfEra = this.convertField(fields.centuryOfEra, hashMap);
        fields.era = this.convertField(fields.era, hashMap);
        fields.dayOfWeek = this.convertField(fields.dayOfWeek, hashMap);
        fields.dayOfMonth = this.convertField(fields.dayOfMonth, hashMap);
        fields.dayOfYear = this.convertField(fields.dayOfYear, hashMap);
        fields.monthOfYear = this.convertField(fields.monthOfYear, hashMap);
        fields.weekOfWeekyear = this.convertField(fields.weekOfWeekyear, hashMap);
        fields.weekyear = this.convertField(fields.weekyear, hashMap);
        fields.weekyearOfCentury = this.convertField(fields.weekyearOfCentury, hashMap);
        fields.millisOfSecond = this.convertField(fields.millisOfSecond, hashMap);
        fields.millisOfDay = this.convertField(fields.millisOfDay, hashMap);
        fields.secondOfMinute = this.convertField(fields.secondOfMinute, hashMap);
        fields.secondOfDay = this.convertField(fields.secondOfDay, hashMap);
        fields.minuteOfHour = this.convertField(fields.minuteOfHour, hashMap);
        fields.minuteOfDay = this.convertField(fields.minuteOfDay, hashMap);
        fields.hourOfDay = this.convertField(fields.hourOfDay, hashMap);
        fields.hourOfHalfday = this.convertField(fields.hourOfHalfday, hashMap);
        fields.clockhourOfDay = this.convertField(fields.clockhourOfDay, hashMap);
        fields.clockhourOfHalfday = this.convertField(fields.clockhourOfHalfday, hashMap);
        fields.halfdayOfDay = this.convertField(fields.halfdayOfDay, hashMap);
    }

    private DurationField convertField(DurationField durationField, HashMap<Object, Object> hashMap) {
        if (durationField == null || !durationField.isSupported()) {
            return durationField;
        }
        if (hashMap.containsKey(durationField)) {
            return (DurationField)hashMap.get(durationField);
        }
        ZonedDurationField zonedDurationField = new ZonedDurationField(durationField, this.getZone());
        hashMap.put(durationField, zonedDurationField);
        return zonedDurationField;
    }

    private DateTimeField convertField(DateTimeField dateTimeField, HashMap<Object, Object> hashMap) {
        if (dateTimeField == null || !dateTimeField.isSupported()) {
            return dateTimeField;
        }
        if (hashMap.containsKey(dateTimeField)) {
            return (DateTimeField)hashMap.get(dateTimeField);
        }
        ZonedDateTimeField zonedDateTimeField = new ZonedDateTimeField(dateTimeField, this.getZone(), this.convertField(dateTimeField.getDurationField(), hashMap), this.convertField(dateTimeField.getRangeDurationField(), hashMap), this.convertField(dateTimeField.getLeapDurationField(), hashMap));
        hashMap.put(dateTimeField, zonedDateTimeField);
        return zonedDateTimeField;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ZonedChronology)) {
            return false;
        }
        ZonedChronology zonedChronology = (ZonedChronology)object;
        return this.getBase().equals(zonedChronology.getBase()) && this.getZone().equals(zonedChronology.getZone());
    }

    public int hashCode() {
        return 326565 + this.getZone().hashCode() * 11 + this.getBase().hashCode() * 7;
    }

    @Override
    public String toString() {
        return "ZonedChronology[" + this.getBase() + ", " + this.getZone().getID() + ']';
    }

    static final class ZonedDateTimeField
    extends BaseDateTimeField {
        private static final long serialVersionUID = -3968986277775529794L;
        final DateTimeField iField;
        final DateTimeZone iZone;
        final DurationField iDurationField;
        final boolean iTimeField;
        final DurationField iRangeDurationField;
        final DurationField iLeapDurationField;

        ZonedDateTimeField(DateTimeField dateTimeField, DateTimeZone dateTimeZone, DurationField durationField, DurationField durationField2, DurationField durationField3) {
            super(dateTimeField.getType());
            if (!dateTimeField.isSupported()) {
                throw new IllegalArgumentException();
            }
            this.iField = dateTimeField;
            this.iZone = dateTimeZone;
            this.iDurationField = durationField;
            this.iTimeField = ZonedChronology.useTimeArithmetic(durationField);
            this.iRangeDurationField = durationField2;
            this.iLeapDurationField = durationField3;
        }

        public boolean isLenient() {
            return this.iField.isLenient();
        }

        public int get(long l) {
            long l2 = this.iZone.convertUTCToLocal(l);
            return this.iField.get(l2);
        }

        public String getAsText(long l, Locale locale) {
            long l2 = this.iZone.convertUTCToLocal(l);
            return this.iField.getAsText(l2, locale);
        }

        public String getAsShortText(long l, Locale locale) {
            long l2 = this.iZone.convertUTCToLocal(l);
            return this.iField.getAsShortText(l2, locale);
        }

        public String getAsText(int n, Locale locale) {
            return this.iField.getAsText(n, locale);
        }

        public String getAsShortText(int n, Locale locale) {
            return this.iField.getAsShortText(n, locale);
        }

        public long add(long l, int n) {
            if (this.iTimeField) {
                int n2 = this.getOffsetToAdd(l);
                long l2 = this.iField.add(l + (long)n2, n);
                return l2 - (long)n2;
            }
            long l3 = this.iZone.convertUTCToLocal(l);
            l3 = this.iField.add(l3, n);
            return this.iZone.convertLocalToUTC(l3, false, l);
        }

        public long add(long l, long l2) {
            if (this.iTimeField) {
                int n = this.getOffsetToAdd(l);
                long l3 = this.iField.add(l + (long)n, l2);
                return l3 - (long)n;
            }
            long l4 = this.iZone.convertUTCToLocal(l);
            l4 = this.iField.add(l4, l2);
            return this.iZone.convertLocalToUTC(l4, false, l);
        }

        public long addWrapField(long l, int n) {
            if (this.iTimeField) {
                int n2 = this.getOffsetToAdd(l);
                long l2 = this.iField.addWrapField(l + (long)n2, n);
                return l2 - (long)n2;
            }
            long l3 = this.iZone.convertUTCToLocal(l);
            l3 = this.iField.addWrapField(l3, n);
            return this.iZone.convertLocalToUTC(l3, false, l);
        }

        public long set(long l, int n) {
            long l2 = this.iZone.convertUTCToLocal(l);
            long l3 = this.iZone.convertLocalToUTC(l2 = this.iField.set(l2, n), false, l);
            if (this.get(l3) != n) {
                IllegalInstantException illegalInstantException = new IllegalInstantException(l2, this.iZone.getID());
                IllegalFieldValueException illegalFieldValueException = new IllegalFieldValueException(this.iField.getType(), n, illegalInstantException.getMessage());
                illegalFieldValueException.initCause(illegalInstantException);
                throw illegalFieldValueException;
            }
            return l3;
        }

        public long set(long l, String string, Locale locale) {
            long l2 = this.iZone.convertUTCToLocal(l);
            l2 = this.iField.set(l2, string, locale);
            return this.iZone.convertLocalToUTC(l2, false, l);
        }

        public int getDifference(long l, long l2) {
            int n = this.getOffsetToAdd(l2);
            return this.iField.getDifference(l + (long)(this.iTimeField ? n : this.getOffsetToAdd(l)), l2 + (long)n);
        }

        public long getDifferenceAsLong(long l, long l2) {
            int n = this.getOffsetToAdd(l2);
            return this.iField.getDifferenceAsLong(l + (long)(this.iTimeField ? n : this.getOffsetToAdd(l)), l2 + (long)n);
        }

        public final DurationField getDurationField() {
            return this.iDurationField;
        }

        public final DurationField getRangeDurationField() {
            return this.iRangeDurationField;
        }

        public boolean isLeap(long l) {
            long l2 = this.iZone.convertUTCToLocal(l);
            return this.iField.isLeap(l2);
        }

        public int getLeapAmount(long l) {
            long l2 = this.iZone.convertUTCToLocal(l);
            return this.iField.getLeapAmount(l2);
        }

        public final DurationField getLeapDurationField() {
            return this.iLeapDurationField;
        }

        public long roundFloor(long l) {
            if (this.iTimeField) {
                int n = this.getOffsetToAdd(l);
                l = this.iField.roundFloor(l + (long)n);
                return l - (long)n;
            }
            long l2 = this.iZone.convertUTCToLocal(l);
            l2 = this.iField.roundFloor(l2);
            return this.iZone.convertLocalToUTC(l2, false, l);
        }

        public long roundCeiling(long l) {
            if (this.iTimeField) {
                int n = this.getOffsetToAdd(l);
                l = this.iField.roundCeiling(l + (long)n);
                return l - (long)n;
            }
            long l2 = this.iZone.convertUTCToLocal(l);
            l2 = this.iField.roundCeiling(l2);
            return this.iZone.convertLocalToUTC(l2, false, l);
        }

        public long remainder(long l) {
            long l2 = this.iZone.convertUTCToLocal(l);
            return this.iField.remainder(l2);
        }

        public int getMinimumValue() {
            return this.iField.getMinimumValue();
        }

        public int getMinimumValue(long l) {
            long l2 = this.iZone.convertUTCToLocal(l);
            return this.iField.getMinimumValue(l2);
        }

        public int getMinimumValue(ReadablePartial readablePartial) {
            return this.iField.getMinimumValue(readablePartial);
        }

        public int getMinimumValue(ReadablePartial readablePartial, int[] nArray) {
            return this.iField.getMinimumValue(readablePartial, nArray);
        }

        public int getMaximumValue() {
            return this.iField.getMaximumValue();
        }

        public int getMaximumValue(long l) {
            long l2 = this.iZone.convertUTCToLocal(l);
            return this.iField.getMaximumValue(l2);
        }

        public int getMaximumValue(ReadablePartial readablePartial) {
            return this.iField.getMaximumValue(readablePartial);
        }

        public int getMaximumValue(ReadablePartial readablePartial, int[] nArray) {
            return this.iField.getMaximumValue(readablePartial, nArray);
        }

        public int getMaximumTextLength(Locale locale) {
            return this.iField.getMaximumTextLength(locale);
        }

        public int getMaximumShortTextLength(Locale locale) {
            return this.iField.getMaximumShortTextLength(locale);
        }

        private int getOffsetToAdd(long l) {
            int n = this.iZone.getOffset(l);
            long l2 = l + (long)n;
            if ((l ^ l2) < 0L && (l ^ (long)n) >= 0L) {
                throw new ArithmeticException("Adding time zone offset caused overflow");
            }
            return n;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof ZonedDateTimeField) {
                ZonedDateTimeField zonedDateTimeField = (ZonedDateTimeField)object;
                return this.iField.equals(zonedDateTimeField.iField) && this.iZone.equals(zonedDateTimeField.iZone) && this.iDurationField.equals(zonedDateTimeField.iDurationField) && this.iRangeDurationField.equals(zonedDateTimeField.iRangeDurationField);
            }
            return false;
        }

        public int hashCode() {
            return this.iField.hashCode() ^ this.iZone.hashCode();
        }
    }

    static class ZonedDurationField
    extends BaseDurationField {
        private static final long serialVersionUID = -485345310999208286L;
        final DurationField iField;
        final boolean iTimeField;
        final DateTimeZone iZone;

        ZonedDurationField(DurationField durationField, DateTimeZone dateTimeZone) {
            super(durationField.getType());
            if (!durationField.isSupported()) {
                throw new IllegalArgumentException();
            }
            this.iField = durationField;
            this.iTimeField = ZonedChronology.useTimeArithmetic(durationField);
            this.iZone = dateTimeZone;
        }

        public boolean isPrecise() {
            return this.iTimeField ? this.iField.isPrecise() : this.iField.isPrecise() && this.iZone.isFixed();
        }

        public long getUnitMillis() {
            return this.iField.getUnitMillis();
        }

        public int getValue(long l, long l2) {
            return this.iField.getValue(l, this.addOffset(l2));
        }

        public long getValueAsLong(long l, long l2) {
            return this.iField.getValueAsLong(l, this.addOffset(l2));
        }

        public long getMillis(int n, long l) {
            return this.iField.getMillis(n, this.addOffset(l));
        }

        public long getMillis(long l, long l2) {
            return this.iField.getMillis(l, this.addOffset(l2));
        }

        public long add(long l, int n) {
            int n2 = this.getOffsetToAdd(l);
            l = this.iField.add(l + (long)n2, n);
            return l - (long)(this.iTimeField ? n2 : this.getOffsetFromLocalToSubtract(l));
        }

        public long add(long l, long l2) {
            int n = this.getOffsetToAdd(l);
            l = this.iField.add(l + (long)n, l2);
            return l - (long)(this.iTimeField ? n : this.getOffsetFromLocalToSubtract(l));
        }

        public int getDifference(long l, long l2) {
            int n = this.getOffsetToAdd(l2);
            return this.iField.getDifference(l + (long)(this.iTimeField ? n : this.getOffsetToAdd(l)), l2 + (long)n);
        }

        public long getDifferenceAsLong(long l, long l2) {
            int n = this.getOffsetToAdd(l2);
            return this.iField.getDifferenceAsLong(l + (long)(this.iTimeField ? n : this.getOffsetToAdd(l)), l2 + (long)n);
        }

        private int getOffsetToAdd(long l) {
            int n = this.iZone.getOffset(l);
            long l2 = l + (long)n;
            if ((l ^ l2) < 0L && (l ^ (long)n) >= 0L) {
                throw new ArithmeticException("Adding time zone offset caused overflow");
            }
            return n;
        }

        private int getOffsetFromLocalToSubtract(long l) {
            int n = this.iZone.getOffsetFromLocal(l);
            long l2 = l - (long)n;
            if ((l ^ l2) < 0L && (l ^ (long)n) < 0L) {
                throw new ArithmeticException("Subtracting time zone offset caused overflow");
            }
            return n;
        }

        private long addOffset(long l) {
            return this.iZone.convertUTCToLocal(l);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof ZonedDurationField) {
                ZonedDurationField zonedDurationField = (ZonedDurationField)object;
                return this.iField.equals(zonedDurationField.iField) && this.iZone.equals(zonedDurationField.iZone);
            }
            return false;
        }

        public int hashCode() {
            return this.iField.hashCode() ^ this.iZone.hashCode();
        }
    }
}

