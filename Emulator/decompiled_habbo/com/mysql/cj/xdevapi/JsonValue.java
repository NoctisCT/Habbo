/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.xdevapi;

public interface JsonValue {
    default public String toFormattedString() {
        return this.toString();
    }
}

