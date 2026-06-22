/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.net.ssl.SSLConfiguration;

public interface SSLComponent {
    public SSLConfiguration getSsl();

    public void setSsl(SSLConfiguration var1);
}

