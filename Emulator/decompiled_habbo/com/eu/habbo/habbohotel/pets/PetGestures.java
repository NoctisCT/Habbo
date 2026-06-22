/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.pets;

public enum PetGestures {
    THIRSTY("thr"),
    TIRED("trd"),
    PLAYFULL("plf"),
    HUNGRY("hng"),
    SAD("sad"),
    HAPPY("sml"),
    QUESTION("que"),
    LVLUP("exp"),
    LOVE("lov"),
    WARNING("und"),
    ENERGY("nrg");

    private final String key;

    private PetGestures(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

