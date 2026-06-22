/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.conditional.IfAction;
import ch.qos.logback.core.joran.conditional.ThenOrElseActionBase;
import ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;

public class ElseAction
extends ThenOrElseActionBase {
    @Override
    void registerEventList(IfAction ifAction, List<SaxEvent> eventList) {
        ifAction.setElseSaxEventList(eventList);
    }
}

