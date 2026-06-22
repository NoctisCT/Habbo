/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.field.LenientDateTimeField;

public final class LenientChronology
extends AssembledChronology {
    private static final long serialVersionUID = -3148237568046877177L;
    private transient Chronology iWithUTC;

    public static LenientChronology getInstance(Chronology chronology) {
        if (chronology == null) {
            throw new IllegalArgumentException("Must supply a chronology");
        }
        return new LenientChronology(chronology);
    }

    private LenientChronology(Chronology chronology) {
        super(chronology, null);
    }

    public Chronology withUTC() {
        if (this.iWithUTC == null) {
            this.iWithUTC = this.getZone() == DateTimeZone.UTC ? this : LenientChronology.getInstance(this.getBase().withUTC());
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
        return LenientChronology.getInstance(this.getBase().withZone(dateTimeZone));
    }

    protected void assemble(AssembledChronology.Fields fields) {
        fields.year = this.convertField(fields.year);
        fields.yearOfEra = this.convertField(fields.yearOfEra);
        fields.yearOfCentury = this.convertField(fields.yearOfCentury);
        fields.centuryOfEra = this.convertField(fields.centuryOfEra);
        fields.era = this.convertField(fields.era);
        fields.dayOfWeek = this.convertField(fields.dayOfWeek);
        fields.dayOfMonth = this.convertField(fields.dayOfMonth);
        fields.dayOfYear = this.convertField(fields.dayOfYear);
        fields.monthOfYear = this.convertField(fields.monthOfYear);
        fields.weekOfWeekyear = this.convertField(fields.weekOfWeekyear);
        fields.weekyear = this.convertField(fields.weekyear);
        fields.weekyearOfCentury = this.convertField(fields.weekyearOfCentury);
        fields.millisOfSecond = this.convertField(fields.millisOfSecond);
        fields.millisOfDay = this.convertField(fields.millisOfDay);
        fields.secondOfMinute = this.convertField(fields.secondOfMinute);
        fields.secondOfDay = this.convertField(fields.secondOfDay);
        fields.minuteOfHour = this.convertField(fields.minuteOfHour);
        fields.minuteOfDay = this.convertField(fields.minuteOfDay);
        fields.hourOfDay = this.convertField(fields.hourOfDay);
        fields.hourOfHalfday = this.convertField(fields.hourOfHalfday);
        fields.clockhourOfDay = this.convertField(fields.clockhourOfDay);
        fields.clockhourOfHalfday = this.convertField(fields.clockhourOfHalfday);
        fields.halfdayOfDay = this.convertField(fields.halfdayOfDay);
    }

    private final DateTimeField convertField(DateTimeField dateTimeField) {
        return LenientDateTimeField.getInstance(dateTimeField, this.getBase());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LenientChronology)) {
            return false;
        }
        LenientChronology lenientChronology = (LenientChronology)object;
        return this.getBase().equals(lenientChronology.getBase());
    }

    public int hashCode() {
        return 236548278 + this.getBase().hashCode() * 7;
    }

    public String toString() {
        return "LenientChronology[" + this.getBase().toString() + ']';
    }
}

