/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.status;

import ch.qos.logback.core.status.StatusBase;

public class InfoStatus
extends StatusBase {
    public InfoStatus(String msg, Object origin) {
        super(0, msg, origin);
    }

    public InfoStatus(String msg, Object origin, Throwable t) {
        super(0, msg, origin, t);
    }
}

