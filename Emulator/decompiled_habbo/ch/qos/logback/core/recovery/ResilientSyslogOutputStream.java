/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.recovery;

import ch.qos.logback.core.net.SyslogOutputStream;
import ch.qos.logback.core.recovery.ResilientOutputStreamBase;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ResilientSyslogOutputStream
extends ResilientOutputStreamBase {
    String syslogHost;
    int port;

    public ResilientSyslogOutputStream(String syslogHost, int port) throws UnknownHostException, SocketException {
        this.syslogHost = syslogHost;
        this.port = port;
        this.os = new SyslogOutputStream(syslogHost, port);
        this.presumedClean = true;
    }

    @Override
    String getDescription() {
        return "syslog [" + this.syslogHost + ":" + this.port + "]";
    }

    @Override
    OutputStream openNewOutputStream() throws IOException {
        return new SyslogOutputStream(this.syslogHost, this.port);
    }

    public String toString() {
        return "c.q.l.c.recovery.ResilientSyslogOutputStream@" + System.identityHashCode(this);
    }
}

