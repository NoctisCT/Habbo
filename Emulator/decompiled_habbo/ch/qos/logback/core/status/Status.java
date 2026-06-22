/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.status;

import java.util.Iterator;

public interface Status {
    public static final int INFO = 0;
    public static final int WARN = 1;
    public static final int ERROR = 2;

    public int getLevel();

    public int getEffectiveLevel();

    public Object getOrigin();

    public String getMessage();

    public Throwable getThrowable();

    public Long getDate();

    public boolean hasChildren();

    public void add(Status var1);

    public boolean remove(Status var1);

    public Iterator<Status> iterator();
}

