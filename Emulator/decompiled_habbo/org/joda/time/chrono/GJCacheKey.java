/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

class GJCacheKey {
    private final DateTimeZone zone;
    private final Instant cutoverInstant;
    private final int minDaysInFirstWeek;

    GJCacheKey(DateTimeZone dateTimeZone, Instant instant, int n) {
        this.zone = dateTimeZone;
        this.cutoverInstant = instant;
        this.minDaysInFirstWeek = n;
    }

    public int hashCode() {
        int n = 1;
        n = 31 * n + (this.cutoverInstant == null ? 0 : this.cutoverInstant.hashCode());
        n = 31 * n + this.minDaysInFirstWeek;
        n = 31 * n + (this.zone == null ? 0 : this.zone.hashCode());
        return n;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof GJCacheKey)) {
            return false;
        }
        GJCacheKey gJCacheKey = (GJCacheKey)object;
        if (this.cutoverInstant == null ? gJCacheKey.cutoverInstant != null : !this.cutoverInstant.equals(gJCacheKey.cutoverInstant)) {
            return false;
        }
        if (this.minDaysInFirstWeek != gJCacheKey.minDaysInFirstWeek) {
            return false;
        }
        return !(this.zone == null ? gJCacheKey.zone != null : !this.zone.equals(gJCacheKey.zone));
    }
}

