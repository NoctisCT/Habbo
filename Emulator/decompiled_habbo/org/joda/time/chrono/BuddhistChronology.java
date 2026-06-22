/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.BasicSingleEraDateTimeField;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.LimitChronology;
import org.joda.time.field.DelegatedDateTimeField;
import org.joda.time.field.DividedDateTimeField;
import org.joda.time.field.OffsetDateTimeField;
import org.joda.time.field.RemainderDateTimeField;
import org.joda.time.field.SkipUndoDateTimeField;
import org.joda.time.field.UnsupportedDurationField;

public final class BuddhistChronology
extends AssembledChronology {
    private static final long serialVersionUID = -3474595157769370126L;
    public static final int BE = 1;
    private static final DateTimeField ERA_FIELD = new BasicSingleEraDateTimeField("BE");
    private static final int BUDDHIST_OFFSET = 543;
    private static final ConcurrentHashMap<DateTimeZone, BuddhistChronology> cCache = new ConcurrentHashMap();
    private static final BuddhistChronology INSTANCE_UTC = BuddhistChronology.getInstance(DateTimeZone.UTC);

    public static BuddhistChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }

    public static BuddhistChronology getInstance() {
        return BuddhistChronology.getInstance(DateTimeZone.getDefault());
    }

    public static BuddhistChronology getInstance(DateTimeZone dateTimeZone) {
        BuddhistChronology buddhistChronology;
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if ((buddhistChronology = cCache.get(dateTimeZone)) == null) {
            buddhistChronology = new BuddhistChronology(GJChronology.getInstance(dateTimeZone, null), null);
            DateTime dateTime = new DateTime(1, 1, 1, 0, 0, 0, 0, buddhistChronology);
            BuddhistChronology buddhistChronology2 = cCache.putIfAbsent(dateTimeZone, buddhistChronology = new BuddhistChronology(LimitChronology.getInstance(buddhistChronology, dateTime, null), ""));
            if (buddhistChronology2 != null) {
                buddhistChronology = buddhistChronology2;
            }
        }
        return buddhistChronology;
    }

    private BuddhistChronology(Chronology chronology, Object object) {
        super(chronology, object);
    }

    private Object readResolve() {
        Chronology chronology = this.getBase();
        return chronology == null ? BuddhistChronology.getInstanceUTC() : BuddhistChronology.getInstance(chronology.getZone());
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
        return BuddhistChronology.getInstance(dateTimeZone);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BuddhistChronology) {
            BuddhistChronology buddhistChronology = (BuddhistChronology)object;
            return this.getZone().equals(buddhistChronology.getZone());
        }
        return false;
    }

    public int hashCode() {
        return "Buddhist".hashCode() * 11 + this.getZone().hashCode();
    }

    public String toString() {
        String string = "BuddhistChronology";
        DateTimeZone dateTimeZone = this.getZone();
        if (dateTimeZone != null) {
            string = string + '[' + dateTimeZone.getID() + ']';
        }
        return string;
    }

    protected void assemble(AssembledChronology.Fields fields) {
        if (this.getParam() == null) {
            fields.eras = UnsupportedDurationField.getInstance(DurationFieldType.eras());
            DateTimeField dateTimeField = fields.year;
            fields.year = new OffsetDateTimeField((DateTimeField)new SkipUndoDateTimeField(this, dateTimeField), 543);
            dateTimeField = fields.yearOfEra;
            fields.yearOfEra = new DelegatedDateTimeField(fields.year, fields.eras, DateTimeFieldType.yearOfEra());
            dateTimeField = fields.weekyear;
            fields.weekyear = new OffsetDateTimeField((DateTimeField)new SkipUndoDateTimeField(this, dateTimeField), 543);
            dateTimeField = new OffsetDateTimeField(fields.yearOfEra, 99);
            fields.centuryOfEra = new DividedDateTimeField(dateTimeField, fields.eras, DateTimeFieldType.centuryOfEra(), 100);
            fields.centuries = fields.centuryOfEra.getDurationField();
            dateTimeField = new RemainderDateTimeField((DividedDateTimeField)fields.centuryOfEra);
            fields.yearOfCentury = new OffsetDateTimeField(dateTimeField, DateTimeFieldType.yearOfCentury(), 1);
            dateTimeField = new RemainderDateTimeField(fields.weekyear, fields.centuries, DateTimeFieldType.weekyearOfCentury(), 100);
            fields.weekyearOfCentury = new OffsetDateTimeField(dateTimeField, DateTimeFieldType.weekyearOfCentury(), 1);
            fields.era = ERA_FIELD;
        }
    }
}

