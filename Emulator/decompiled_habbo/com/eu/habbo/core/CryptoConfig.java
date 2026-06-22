/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.core;

public class CryptoConfig {
    private final boolean enabled;
    private final String exponent;
    private final String modulus;
    private final String privateExponent;

    public CryptoConfig(boolean enabled, String exponent, String modulus, String privateExponent) {
        this.enabled = enabled;
        this.exponent = exponent;
        this.modulus = modulus;
        this.privateExponent = privateExponent;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getExponent() {
        return this.exponent;
    }

    public String getModulus() {
        return this.modulus;
    }

    public String getPrivateExponent() {
        return this.privateExponent;
    }
}

