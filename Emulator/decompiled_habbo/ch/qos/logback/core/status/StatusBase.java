/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.status;

import ch.qos.logback.core.status.Status;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class StatusBase
implements Status {
    private static final List<Status> EMPTY_LIST = new ArrayList<Status>(0);
    int level;
    final String message;
    final Object origin;
    List<Status> childrenList;
    Throwable throwable;
    long date;

    StatusBase(int level, String msg, Object origin) {
        this(level, msg, origin, null);
    }

    StatusBase(int level, String msg, Object origin, Throwable t) {
        this.level = level;
        this.message = msg;
        this.origin = origin;
        this.throwable = t;
        this.date = System.currentTimeMillis();
    }

    @Override
    public synchronized void add(Status child) {
        if (child == null) {
            throw new NullPointerException("Null values are not valid Status.");
        }
        if (this.childrenList == null) {
            this.childrenList = new ArrayList<Status>();
        }
        this.childrenList.add(child);
    }

    @Override
    public synchronized boolean hasChildren() {
        return this.childrenList != null && this.childrenList.size() > 0;
    }

    @Override
    public synchronized Iterator<Status> iterator() {
        if (this.childrenList != null) {
            return this.childrenList.iterator();
        }
        return EMPTY_LIST.iterator();
    }

    @Override
    public synchronized boolean remove(Status statusToRemove) {
        if (this.childrenList == null) {
            return false;
        }
        return this.childrenList.remove(statusToRemove);
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public synchronized int getEffectiveLevel() {
        int result = this.level;
        Iterator<Status> it = this.iterator();
        while (it.hasNext()) {
            Status s = it.next();
            int effLevel = s.getEffectiveLevel();
            if (effLevel <= result) continue;
            result = effLevel;
        }
        return result;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Object getOrigin() {
        return this.origin;
    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override
    public Long getDate() {
        return this.date;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        switch (this.getEffectiveLevel()) {
            case 0: {
                buf.append("INFO");
                break;
            }
            case 1: {
                buf.append("WARN");
                break;
            }
            case 2: {
                buf.append("ERROR");
            }
        }
        if (this.origin != null) {
            buf.append(" in ");
            buf.append(this.origin);
            buf.append(" -");
        }
        buf.append(" ");
        buf.append(this.message);
        if (this.throwable != null) {
            buf.append(" ");
            buf.append(this.throwable);
        }
        return buf.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.level;
        result = 31 * result + (this.message == null ? 0 : this.message.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        StatusBase other = (StatusBase)obj;
        if (this.level != other.level) {
            return false;
        }
        return !(this.message == null ? other.message != null : !this.message.equals(other.message));
    }
}

