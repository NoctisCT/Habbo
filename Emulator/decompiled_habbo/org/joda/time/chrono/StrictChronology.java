/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.field.StrictDateTimeField;

public final class StrictChronology
extends AssembledChronology {
    private static final long serialVersionUID = 6633006628097111960L;
    private transient Chronology iWithUTC;

    public static StrictChronology getInstance(Chronology chronology) {
        if (chronology == null) {
            throw new IllegalArgumentException("Must supply a chronology");
        }
        return new StrictChronology(chronology);
    }

    private StrictChronology(Chronology chronology) {
        super(chronology, null);
    }

    public Chronology withUTC() {
        if (this.iWithUTC == null) {
            this.iWithUTC = this.getZone() == DateTimeZone.UTC ? this : StrictChronology.getInstance(this.getBase().withUTC());
        }
        return this.iWithUTC;
    }

    public Chronology withZone(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if (dateTimeZone == DateTimeZone.UTC) {
            return this.withUTC();
        }
        if (dateTimeZone == this.getZone()) {
            return this;
        }
        return StrictChronology.getInstance(this.getBase().withZone(dateTimeZone));
    }

    protected void assemble(AssembledChronology.Fields fields) {
        fields.year = StrictChronology.convertField(fields.year);
        fields.yearOfEra = StrictChronology.convertField(fields.yearOfEra);
        fields.yearOfCentury = StrictChronology.convertField(fields.yearOfCentury);
        fields.centuryOfEra = StrictChronology.convertField(fields.centuryOfEra);
        fields.era = StrictChronology.convertField(fields.era);
        fields.dayOfWeek = StrictChronology.convertField(fields.dayOfWeek);
        fields.dayOfMonth = StrictChronology.convertField(fields.dayOfMonth);
        fields.dayOfYear = StrictChronology.convertField(fields.dayOfYear);
        fields.monthOfYear = StrictChronology.convertField(fields.monthOfYear);
        fields.weekOfWeekyear = StrictChronology.convertField(fields.weekOfWeekyear);
        fields.weekyear = StrictChronology.convertField(fields.weekyear);
        fields.weekyearOfCentury = StrictChronology.convertField(fields.weekyearOfCentury);
        fields.millisOfSecond = StrictChronology.convertField(fields.millisOfSecond);
        fields.millisOfDay = StrictChronology.convertField(fields.millisOfDay);
        fields.secondOfMinute = StrictChronology.convertField(fields.secondOfMinute);
        fields.secondOfDay = StrictChronology.convertField(fields.secondOfDay);
        fields.minuteOfHour = StrictChronology.convertField(fields.minuteOfHour);
        fields.minuteOfDay = StrictChronology.convertField(fields.minuteOfDay);
        fields.hourOfDay = StrictChronology.convertField(fields.hourOfDay);
        fields.hourOfHalfday = StrictChronology.convertField(fields.hourOfHalfday);
        fields.clockhourOfDay = StrictChronology.convertField(fields.clockhourOfDay);
        fields.clockhourOfHalfday = StrictChronology.convertField(fields.clockhourOfHalfday);
        fields.halfdayOfDay = StrictChronology.convertField(fields.halfdayOfDay);
    }

    private static final DateTimeField convertField(DateTimeField dateTimeField) {
        return StrictDateTimeField.getInstance(dateTimeField);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof StrictChronology)) {
            return false;
        }
        StrictChronology strictChronology = (StrictChronology)object;
        return this.getBase().equals(strictChronology.getBase());
    }

    public int hashCode() {
        return 352831696 + this.getBase().hashCode() * 7;
    }

    public String toString() {
        return "StrictChronology[" + this.getBase().toString() + ']';
    }
}

