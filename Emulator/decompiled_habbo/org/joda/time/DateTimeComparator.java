/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.io.Serializable;
import java.util.Comparator;
import org.joda.time.Chronology;
import org.joda.time.DateTimeFieldType;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.InstantConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DateTimeComparator
implements Comparator<Object>,
Serializable {
    private static final long serialVersionUID = -6097339773320178364L;
    private static final DateTimeComparator ALL_INSTANCE = new DateTimeComparator(null, null);
    private static final DateTimeComparator DATE_INSTANCE = new DateTimeComparator(DateTimeFieldType.dayOfYear(), null);
    private static final DateTimeComparator TIME_INSTANCE = new DateTimeComparator(null, DateTimeFieldType.dayOfYear());
    private final DateTimeFieldType iLowerLimit;
    private final DateTimeFieldType iUpperLimit;

    public static DateTimeComparator getInstance() {
        return ALL_INSTANCE;
    }

    public static DateTimeComparator getInstance(DateTimeFieldType dateTimeFieldType) {
        return DateTimeComparator.getInstance(dateTimeFieldType, null);
    }

    public static DateTimeComparator getInstance(DateTimeFieldType dateTimeFieldType, DateTimeFieldType dateTimeFieldType2) {
        if (dateTimeFieldType == null && dateTimeFieldType2 == null) {
            return ALL_INSTANCE;
        }
        if (dateTimeFieldType == DateTimeFieldType.dayOfYear() && dateTimeFieldType2 == null) {
            return DATE_INSTANCE;
        }
        if (dateTimeFieldType == null && dateTimeFieldType2 == DateTimeFieldType.dayOfYear()) {
            return TIME_INSTANCE;
        }
        return new DateTimeComparator(dateTimeFieldType, dateTimeFieldType2);
    }

    public static DateTimeComparator getDateOnlyInstance() {
        return DATE_INSTANCE;
    }

    public static DateTimeComparator getTimeOnlyInstance() {
        return TIME_INSTANCE;
    }

    protected DateTimeComparator(DateTimeFieldType dateTimeFieldType, DateTimeFieldType dateTimeFieldType2) {
        this.iLowerLimit = dateTimeFieldType;
        this.iUpperLimit = dateTimeFieldType2;
    }

    public DateTimeFieldType getLowerLimit() {
        return this.iLowerLimit;
    }

    public DateTimeFieldType getUpperLimit() {
        return this.iUpperLimit;
    }

    @Override
    public int compare(Object object, Object object2) {
        InstantConverter instantConverter = ConverterManager.getInstance().getInstantConverter(object);
        Chronology chronology = instantConverter.getChronology(object, (Chronology)null);
        long l = instantConverter.getInstantMillis(object, chronology);
        if (object == object2) {
            return 0;
        }
        instantConverter = ConverterManager.getInstance().getInstantConverter(object2);
        Chronology chronology2 = instantConverter.getChronology(object2, (Chronology)null);
        long l2 = instantConverter.getInstantMillis(object2, chronology2);
        if (this.iLowerLimit != null) {
            l = this.iLowerLimit.getField(chronology).roundFloor(l);
            l2 = this.iLowerLimit.getField(chronology2).roundFloor(l2);
        }
        if (this.iUpperLimit != null) {
            l = this.iUpperLimit.getField(chronology).remainder(l);
            l2 = this.iUpperLimit.getField(chronology2).remainder(l2);
        }
        if (l < l2) {
            return -1;
        }
        if (l > l2) {
            return 1;
        }
        return 0;
    }

    private Object readResolve() {
        return DateTimeComparator.getInstance(this.iLowerLimit, this.iUpperLimit);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DateTimeComparator) {
            DateTimeComparator dateTimeComparator = (DateTimeComparator)object;
            return (this.iLowerLimit == dateTimeComparator.getLowerLimit() || this.iLowerLimit != null && this.iLowerLimit.equals(dateTimeComparator.getLowerLimit())) && (this.iUpperLimit == dateTimeComparator.getUpperLimit() || this.iUpperLimit != null && this.iUpperLimit.equals(dateTimeComparator.getUpperLimit()));
        }
        return false;
    }

    public int hashCode() {
        return (this.iLowerLimit == null ? 0 : this.iLowerLimit.hashCode()) + 123 * (this.iUpperLimit == null ? 0 : this.iUpperLimit.hashCode());
    }

    public String toString() {
        if (this.iLowerLimit == this.iUpperLimit) {
            return "DateTimeComparator[" + (this.iLowerLimit == null ? "" : this.iLowerLimit.getName()) + "]";
        }
        return "DateTimeComparator[" + (this.iLowerLimit == null ? "" : this.iLowerLimit.getName()) + "-" + (this.iUpperLimit == null ? "" : this.iUpperLimit.getName()) + "]";
    }
}

