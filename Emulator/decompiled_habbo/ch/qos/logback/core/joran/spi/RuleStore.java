/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.ElementSelector;
import java.util.List;

public interface RuleStore {
    public void addRule(ElementSelector var1, String var2) throws ClassNotFoundException;

    public void addRule(ElementSelector var1, Action var2);

    public List<Action> matchActions(ElementPath var1);
}

