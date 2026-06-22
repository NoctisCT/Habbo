/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.helpers.NOPAppender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.sift.AppenderFactory;
import ch.qos.logback.core.spi.AbstractComponentTracker;
import ch.qos.logback.core.spi.ContextAwareImpl;

public class AppenderTracker<E>
extends AbstractComponentTracker<Appender<E>> {
    int nopaWarningCount = 0;
    final Context context;
    final AppenderFactory<E> appenderFactory;
    final ContextAwareImpl contextAware;

    public AppenderTracker(Context context, AppenderFactory<E> appenderFactory) {
        this.context = context;
        this.appenderFactory = appenderFactory;
        this.contextAware = new ContextAwareImpl(context, this);
    }

    @Override
    protected void processPriorToRemoval(Appender<E> component) {
        component.stop();
    }

    @Override
    protected Appender<E> buildComponent(String key) {
        Appender<E> appender = null;
        try {
            appender = this.appenderFactory.buildAppender(this.context, key);
        }
        catch (JoranException je) {
            this.contextAware.addError("Error while building appender with discriminating value [" + key + "]");
        }
        if (appender == null) {
            appender = this.buildNOPAppender(key);
        }
        return appender;
    }

    private NOPAppender<E> buildNOPAppender(String key) {
        if (this.nopaWarningCount < 4) {
            ++this.nopaWarningCount;
            this.contextAware.addError("Building NOPAppender for discriminating value [" + key + "]");
        }
        NOPAppender nopa = new NOPAppender();
        nopa.setContext(this.context);
        nopa.start();
        return nopa;
    }

    @Override
    protected boolean isComponentStale(Appender<E> appender) {
        return !appender.isStarted();
    }
}

