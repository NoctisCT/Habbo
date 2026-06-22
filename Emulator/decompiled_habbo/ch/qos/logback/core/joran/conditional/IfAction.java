/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.conditional.Condition;
import ch.qos.logback.core.joran.conditional.IfState;
import ch.qos.logback.core.joran.conditional.PropertyEvalScriptBuilder;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;

public class IfAction
extends Action {
    private static final String CONDITION_ATTR = "condition";
    public static final String MISSING_JANINO_MSG = "Could not find Janino library on the class path. Skipping conditional processing.";
    public static final String MISSING_JANINO_SEE = "See also http://logback.qos.ch/codes.html#ifJanino";
    Stack<IfState> stack = new Stack();

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        IfState state = new IfState();
        boolean emptyStack = this.stack.isEmpty();
        this.stack.push(state);
        if (!emptyStack) {
            return;
        }
        ic.pushObject(this);
        if (!EnvUtil.isJaninoAvailable()) {
            this.addError(MISSING_JANINO_MSG);
            this.addError(MISSING_JANINO_SEE);
            return;
        }
        state.active = true;
        Condition condition = null;
        String conditionAttribute = attributes.getValue(CONDITION_ATTR);
        if (!OptionHelper.isEmpty(conditionAttribute)) {
            conditionAttribute = OptionHelper.substVars(conditionAttribute, ic, this.context);
            PropertyEvalScriptBuilder pesb = new PropertyEvalScriptBuilder(ic);
            pesb.setContext(this.context);
            try {
                condition = pesb.build(conditionAttribute);
            }
            catch (Exception e) {
                this.addError("Failed to parse condition [" + conditionAttribute + "]", e);
            }
            if (condition != null) {
                state.boolResult = condition.evaluate();
            }
        }
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {
        IfState state = this.stack.pop();
        if (!state.active) {
            return;
        }
        Object o = ic.peekObject();
        if (o == null) {
            throw new IllegalStateException("Unexpected null object on stack");
        }
        if (!(o instanceof IfAction)) {
            throw new IllegalStateException("Unexpected object of type [" + o.getClass() + "] on stack");
        }
        if (o != this) {
            throw new IllegalStateException("IfAction different then current one on stack");
        }
        ic.popObject();
        if (state.boolResult == null) {
            this.addError("Failed to determine \"if then else\" result");
            return;
        }
        Interpreter interpreter = ic.getJoranInterpreter();
        List<SaxEvent> listToPlay = state.thenSaxEventList;
        if (!state.boolResult.booleanValue()) {
            listToPlay = state.elseSaxEventList;
        }
        if (listToPlay != null) {
            interpreter.getEventPlayer().addEventsDynamically(listToPlay, 1);
        }
    }

    public void setThenSaxEventList(List<SaxEvent> thenSaxEventList) {
        IfState state = (IfState)this.stack.firstElement();
        if (!state.active) {
            throw new IllegalStateException("setThenSaxEventList() invoked on inactive IfAction");
        }
        state.thenSaxEventList = thenSaxEventList;
    }

    public void setElseSaxEventList(List<SaxEvent> elseSaxEventList) {
        IfState state = (IfState)this.stack.firstElement();
        if (!state.active) {
            throw new IllegalStateException("setElseSaxEventList() invoked on inactive IfAction");
        }
        state.elseSaxEventList = elseSaxEventList;
    }

    public boolean isActive() {
        if (this.stack == null) {
            return false;
        }
        if (this.stack.isEmpty()) {
            return false;
        }
        return this.stack.peek().active;
    }
}

