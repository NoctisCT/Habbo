/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.base.BaseDateTime;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.DecoratedDurationField;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class LimitChronology
extends AssembledChronology {
    private static final long serialVersionUID = 7670866536893052522L;
    final DateTime iLowerLimit;
    final DateTime iUpperLimit;
    private transient LimitChronology iWithUTC;

    public static LimitChronology getInstance(Chronology chronology, ReadableDateTime readableDateTime, ReadableDateTime readableDateTime2) {
        if (chronology == null) {
            throw new IllegalArgumentException("Must supply a chronology");
        }
        readableDateTime = readableDateTime == null ? null : readableDateTime.toDateTime();
        ReadableDateTime readableDateTime3 = readableDateTime2 = readableDateTime2 == null ? null : readableDateTime2.toDateTime();
        if (readableDateTime != null && readableDateTime2 != null && !readableDateTime.isBefore(readableDateTime2)) {
            throw new IllegalArgumentException("The lower limit must be come before than the upper limit");
        }
        return new LimitChronology(chronology, (DateTime)readableDateTime, (DateTime)readableDateTime2);
    }

    private LimitChronology(Chronology chronology, DateTime dateTime, DateTime dateTime2) {
        super(chronology, null);
        this.iLowerLimit = dateTime;
        this.iUpperLimit = dateTime2;
    }

    public DateTime getLowerLimit() {
        return this.iLowerLimit;
    }

    public DateTime getUpperLimit() {
        return this.iUpperLimit;
    }

    @Override
    public Chronology withUTC() {
        return this.withZone(DateTimeZone.UTC);
    }

    @Override
    public Chronology withZone(DateTimeZone dateTimeZone) {
        Serializable serializable;
        BaseDateTime baseDateTime;
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if (dateTimeZone == this.getZone()) {
            return this;
        }
        if (dateTimeZone == DateTimeZone.UTC && this.iWithUTC != null) {
            return this.iWithUTC;
        }
        DateTime dateTime = this.iLowerLimit;
        if (dateTime != null) {
            baseDateTime = dateTime.toMutableDateTime();
            ((MutableDateTime)baseDateTime).setZoneRetainFields(dateTimeZone);
            dateTime = baseDateTime.toDateTime();
        }
        if ((baseDateTime = this.iUpperLimit) != null) {
            serializable = baseDateTime.toMutableDateTime();
            serializable.setZoneRetainFields(dateTimeZone);
            baseDateTime = serializable.toDateTime();
        }
        serializable = LimitChronology.getInstance(this.getBase().withZone(dateTimeZone), dateTime, baseDateTime);
        if (dateTimeZone == DateTimeZone.UTC) {
            this.iWithUTC = serializable;
        }
        return serializable;
    }

    @Override
    public long getDateTimeMillis(int n, int n2, int n3, int n4) throws IllegalArgumentException {
        long l = this.getBase().getDateTimeMillis(n, n2, n3, n4);
        this.checkLimits(l, "resulting");
        return l;
    }

    @Override
    public long getDateTimeMillis(int n, int n2, int n3, int n4, int n5, int n6, int n7) throws IllegalArgumentException {
        long l = this.getBase().getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
        this.checkLimits(l, "resulting");
        return l;
    }

    @Override
    public long getDateTimeMillis(long l, int n, int n2, int n3, int n4) throws IllegalArgumentException {
        this.checkLimits(l, null);
        l = this.getBase().getDateTimeMillis(l, n, n2, n3, n4);
        this.checkLimits(l, "resulting");
        return l;
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
        LimitDurationField limitDurationField = new LimitDurationField(durationField);
        hashMap.put(durationField, limitDurationField);
        return limitDurationField;
    }

    private DateTimeField convertField(DateTimeField dateTimeField, HashMap<Object, Object> hashMap) {
        if (dateTimeField == null || !dateTimeField.isSupported()) {
            return dateTimeField;
        }
        if (hashMap.containsKey(dateTimeField)) {
            return (DateTimeField)hashMap.get(dateTimeField);
        }
        LimitDateTimeField limitDateTimeField = new LimitDateTimeField(dateTimeField, this.convertField(dateTimeField.getDurationField(), hashMap), this.convertField(dateTimeField.getRangeDurationField(), hashMap), this.convertField(dateTimeField.getLeapDurationField(), hashMap));
        hashMap.put(dateTimeField, limitDateTimeField);
        return limitDateTimeField;
    }

    void checkLimits(long l, String string) {
        DateTime dateTime = this.iLowerLimit;
        if (dateTime != null && l < dateTime.getMillis()) {
            throw new LimitException(string, true);
        }
        dateTime = this.iUpperLimit;
        if (dateTime != null && l >= dateTime.getMillis()) {
            throw new LimitException(string, false);
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LimitChronology)) {
            return false;
        }
        LimitChronology limitChronology = (LimitChronology)object;
        return this.getBase().equals(limitChronology.getBase()) && FieldUtils.equals(this.getLowerLimit(), limitChronology.getLowerLimit()) && FieldUtils.equals(this.getUpperLimit(), limitChronology.getUpperLimit());
    }

    public int hashCode() {
        int n = 317351877;
        n += this.getLowerLimit() != null ? this.getLowerLimit().hashCode() : 0;
        n += this.getUpperLimit() != null ? this.getUpperLimit().hashCode() : 0;
        return n += this.getBase().hashCode() * 7;
    }

    @Override
    public String toString() {
        return "LimitChronology[" + this.getBase().toString() + ", " + (this.getLowerLimit() == null ? "NoLimit" : this.getLowerLimit().toString()) + ", " + (this.getUpperLimit() == null ? "NoLimit" : this.getUpperLimit().toString()) + ']';
    }

    private class LimitDateTimeField
    extends DecoratedDateTimeField {
        private static final long serialVersionUID = -2435306746995699312L;
        private final DurationField iDurationField;
        private final DurationField iRangeDurationField;
        private final DurationField iLeapDurationField;

        LimitDateTimeField(DateTimeField dateTimeField, DurationField durationField, DurationField durationField2, DurationField durationField3) {
            super(dateTimeField, dateTimeField.getType());
            this.iDurationField = durationField;
            this.iRangeDurationField = durationField2;
            this.iLeapDurationField = durationField3;
        }

        public int get(long l) {
            LimitChronology.this.checkLimits(l, null);
            return this.getWrappedField().get(l);
        }

        public String getAsText(long l, Locale locale) {
            LimitChronology.this.checkLimits(l, null);
            return this.getWrappedField().getAsText(l, locale);
        }

        public String getAsShortText(long l, Locale locale) {
            LimitChronology.this.checkLimits(l, null);
            return this.getWrappedField().getAsShortText(l, locale);
        }

        public long add(long l, int n) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().add(l, n);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public long add(long l, long l2) {
            LimitChronology.this.checkLimits(l, null);
            long l3 = this.getWrappedField().add(l, l2);
            LimitChronology.this.checkLimits(l3, "resulting");
            return l3;
        }

        public long addWrapField(long l, int n) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().addWrapField(l, n);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public int getDifference(long l, long l2) {
            LimitChronology.this.checkLimits(l, "minuend");
            LimitChronology.this.checkLimits(l2, "subtrahend");
            return this.getWrappedField().getDifference(l, l2);
        }

        public long getDifferenceAsLong(long l, long l2) {
            LimitChronology.this.checkLimits(l, "minuend");
            LimitChronology.this.checkLimits(l2, "subtrahend");
            return this.getWrappedField().getDifferenceAsLong(l, l2);
        }

        public long set(long l, int n) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().set(l, n);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public long set(long l, String string, Locale locale) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().set(l, string, locale);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public final DurationField getDurationField() {
            return this.iDurationField;
        }

        public final DurationField getRangeDurationField() {
            return this.iRangeDurationField;
        }

        public boolean isLeap(long l) {
            LimitChronology.this.checkLimits(l, null);
            return this.getWrappedField().isLeap(l);
        }

        public int getLeapAmount(long l) {
            LimitChronology.this.checkLimits(l, null);
            return this.getWrappedField().getLeapAmount(l);
        }

        public final DurationField getLeapDurationField() {
            return this.iLeapDurationField;
        }

        public long roundFloor(long l) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().roundFloor(l);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public long roundCeiling(long l) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().roundCeiling(l);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public long roundHalfFloor(long l) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().roundHalfFloor(l);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public long roundHalfCeiling(long l) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().roundHalfCeiling(l);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public long roundHalfEven(long l) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().roundHalfEven(l);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public long remainder(long l) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().remainder(l);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public int getMinimumValue(long l) {
            LimitChronology.this.checkLimits(l, null);
            return this.getWrappedField().getMinimumValue(l);
        }

        public int getMaximumValue(long l) {
            LimitChronology.this.checkLimits(l, null);
            return this.getWrappedField().getMaximumValue(l);
        }

        public int getMaximumTextLength(Locale locale) {
            return this.getWrappedField().getMaximumTextLength(locale);
        }

        public int getMaximumShortTextLength(Locale locale) {
            return this.getWrappedField().getMaximumShortTextLength(locale);
        }
    }

    private class LimitDurationField
    extends DecoratedDurationField {
        private static final long serialVersionUID = 8049297699408782284L;

        LimitDurationField(DurationField durationField) {
            super(durationField, durationField.getType());
        }

        public int getValue(long l, long l2) {
            LimitChronology.this.checkLimits(l2, null);
            return this.getWrappedField().getValue(l, l2);
        }

        public long getValueAsLong(long l, long l2) {
            LimitChronology.this.checkLimits(l2, null);
            return this.getWrappedField().getValueAsLong(l, l2);
        }

        public long getMillis(int n, long l) {
            LimitChronology.this.checkLimits(l, null);
            return this.getWrappedField().getMillis(n, l);
        }

        public long getMillis(long l, long l2) {
            LimitChronology.this.checkLimits(l2, null);
            return this.getWrappedField().getMillis(l, l2);
        }

        public long add(long l, int n) {
            LimitChronology.this.checkLimits(l, null);
            long l2 = this.getWrappedField().add(l, n);
            LimitChronology.this.checkLimits(l2, "resulting");
            return l2;
        }

        public long add(long l, long l2) {
            LimitChronology.this.checkLimits(l, null);
            long l3 = this.getWrappedField().add(l, l2);
            LimitChronology.this.checkLimits(l3, "resulting");
            return l3;
        }

        public int getDifference(long l, long l2) {
            LimitChronology.this.checkLimits(l, "minuend");
            LimitChronology.this.checkLimits(l2, "subtrahend");
            return this.getWrappedField().getDifference(l, l2);
        }

        public long getDifferenceAsLong(long l, long l2) {
            LimitChronology.this.checkLimits(l, "minuend");
            LimitChronology.this.checkLimits(l2, "subtrahend");
            return this.getWrappedField().getDifferenceAsLong(l, l2);
        }
    }

    private class LimitException
    extends IllegalArgumentException {
        private static final long serialVersionUID = -5924689995607498581L;
        private final boolean iIsLow;

        LimitException(String string, boolean bl) {
            super(string);
            this.iIsLow = bl;
        }

        public String getMessage() {
            StringBuffer stringBuffer = new StringBuffer(85);
            stringBuffer.append("The");
            String string = super.getMessage();
            if (string != null) {
                stringBuffer.append(' ');
                stringBuffer.append(string);
            }
            stringBuffer.append(" instant is ");
            DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
            dateTimeFormatter = dateTimeFormatter.withChronology(LimitChronology.this.getBase());
            if (this.iIsLow) {
                stringBuffer.append("below the supported minimum of ");
                dateTimeFormatter.printTo(stringBuffer, LimitChronology.this.getLowerLimit().getMillis());
            } else {
                stringBuffer.append("above the supported maximum of ");
                dateTimeFormatter.printTo(stringBuffer, LimitChronology.this.getUpperLimit().getMillis());
            }
            stringBuffer.append(" (");
            stringBuffer.append(LimitChronology.this.getBase());
            stringBuffer.append(')');
            return stringBuffer.toString();
        }

        public String toString() {
            return "IllegalArgumentException: " + this.getMessage();
        }
    }
}

