/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users;

public enum DanceType {
    NONE(0),
    HAB_HOP(1),
    POGO_MOGO(2),
    DUCK_FUNK(3),
    THE_ROLLIE(4);

    private final int type;

    private DanceType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}

