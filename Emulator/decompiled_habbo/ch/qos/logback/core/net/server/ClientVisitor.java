/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.Client;

public interface ClientVisitor<T extends Client> {
    public void visit(T var1);
}

