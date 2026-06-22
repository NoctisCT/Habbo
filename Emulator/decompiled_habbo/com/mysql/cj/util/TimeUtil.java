/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.util;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

public class TimeUtil {
    static final TimeZone GMT_TIMEZONE;
    private static final String TIME_ZONE_MAPPINGS_RESOURCE = "/com/mysql/cj/util/TimeZoneMapping.properties";
    private static Properties timeZoneMappings;
    protected static final Method systemNanoTimeMethod;

    public static boolean nanoTimeAvailable() {
        return systemNanoTimeMethod != null;
    }

    public static long getCurrentTimeNanosOrMillis() {
        if (systemNanoTimeMethod != null) {
            try {
                return (Long)systemNanoTimeMethod.invoke(null, (Object[])null);
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (InvocationTargetException invocationTargetException) {
                // empty catch block
            }
        }
        return System.currentTimeMillis();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getCanonicalTimezone(String timezoneStr, ExceptionInterceptor exceptionInterceptor) {
        if (timezoneStr == null) {
            return null;
        }
        if ((timezoneStr = timezoneStr.trim()).length() > 2 && (timezoneStr.charAt(0) == '+' || timezoneStr.charAt(0) == '-') && Character.isDigit(timezoneStr.charAt(1))) {
            return "GMT" + timezoneStr;
        }
        Class<TimeUtil> clazz = TimeUtil.class;
        synchronized (TimeUtil.class) {
            if (timeZoneMappings == null) {
                TimeUtil.loadTimeZoneMappings(exceptionInterceptor);
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            String canonicalTz = timeZoneMappings.getProperty(timezoneStr);
            if (canonicalTz != null) {
                return canonicalTz;
            }
            throw ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("TimeUtil.UnrecognizedTimezoneId", new Object[]{timezoneStr}), exceptionInterceptor);
        }
    }

    public static Timestamp adjustNanosPrecision(Timestamp ts, int fsp, boolean serverRoundFracSecs) {
        int nanos;
        if (fsp < 0 || fsp > 6) {
            throw ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
        }
        Timestamp res = (Timestamp)ts.clone();
        double tail = Math.pow(10.0, 9 - fsp);
        int n = nanos = serverRoundFracSecs ? (int)Math.round((double)res.getNanos() / tail) * (int)tail : (int)((double)res.getNanos() / tail) * (int)tail;
        if (nanos > 999999999) {
            nanos %= 1000000000;
            res.setTime(res.getTime() + 1000L);
        }
        res.setNanos(nanos);
        return res;
    }

    public static LocalDateTime adjustNanosPrecision(LocalDateTime x, int fsp, boolean serverRoundFracSecs) {
        int adjustedNano;
        if (fsp < 0 || fsp > 6) {
            throw ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
        }
        int originalNano = x.getNano();
        double tail = Math.pow(10.0, 9 - fsp);
        int n = adjustedNano = serverRoundFracSecs ? (int)Math.round((double)originalNano / tail) * (int)tail : (int)((double)originalNano / tail) * (int)tail;
        if (adjustedNano > 999999999) {
            adjustedNano %= 1000000000;
            x = x.plusSeconds(1L);
        }
        return x.withNano(adjustedNano);
    }

    public static LocalTime adjustNanosPrecision(LocalTime x, int fsp, boolean serverRoundFracSecs) {
        int adjustedNano;
        if (fsp < 0 || fsp > 6) {
            throw ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
        }
        int originalNano = x.getNano();
        double tail = Math.pow(10.0, 9 - fsp);
        int n = adjustedNano = serverRoundFracSecs ? (int)Math.round((double)originalNano / tail) * (int)tail : (int)((double)originalNano / tail) * (int)tail;
        if (adjustedNano > 999999999) {
            adjustedNano %= 1000000000;
            x = x.plusSeconds(1L);
        }
        return x.withNano(adjustedNano);
    }

    public static String formatNanos(int nanos, int fsp) {
        return TimeUtil.formatNanos(nanos, fsp, true);
    }

    public static String formatNanos(int nanos, int fsp, boolean truncateTrailingZeros) {
        if (nanos < 0 || nanos > 999999999) {
            throw ExceptionFactory.createException(WrongArgumentException.class, "nanos value must be in 0 to 999999999 range but was " + nanos);
        }
        if (fsp < 0 || fsp > 6) {
            throw ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range but was " + fsp);
        }
        if (fsp == 0 || nanos == 0) {
            return "0";
        }
        if ((nanos = (int)((double)nanos / Math.pow(10.0, 9 - fsp))) == 0) {
            return "0";
        }
        String nanosString = Integer.toString(nanos);
        String zeroPadding = "000000000";
        nanosString = "000000000".substring(0, fsp - nanosString.length()) + nanosString;
        if (truncateTrailingZeros) {
            int pos = fsp - 1;
            while (nanosString.charAt(pos) == '0') {
                --pos;
            }
            nanosString = nanosString.substring(0, pos + 1);
        }
        return nanosString;
    }

    private static void loadTimeZoneMappings(ExceptionInterceptor exceptionInterceptor) {
        timeZoneMappings = new Properties();
        try {
            timeZoneMappings.load(TimeUtil.class.getResourceAsStream(TIME_ZONE_MAPPINGS_RESOURCE));
        }
        catch (IOException e) {
            throw ExceptionFactory.createException(Messages.getString("TimeUtil.LoadTimeZoneMappingError"), exceptionInterceptor);
        }
        for (String tz : TimeZone.getAvailableIDs()) {
            if (timeZoneMappings.containsKey(tz)) continue;
            timeZoneMappings.put(tz, tz);
        }
    }

    public static Timestamp truncateFractionalSeconds(Timestamp timestamp) {
        Timestamp truncatedTimestamp = new Timestamp(timestamp.getTime());
        truncatedTimestamp.setNanos(0);
        return truncatedTimestamp;
    }

    public static SimpleDateFormat getSimpleDateFormat(SimpleDateFormat cachedSimpleDateFormat, String pattern, TimeZone tz) {
        SimpleDateFormat sdf;
        SimpleDateFormat simpleDateFormat = sdf = cachedSimpleDateFormat != null ? cachedSimpleDateFormat : new SimpleDateFormat(pattern, Locale.US);
        if (tz != null) {
            sdf.setTimeZone(tz);
        }
        return sdf;
    }

    public static SimpleDateFormat getSimpleDateFormat(String pattern, Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
        if (cal != null) {
            sdf.setCalendar(cal);
        }
        return sdf;
    }

    public static final String getDateTimePattern(String dt, boolean toTime) throws IOException {
        int i;
        int size;
        char c;
        int n;
        Object[] v;
        int z;
        int dtLength;
        int n2 = dtLength = dt != null ? dt.length() : 0;
        if (dtLength >= 8 && dtLength <= 10) {
            int dashCount = 0;
            boolean isDateOnly = true;
            for (int i2 = 0; i2 < dtLength; ++i2) {
                char c2 = dt.charAt(i2);
                if (!Character.isDigit(c2) && c2 != '-') {
                    isDateOnly = false;
                    break;
                }
                if (c2 != '-') continue;
                ++dashCount;
            }
            if (isDateOnly && dashCount == 2) {
                return "yyyy-MM-dd";
            }
        }
        boolean colonsOnly = true;
        for (int i3 = 0; i3 < dtLength; ++i3) {
            char c3 = dt.charAt(i3);
            if (Character.isDigit(c3) || c3 == ':') continue;
            colonsOnly = false;
            break;
        }
        if (colonsOnly) {
            return "HH:mm:ss";
        }
        StringReader reader = new StringReader(dt + " ");
        ArrayList<Object[]> vec = new ArrayList<Object[]>();
        ArrayList<Object[]> vecRemovelist = new ArrayList<Object[]>();
        Object[] nv = new Object[]{Character.valueOf('y'), new StringBuilder(), 0};
        vec.add(nv);
        if (toTime) {
            nv = new Object[]{Character.valueOf('h'), new StringBuilder(), 0};
            vec.add(nv);
        }
        while ((z = reader.read()) != -1) {
            char separator = (char)z;
            int maxvecs = vec.size();
            for (int count = 0; count < maxvecs; ++count) {
                v = (Object[])vec.get(count);
                n = (Integer)v[2];
                c = TimeUtil.getSuccessor(((Character)v[0]).charValue(), n);
                if (!Character.isLetterOrDigit(separator)) {
                    if (c == ((Character)v[0]).charValue() && c != 'S') {
                        vecRemovelist.add(v);
                        continue;
                    }
                    ((StringBuilder)v[1]).append(separator);
                    if (c != 'X' && c != 89) continue;
                    v[2] = 4;
                    continue;
                }
                if (c == 'X') {
                    c = 'y';
                    nv = new Object[3];
                    nv[1] = new StringBuilder(((StringBuilder)v[1]).toString()).append('M');
                    nv[0] = Character.valueOf('M');
                    nv[2] = 1;
                    vec.add(nv);
                } else if (c == 'Y') {
                    c = 'M';
                    nv = new Object[3];
                    nv[1] = new StringBuilder(((StringBuilder)v[1]).toString()).append('d');
                    nv[0] = Character.valueOf('d');
                    nv[2] = 1;
                    vec.add(nv);
                }
                ((StringBuilder)v[1]).append(c);
                if (c == ((Character)v[0]).charValue()) {
                    v[2] = n + 1;
                    continue;
                }
                v[0] = Character.valueOf(c);
                v[2] = 1;
            }
            size = vecRemovelist.size();
            for (i = 0; i < size; ++i) {
                v = (Object[])vecRemovelist.get(i);
                vec.remove(v);
            }
            vecRemovelist.clear();
        }
        size = vec.size();
        for (i = 0; i < size; ++i) {
            boolean containsEnd;
            v = (Object[])vec.get(i);
            c = ((Character)v[0]).charValue();
            boolean bk = TimeUtil.getSuccessor(c, n = ((Integer)v[2]).intValue()) != c;
            boolean atEnd = (c == 's' || c == 'm' || c == 'h' && toTime) && bk;
            boolean finishesAtDate = bk && c == 'd' && !toTime;
            boolean bl = containsEnd = ((StringBuilder)v[1]).toString().indexOf(87) != -1;
            if ((atEnd || finishesAtDate) && !containsEnd) continue;
            vecRemovelist.add(v);
        }
        size = vecRemovelist.size();
        for (i = 0; i < size; ++i) {
            vec.remove(vecRemovelist.get(i));
        }
        vecRemovelist.clear();
        v = (Object[])vec.get(0);
        StringBuilder format = (StringBuilder)v[1];
        format.setLength(format.length() - 1);
        return format.toString();
    }

    private static final char getSuccessor(char c, int n) {
        return (char)(c == 'y' && n == 2 ? 88 : (c == 'y' && n < 4 ? 121 : (c == 'y' ? 77 : (c == 'M' && n == 2 ? 89 : (c == 'M' && n < 3 ? 77 : (c == 'M' ? 100 : (c == 'd' && n < 2 ? 100 : (c == 'd' ? 72 : (c == 'H' && n < 2 ? 72 : (c == 'H' ? 109 : (c == 'm' && n < 2 ? 109 : (c == 'm' ? 115 : (c == 's' && n < 2 ? 115 : 87)))))))))))));
    }

    static {
        Method aMethod;
        GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
        timeZoneMappings = null;
        try {
            aMethod = System.class.getMethod("nanoTime", null);
        }
        catch (SecurityException e) {
            aMethod = null;
        }
        catch (NoSuchMethodException e) {
            aMethod = null;
        }
        systemNanoTimeMethod = aMethod;
    }
}

