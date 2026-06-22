/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.event.stax;

import ch.qos.logback.core.joran.event.stax.StaxEvent;
import ch.qos.logback.core.joran.spi.ElementPath;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;

public class StartEvent
extends StaxEvent {
    List<Attribute> attributes;
    public ElementPath elementPath;

    StartEvent(ElementPath elementPath, String name, Iterator<Attribute> attributeIterator, Location location) {
        super(name, location);
        this.populateAttributes(attributeIterator);
        this.elementPath = elementPath;
    }

    private void populateAttributes(Iterator<Attribute> attributeIterator) {
        while (attributeIterator.hasNext()) {
            if (this.attributes == null) {
                this.attributes = new ArrayList<Attribute>(2);
            }
            this.attributes.add(attributeIterator.next());
        }
    }

    public ElementPath getElementPath() {
        return this.elementPath;
    }

    public List<Attribute> getAttributeList() {
        return this.attributes;
    }

    Attribute getAttributeByName(String name) {
        if (this.attributes == null) {
            return null;
        }
        for (Attribute attr : this.attributes) {
            if (!name.equals(attr.getName().getLocalPart())) continue;
            return attr;
        }
        return null;
    }

    public String toString() {
        return "StartEvent(" + this.getName() + ")  [" + this.location.getLineNumber() + "," + this.location.getColumnNumber() + "]";
    }
}

