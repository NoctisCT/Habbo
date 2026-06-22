/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.status;

import ch.qos.logback.core.status.OnPrintStreamStatusListenerBase;
import java.io.PrintStream;

public class OnErrorConsoleStatusListener
extends OnPrintStreamStatusListenerBase {
    @Override
    protected PrintStream getPrintStream() {
        return System.err;
    }
}

