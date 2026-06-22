/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.Chronology;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOYearOfEraDateTimeField;
import org.joda.time.chrono.ZonedChronology;
import org.joda.time.field.DividedDateTimeField;
import org.joda.time.field.RemainderDateTimeField;

public final class ISOChronology
extends AssembledChronology {
    private static final long serialVersionUID = -6212696554273812441L;
    private static final ISOChronology INSTANCE_UTC;
    private static final ConcurrentHashMap<DateTimeZone, ISOChronology> cCache;

    public static ISOChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }

    public static ISOChronology getInstance() {
        return ISOChronology.getInstance(DateTimeZone.getDefault());
    }

    public static ISOChronology getInstance(DateTimeZone dateTimeZone) {
        ISOChronology iSOChronology;
        ISOChronology iSOChronology2;
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if ((iSOChronology2 = cCache.get(dateTimeZone)) == null && (iSOChronology = cCache.putIfAbsent(dateTimeZone, iSOChronology2 = new ISOChronology(ZonedChronology.getInstance(INSTANCE_UTC, dateTimeZone)))) != null) {
            iSOChronology2 = iSOChronology;
        }
        return iSOChronology2;
    }

    private ISOChronology(Chronology chronology) {
        super(chronology, null);
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
        return ISOChronology.getInstance(dateTimeZone);
    }

    public String toString() {
        String string = "ISOChronology";
        DateTimeZone dateTimeZone = this.getZone();
        if (dateTimeZone != null) {
            string = string + '[' + dateTimeZone.getID() + ']';
        }
        return string;
    }

    protected void assemble(AssembledChronology.Fields fields) {
        if (this.getBase().getZone() == DateTimeZone.UTC) {
            fields.centuryOfEra = new DividedDateTimeField(ISOYearOfEraDateTimeField.INSTANCE, DateTimeFieldType.centuryOfEra(), 100);
            fields.centuries = fields.centuryOfEra.getDurationField();
            fields.yearOfCentury = new RemainderDateTimeField((DividedDateTimeField)fields.centuryOfEra, DateTimeFieldType.yearOfCentury());
            fields.weekyearOfCentury = new RemainderDateTimeField((DividedDateTimeField)fields.centuryOfEra, fields.weekyears, DateTimeFieldType.weekyearOfCentury());
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ISOChronology) {
            ISOChronology iSOChronology = (ISOChronology)object;
            return this.getZone().equals(iSOChronology.getZone());
        }
        return false;
    }

    public int hashCode() {
        return "ISO".hashCode() * 11 + this.getZone().hashCode();
    }

    private Object writeReplace() {
        return new Stub(this.getZone());
    }

    static {
        cCache = new ConcurrentHashMap();
        INSTANCE_UTC = new ISOChronology(GregorianChronology.getInstanceUTC());
        cCache.put(DateTimeZone.UTC, INSTANCE_UTC);
    }

    private static final class Stub
    implements Serializable {
        private static final long serialVersionUID = -6212696554273812441L;
        private transient DateTimeZone iZone;

        Stub(DateTimeZone dateTimeZone) {
            this.iZone = dateTimeZone;
        }

        private Object readResolve() {
            return ISOChronology.getInstance(this.iZone);
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iZone);
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iZone = (DateTimeZone)objectInputStream.readObject();
        }
    }
}

