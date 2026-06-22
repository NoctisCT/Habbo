/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.tz;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.joda.time.DateTimeUtils;
import org.joda.time.tz.NameProvider;

public class DefaultNameProvider
implements NameProvider {
    private HashMap<Locale, Map<String, Map<String, Object>>> iByLocaleCache = this.createCache();
    private HashMap<Locale, Map<String, Map<Boolean, Object>>> iByLocaleCache2 = this.createCache();

    public String getShortName(Locale locale, String string, String string2) {
        String[] stringArray = this.getNameSet(locale, string, string2);
        return stringArray == null ? null : stringArray[0];
    }

    public String getName(Locale locale, String string, String string2) {
        String[] stringArray = this.getNameSet(locale, string, string2);
        return stringArray == null ? null : stringArray[1];
    }

    private synchronized String[] getNameSet(Locale locale, String string, String string2) {
        HashMap hashMap;
        if (locale == null || string == null || string2 == null) {
            return null;
        }
        HashMap hashMap2 = this.iByLocaleCache.get(locale);
        if (hashMap2 == null) {
            hashMap2 = this.createCache();
            this.iByLocaleCache.put(locale, hashMap2);
        }
        if ((hashMap = hashMap2.get(string)) == null) {
            hashMap = this.createCache();
            hashMap2.put(string, hashMap);
            String[][] stringArray = DateTimeUtils.getDateFormatSymbols(Locale.ENGLISH).getZoneStrings();
            String[] stringArray2 = null;
            for (String[] stringArray3 : stringArray) {
                if (stringArray3 == null || stringArray3.length < 5 || !string.equals(stringArray3[0])) continue;
                stringArray2 = stringArray3;
                break;
            }
            String[][] stringArray4 = DateTimeUtils.getDateFormatSymbols(locale).getZoneStrings();
            String[] stringArray5 = null;
            for (String[] stringArray6 : stringArray4) {
                if (stringArray6 == null || stringArray6.length < 5 || !string.equals(stringArray6[0])) continue;
                stringArray5 = stringArray6;
                break;
            }
            if (stringArray2 != null && stringArray5 != null) {
                hashMap.put(stringArray2[2], new String[]{stringArray5[2], stringArray5[1]});
                if (stringArray2[2].equals(stringArray2[4])) {
                    hashMap.put(stringArray2[4] + "-Summer", new String[]{stringArray5[4], stringArray5[3]});
                } else {
                    hashMap.put(stringArray2[4], new String[]{stringArray5[4], stringArray5[3]});
                }
            }
        }
        return (String[])hashMap.get(string2);
    }

    public String getShortName(Locale locale, String string, String string2, boolean bl) {
        String[] stringArray = this.getNameSet(locale, string, string2, bl);
        return stringArray == null ? null : stringArray[0];
    }

    public String getName(Locale locale, String string, String string2, boolean bl) {
        String[] stringArray = this.getNameSet(locale, string, string2, bl);
        return stringArray == null ? null : stringArray[1];
    }

    private synchronized String[] getNameSet(Locale locale, String string, String string2, boolean bl) {
        HashMap hashMap;
        HashMap hashMap2;
        if (locale == null || string == null || string2 == null) {
            return null;
        }
        if (string.startsWith("Etc/")) {
            string = string.substring(4);
        }
        if ((hashMap2 = this.iByLocaleCache2.get(locale)) == null) {
            hashMap2 = this.createCache();
            this.iByLocaleCache2.put(locale, hashMap2);
        }
        if ((hashMap = hashMap2.get(string)) == null) {
            hashMap = this.createCache();
            hashMap2.put(string, hashMap);
            String[][] stringArray = DateTimeUtils.getDateFormatSymbols(Locale.ENGLISH).getZoneStrings();
            String[] stringArray2 = null;
            for (String[] stringArray3 : stringArray) {
                if (stringArray3 == null || stringArray3.length < 5 || !string.equals(stringArray3[0])) continue;
                stringArray2 = stringArray3;
                break;
            }
            String[][] stringArray4 = DateTimeUtils.getDateFormatSymbols(locale).getZoneStrings();
            String[] stringArray5 = null;
            for (String[] stringArray6 : stringArray4) {
                if (stringArray6 == null || stringArray6.length < 5 || !string.equals(stringArray6[0])) continue;
                stringArray5 = stringArray6;
                break;
            }
            if (stringArray2 != null && stringArray5 != null) {
                hashMap.put(Boolean.TRUE, new String[]{stringArray5[2], stringArray5[1]});
                hashMap.put(Boolean.FALSE, new String[]{stringArray5[4], stringArray5[3]});
            }
        }
        return (String[])hashMap.get(bl);
    }

    private HashMap createCache() {
        return new HashMap(7);
    }
}

