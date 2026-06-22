/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.spi.ContextAwareImpl;
import org.xml.sax.Locator;

class CAI_WithLocatorSupport
extends ContextAwareImpl {
    CAI_WithLocatorSupport(Context context, Interpreter interpreter) {
        super(context, interpreter);
    }

    @Override
    protected Object getOrigin() {
        Interpreter i = (Interpreter)super.getOrigin();
        Locator locator = i.locator;
        if (locator != null) {
            return Interpreter.class.getName() + "@" + locator.getLineNumber() + ":" + locator.getColumnNumber();
        }
        return Interpreter.class.getName() + "@NA:NA";
    }
}

