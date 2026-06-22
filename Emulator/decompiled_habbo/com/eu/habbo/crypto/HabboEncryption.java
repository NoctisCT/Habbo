/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.crypto;

import com.eu.habbo.crypto.HabboDiffieHellman;
import com.eu.habbo.crypto.HabboRSACrypto;

public class HabboEncryption {
    private final HabboRSACrypto crypto;
    private final HabboDiffieHellman diffie;

    public HabboEncryption(String e, String n, String d) {
        this.crypto = new HabboRSACrypto(e, n, d);
        this.diffie = new HabboDiffieHellman(this.crypto);
    }

    public HabboRSACrypto getCrypto() {
        return this.crypto;
    }

    public HabboDiffieHellman getDiffie() {
        return this.diffie;
    }
}

