/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.tz;

import org.joda.time.DateTimeZone;

public class CachedDateTimeZone
extends DateTimeZone {
    private static final long serialVersionUID = 5472298452022250685L;
    private static final int cInfoCacheMask;
    private final DateTimeZone iZone;
    private final transient Info[] iInfoCache = new Info[cInfoCacheMask + 1];

    public static CachedDateTimeZone forZone(DateTimeZone dateTimeZone) {
        if (dateTimeZone instanceof CachedDateTimeZone) {
            return (CachedDateTimeZone)dateTimeZone;
        }
        return new CachedDateTimeZone(dateTimeZone);
    }

    private CachedDateTimeZone(DateTimeZone dateTimeZone) {
        super(dateTimeZone.getID());
        this.iZone = dateTimeZone;
    }

    public DateTimeZone getUncachedZone() {
        return this.iZone;
    }

    public String getNameKey(long l) {
        return this.getInfo(l).getNameKey(l);
    }

    public int getOffset(long l) {
        return this.getInfo(l).getOffset(l);
    }

    public int getStandardOffset(long l) {
        return this.getInfo(l).getStandardOffset(l);
    }

    public boolean isFixed() {
        return this.iZone.isFixed();
    }

    public long nextTransition(long l) {
        return this.iZone.nextTransition(l);
    }

    public long previousTransition(long l) {
        return this.iZone.previousTransition(l);
    }

    public int hashCode() {
        return this.iZone.hashCode();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof CachedDateTimeZone) {
            return this.iZone.equals(((CachedDateTimeZone)object).iZone);
        }
        return false;
    }

    private Info getInfo(long l) {
        Info[] infoArray = this.iInfoCache;
        int n = (int)(l >> 32);
        int n2 = n & cInfoCacheMask;
        Info info = infoArray[n2];
        if (info == null || (int)(info.iPeriodStart >> 32) != n) {
            infoArray[n2] = info = this.createInfo(l);
        }
        return info;
    }

    private Info createInfo(long l) {
        long l2;
        long l3 = l & 0xFFFFFFFF00000000L;
        Info info = new Info(this.iZone, l3);
        long l4 = l3 | 0xFFFFFFFFL;
        Info info2 = info;
        while ((l2 = this.iZone.nextTransition(l3)) != l3 && l2 <= l4) {
            l3 = l2;
            info2 = info2.iNextInfo = new Info(this.iZone, l3);
        }
        return info;
    }

    static {
        int n;
        Integer n2;
        try {
            n2 = Integer.getInteger("org.joda.time.tz.CachedDateTimeZone.size");
        }
        catch (SecurityException securityException) {
            n2 = null;
        }
        if (n2 == null) {
            n = 512;
        } else {
            n = n2;
            --n;
            int n3 = 0;
            while (n > 0) {
                ++n3;
                n >>= 1;
            }
            n = 1 << n3;
        }
        cInfoCacheMask = n - 1;
    }

    private static final class Info {
        public final long iPeriodStart;
        public final DateTimeZone iZoneRef;
        Info iNextInfo;
        private String iNameKey;
        private int iOffset = Integer.MIN_VALUE;
        private int iStandardOffset = Integer.MIN_VALUE;

        Info(DateTimeZone dateTimeZone, long l) {
            this.iPeriodStart = l;
            this.iZoneRef = dateTimeZone;
        }

        public String getNameKey(long l) {
            if (this.iNextInfo == null || l < this.iNextInfo.iPeriodStart) {
                if (this.iNameKey == null) {
                    this.iNameKey = this.iZoneRef.getNameKey(this.iPeriodStart);
                }
                return this.iNameKey;
            }
            return this.iNextInfo.getNameKey(l);
        }

        public int getOffset(long l) {
            if (this.iNextInfo == null || l < this.iNextInfo.iPeriodStart) {
                if (this.iOffset == Integer.MIN_VALUE) {
                    this.iOffset = this.iZoneRef.getOffset(this.iPeriodStart);
                }
                return this.iOffset;
            }
            return this.iNextInfo.getOffset(l);
        }

        public int getStandardOffset(long l) {
            if (this.iNextInfo == null || l < this.iNextInfo.iPeriodStart) {
                if (this.iStandardOffset == Integer.MIN_VALUE) {
                    this.iStandardOffset = this.iZoneRef.getStandardOffset(this.iPeriodStart);
                }
                return this.iStandardOffset;
            }
            return this.iNextInfo.getStandardOffset(l);
        }
    }
}

