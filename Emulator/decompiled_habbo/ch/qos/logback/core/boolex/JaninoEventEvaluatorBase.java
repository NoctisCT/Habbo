/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.janino.ScriptEvaluator
 */
package ch.qos.logback.core.boolex;

import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import ch.qos.logback.core.boolex.Matcher;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.janino.ScriptEvaluator;

public abstract class JaninoEventEvaluatorBase<E>
extends EventEvaluatorBase<E> {
    static Class<?> EXPRESSION_TYPE = Boolean.TYPE;
    static Class<?>[] THROWN_EXCEPTIONS = new Class[1];
    public static final int ERROR_THRESHOLD = 4;
    private String expression;
    ScriptEvaluator scriptEvaluator;
    private int errorCount = 0;
    protected List<Matcher> matcherList = new ArrayList<Matcher>();

    protected abstract String getDecoratedExpression();

    protected abstract String[] getParameterNames();

    protected abstract Class<?>[] getParameterTypes();

    protected abstract Object[] getParameterValues(E var1);

    @Override
    public void start() {
        try {
            assert (this.context != null);
            this.scriptEvaluator = new ScriptEvaluator(this.getDecoratedExpression(), EXPRESSION_TYPE, this.getParameterNames(), (Class[])this.getParameterTypes(), (Class[])THROWN_EXCEPTIONS);
            super.start();
        }
        catch (Exception e) {
            this.addError("Could not start evaluator with expression [" + this.expression + "]", e);
        }
    }

    @Override
    public boolean evaluate(E event) throws EvaluationException {
        if (!this.isStarted()) {
            throw new IllegalStateException("Evaluator [" + this.name + "] was called in stopped state");
        }
        try {
            Boolean result = (Boolean)this.scriptEvaluator.evaluate(this.getParameterValues(event));
            return result;
        }
        catch (Exception ex) {
            ++this.errorCount;
            if (this.errorCount >= 4) {
                this.stop();
            }
            throw new EvaluationException("Evaluator [" + this.name + "] caused an exception", ex);
        }
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void addMatcher(Matcher matcher) {
        this.matcherList.add(matcher);
    }

    public List<Matcher> getMatcherList() {
        return this.matcherList;
    }

    static {
        JaninoEventEvaluatorBase.THROWN_EXCEPTIONS[0] = EvaluationException.class;
    }
}

