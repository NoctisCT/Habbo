/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.WarnStatus;
import java.util.List;

public abstract class UnsynchronizedAppenderBase<E>
extends ContextAwareBase
implements Appender<E> {
    protected boolean started = false;
    private ThreadLocal<Boolean> guard = new ThreadLocal();
    protected String name;
    private FilterAttachableImpl<E> fai = new FilterAttachableImpl();
    private int statusRepeatCount = 0;
    private int exceptionCount = 0;
    static final int ALLOWED_REPEATS = 3;

    @Override
    public String getName() {
        return this.name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doAppend(E eventObject) {
        if (Boolean.TRUE.equals(this.guard.get())) {
            return;
        }
        try {
            this.guard.set(Boolean.TRUE);
            if (!this.started) {
                if (this.statusRepeatCount++ < 3) {
                    this.addStatus(new WarnStatus("Attempted to append to non started appender [" + this.name + "].", this));
                }
                return;
            }
            if (this.getFilterChainDecision(eventObject) == FilterReply.DENY) {
                return;
            }
            this.append(eventObject);
        }
        catch (Exception e) {
            if (this.exceptionCount++ < 3) {
                this.addError("Appender [" + this.name + "] failed to append.", e);
            }
        }
        finally {
            this.guard.set(Boolean.FALSE);
        }
    }

    protected abstract void append(E var1);

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }

    public String toString() {
        return this.getClass().getName() + "[" + this.name + "]";
    }

    @Override
    public void addFilter(Filter<E> newFilter) {
        this.fai.addFilter(newFilter);
    }

    @Override
    public void clearAllFilters() {
        this.fai.clearAllFilters();
    }

    @Override
    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return this.fai.getCopyOfAttachedFiltersList();
    }

    @Override
    public FilterReply getFilterChainDecision(E event) {
        return this.fai.getFilterChainDecision(event);
    }
}

