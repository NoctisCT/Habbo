/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.GroovyClassLoader
 *  groovy.lang.GroovyObject
 *  groovy.lang.Script
 *  org.codehaus.groovy.control.CompilationFailedException
 */
package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.boolex.IEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import ch.qos.logback.core.util.FileUtil;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilationFailedException;

public class GEventEvaluator
extends EventEvaluatorBase<ILoggingEvent> {
    String expression;
    IEvaluator delegateEvaluator;
    Script script;

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public void start() {
        int errors = 0;
        if (this.expression == null || this.expression.length() == 0) {
            this.addError("Empty expression");
            return;
        }
        this.addInfo("Expression to evaluate [" + this.expression + "]");
        ClassLoader classLoader = this.getClass().getClassLoader();
        String currentPackageName = this.getClass().getPackage().getName();
        currentPackageName = currentPackageName.replace('.', '/');
        FileUtil fileUtil = new FileUtil(this.getContext());
        String scriptText = fileUtil.resourceAsString(classLoader, currentPackageName + "/EvaluatorTemplate.groovy");
        if (scriptText == null) {
            return;
        }
        scriptText = scriptText.replace("//EXPRESSION", this.expression);
        GroovyClassLoader gLoader = new GroovyClassLoader(classLoader);
        try {
            Class scriptClass = gLoader.parseClass(scriptText);
            GroovyObject goo = (GroovyObject)scriptClass.newInstance();
            this.delegateEvaluator = (IEvaluator)goo;
        }
        catch (CompilationFailedException cfe) {
            this.addError("Failed to compile expression [" + this.expression + "]", cfe);
            ++errors;
        }
        catch (Exception e) {
            this.addError("Failed to compile expression [" + this.expression + "]", e);
            ++errors;
        }
        if (errors == 0) {
            super.start();
        }
    }

    @Override
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        if (this.delegateEvaluator == null) {
            return false;
        }
        return this.delegateEvaluator.doEvaluate(event);
    }
}

