/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.CachingDateFormatter;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class TimestampAction
extends Action {
    static String DATE_PATTERN_ATTRIBUTE = "datePattern";
    static String TIME_REFERENCE_ATTRIBUTE = "timeReference";
    static String CONTEXT_BIRTH = "contextBirth";
    boolean inError = false;

    @Override
    public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
        long timeReference;
        String timeReferenceStr;
        String datePatternStr;
        String keyStr = attributes.getValue("key");
        if (OptionHelper.isEmpty(keyStr)) {
            this.addError("Attribute named [key] cannot be empty");
            this.inError = true;
        }
        if (OptionHelper.isEmpty(datePatternStr = attributes.getValue(DATE_PATTERN_ATTRIBUTE))) {
            this.addError("Attribute named [" + DATE_PATTERN_ATTRIBUTE + "] cannot be empty");
            this.inError = true;
        }
        if (CONTEXT_BIRTH.equalsIgnoreCase(timeReferenceStr = attributes.getValue(TIME_REFERENCE_ATTRIBUTE))) {
            this.addInfo("Using context birth as time reference.");
            timeReference = this.context.getBirthTime();
        } else {
            timeReference = System.currentTimeMillis();
            this.addInfo("Using current interpretation time, i.e. now, as time reference.");
        }
        if (this.inError) {
            return;
        }
        String scopeStr = attributes.getValue("scope");
        ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
        CachingDateFormatter sdf = new CachingDateFormatter(datePatternStr);
        String val = sdf.format(timeReference);
        this.addInfo("Adding property to the context with key=\"" + keyStr + "\" and value=\"" + val + "\" to the " + (Object)((Object)scope) + " scope");
        ActionUtil.setProperty(ec, keyStr, val, scope);
    }

    @Override
    public void end(InterpretationContext ec, String name) throws ActionException {
    }
}

