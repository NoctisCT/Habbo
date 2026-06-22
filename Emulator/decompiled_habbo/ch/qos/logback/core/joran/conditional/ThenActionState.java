/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.event.InPlayListener;
import ch.qos.logback.core.joran.event.SaxEvent;
import java.util.ArrayList;
import java.util.List;

class ThenActionState
implements InPlayListener {
    List<SaxEvent> eventList = new ArrayList<SaxEvent>();
    boolean isRegistered = false;

    ThenActionState() {
    }

    @Override
    public void inPlay(SaxEvent event) {
        this.eventList.add(event);
    }
}

