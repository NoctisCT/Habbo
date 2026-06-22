/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;
import org.xml.sax.Attributes;

public abstract class AbstractEventEvaluatorAction
extends Action {
    EventEvaluator<?> evaluator;
    boolean inError = false;

    @Override
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
        this.inError = false;
        this.evaluator = null;
        String className = attributes.getValue("class");
        if (OptionHelper.isEmpty(className)) {
            className = this.defaultClassName();
            this.addInfo("Assuming default evaluator class [" + className + "]");
        }
        if (OptionHelper.isEmpty(className)) {
            className = this.defaultClassName();
            this.inError = true;
            this.addError("Mandatory \"class\" attribute not set for <evaluator>");
            return;
        }
        String evaluatorName = attributes.getValue("name");
        if (OptionHelper.isEmpty(evaluatorName)) {
            this.inError = true;
            this.addError("Mandatory \"name\" attribute not set for <evaluator>");
            return;
        }
        try {
            this.evaluator = (EventEvaluator)OptionHelper.instantiateByClassName(className, EventEvaluator.class, this.context);
            this.evaluator.setContext(this.context);
            this.evaluator.setName(evaluatorName);
            ec.pushObject(this.evaluator);
            this.addInfo("Adding evaluator named [" + evaluatorName + "] to the object stack");
        }
        catch (Exception oops) {
            this.inError = true;
            this.addError("Could not create evaluator of type " + className + "].", oops);
        }
    }

    protected abstract String defaultClassName();

    @Override
    public void end(InterpretationContext ec, String e) {
        Object o;
        if (this.inError) {
            return;
        }
        if (this.evaluator instanceof LifeCycle) {
            this.evaluator.start();
            this.addInfo("Starting evaluator named [" + this.evaluator.getName() + "]");
        }
        if ((o = ec.peekObject()) != this.evaluator) {
            this.addWarn("The object on the top the of the stack is not the evaluator pushed earlier.");
        } else {
            ec.popObject();
            try {
                Map evaluatorMap = (Map)this.context.getObject("EVALUATOR_MAP");
                if (evaluatorMap == null) {
                    this.addError("Could not find EvaluatorMap");
                } else {
                    evaluatorMap.put(this.evaluator.getName(), this.evaluator);
                }
            }
            catch (Exception ex) {
                this.addError("Could not set evaluator named [" + this.evaluator + "].", ex);
            }
        }
    }

    public void finish(InterpretationContext ec) {
    }
}

