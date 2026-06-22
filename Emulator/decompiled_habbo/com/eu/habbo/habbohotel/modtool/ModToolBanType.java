/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.modtool;

public enum ModToolBanType {
    ACCOUNT("account"),
    MACHINE("machine"),
    SUPER("super"),
    IP("ip"),
    UNKNOWN("???");

    private final String type;

    private ModToolBanType(String type) {
        this.type = type;
    }

    public static ModToolBanType fromString(String type) {
        for (ModToolBanType t : ModToolBanType.values()) {
            if (!t.type.equalsIgnoreCase(type)) continue;
            return t;
        }
        return UNKNOWN;
    }

    public String getType() {
        return this.type;
    }
}

