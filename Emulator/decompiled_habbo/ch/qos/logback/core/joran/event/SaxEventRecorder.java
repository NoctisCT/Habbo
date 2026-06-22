/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.event;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.event.BodyEvent;
import ch.qos.logback.core.joran.event.EndEvent;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.StartEvent;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareImpl;
import ch.qos.logback.core.status.Status;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxEventRecorder
extends DefaultHandler
implements ContextAware {
    final ContextAwareImpl cai;
    public List<SaxEvent> saxEventList = new ArrayList<SaxEvent>();
    Locator locator;
    ElementPath globalElementPath = new ElementPath();

    public SaxEventRecorder(Context context) {
        this.cai = new ContextAwareImpl(context, this);
    }

    public final void recordEvents(InputStream inputStream) throws JoranException {
        this.recordEvents(new InputSource(inputStream));
    }

    public List<SaxEvent> recordEvents(InputSource inputSource) throws JoranException {
        SAXParser saxParser = this.buildSaxParser();
        try {
            saxParser.parse(inputSource, (DefaultHandler)this);
            return this.saxEventList;
        }
        catch (IOException ie) {
            this.handleError("I/O error occurred while parsing xml file", ie);
        }
        catch (SAXException se) {
            throw new JoranException("Problem parsing XML document. See previously reported errors.", se);
        }
        catch (Exception ex) {
            this.handleError("Unexpected exception while parsing XML document.", ex);
        }
        throw new IllegalStateException("This point can never be reached");
    }

    private void handleError(String errMsg, Throwable t) throws JoranException {
        this.addError(errMsg, t);
        throw new JoranException(errMsg, t);
    }

    private SAXParser buildSaxParser() throws JoranException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            spf.setNamespaceAware(true);
            return spf.newSAXParser();
        }
        catch (Exception pce) {
            String errMsg = "Parser configuration error occurred";
            this.addError(errMsg, pce);
            throw new JoranException(errMsg, pce);
        }
    }

    @Override
    public void startDocument() {
    }

    public Locator getLocator() {
        return this.locator;
    }

    @Override
    public void setDocumentLocator(Locator l) {
        this.locator = l;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        String tagName = this.getTagName(localName, qName);
        this.globalElementPath.push(tagName);
        ElementPath current = this.globalElementPath.duplicate();
        this.saxEventList.add(new StartEvent(current, namespaceURI, localName, qName, atts, this.getLocator()));
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String bodyStr = new String(ch, start, length);
        SaxEvent lastEvent = this.getLastEvent();
        if (lastEvent instanceof BodyEvent) {
            BodyEvent be = (BodyEvent)lastEvent;
            be.append(bodyStr);
        } else if (!this.isSpaceOnly(bodyStr)) {
            this.saxEventList.add(new BodyEvent(bodyStr, this.getLocator()));
        }
    }

    boolean isSpaceOnly(String bodyStr) {
        String bodyTrimmed = bodyStr.trim();
        return bodyTrimmed.length() == 0;
    }

    SaxEvent getLastEvent() {
        if (this.saxEventList.isEmpty()) {
            return null;
        }
        int size = this.saxEventList.size();
        return this.saxEventList.get(size - 1);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) {
        this.saxEventList.add(new EndEvent(namespaceURI, localName, qName, this.getLocator()));
        this.globalElementPath.pop();
    }

    String getTagName(String localName, String qName) {
        String tagName = localName;
        if (tagName == null || tagName.length() < 1) {
            tagName = qName;
        }
        return tagName;
    }

    @Override
    public void error(SAXParseException spe) throws SAXException {
        this.addError("XML_PARSING - Parsing error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
        this.addError(spe.toString());
    }

    @Override
    public void fatalError(SAXParseException spe) throws SAXException {
        this.addError("XML_PARSING - Parsing fatal error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
        this.addError(spe.toString());
    }

    @Override
    public void warning(SAXParseException spe) throws SAXException {
        this.addWarn("XML_PARSING - Parsing warning on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber(), spe);
    }

    @Override
    public void addError(String msg) {
        this.cai.addError(msg);
    }

    @Override
    public void addError(String msg, Throwable ex) {
        this.cai.addError(msg, ex);
    }

    @Override
    public void addInfo(String msg) {
        this.cai.addInfo(msg);
    }

    @Override
    public void addInfo(String msg, Throwable ex) {
        this.cai.addInfo(msg, ex);
    }

    @Override
    public void addStatus(Status status) {
        this.cai.addStatus(status);
    }

    @Override
    public void addWarn(String msg) {
        this.cai.addWarn(msg);
    }

    @Override
    public void addWarn(String msg, Throwable ex) {
        this.cai.addWarn(msg, ex);
    }

    @Override
    public Context getContext() {
        return this.cai.getContext();
    }

    @Override
    public void setContext(Context context) {
        this.cai.setContext(context);
    }

    public List<SaxEvent> getSaxEventList() {
        return this.saxEventList;
    }
}

