/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.DefaultClass;
import ch.qos.logback.core.sift.Discriminator;
import ch.qos.logback.core.sift.SiftingAppenderBase;
import org.slf4j.Marker;

public class SiftingAppender
extends SiftingAppenderBase<ILoggingEvent> {
    @Override
    protected long getTimestamp(ILoggingEvent event) {
        return event.getTimeStamp();
    }

    @Override
    @DefaultClass(value=MDCBasedDiscriminator.class)
    public void setDiscriminator(Discriminator<ILoggingEvent> discriminator) {
        super.setDiscriminator(discriminator);
    }

    @Override
    protected boolean eventMarksEndOfLife(ILoggingEvent event) {
        Marker marker = event.getMarker();
        if (marker == null) {
            return false;
        }
        return marker.contains(ClassicConstants.FINALIZE_SESSION_MARKER);
    }
}

