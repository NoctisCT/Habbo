/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.net;

import ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.AbstractSSLSocketAppender;
import ch.qos.logback.core.spi.PreSerializationTransformer;

public class SSLSocketAppender
extends AbstractSSLSocketAppender<ILoggingEvent> {
    private final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
    private boolean includeCallerData;

    @Override
    protected void postProcessEvent(ILoggingEvent event) {
        if (this.includeCallerData) {
            event.getCallerData();
        }
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }

    @Override
    public PreSerializationTransformer<ILoggingEvent> getPST() {
        return this.pst;
    }
}

