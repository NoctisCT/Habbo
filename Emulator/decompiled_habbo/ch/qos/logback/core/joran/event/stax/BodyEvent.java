/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.event.stax;

import ch.qos.logback.core.joran.event.stax.StaxEvent;
import javax.xml.stream.Location;

public class BodyEvent
extends StaxEvent {
    private String text;

    BodyEvent(String text, Location location) {
        super(null, location);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    void append(String txt) {
        this.text = this.text + txt;
    }

    public String toString() {
        return "BodyEvent(" + this.getText() + ")" + this.location.getLineNumber() + "," + this.location.getColumnNumber();
    }
}

