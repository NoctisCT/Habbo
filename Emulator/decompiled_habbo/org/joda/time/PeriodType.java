/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DurationFieldType;
import org.joda.time.ReadablePeriod;
import org.joda.time.field.FieldUtils;

public class PeriodType
implements Serializable {
    private static final long serialVersionUID = 2274324892792009998L;
    private static final Map<PeriodType, Object> cTypes = new HashMap<PeriodType, Object>(32);
    static int YEAR_INDEX = 0;
    static int MONTH_INDEX = 1;
    static int WEEK_INDEX = 2;
    static int DAY_INDEX = 3;
    static int HOUR_INDEX = 4;
    static int MINUTE_INDEX = 5;
    static int SECOND_INDEX = 6;
    static int MILLI_INDEX = 7;
    private static PeriodType cStandard;
    private static PeriodType cYMDTime;
    private static PeriodType cYMD;
    private static PeriodType cYWDTime;
    private static PeriodType cYWD;
    private static PeriodType cYDTime;
    private static PeriodType cYD;
    private static PeriodType cDTime;
    private static PeriodType cTime;
    private static PeriodType cYears;
    private static PeriodType cMonths;
    private static PeriodType cWeeks;
    private static PeriodType cDays;
    private static PeriodType cHours;
    private static PeriodType cMinutes;
    private static PeriodType cSeconds;
    private static PeriodType cMillis;
    private final String iName;
    private final DurationFieldType[] iTypes;
    private final int[] iIndices;

    public static PeriodType standard() {
        PeriodType periodType = cStandard;
        if (periodType == null) {
            cStandard = periodType = new PeriodType("Standard", new DurationFieldType[]{DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.weeks(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis()}, new int[]{0, 1, 2, 3, 4, 5, 6, 7});
        }
        return periodType;
    }

    public static PeriodType yearMonthDayTime() {
        PeriodType periodType = cYMDTime;
        if (periodType == null) {
            cYMDTime = periodType = new PeriodType("YearMonthDayTime", new DurationFieldType[]{DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis()}, new int[]{0, 1, -1, 2, 3, 4, 5, 6});
        }
        return periodType;
    }

    public static PeriodType yearMonthDay() {
        PeriodType periodType = cYMD;
        if (periodType == null) {
            cYMD = periodType = new PeriodType("YearMonthDay", new DurationFieldType[]{DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.days()}, new int[]{0, 1, -1, 2, -1, -1, -1, -1});
        }
        return periodType;
    }

    public static PeriodType yearWeekDayTime() {
        PeriodType periodType = cYWDTime;
        if (periodType == null) {
            cYWDTime = periodType = new PeriodType("YearWeekDayTime", new DurationFieldType[]{DurationFieldType.years(), DurationFieldType.weeks(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis()}, new int[]{0, -1, 1, 2, 3, 4, 5, 6});
        }
        return periodType;
    }

    public static PeriodType yearWeekDay() {
        PeriodType periodType = cYWD;
        if (periodType == null) {
            cYWD = periodType = new PeriodType("YearWeekDay", new DurationFieldType[]{DurationFieldType.years(), DurationFieldType.weeks(), DurationFieldType.days()}, new int[]{0, -1, 1, 2, -1, -1, -1, -1});
        }
        return periodType;
    }

    public static PeriodType yearDayTime() {
        PeriodType periodType = cYDTime;
        if (periodType == null) {
            cYDTime = periodType = new PeriodType("YearDayTime", new DurationFieldType[]{DurationFieldType.years(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis()}, new int[]{0, -1, -1, 1, 2, 3, 4, 5});
        }
        return periodType;
    }

    public static PeriodType yearDay() {
        PeriodType periodType = cYD;
        if (periodType == null) {
            cYD = periodType = new PeriodType("YearDay", new DurationFieldType[]{DurationFieldType.years(), DurationFieldType.days()}, new int[]{0, -1, -1, 1, -1, -1, -1, -1});
        }
        return periodType;
    }

    public static PeriodType dayTime() {
        PeriodType periodType = cDTime;
        if (periodType == null) {
            cDTime = periodType = new PeriodType("DayTime", new DurationFieldType[]{DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis()}, new int[]{-1, -1, -1, 0, 1, 2, 3, 4});
        }
        return periodType;
    }

    public static PeriodType time() {
        PeriodType periodType = cTime;
        if (periodType == null) {
            cTime = periodType = new PeriodType("Time", new DurationFieldType[]{DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis()}, new int[]{-1, -1, -1, -1, 0, 1, 2, 3});
        }
        return periodType;
    }

    public static PeriodType years() {
        PeriodType periodType = cYears;
        if (periodType == null) {
            cYears = periodType = new PeriodType("Years", new DurationFieldType[]{DurationFieldType.years()}, new int[]{0, -1, -1, -1, -1, -1, -1, -1});
        }
        return periodType;
    }

    public static PeriodType months() {
        PeriodType periodType = cMonths;
        if (periodType == null) {
            cMonths = periodType = new PeriodType("Months", new DurationFieldType[]{DurationFieldType.months()}, new int[]{-1, 0, -1, -1, -1, -1, -1, -1});
        }
        return periodType;
    }

    public static PeriodType weeks() {
        PeriodType periodType = cWeeks;
        if (periodType == null) {
            cWeeks = periodType = new PeriodType("Weeks", new DurationFieldType[]{DurationFieldType.weeks()}, new int[]{-1, -1, 0, -1, -1, -1, -1, -1});
        }
        return periodType;
    }

    public static PeriodType days() {
        PeriodType periodType = cDays;
        if (periodType == null) {
            cDays = periodType = new PeriodType("Days", new DurationFieldType[]{DurationFieldType.days()}, new int[]{-1, -1, -1, 0, -1, -1, -1, -1});
        }
        return periodType;
    }

    public static PeriodType hours() {
        PeriodType periodType = cHours;
        if (periodType == null) {
            cHours = periodType = new PeriodType("Hours", new DurationFieldType[]{DurationFieldType.hours()}, new int[]{-1, -1, -1, -1, 0, -1, -1, -1});
        }
        return periodType;
    }

    public static PeriodType minutes() {
        PeriodType periodType = cMinutes;
        if (periodType == null) {
            cMinutes = periodType = new PeriodType("Minutes", new DurationFieldType[]{DurationFieldType.minutes()}, new int[]{-1, -1, -1, -1, -1, 0, -1, -1});
        }
        return periodType;
    }

    public static PeriodType seconds() {
        PeriodType periodType = cSeconds;
        if (periodType == null) {
            cSeconds = periodType = new PeriodType("Seconds", new DurationFieldType[]{DurationFieldType.seconds()}, new int[]{-1, -1, -1, -1, -1, -1, 0, -1});
        }
        return periodType;
    }

    public static PeriodType millis() {
        PeriodType periodType = cMillis;
        if (periodType == null) {
            cMillis = periodType = new PeriodType("Millis", new DurationFieldType[]{DurationFieldType.millis()}, new int[]{-1, -1, -1, -1, -1, -1, -1, 0});
        }
        return periodType;
    }

    public static synchronized PeriodType forFields(DurationFieldType[] durationFieldTypeArray) {
        PeriodType periodType;
        Object object;
        if (durationFieldTypeArray == null || durationFieldTypeArray.length == 0) {
            throw new IllegalArgumentException("Types array must not be null or empty");
        }
        for (int i = 0; i < durationFieldTypeArray.length; ++i) {
            if (durationFieldTypeArray[i] != null) continue;
            throw new IllegalArgumentException("Types array must not contain null");
        }
        Map<PeriodType, Object> map = cTypes;
        if (map.isEmpty()) {
            map.put(PeriodType.standard(), PeriodType.standard());
            map.put(PeriodType.yearMonthDayTime(), PeriodType.yearMonthDayTime());
            map.put(PeriodType.yearMonthDay(), PeriodType.yearMonthDay());
            map.put(PeriodType.yearWeekDayTime(), PeriodType.yearWeekDayTime());
            map.put(PeriodType.yearWeekDay(), PeriodType.yearWeekDay());
            map.put(PeriodType.yearDayTime(), PeriodType.yearDayTime());
            map.put(PeriodType.yearDay(), PeriodType.yearDay());
            map.put(PeriodType.dayTime(), PeriodType.dayTime());
            map.put(PeriodType.time(), PeriodType.time());
            map.put(PeriodType.years(), PeriodType.years());
            map.put(PeriodType.months(), PeriodType.months());
            map.put(PeriodType.weeks(), PeriodType.weeks());
            map.put(PeriodType.days(), PeriodType.days());
            map.put(PeriodType.hours(), PeriodType.hours());
            map.put(PeriodType.minutes(), PeriodType.minutes());
            map.put(PeriodType.seconds(), PeriodType.seconds());
            map.put(PeriodType.millis(), PeriodType.millis());
        }
        if ((object = map.get(periodType = new PeriodType(null, durationFieldTypeArray, null))) instanceof PeriodType) {
            return (PeriodType)object;
        }
        if (object != null) {
            throw new IllegalArgumentException("PeriodType does not support fields: " + object);
        }
        PeriodType periodType2 = PeriodType.standard();
        ArrayList<DurationFieldType> arrayList = new ArrayList<DurationFieldType>(Arrays.asList(durationFieldTypeArray));
        if (!arrayList.remove(DurationFieldType.years())) {
            periodType2 = periodType2.withYearsRemoved();
        }
        if (!arrayList.remove(DurationFieldType.months())) {
            periodType2 = periodType2.withMonthsRemoved();
        }
        if (!arrayList.remove(DurationFieldType.weeks())) {
            periodType2 = periodType2.withWeeksRemoved();
        }
        if (!arrayList.remove(DurationFieldType.days())) {
            periodType2 = periodType2.withDaysRemoved();
        }
        if (!arrayList.remove(DurationFieldType.hours())) {
            periodType2 = periodType2.withHoursRemoved();
        }
        if (!arrayList.remove(DurationFieldType.minutes())) {
            periodType2 = periodType2.withMinutesRemoved();
        }
        if (!arrayList.remove(DurationFieldType.seconds())) {
            periodType2 = periodType2.withSecondsRemoved();
        }
        if (!arrayList.remove(DurationFieldType.millis())) {
            periodType2 = periodType2.withMillisRemoved();
        }
        if (arrayList.size() > 0) {
            map.put(periodType, arrayList);
            throw new IllegalArgumentException("PeriodType does not support fields: " + arrayList);
        }
        PeriodType periodType3 = new PeriodType(null, periodType2.iTypes, null);
        PeriodType periodType4 = (PeriodType)map.get(periodType3);
        if (periodType4 != null) {
            map.put(periodType3, periodType4);
            return periodType4;
        }
        map.put(periodType3, periodType2);
        return periodType2;
    }

    protected PeriodType(String string, DurationFieldType[] durationFieldTypeArray, int[] nArray) {
        this.iName = string;
        this.iTypes = durationFieldTypeArray;
        this.iIndices = nArray;
    }

    public String getName() {
        return this.iName;
    }

    public int size() {
        return this.iTypes.length;
    }

    public DurationFieldType getFieldType(int n) {
        return this.iTypes[n];
    }

    public boolean isSupported(DurationFieldType durationFieldType) {
        return this.indexOf(durationFieldType) >= 0;
    }

    public int indexOf(DurationFieldType durationFieldType) {
        int n = this.size();
        for (int i = 0; i < n; ++i) {
            if (this.iTypes[i] != durationFieldType) continue;
            return i;
        }
        return -1;
    }

    public String toString() {
        return "PeriodType[" + this.getName() + "]";
    }

    int getIndexedField(ReadablePeriod readablePeriod, int n) {
        int n2 = this.iIndices[n];
        return n2 == -1 ? 0 : readablePeriod.getValue(n2);
    }

    boolean setIndexedField(ReadablePeriod readablePeriod, int n, int[] nArray, int n2) {
        int n3 = this.iIndices[n];
        if (n3 == -1) {
            throw new UnsupportedOperationException("Field is not supported");
        }
        nArray[n3] = n2;
        return true;
    }

    boolean addIndexedField(ReadablePeriod readablePeriod, int n, int[] nArray, int n2) {
        if (n2 == 0) {
            return false;
        }
        int n3 = this.iIndices[n];
        if (n3 == -1) {
            throw new UnsupportedOperationException("Field is not supported");
        }
        nArray[n3] = FieldUtils.safeAdd(nArray[n3], n2);
        return true;
    }

    public PeriodType withYearsRemoved() {
        return this.withFieldRemoved(0, "NoYears");
    }

    public PeriodType withMonthsRemoved() {
        return this.withFieldRemoved(1, "NoMonths");
    }

    public PeriodType withWeeksRemoved() {
        return this.withFieldRemoved(2, "NoWeeks");
    }

    public PeriodType withDaysRemoved() {
        return this.withFieldRemoved(3, "NoDays");
    }

    public PeriodType withHoursRemoved() {
        return this.withFieldRemoved(4, "NoHours");
    }

    public PeriodType withMinutesRemoved() {
        return this.withFieldRemoved(5, "NoMinutes");
    }

    public PeriodType withSecondsRemoved() {
        return this.withFieldRemoved(6, "NoSeconds");
    }

    public PeriodType withMillisRemoved() {
        return this.withFieldRemoved(7, "NoMillis");
    }

    private PeriodType withFieldRemoved(int n, String string) {
        int n2 = this.iIndices[n];
        if (n2 == -1) {
            return this;
        }
        DurationFieldType[] durationFieldTypeArray = new DurationFieldType[this.size() - 1];
        for (int i = 0; i < this.iTypes.length; ++i) {
            if (i < n2) {
                durationFieldTypeArray[i] = this.iTypes[i];
                continue;
            }
            if (i <= n2) continue;
            durationFieldTypeArray[i - 1] = this.iTypes[i];
        }
        int[] nArray = new int[8];
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = i < n ? this.iIndices[i] : (i > n ? (this.iIndices[i] == -1 ? -1 : this.iIndices[i] - 1) : -1);
        }
        return new PeriodType(this.getName() + string, durationFieldTypeArray, nArray);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PeriodType)) {
            return false;
        }
        PeriodType periodType = (PeriodType)object;
        return Arrays.equals(this.iTypes, periodType.iTypes);
    }

    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.iTypes.length; ++i) {
            n += this.iTypes[i].hashCode();
        }
        return n;
    }
}

