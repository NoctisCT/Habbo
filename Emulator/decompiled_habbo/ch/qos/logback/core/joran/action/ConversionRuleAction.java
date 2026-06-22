/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import java.util.HashMap;
import org.xml.sax.Attributes;

public class ConversionRuleAction
extends Action {
    boolean inError = false;

    @Override
    public void begin(InterpretationContext ec, String localName, Attributes attributes) {
        this.inError = false;
        String conversionWord = attributes.getValue("conversionWord");
        String converterClass = attributes.getValue("converterClass");
        if (OptionHelper.isEmpty(conversionWord)) {
            this.inError = true;
            String errorMsg = "No 'conversionWord' attribute in <conversionRule>";
            this.addError(errorMsg);
            return;
        }
        if (OptionHelper.isEmpty(converterClass)) {
            this.inError = true;
            String errorMsg = "No 'converterClass' attribute in <conversionRule>";
            ec.addError(errorMsg);
            return;
        }
        try {
            HashMap<String, String> ruleRegistry = (HashMap<String, String>)this.context.getObject("PATTERN_RULE_REGISTRY");
            if (ruleRegistry == null) {
                ruleRegistry = new HashMap<String, String>();
                this.context.putObject("PATTERN_RULE_REGISTRY", ruleRegistry);
            }
            this.addInfo("registering conversion word " + conversionWord + " with class [" + converterClass + "]");
            ruleRegistry.put(conversionWord, converterClass);
        }
        catch (Exception oops) {
            this.inError = true;
            String errorMsg = "Could not add conversion rule to PatternLayout.";
            this.addError(errorMsg);
        }
    }

    @Override
    public void end(InterpretationContext ec, String n) {
    }

    public void finish(InterpretationContext ec) {
    }
}

