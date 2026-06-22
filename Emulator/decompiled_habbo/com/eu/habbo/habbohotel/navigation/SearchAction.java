/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.navigation;

public enum SearchAction {
    NONE(0),
    MORE(1),
    BACK(2);

    public final int type;

    private SearchAction(int type) {
        this.type = type;
    }
}

