/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class LoggerAction
extends Action {
    public static final String LEVEL_ATTRIBUTE = "level";
    boolean inError = false;
    Logger logger;

    @Override
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
        String additivityStr;
        this.inError = false;
        this.logger = null;
        LoggerContext loggerContext = (LoggerContext)this.context;
        String loggerName = ec.subst(attributes.getValue("name"));
        if (OptionHelper.isEmpty(loggerName)) {
            this.inError = true;
            String aroundLine = this.getLineColStr(ec);
            String errorMsg = "No 'name' attribute in element " + name + ", around " + aroundLine;
            this.addError(errorMsg);
            return;
        }
        this.logger = loggerContext.getLogger(loggerName);
        String levelStr = ec.subst(attributes.getValue(LEVEL_ATTRIBUTE));
        if (!OptionHelper.isEmpty(levelStr)) {
            if ("INHERITED".equalsIgnoreCase(levelStr) || "NULL".equalsIgnoreCase(levelStr)) {
                this.addInfo("Setting level of logger [" + loggerName + "] to null, i.e. INHERITED");
                this.logger.setLevel(null);
            } else {
                Level level = Level.toLevel(levelStr);
                this.addInfo("Setting level of logger [" + loggerName + "] to " + level);
                this.logger.setLevel(level);
            }
        }
        if (!OptionHelper.isEmpty(additivityStr = ec.subst(attributes.getValue("additivity")))) {
            boolean additive = OptionHelper.toBoolean(additivityStr, true);
            this.addInfo("Setting additivity of logger [" + loggerName + "] to " + additive);
            this.logger.setAdditive(additive);
        }
        ec.pushObject(this.logger);
    }

    @Override
    public void end(InterpretationContext ec, String e) {
        if (this.inError) {
            return;
        }
        Object o = ec.peekObject();
        if (o != this.logger) {
            this.addWarn("The object on the top the of the stack is not " + this.logger + " pushed earlier");
            this.addWarn("It is: " + o);
        } else {
            ec.popObject();
        }
    }

    public void finish(InterpretationContext ec) {
    }
}

