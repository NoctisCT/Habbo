/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.net.server.Client;

interface RemoteAppenderClient
extends Client {
    public void setLoggerContext(LoggerContext var1);
}

