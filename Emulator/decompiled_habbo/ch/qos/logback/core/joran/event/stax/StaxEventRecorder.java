/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.event.stax;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.event.stax.BodyEvent;
import ch.qos.logback.core.joran.event.stax.EndEvent;
import ch.qos.logback.core.joran.event.stax.StartEvent;
import ch.qos.logback.core.joran.event.stax.StaxEvent;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StaxEventRecorder
extends ContextAwareBase {
    List<StaxEvent> eventList = new ArrayList<StaxEvent>();
    ElementPath globalElementPath = new ElementPath();

    public StaxEventRecorder(Context context) {
        this.setContext(context);
    }

    public void recordEvents(InputStream inputStream) throws JoranException {
        try {
            XMLEventReader xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
            this.read(xmlEventReader);
        }
        catch (XMLStreamException e) {
            throw new JoranException("Problem parsing XML document. See previously reported errors.", e);
        }
    }

    public List<StaxEvent> getEventList() {
        return this.eventList;
    }

    private void read(XMLEventReader xmlEventReader) throws XMLStreamException {
        while (xmlEventReader.hasNext()) {
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            switch (xmlEvent.getEventType()) {
                case 1: {
                    this.addStartElement(xmlEvent);
                    break;
                }
                case 4: {
                    this.addCharacters(xmlEvent);
                    break;
                }
                case 2: {
                    this.addEndEvent(xmlEvent);
                    break;
                }
            }
        }
    }

    private void addStartElement(XMLEvent xmlEvent) {
        StartElement se = xmlEvent.asStartElement();
        String tagName = se.getName().getLocalPart();
        this.globalElementPath.push(tagName);
        ElementPath current = this.globalElementPath.duplicate();
        StartEvent startEvent = new StartEvent(current, tagName, se.getAttributes(), se.getLocation());
        this.eventList.add(startEvent);
    }

    private void addCharacters(XMLEvent xmlEvent) {
        Characters characters = xmlEvent.asCharacters();
        StaxEvent lastEvent = this.getLastEvent();
        if (lastEvent instanceof BodyEvent) {
            BodyEvent be = (BodyEvent)lastEvent;
            be.append(characters.getData());
        } else if (!characters.isWhiteSpace()) {
            BodyEvent bodyEvent = new BodyEvent(characters.getData(), xmlEvent.getLocation());
            this.eventList.add(bodyEvent);
        }
    }

    private void addEndEvent(XMLEvent xmlEvent) {
        EndElement ee = xmlEvent.asEndElement();
        String tagName = ee.getName().getLocalPart();
        EndEvent endEvent = new EndEvent(tagName, ee.getLocation());
        this.eventList.add(endEvent);
        this.globalElementPath.pop();
    }

    StaxEvent getLastEvent() {
        if (this.eventList.isEmpty()) {
            return null;
        }
        int size = this.eventList.size();
        if (size == 0) {
            return null;
        }
        return this.eventList.get(size - 1);
    }
}

