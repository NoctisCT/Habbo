/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import ch.qos.logback.core.Appender;
import java.util.Iterator;

public interface AppenderAttachable<E> {
    public void addAppender(Appender<E> var1);

    public Iterator<Appender<E>> iteratorForAppenders();

    public Appender<E> getAppender(String var1);

    public boolean isAttached(Appender<E> var1);

    public void detachAndStopAllAppenders();

    public boolean detachAppender(Appender<E> var1);

    public boolean detachAppender(String var1);
}

