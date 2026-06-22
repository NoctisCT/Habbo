/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Marker;

public class OnMarkerEvaluator
extends EventEvaluatorBase<ILoggingEvent> {
    List<String> markerList = new ArrayList<String>();

    public void addMarker(String markerStr) {
        this.markerList.add(markerStr);
    }

    @Override
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        Marker eventsMarker = event.getMarker();
        if (eventsMarker == null) {
            return false;
        }
        for (String markerStr : this.markerList) {
            if (!eventsMarker.contains(markerStr)) continue;
            return true;
        }
        return false;
    }
}

