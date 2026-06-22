/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.xml.sax.Attributes;

public class PropertyAction
extends Action {
    static final String RESOURCE_ATTRIBUTE = "resource";
    static String INVALID_ATTRIBUTES = "In <property> element, either the \"file\" attribute alone, or the \"resource\" element alone, or both the \"name\" and \"value\" attributes must be set.";

    @Override
    public void begin(InterpretationContext ec, String localName, Attributes attributes) {
        if ("substitutionProperty".equals(localName)) {
            this.addWarn("[substitutionProperty] element has been deprecated. Please use the [property] element instead.");
        }
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        String scopeStr = attributes.getValue("scope");
        ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
        if (this.checkFileAttributeSanity(attributes)) {
            String file = attributes.getValue("file");
            file = ec.subst(file);
            try {
                FileInputStream istream = new FileInputStream(file);
                this.loadAndSetProperties(ec, istream, scope);
            }
            catch (FileNotFoundException e) {
                this.addError("Could not find properties file [" + file + "].");
            }
            catch (IOException e1) {
                this.addError("Could not read properties file [" + file + "].", e1);
            }
        } else if (this.checkResourceAttributeSanity(attributes)) {
            String resource = attributes.getValue(RESOURCE_ATTRIBUTE);
            URL resourceURL = Loader.getResourceBySelfClassLoader(resource = ec.subst(resource));
            if (resourceURL == null) {
                this.addError("Could not find resource [" + resource + "].");
            } else {
                try {
                    InputStream istream = resourceURL.openStream();
                    this.loadAndSetProperties(ec, istream, scope);
                }
                catch (IOException e) {
                    this.addError("Could not read resource file [" + resource + "].", e);
                }
            }
        } else if (this.checkValueNameAttributesSanity(attributes)) {
            value = RegularEscapeUtil.basicEscape(value);
            value = value.trim();
            value = ec.subst(value);
            ActionUtil.setProperty(ec, name, value, scope);
        } else {
            this.addError(INVALID_ATTRIBUTES);
        }
    }

    void loadAndSetProperties(InterpretationContext ec, InputStream istream, ActionUtil.Scope scope) throws IOException {
        Properties props = new Properties();
        props.load(istream);
        istream.close();
        ActionUtil.setProperties(ec, props, scope);
    }

    boolean checkFileAttributeSanity(Attributes attributes) {
        String file = attributes.getValue("file");
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        String resource = attributes.getValue(RESOURCE_ATTRIBUTE);
        return !OptionHelper.isEmpty(file) && OptionHelper.isEmpty(name) && OptionHelper.isEmpty(value) && OptionHelper.isEmpty(resource);
    }

    boolean checkResourceAttributeSanity(Attributes attributes) {
        String file = attributes.getValue("file");
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        String resource = attributes.getValue(RESOURCE_ATTRIBUTE);
        return !OptionHelper.isEmpty(resource) && OptionHelper.isEmpty(name) && OptionHelper.isEmpty(value) && OptionHelper.isEmpty(file);
    }

    boolean checkValueNameAttributesSanity(Attributes attributes) {
        String file = attributes.getValue("file");
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        String resource = attributes.getValue(RESOURCE_ATTRIBUTE);
        return !OptionHelper.isEmpty(name) && !OptionHelper.isEmpty(value) && OptionHelper.isEmpty(file) && OptionHelper.isEmpty(resource);
    }

    @Override
    public void end(InterpretationContext ec, String name) {
    }

    public void finish(InterpretationContext ec) {
    }
}

