/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.internal.ObjectUtil;
import java.security.KeyStore;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;

final class TrustManagerFactoryWrapper
extends SimpleTrustManagerFactory {
    private final TrustManager tm;

    TrustManagerFactoryWrapper(TrustManager tm) {
        this.tm = ObjectUtil.checkNotNull(tm, "tm");
    }

    @Override
    protected void engineInit(KeyStore keyStore) throws Exception {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{this.tm};
    }
}

