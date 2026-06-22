/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.tz;

import java.util.Collections;
import java.util.Set;
import org.joda.time.DateTimeZone;
import org.joda.time.tz.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class UTCProvider
implements Provider {
    private static final Set<String> AVAILABLE_IDS = Collections.singleton("UTC");

    @Override
    public DateTimeZone getZone(String string) {
        if ("UTC".equalsIgnoreCase(string)) {
            return DateTimeZone.UTC;
        }
        return null;
    }

    @Override
    public Set<String> getAvailableIDs() {
        return AVAILABLE_IDS;
    }
}

