/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.util.COWArrayList;
import java.util.Iterator;

public class AppenderAttachableImpl<E>
implements AppenderAttachable<E> {
    private final COWArrayList<Appender<E>> appenderList = new COWArrayList<Appender>(new Appender[0]);
    static final long START = System.currentTimeMillis();

    @Override
    public void addAppender(Appender<E> newAppender) {
        if (newAppender == null) {
            throw new IllegalArgumentException("Null argument disallowed");
        }
        this.appenderList.addIfAbsent(newAppender);
    }

    public int appendLoopOnAppenders(E e) {
        int size = 0;
        Appender<E>[] appenderArray = this.appenderList.asTypedArray();
        int len = appenderArray.length;
        for (int i = 0; i < len; ++i) {
            appenderArray[i].doAppend(e);
            ++size;
        }
        return size;
    }

    @Override
    public Iterator<Appender<E>> iteratorForAppenders() {
        return this.appenderList.iterator();
    }

    @Override
    public Appender<E> getAppender(String name) {
        if (name == null) {
            return null;
        }
        for (Appender<E> appender : this.appenderList) {
            if (!name.equals(appender.getName())) continue;
            return appender;
        }
        return null;
    }

    @Override
    public boolean isAttached(Appender<E> appender) {
        if (appender == null) {
            return false;
        }
        for (Appender<E> a : this.appenderList) {
            if (a != appender) continue;
            return true;
        }
        return false;
    }

    @Override
    public void detachAndStopAllAppenders() {
        for (Appender<E> a : this.appenderList) {
            a.stop();
        }
        this.appenderList.clear();
    }

    @Override
    public boolean detachAppender(Appender<E> appender) {
        if (appender == null) {
            return false;
        }
        boolean result = this.appenderList.remove(appender);
        return result;
    }

    @Override
    public boolean detachAppender(String name) {
        if (name == null) {
            return false;
        }
        boolean removed = false;
        for (Appender<E> a : this.appenderList) {
            if (!name.equals(a.getName())) continue;
            removed = this.appenderList.remove(a);
            break;
        }
        return removed;
    }
}

