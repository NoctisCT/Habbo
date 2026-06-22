/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.event.stax;

import ch.qos.logback.core.joran.event.stax.StaxEvent;
import javax.xml.stream.Location;

public class EndEvent
extends StaxEvent {
    public EndEvent(String name, Location location) {
        super(name, location);
    }

    public String toString() {
        return "EndEvent(" + this.getName() + ")  [" + this.location.getLineNumber() + "," + this.location.getColumnNumber() + "]";
    }
}

