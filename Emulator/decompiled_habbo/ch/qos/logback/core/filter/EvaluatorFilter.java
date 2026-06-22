/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.filter;

import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class EvaluatorFilter<E>
extends AbstractMatcherFilter<E> {
    EventEvaluator<E> evaluator;

    @Override
    public void start() {
        if (this.evaluator != null) {
            super.start();
        } else {
            this.addError("No evaluator set for filter " + this.getName());
        }
    }

    public EventEvaluator<E> getEvaluator() {
        return this.evaluator;
    }

    public void setEvaluator(EventEvaluator<E> evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public FilterReply decide(E event) {
        if (!this.isStarted() || !this.evaluator.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        try {
            if (this.evaluator.evaluate(event)) {
                return this.onMatch;
            }
            return this.onMismatch;
        }
        catch (EvaluationException e) {
            this.addError("Evaluator " + this.evaluator.getName() + " threw an exception", e);
            return FilterReply.NEUTRAL;
        }
    }
}

