/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public class NOPAction
extends Action {
    @Override
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
    }

    @Override
    public void end(InterpretationContext ec, String name) {
    }
}

