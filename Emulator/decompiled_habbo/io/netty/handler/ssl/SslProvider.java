/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSsl;

public enum SslProvider {
    JDK,
    OPENSSL,
    OPENSSL_REFCNT;


    public static boolean isAlpnSupported(SslProvider provider) {
        switch (provider) {
            case JDK: {
                return JdkAlpnApplicationProtocolNegotiator.isAlpnSupported();
            }
            case OPENSSL: 
            case OPENSSL_REFCNT: {
                return OpenSsl.isAlpnSupported();
            }
        }
        throw new Error("Unknown SslProvider: " + (Object)((Object)provider));
    }
}

