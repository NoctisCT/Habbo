/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.tz;

import java.util.Locale;

public interface NameProvider {
    public String getShortName(Locale var1, String var2, String var3);

    public String getName(Locale var1, String var2, String var3);
}

