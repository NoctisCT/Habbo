/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.tz;

import java.util.Set;
import org.joda.time.DateTimeZone;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Provider {
    public DateTimeZone getZone(String var1);

    public Set<String> getAvailableIDs();
}

