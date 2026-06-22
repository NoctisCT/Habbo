/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.net;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.boolex.OnErrorEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.net.SMTPAppenderBase;
import org.slf4j.Marker;

public class SMTPAppender
extends SMTPAppenderBase<ILoggingEvent> {
    static final String DEFAULT_SUBJECT_PATTERN = "%logger{20} - %m";
    private int bufferSize = 512;
    private boolean includeCallerData = false;

    public SMTPAppender() {
    }

    @Override
    public void start() {
        if (this.eventEvaluator == null) {
            OnErrorEvaluator onError = new OnErrorEvaluator();
            onError.setContext(this.getContext());
            onError.setName("onError");
            onError.start();
            this.eventEvaluator = onError;
        }
        super.start();
    }

    public SMTPAppender(EventEvaluator<ILoggingEvent> eventEvaluator) {
        this.eventEvaluator = eventEvaluator;
    }

    @Override
    protected void subAppend(CyclicBuffer<ILoggingEvent> cb, ILoggingEvent event) {
        if (this.includeCallerData) {
            event.getCallerData();
        }
        event.prepareForDeferredProcessing();
        cb.add(event);
    }

    @Override
    protected void fillBuffer(CyclicBuffer<ILoggingEvent> cb, StringBuffer sbuf) {
        int len = cb.length();
        for (int i = 0; i < len; ++i) {
            ILoggingEvent event = cb.get();
            sbuf.append(this.layout.doLayout(event));
        }
    }

    @Override
    protected boolean eventMarksEndOfLife(ILoggingEvent eventObject) {
        Marker marker = eventObject.getMarker();
        if (marker == null) {
            return false;
        }
        return marker.contains(ClassicConstants.FINALIZE_SESSION_MARKER);
    }

    @Override
    protected Layout<ILoggingEvent> makeSubjectLayout(String subjectStr) {
        if (subjectStr == null) {
            subjectStr = DEFAULT_SUBJECT_PATTERN;
        }
        PatternLayout pl = new PatternLayout();
        pl.setContext(this.getContext());
        pl.setPattern(subjectStr);
        pl.setPostCompileProcessor(null);
        pl.start();
        return pl;
    }

    protected PatternLayout makeNewToPatternLayout(String toPattern) {
        PatternLayout pl = new PatternLayout();
        pl.setPattern(toPattern + "%nopex");
        return pl;
    }

    public boolean isIncludeCallerData() {
        return this.includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }
}

