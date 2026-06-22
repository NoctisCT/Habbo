/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.FormattingConverter;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.Status;
import java.util.List;

public abstract class DynamicConverter<E>
extends FormattingConverter<E>
implements LifeCycle,
ContextAware {
    ContextAwareBase cab = new ContextAwareBase(this);
    private List<String> optionList;
    protected boolean started = false;

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

    public void setOptionList(List<String> optionList) {
        this.optionList = optionList;
    }

    public String getFirstOption() {
        if (this.optionList == null || this.optionList.size() == 0) {
            return null;
        }
        return this.optionList.get(0);
    }

    protected List<String> getOptionList() {
        return this.optionList;
    }

    @Override
    public void setContext(Context context) {
        this.cab.setContext(context);
    }

    @Override
    public Context getContext() {
        return this.cab.getContext();
    }

    @Override
    public void addStatus(Status status) {
        this.cab.addStatus(status);
    }

    @Override
    public void addInfo(String msg) {
        this.cab.addInfo(msg);
    }

    @Override
    public void addInfo(String msg, Throwable ex) {
        this.cab.addInfo(msg, ex);
    }

    @Override
    public void addWarn(String msg) {
        this.cab.addWarn(msg);
    }

    @Override
    public void addWarn(String msg, Throwable ex) {
        this.cab.addWarn(msg, ex);
    }

    @Override
    public void addError(String msg) {
        this.cab.addError(msg);
    }

    @Override
    public void addError(String msg, Throwable ex) {
        this.cab.addError(msg, ex);
    }
}

