/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.navigation;

public enum ListMode {
    LIST(0),
    THUMBNAILS(1),
    FORCED_THUNBNAILS(2);

    public final int type;

    private ListMode(int type) {
        this.type = type;
    }

    public static ListMode fromType(int type) {
        for (ListMode m : ListMode.values()) {
            if (m.type != type) continue;
            return m;
        }
        return LIST;
    }
}

