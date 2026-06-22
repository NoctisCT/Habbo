/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.SaxEventRecorder;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.xml.sax.Attributes;

public class IncludeAction
extends Action {
    private static final String INCLUDED_TAG = "included";
    private static final String FILE_ATTR = "file";
    private static final String URL_ATTR = "url";
    private static final String RESOURCE_ATTR = "resource";
    private static final String OPTIONAL_ATTR = "optional";
    private String attributeInUse;
    private boolean optional;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
        SaxEventRecorder recorder = new SaxEventRecorder(this.context);
        this.attributeInUse = null;
        this.optional = OptionHelper.toBoolean(attributes.getValue(OPTIONAL_ATTR), false);
        if (!this.checkAttributes(attributes)) {
            return;
        }
        InputStream in = this.getInputStream(ec, attributes);
        try {
            if (in != null) {
                this.parseAndRecord(in, recorder);
                this.trimHeadAndTail(recorder);
                ec.getJoranInterpreter().getEventPlayer().addEventsDynamically(recorder.saxEventList, 2);
            }
        }
        catch (JoranException e) {
            this.addError("Error while parsing  " + this.attributeInUse, e);
        }
        finally {
            this.close(in);
        }
    }

    void close(InputStream in) {
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private boolean checkAttributes(Attributes attributes) {
        String fileAttribute = attributes.getValue(FILE_ATTR);
        String urlAttribute = attributes.getValue(URL_ATTR);
        String resourceAttribute = attributes.getValue(RESOURCE_ATTR);
        int count = 0;
        if (!OptionHelper.isEmpty(fileAttribute)) {
            ++count;
        }
        if (!OptionHelper.isEmpty(urlAttribute)) {
            ++count;
        }
        if (!OptionHelper.isEmpty(resourceAttribute)) {
            ++count;
        }
        if (count == 0) {
            this.addError("One of \"path\", \"resource\" or \"url\" attributes must be set.");
            return false;
        }
        if (count > 1) {
            this.addError("Only one of \"file\", \"url\" or \"resource\" attributes should be set.");
            return false;
        }
        if (count == 1) {
            return true;
        }
        throw new IllegalStateException("Count value [" + count + "] is not expected");
    }

    URL attributeToURL(String urlAttribute) {
        try {
            return new URL(urlAttribute);
        }
        catch (MalformedURLException mue) {
            String errMsg = "URL [" + urlAttribute + "] is not well formed.";
            this.addError(errMsg, mue);
            return null;
        }
    }

    InputStream openURL(URL url) {
        try {
            return url.openStream();
        }
        catch (IOException e) {
            this.optionalWarning("Failed to open [" + url.toString() + "]");
            return null;
        }
    }

    URL resourceAsURL(String resourceAttribute) {
        URL url = Loader.getResourceBySelfClassLoader(resourceAttribute);
        if (url == null) {
            this.optionalWarning("Could not find resource corresponding to [" + resourceAttribute + "]");
            return null;
        }
        return url;
    }

    private void optionalWarning(String msg) {
        if (!this.optional) {
            this.addWarn(msg);
        }
    }

    URL filePathAsURL(String path) {
        URI uri = new File(path).toURI();
        try {
            return uri.toURL();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    URL getInputURL(InterpretationContext ec, Attributes attributes) {
        String fileAttribute = attributes.getValue(FILE_ATTR);
        String urlAttribute = attributes.getValue(URL_ATTR);
        String resourceAttribute = attributes.getValue(RESOURCE_ATTR);
        if (!OptionHelper.isEmpty(fileAttribute)) {
            this.attributeInUse = ec.subst(fileAttribute);
            return this.filePathAsURL(this.attributeInUse);
        }
        if (!OptionHelper.isEmpty(urlAttribute)) {
            this.attributeInUse = ec.subst(urlAttribute);
            return this.attributeToURL(this.attributeInUse);
        }
        if (!OptionHelper.isEmpty(resourceAttribute)) {
            this.attributeInUse = ec.subst(resourceAttribute);
            return this.resourceAsURL(this.attributeInUse);
        }
        throw new IllegalStateException("A URL stream should have been returned");
    }

    InputStream getInputStream(InterpretationContext ec, Attributes attributes) {
        URL inputURL = this.getInputURL(ec, attributes);
        if (inputURL == null) {
            return null;
        }
        ConfigurationWatchListUtil.addToWatchList(this.context, inputURL);
        return this.openURL(inputURL);
    }

    private void trimHeadAndTail(SaxEventRecorder recorder) {
        SaxEvent last;
        List<SaxEvent> saxEventList = recorder.saxEventList;
        if (saxEventList.size() == 0) {
            return;
        }
        SaxEvent first = saxEventList.get(0);
        if (first != null && first.qName.equalsIgnoreCase(INCLUDED_TAG)) {
            saxEventList.remove(0);
        }
        if ((last = saxEventList.get(recorder.saxEventList.size() - 1)) != null && last.qName.equalsIgnoreCase(INCLUDED_TAG)) {
            saxEventList.remove(recorder.saxEventList.size() - 1);
        }
    }

    private void parseAndRecord(InputStream inputSource, SaxEventRecorder recorder) throws JoranException {
        recorder.setContext(this.context);
        recorder.recordEvents(inputSource);
    }

    @Override
    public void end(InterpretationContext ec, String name) throws ActionException {
    }
}

