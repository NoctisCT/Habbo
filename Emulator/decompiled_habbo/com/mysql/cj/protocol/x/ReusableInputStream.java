/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.protocol.x;

import java.io.FilterInputStream;
import java.io.InputStream;

public class ReusableInputStream
extends FilterInputStream {
    protected ReusableInputStream(InputStream in) {
        super(in);
    }

    public InputStream setInputStream(InputStream newIn) {
        InputStream previousIn = this.in;
        this.in = newIn;
        return previousIn;
    }
}

