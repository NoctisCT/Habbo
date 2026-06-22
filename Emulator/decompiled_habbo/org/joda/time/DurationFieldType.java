/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.io.Serializable;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationField;

public abstract class DurationFieldType
implements Serializable {
    private static final long serialVersionUID = 8765135187319L;
    static final byte ERAS = 1;
    static final byte CENTURIES = 2;
    static final byte WEEKYEARS = 3;
    static final byte YEARS = 4;
    static final byte MONTHS = 5;
    static final byte WEEKS = 6;
    static final byte DAYS = 7;
    static final byte HALFDAYS = 8;
    static final byte HOURS = 9;
    static final byte MINUTES = 10;
    static final byte SECONDS = 11;
    static final byte MILLIS = 12;
    static final DurationFieldType ERAS_TYPE = new StandardDurationFieldType("eras", 1);
    static final DurationFieldType CENTURIES_TYPE = new StandardDurationFieldType("centuries", 2);
    static final DurationFieldType WEEKYEARS_TYPE = new StandardDurationFieldType("weekyears", 3);
    static final DurationFieldType YEARS_TYPE = new StandardDurationFieldType("years", 4);
    static final DurationFieldType MONTHS_TYPE = new StandardDurationFieldType("months", 5);
    static final DurationFieldType WEEKS_TYPE = new StandardDurationFieldType("weeks", 6);
    static final DurationFieldType DAYS_TYPE = new StandardDurationFieldType("days", 7);
    static final DurationFieldType HALFDAYS_TYPE = new StandardDurationFieldType("halfdays", 8);
    static final DurationFieldType HOURS_TYPE = new StandardDurationFieldType("hours", 9);
    static final DurationFieldType MINUTES_TYPE = new StandardDurationFieldType("minutes", 10);
    static final DurationFieldType SECONDS_TYPE = new StandardDurationFieldType("seconds", 11);
    static final DurationFieldType MILLIS_TYPE = new StandardDurationFieldType("millis", 12);
    private final String iName;

    protected DurationFieldType(String string) {
        this.iName = string;
    }

    public static DurationFieldType millis() {
        return MILLIS_TYPE;
    }

    public static DurationFieldType seconds() {
        return SECONDS_TYPE;
    }

    public static DurationFieldType minutes() {
        return MINUTES_TYPE;
    }

    public static DurationFieldType hours() {
        return HOURS_TYPE;
    }

    public static DurationFieldType halfdays() {
        return HALFDAYS_TYPE;
    }

    public static DurationFieldType days() {
        return DAYS_TYPE;
    }

    public static DurationFieldType weeks() {
        return WEEKS_TYPE;
    }

    public static DurationFieldType weekyears() {
        return WEEKYEARS_TYPE;
    }

    public static DurationFieldType months() {
        return MONTHS_TYPE;
    }

    public static DurationFieldType years() {
        return YEARS_TYPE;
    }

    public static DurationFieldType centuries() {
        return CENTURIES_TYPE;
    }

    public static DurationFieldType eras() {
        return ERAS_TYPE;
    }

    public String getName() {
        return this.iName;
    }

    public abstract DurationField getField(Chronology var1);

    public boolean isSupported(Chronology chronology) {
        return this.getField(chronology).isSupported();
    }

    public String toString() {
        return this.getName();
    }

    private static class StandardDurationFieldType
    extends DurationFieldType {
        private static final long serialVersionUID = 31156755687123L;
        private final byte iOrdinal;

        StandardDurationFieldType(String string, byte by) {
            super(string);
            this.iOrdinal = by;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof StandardDurationFieldType) {
                return this.iOrdinal == ((StandardDurationFieldType)object).iOrdinal;
            }
            return false;
        }

        public int hashCode() {
            return 1 << this.iOrdinal;
        }

        public DurationField getField(Chronology chronology) {
            chronology = DateTimeUtils.getChronology(chronology);
            switch (this.iOrdinal) {
                case 1: {
                    return chronology.eras();
                }
                case 2: {
                    return chronology.centuries();
                }
                case 3: {
                    return chronology.weekyears();
                }
                case 4: {
                    return chronology.years();
                }
                case 5: {
                    return chronology.months();
                }
                case 6: {
                    return chronology.weeks();
                }
                case 7: {
                    return chronology.days();
                }
                case 8: {
                    return chronology.halfdays();
                }
                case 9: {
                    return chronology.hours();
                }
                case 10: {
                    return chronology.minutes();
                }
                case 11: {
                    return chronology.seconds();
                }
                case 12: {
                    return chronology.millis();
                }
            }
            throw new InternalError();
        }

        private Object readResolve() {
            switch (this.iOrdinal) {
                case 1: {
                    return ERAS_TYPE;
                }
                case 2: {
                    return CENTURIES_TYPE;
                }
                case 3: {
                    return WEEKYEARS_TYPE;
                }
                case 4: {
                    return YEARS_TYPE;
                }
                case 5: {
                    return MONTHS_TYPE;
                }
                case 6: {
                    return WEEKS_TYPE;
                }
                case 7: {
                    return DAYS_TYPE;
                }
                case 8: {
                    return HALFDAYS_TYPE;
                }
                case 9: {
                    return HOURS_TYPE;
                }
                case 10: {
                    return MINUTES_TYPE;
                }
                case 11: {
                    return SECONDS_TYPE;
                }
                case 12: {
                    return MILLIS_TYPE;
                }
            }
            return this;
        }
    }
}

