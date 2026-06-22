/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.spi.ContextAware;
import java.util.Date;
import java.util.concurrent.Future;

public interface ArchiveRemover
extends ContextAware {
    public void clean(Date var1);

    public void setMaxHistory(int var1);

    public void setTotalSizeCap(long var1);

    public Future<?> cleanAsynchronously(Date var1);
}

