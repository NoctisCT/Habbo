/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.io.Serializable;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeUtils;
import org.joda.time.DurationFieldType;

public abstract class DateTimeFieldType
implements Serializable {
    private static final long serialVersionUID = -42615285973990L;
    static final byte ERA = 1;
    static final byte YEAR_OF_ERA = 2;
    static final byte CENTURY_OF_ERA = 3;
    static final byte YEAR_OF_CENTURY = 4;
    static final byte YEAR = 5;
    static final byte DAY_OF_YEAR = 6;
    static final byte MONTH_OF_YEAR = 7;
    static final byte DAY_OF_MONTH = 8;
    static final byte WEEKYEAR_OF_CENTURY = 9;
    static final byte WEEKYEAR = 10;
    static final byte WEEK_OF_WEEKYEAR = 11;
    static final byte DAY_OF_WEEK = 12;
    static final byte HALFDAY_OF_DAY = 13;
    static final byte HOUR_OF_HALFDAY = 14;
    static final byte CLOCKHOUR_OF_HALFDAY = 15;
    static final byte CLOCKHOUR_OF_DAY = 16;
    static final byte HOUR_OF_DAY = 17;
    static final byte MINUTE_OF_DAY = 18;
    static final byte MINUTE_OF_HOUR = 19;
    static final byte SECOND_OF_DAY = 20;
    static final byte SECOND_OF_MINUTE = 21;
    static final byte MILLIS_OF_DAY = 22;
    static final byte MILLIS_OF_SECOND = 23;
    private static final DateTimeFieldType ERA_TYPE = new StandardDateTimeFieldType("era", 1, DurationFieldType.eras(), null);
    private static final DateTimeFieldType YEAR_OF_ERA_TYPE = new StandardDateTimeFieldType("yearOfEra", 2, DurationFieldType.years(), DurationFieldType.eras());
    private static final DateTimeFieldType CENTURY_OF_ERA_TYPE = new StandardDateTimeFieldType("centuryOfEra", 3, DurationFieldType.centuries(), DurationFieldType.eras());
    private static final DateTimeFieldType YEAR_OF_CENTURY_TYPE = new StandardDateTimeFieldType("yearOfCentury", 4, DurationFieldType.years(), DurationFieldType.centuries());
    private static final DateTimeFieldType YEAR_TYPE = new StandardDateTimeFieldType("year", 5, DurationFieldType.years(), null);
    private static final DateTimeFieldType DAY_OF_YEAR_TYPE = new StandardDateTimeFieldType("dayOfYear", 6, DurationFieldType.days(), DurationFieldType.years());
    private static final DateTimeFieldType MONTH_OF_YEAR_TYPE = new StandardDateTimeFieldType("monthOfYear", 7, DurationFieldType.months(), DurationFieldType.years());
    private static final DateTimeFieldType DAY_OF_MONTH_TYPE = new StandardDateTimeFieldType("dayOfMonth", 8, DurationFieldType.days(), DurationFieldType.months());
    private static final DateTimeFieldType WEEKYEAR_OF_CENTURY_TYPE = new StandardDateTimeFieldType("weekyearOfCentury", 9, DurationFieldType.weekyears(), DurationFieldType.centuries());
    private static final DateTimeFieldType WEEKYEAR_TYPE = new StandardDateTimeFieldType("weekyear", 10, DurationFieldType.weekyears(), null);
    private static final DateTimeFieldType WEEK_OF_WEEKYEAR_TYPE = new StandardDateTimeFieldType("weekOfWeekyear", 11, DurationFieldType.weeks(), DurationFieldType.weekyears());
    private static final DateTimeFieldType DAY_OF_WEEK_TYPE = new StandardDateTimeFieldType("dayOfWeek", 12, DurationFieldType.days(), DurationFieldType.weeks());
    private static final DateTimeFieldType HALFDAY_OF_DAY_TYPE = new StandardDateTimeFieldType("halfdayOfDay", 13, DurationFieldType.halfdays(), DurationFieldType.days());
    private static final DateTimeFieldType HOUR_OF_HALFDAY_TYPE = new StandardDateTimeFieldType("hourOfHalfday", 14, DurationFieldType.hours(), DurationFieldType.halfdays());
    private static final DateTimeFieldType CLOCKHOUR_OF_HALFDAY_TYPE = new StandardDateTimeFieldType("clockhourOfHalfday", 15, DurationFieldType.hours(), DurationFieldType.halfdays());
    private static final DateTimeFieldType CLOCKHOUR_OF_DAY_TYPE = new StandardDateTimeFieldType("clockhourOfDay", 16, DurationFieldType.hours(), DurationFieldType.days());
    private static final DateTimeFieldType HOUR_OF_DAY_TYPE = new StandardDateTimeFieldType("hourOfDay", 17, DurationFieldType.hours(), DurationFieldType.days());
    private static final DateTimeFieldType MINUTE_OF_DAY_TYPE = new StandardDateTimeFieldType("minuteOfDay", 18, DurationFieldType.minutes(), DurationFieldType.days());
    private static final DateTimeFieldType MINUTE_OF_HOUR_TYPE = new StandardDateTimeFieldType("minuteOfHour", 19, DurationFieldType.minutes(), DurationFieldType.hours());
    private static final DateTimeFieldType SECOND_OF_DAY_TYPE = new StandardDateTimeFieldType("secondOfDay", 20, DurationFieldType.seconds(), DurationFieldType.days());
    private static final DateTimeFieldType SECOND_OF_MINUTE_TYPE = new StandardDateTimeFieldType("secondOfMinute", 21, DurationFieldType.seconds(), DurationFieldType.minutes());
    private static final DateTimeFieldType MILLIS_OF_DAY_TYPE = new StandardDateTimeFieldType("millisOfDay", 22, DurationFieldType.millis(), DurationFieldType.days());
    private static final DateTimeFieldType MILLIS_OF_SECOND_TYPE = new StandardDateTimeFieldType("millisOfSecond", 23, DurationFieldType.millis(), DurationFieldType.seconds());
    private final String iName;

    protected DateTimeFieldType(String string) {
        this.iName = string;
    }

    public static DateTimeFieldType millisOfSecond() {
        return MILLIS_OF_SECOND_TYPE;
    }

    public static DateTimeFieldType millisOfDay() {
        return MILLIS_OF_DAY_TYPE;
    }

    public static DateTimeFieldType secondOfMinute() {
        return SECOND_OF_MINUTE_TYPE;
    }

    public static DateTimeFieldType secondOfDay() {
        return SECOND_OF_DAY_TYPE;
    }

    public static DateTimeFieldType minuteOfHour() {
        return MINUTE_OF_HOUR_TYPE;
    }

    public static DateTimeFieldType minuteOfDay() {
        return MINUTE_OF_DAY_TYPE;
    }

    public static DateTimeFieldType hourOfDay() {
        return HOUR_OF_DAY_TYPE;
    }

    public static DateTimeFieldType clockhourOfDay() {
        return CLOCKHOUR_OF_DAY_TYPE;
    }

    public static DateTimeFieldType hourOfHalfday() {
        return HOUR_OF_HALFDAY_TYPE;
    }

    public static DateTimeFieldType clockhourOfHalfday() {
        return CLOCKHOUR_OF_HALFDAY_TYPE;
    }

    public static DateTimeFieldType halfdayOfDay() {
        return HALFDAY_OF_DAY_TYPE;
    }

    public static DateTimeFieldType dayOfWeek() {
        return DAY_OF_WEEK_TYPE;
    }

    public static DateTimeFieldType dayOfMonth() {
        return DAY_OF_MONTH_TYPE;
    }

    public static DateTimeFieldType dayOfYear() {
        return DAY_OF_YEAR_TYPE;
    }

    public static DateTimeFieldType weekOfWeekyear() {
        return WEEK_OF_WEEKYEAR_TYPE;
    }

    public static DateTimeFieldType weekyear() {
        return WEEKYEAR_TYPE;
    }

    public static DateTimeFieldType weekyearOfCentury() {
        return WEEKYEAR_OF_CENTURY_TYPE;
    }

    public static DateTimeFieldType monthOfYear() {
        return MONTH_OF_YEAR_TYPE;
    }

    public static DateTimeFieldType year() {
        return YEAR_TYPE;
    }

    public static DateTimeFieldType yearOfEra() {
        return YEAR_OF_ERA_TYPE;
    }

    public static DateTimeFieldType yearOfCentury() {
        return YEAR_OF_CENTURY_TYPE;
    }

    public static DateTimeFieldType centuryOfEra() {
        return CENTURY_OF_ERA_TYPE;
    }

    public static DateTimeFieldType era() {
        return ERA_TYPE;
    }

    public String getName() {
        return this.iName;
    }

    public abstract DurationFieldType getDurationType();

    public abstract DurationFieldType getRangeDurationType();

    public abstract DateTimeField getField(Chronology var1);

    public boolean isSupported(Chronology chronology) {
        return this.getField(chronology).isSupported();
    }

    public String toString() {
        return this.getName();
    }

    private static class StandardDateTimeFieldType
    extends DateTimeFieldType {
        private static final long serialVersionUID = -9937958251642L;
        private final byte iOrdinal;
        private final transient DurationFieldType iUnitType;
        private final transient DurationFieldType iRangeType;

        StandardDateTimeFieldType(String string, byte by, DurationFieldType durationFieldType, DurationFieldType durationFieldType2) {
            super(string);
            this.iOrdinal = by;
            this.iUnitType = durationFieldType;
            this.iRangeType = durationFieldType2;
        }

        public DurationFieldType getDurationType() {
            return this.iUnitType;
        }

        public DurationFieldType getRangeDurationType() {
            return this.iRangeType;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof StandardDateTimeFieldType) {
                return this.iOrdinal == ((StandardDateTimeFieldType)object).iOrdinal;
            }
            return false;
        }

        public int hashCode() {
            return 1 << this.iOrdinal;
        }

        public DateTimeField getField(Chronology chronology) {
            chronology = DateTimeUtils.getChronology(chronology);
            switch (this.iOrdinal) {
                case 1: {
                    return chronology.era();
                }
                case 2: {
                    return chronology.yearOfEra();
                }
                case 3: {
                    return chronology.centuryOfEra();
                }
                case 4: {
                    return chronology.yearOfCentury();
                }
                case 5: {
                    return chronology.year();
                }
                case 6: {
                    return chronology.dayOfYear();
                }
                case 7: {
                    return chronology.monthOfYear();
                }
                case 8: {
                    return chronology.dayOfMonth();
                }
                case 9: {
                    return chronology.weekyearOfCentury();
                }
                case 10: {
                    return chronology.weekyear();
                }
                case 11: {
                    return chronology.weekOfWeekyear();
                }
                case 12: {
                    return chronology.dayOfWeek();
                }
                case 13: {
                    return chronology.halfdayOfDay();
                }
                case 14: {
                    return chronology.hourOfHalfday();
                }
                case 15: {
                    return chronology.clockhourOfHalfday();
                }
                case 16: {
                    return chronology.clockhourOfDay();
                }
                case 17: {
                    return chronology.hourOfDay();
                }
                case 18: {
                    return chronology.minuteOfDay();
                }
                case 19: {
                    return chronology.minuteOfHour();
                }
                case 20: {
                    return chronology.secondOfDay();
                }
                case 21: {
                    return chronology.secondOfMinute();
                }
                case 22: {
                    return chronology.millisOfDay();
                }
                case 23: {
                    return chronology.millisOfSecond();
                }
            }
            throw new InternalError();
        }

        private Object readResolve() {
            switch (this.iOrdinal) {
                case 1: {
                    return ERA_TYPE;
                }
                case 2: {
                    return YEAR_OF_ERA_TYPE;
                }
                case 3: {
                    return CENTURY_OF_ERA_TYPE;
                }
                case 4: {
                    return YEAR_OF_CENTURY_TYPE;
                }
                case 5: {
                    return YEAR_TYPE;
                }
                case 6: {
                    return DAY_OF_YEAR_TYPE;
                }
                case 7: {
                    return MONTH_OF_YEAR_TYPE;
                }
                case 8: {
                    return DAY_OF_MONTH_TYPE;
                }
                case 9: {
                    return WEEKYEAR_OF_CENTURY_TYPE;
                }
                case 10: {
                    return WEEKYEAR_TYPE;
                }
                case 11: {
                    return WEEK_OF_WEEKYEAR_TYPE;
                }
                case 12: {
                    return DAY_OF_WEEK_TYPE;
                }
                case 13: {
                    return HALFDAY_OF_DAY_TYPE;
                }
                case 14: {
                    return HOUR_OF_HALFDAY_TYPE;
                }
                case 15: {
                    return CLOCKHOUR_OF_HALFDAY_TYPE;
                }
                case 16: {
                    return CLOCKHOUR_OF_DAY_TYPE;
                }
                case 17: {
                    return HOUR_OF_DAY_TYPE;
                }
                case 18: {
                    return MINUTE_OF_DAY_TYPE;
                }
                case 19: {
                    return MINUTE_OF_HOUR_TYPE;
                }
                case 20: {
                    return SECOND_OF_DAY_TYPE;
                }
                case 21: {
                    return SECOND_OF_MINUTE_TYPE;
                }
                case 22: {
                    return MILLIS_OF_DAY_TYPE;
                }
                case 23: {
                    return MILLIS_OF_SECOND_TYPE;
                }
            }
            return this;
        }
    }
}

