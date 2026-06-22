/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public interface Layout<E>
extends ContextAware,
LifeCycle {
    public String doLayout(E var1);

    public String getFileHeader();

    public String getPresentationHeader();

    public String getPresentationFooter();

    public String getFileFooter();

    public String getContentType();
}

