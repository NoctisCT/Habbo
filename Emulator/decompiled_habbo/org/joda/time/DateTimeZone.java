/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.FromString
 *  org.joda.convert.ToString
 */
package org.joda.time;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.IllegalInstantException;
import org.joda.time.JodaTimePermission;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.UTCDateTimeZone;
import org.joda.time.chrono.BaseChronology;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.FormatUtils;
import org.joda.time.tz.DefaultNameProvider;
import org.joda.time.tz.FixedDateTimeZone;
import org.joda.time.tz.NameProvider;
import org.joda.time.tz.Provider;
import org.joda.time.tz.UTCProvider;
import org.joda.time.tz.ZoneInfoProvider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class DateTimeZone
implements Serializable {
    private static final long serialVersionUID = 5546345482340108586L;
    public static final DateTimeZone UTC = UTCDateTimeZone.INSTANCE;
    private static final int MAX_MILLIS = 86399999;
    private static final AtomicReference<Provider> cProvider = new AtomicReference();
    private static final AtomicReference<NameProvider> cNameProvider = new AtomicReference();
    private static final AtomicReference<DateTimeZone> cDefault = new AtomicReference();
    public static final String DEFAULT_TZ_DATA_PATH = "org/joda/time/tz/data";
    private final String iID;

    public static DateTimeZone getDefault() {
        DateTimeZone dateTimeZone = cDefault.get();
        if (dateTimeZone == null) {
            try {
                try {
                    String string = System.getProperty("user.timezone");
                    if (string != null) {
                        dateTimeZone = DateTimeZone.forID(string);
                    }
                }
                catch (RuntimeException runtimeException) {
                    // empty catch block
                }
                if (dateTimeZone == null) {
                    dateTimeZone = DateTimeZone.forTimeZone(TimeZone.getDefault());
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            if (dateTimeZone == null) {
                dateTimeZone = UTC;
            }
            if (!cDefault.compareAndSet(null, dateTimeZone)) {
                dateTimeZone = cDefault.get();
            }
        }
        return dateTimeZone;
    }

    public static void setDefault(DateTimeZone dateTimeZone) throws SecurityException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("DateTimeZone.setDefault"));
        }
        if (dateTimeZone == null) {
            throw new IllegalArgumentException("The datetime zone must not be null");
        }
        cDefault.set(dateTimeZone);
    }

    @FromString
    public static DateTimeZone forID(String string) {
        if (string == null) {
            return DateTimeZone.getDefault();
        }
        if (string.equals("UTC")) {
            return UTC;
        }
        DateTimeZone dateTimeZone = DateTimeZone.getProvider().getZone(string);
        if (dateTimeZone != null) {
            return dateTimeZone;
        }
        if (string.startsWith("+") || string.startsWith("-")) {
            int n = DateTimeZone.parseOffset(string);
            if ((long)n == 0L) {
                return UTC;
            }
            string = DateTimeZone.printOffset(n);
            return DateTimeZone.fixedOffsetZone(string, n);
        }
        throw new IllegalArgumentException("The datetime zone id '" + string + "' is not recognised");
    }

    public static DateTimeZone forOffsetHours(int n) throws IllegalArgumentException {
        return DateTimeZone.forOffsetHoursMinutes(n, 0);
    }

    public static DateTimeZone forOffsetHoursMinutes(int n, int n2) throws IllegalArgumentException {
        if (n == 0 && n2 == 0) {
            return UTC;
        }
        if (n < -23 || n > 23) {
            throw new IllegalArgumentException("Hours out of range: " + n);
        }
        if (n2 < -59 || n2 > 59) {
            throw new IllegalArgumentException("Minutes out of range: " + n2);
        }
        if (n > 0 && n2 < 0) {
            throw new IllegalArgumentException("Positive hours must not have negative minutes: " + n2);
        }
        int n3 = 0;
        try {
            int n4 = n * 60;
            n2 = n4 < 0 ? n4 - Math.abs(n2) : n4 + n2;
            n3 = FieldUtils.safeMultiply(n2, 60000);
        }
        catch (ArithmeticException arithmeticException) {
            throw new IllegalArgumentException("Offset is too large");
        }
        return DateTimeZone.forOffsetMillis(n3);
    }

    public static DateTimeZone forOffsetMillis(int n) {
        if (n < -86399999 || n > 86399999) {
            throw new IllegalArgumentException("Millis out of range: " + n);
        }
        String string = DateTimeZone.printOffset(n);
        return DateTimeZone.fixedOffsetZone(string, n);
    }

    public static DateTimeZone forTimeZone(TimeZone timeZone) {
        if (timeZone == null) {
            return DateTimeZone.getDefault();
        }
        String string = timeZone.getID();
        if (string == null) {
            throw new IllegalArgumentException("The TimeZone id must not be null");
        }
        if (string.equals("UTC")) {
            return UTC;
        }
        DateTimeZone dateTimeZone = null;
        String string2 = DateTimeZone.getConvertedId(string);
        Provider provider = DateTimeZone.getProvider();
        if (string2 != null) {
            dateTimeZone = provider.getZone(string2);
        }
        if (dateTimeZone == null) {
            dateTimeZone = provider.getZone(string);
        }
        if (dateTimeZone != null) {
            return dateTimeZone;
        }
        if (string2 == null && ((string2 = string).startsWith("GMT+") || string2.startsWith("GMT-"))) {
            char c;
            if ((string2 = string2.substring(3)).length() > 2 && (c = (char)string2.charAt(1)) > '9' && Character.isDigit(c)) {
                string2 = DateTimeZone.convertToAsciiNumber(string2);
            }
            if ((long)(c = DateTimeZone.parseOffset(string2)) == 0L) {
                return UTC;
            }
            string2 = DateTimeZone.printOffset(c);
            return DateTimeZone.fixedOffsetZone(string2, c);
        }
        throw new IllegalArgumentException("The datetime zone id '" + string + "' is not recognised");
    }

    private static String convertToAsciiNumber(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < stringBuilder.length(); ++i) {
            char c = stringBuilder.charAt(i);
            int n = Character.digit(c, 10);
            if (n < 0) continue;
            stringBuilder.setCharAt(i, (char)(48 + n));
        }
        return stringBuilder.toString();
    }

    private static DateTimeZone fixedOffsetZone(String string, int n) {
        if (n == 0) {
            return UTC;
        }
        return new FixedDateTimeZone(string, null, n, n);
    }

    public static Set<String> getAvailableIDs() {
        return DateTimeZone.getProvider().getAvailableIDs();
    }

    public static Provider getProvider() {
        Provider provider = cProvider.get();
        if (provider == null && !cProvider.compareAndSet(null, provider = DateTimeZone.getDefaultProvider())) {
            provider = cProvider.get();
        }
        return provider;
    }

    public static void setProvider(Provider provider) throws SecurityException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("DateTimeZone.setProvider"));
        }
        if (provider == null) {
            provider = DateTimeZone.getDefaultProvider();
        } else {
            DateTimeZone.validateProvider(provider);
        }
        cProvider.set(provider);
    }

    private static Provider validateProvider(Provider provider) {
        Set<String> set = provider.getAvailableIDs();
        if (set == null || set.size() == 0) {
            throw new IllegalArgumentException("The provider doesn't have any available ids");
        }
        if (!set.contains("UTC")) {
            throw new IllegalArgumentException("The provider doesn't support UTC");
        }
        if (!UTC.equals(provider.getZone("UTC"))) {
            throw new IllegalArgumentException("Invalid UTC zone provided");
        }
        return provider;
    }

    private static Provider getDefaultProvider() {
        Object object;
        try {
            object = System.getProperty("org.joda.time.DateTimeZone.Provider");
            if (object != null) {
                try {
                    Class<?> clazz = Class.forName((String)object, false, DateTimeZone.class.getClassLoader());
                    if (!Provider.class.isAssignableFrom(clazz)) {
                        throw new IllegalArgumentException("System property referred to class that does not implement " + Provider.class);
                    }
                    Provider provider = clazz.asSubclass(Provider.class).getConstructor(new Class[0]).newInstance(new Object[0]);
                    return DateTimeZone.validateProvider(provider);
                }
                catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        try {
            object = System.getProperty("org.joda.time.DateTimeZone.Folder");
            if (object != null) {
                try {
                    ZoneInfoProvider zoneInfoProvider = new ZoneInfoProvider(new File((String)object));
                    return DateTimeZone.validateProvider(zoneInfoProvider);
                }
                catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        try {
            object = new ZoneInfoProvider(DEFAULT_TZ_DATA_PATH);
            return DateTimeZone.validateProvider((Provider)object);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return new UTCProvider();
        }
    }

    public static NameProvider getNameProvider() {
        NameProvider nameProvider = cNameProvider.get();
        if (nameProvider == null && !cNameProvider.compareAndSet(null, nameProvider = DateTimeZone.getDefaultNameProvider())) {
            nameProvider = cNameProvider.get();
        }
        return nameProvider;
    }

    public static void setNameProvider(NameProvider nameProvider) throws SecurityException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("DateTimeZone.setNameProvider"));
        }
        if (nameProvider == null) {
            nameProvider = DateTimeZone.getDefaultNameProvider();
        }
        cNameProvider.set(nameProvider);
    }

    private static NameProvider getDefaultNameProvider() {
        NameProvider nameProvider;
        block6: {
            nameProvider = null;
            try {
                String string = System.getProperty("org.joda.time.DateTimeZone.NameProvider");
                if (string == null) break block6;
                try {
                    Class<?> clazz = Class.forName(string, false, DateTimeZone.class.getClassLoader());
                    if (!NameProvider.class.isAssignableFrom(clazz)) {
                        throw new IllegalArgumentException("System property referred to class that does not implement " + NameProvider.class);
                    }
                    nameProvider = clazz.asSubclass(NameProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
        if (nameProvider == null) {
            nameProvider = new DefaultNameProvider();
        }
        return nameProvider;
    }

    private static String getConvertedId(String string) {
        return LazyInit.CONVERSION_MAP.get(string);
    }

    private static int parseOffset(String string) {
        return -((int)LazyInit.OFFSET_FORMATTER.parseMillis(string));
    }

    private static String printOffset(int n) {
        StringBuffer stringBuffer = new StringBuffer();
        if (n >= 0) {
            stringBuffer.append('+');
        } else {
            stringBuffer.append('-');
            n = -n;
        }
        int n2 = n / 3600000;
        FormatUtils.appendPaddedInteger(stringBuffer, n2, 2);
        int n3 = (n -= n2 * 3600000) / 60000;
        stringBuffer.append(':');
        FormatUtils.appendPaddedInteger(stringBuffer, n3, 2);
        if ((n -= n3 * 60000) == 0) {
            return stringBuffer.toString();
        }
        int n4 = n / 1000;
        stringBuffer.append(':');
        FormatUtils.appendPaddedInteger(stringBuffer, n4, 2);
        if ((n -= n4 * 1000) == 0) {
            return stringBuffer.toString();
        }
        stringBuffer.append('.');
        FormatUtils.appendPaddedInteger(stringBuffer, n, 3);
        return stringBuffer.toString();
    }

    protected DateTimeZone(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.iID = string;
    }

    @ToString
    public final String getID() {
        return this.iID;
    }

    public abstract String getNameKey(long var1);

    public final String getShortName(long l) {
        return this.getShortName(l, null);
    }

    public String getShortName(long l, Locale locale) {
        String string;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if ((string = this.getNameKey(l)) == null) {
            return this.iID;
        }
        NameProvider nameProvider = DateTimeZone.getNameProvider();
        String string2 = nameProvider instanceof DefaultNameProvider ? ((DefaultNameProvider)nameProvider).getShortName(locale, this.iID, string, this.isStandardOffset(l)) : nameProvider.getShortName(locale, this.iID, string);
        if (string2 != null) {
            return string2;
        }
        return DateTimeZone.printOffset(this.getOffset(l));
    }

    public final String getName(long l) {
        return this.getName(l, null);
    }

    public String getName(long l, Locale locale) {
        String string;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if ((string = this.getNameKey(l)) == null) {
            return this.iID;
        }
        NameProvider nameProvider = DateTimeZone.getNameProvider();
        String string2 = nameProvider instanceof DefaultNameProvider ? ((DefaultNameProvider)nameProvider).getName(locale, this.iID, string, this.isStandardOffset(l)) : nameProvider.getName(locale, this.iID, string);
        if (string2 != null) {
            return string2;
        }
        return DateTimeZone.printOffset(this.getOffset(l));
    }

    public abstract int getOffset(long var1);

    public final int getOffset(ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.getOffset(DateTimeUtils.currentTimeMillis());
        }
        return this.getOffset(readableInstant.getMillis());
    }

    public abstract int getStandardOffset(long var1);

    public boolean isStandardOffset(long l) {
        return this.getOffset(l) == this.getStandardOffset(l);
    }

    public int getOffsetFromLocal(long l) {
        int n;
        int n2;
        long l2;
        long l3;
        int n3;
        int n4 = this.getOffset(l);
        if (n4 != (n3 = this.getOffset(l3 = l - (long)n4))) {
            if (n4 - n3 < 0) {
                long l4;
                long l5 = this.nextTransition(l3);
                if (l5 == l - (long)n4) {
                    l5 = Long.MAX_VALUE;
                }
                if ((l4 = this.nextTransition(l - (long)n3)) == l - (long)n3) {
                    l4 = Long.MAX_VALUE;
                }
                if (l5 != l4) {
                    return n4;
                }
            }
        } else if (n4 >= 0 && (l2 = this.previousTransition(l3)) < l3 && l3 - l2 <= (long)(n2 = (n = this.getOffset(l2)) - n4)) {
            return n;
        }
        return n3;
    }

    public long convertUTCToLocal(long l) {
        int n = this.getOffset(l);
        long l2 = l + (long)n;
        if ((l ^ l2) < 0L && (l ^ (long)n) >= 0L) {
            throw new ArithmeticException("Adding time zone offset caused overflow");
        }
        return l2;
    }

    public long convertLocalToUTC(long l, boolean bl, long l2) {
        int n = this.getOffset(l2);
        long l3 = l - (long)n;
        int n2 = this.getOffset(l3);
        if (n2 == n) {
            return l3;
        }
        return this.convertLocalToUTC(l, bl);
    }

    public long convertLocalToUTC(long l, boolean bl) {
        long l2;
        int n;
        int n2 = this.getOffset(l);
        if (n2 != (n = this.getOffset(l - (long)n2)) && (bl || n2 < 0)) {
            long l3;
            l2 = this.nextTransition(l - (long)n2);
            if (l2 == l - (long)n2) {
                l2 = Long.MAX_VALUE;
            }
            if ((l3 = this.nextTransition(l - (long)n)) == l - (long)n) {
                l3 = Long.MAX_VALUE;
            }
            if (l2 != l3) {
                if (bl) {
                    throw new IllegalInstantException(l, this.getID());
                }
                n = n2;
            }
        }
        if ((l ^ (l2 = l - (long)n)) < 0L && (l ^ (long)n) < 0L) {
            throw new ArithmeticException("Subtracting time zone offset caused overflow");
        }
        return l2;
    }

    public long getMillisKeepLocal(DateTimeZone dateTimeZone, long l) {
        if (dateTimeZone == null) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        if (dateTimeZone == this) {
            return l;
        }
        long l2 = this.convertUTCToLocal(l);
        return dateTimeZone.convertLocalToUTC(l2, false, l);
    }

    public boolean isLocalDateTimeGap(LocalDateTime localDateTime) {
        if (this.isFixed()) {
            return false;
        }
        try {
            localDateTime.toDateTime(this);
            return false;
        }
        catch (IllegalInstantException illegalInstantException) {
            return true;
        }
    }

    public long adjustOffset(long l, boolean bl) {
        long l2;
        long l3 = l - 10800000L;
        long l4 = l + 10800000L;
        long l5 = this.getOffset(l3);
        if (l5 <= (l2 = (long)this.getOffset(l4))) {
            return l;
        }
        long l6 = l5 - l2;
        long l7 = this.nextTransition(l3);
        long l8 = l7 - l6;
        long l9 = l7 + l6;
        if (l < l8 || l >= l9) {
            return l;
        }
        long l10 = l - l8;
        if (l10 >= l6) {
            return bl ? l : l - l6;
        }
        return bl ? l + l6 : l;
    }

    public abstract boolean isFixed();

    public abstract long nextTransition(long var1);

    public abstract long previousTransition(long var1);

    public TimeZone toTimeZone() {
        return TimeZone.getTimeZone(this.iID);
    }

    public abstract boolean equals(Object var1);

    public int hashCode() {
        return 57 + this.getID().hashCode();
    }

    public String toString() {
        return this.getID();
    }

    protected Object writeReplace() throws ObjectStreamException {
        return new Stub(this.iID);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class LazyInit {
        static final Map<String, String> CONVERSION_MAP = LazyInit.buildMap();
        static final DateTimeFormatter OFFSET_FORMATTER = LazyInit.buildFormatter();

        LazyInit() {
        }

        private static DateTimeFormatter buildFormatter() {
            BaseChronology baseChronology = new BaseChronology(){
                private static final long serialVersionUID = -3128740902654445468L;

                public DateTimeZone getZone() {
                    return null;
                }

                public Chronology withUTC() {
                    return this;
                }

                public Chronology withZone(DateTimeZone dateTimeZone) {
                    return this;
                }

                public String toString() {
                    return this.getClass().getName();
                }
            };
            return new DateTimeFormatterBuilder().appendTimeZoneOffset(null, true, 2, 4).toFormatter().withChronology(baseChronology);
        }

        private static Map<String, String> buildMap() {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("GMT", "UTC");
            hashMap.put("WET", "WET");
            hashMap.put("CET", "CET");
            hashMap.put("MET", "CET");
            hashMap.put("ECT", "CET");
            hashMap.put("EET", "EET");
            hashMap.put("MIT", "Pacific/Apia");
            hashMap.put("HST", "Pacific/Honolulu");
            hashMap.put("AST", "America/Anchorage");
            hashMap.put("PST", "America/Los_Angeles");
            hashMap.put("MST", "America/Denver");
            hashMap.put("PNT", "America/Phoenix");
            hashMap.put("CST", "America/Chicago");
            hashMap.put("EST", "America/New_York");
            hashMap.put("IET", "America/Indiana/Indianapolis");
            hashMap.put("PRT", "America/Puerto_Rico");
            hashMap.put("CNT", "America/St_Johns");
            hashMap.put("AGT", "America/Argentina/Buenos_Aires");
            hashMap.put("BET", "America/Sao_Paulo");
            hashMap.put("ART", "Africa/Cairo");
            hashMap.put("CAT", "Africa/Harare");
            hashMap.put("EAT", "Africa/Addis_Ababa");
            hashMap.put("NET", "Asia/Yerevan");
            hashMap.put("PLT", "Asia/Karachi");
            hashMap.put("IST", "Asia/Kolkata");
            hashMap.put("BST", "Asia/Dhaka");
            hashMap.put("VST", "Asia/Ho_Chi_Minh");
            hashMap.put("CTT", "Asia/Shanghai");
            hashMap.put("JST", "Asia/Tokyo");
            hashMap.put("ACT", "Australia/Darwin");
            hashMap.put("AET", "Australia/Sydney");
            hashMap.put("SST", "Pacific/Guadalcanal");
            hashMap.put("NST", "Pacific/Auckland");
            return Collections.unmodifiableMap(hashMap);
        }
    }

    private static final class Stub
    implements Serializable {
        private static final long serialVersionUID = -6471952376487863581L;
        private transient String iID;

        Stub(String string) {
            this.iID = string;
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeUTF(this.iID);
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException {
            this.iID = objectInputStream.readUTF();
        }

        private Object readResolve() throws ObjectStreamException {
            return DateTimeZone.forID(this.iID);
        }
    }
}

