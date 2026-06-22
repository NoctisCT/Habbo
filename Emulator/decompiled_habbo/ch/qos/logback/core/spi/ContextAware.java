/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;

public interface ContextAware {
    public void setContext(Context var1);

    public Context getContext();

    public void addStatus(Status var1);

    public void addInfo(String var1);

    public void addInfo(String var1, Throwable var2);

    public void addWarn(String var1);

    public void addWarn(String var1, Throwable var2);

    public void addError(String var1);

    public void addError(String var1, Throwable var2);
}

