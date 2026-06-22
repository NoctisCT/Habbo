/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.DefaultNestedComponentRules;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.action.AppenderAction;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SiftingJoranConfigurator
extends SiftingJoranConfiguratorBase<ILoggingEvent> {
    SiftingJoranConfigurator(String key, String value, Map<String, String> parentPropertyMap) {
        super(key, value, parentPropertyMap);
    }

    @Override
    protected ElementPath initialElementPath() {
        return new ElementPath("configuration");
    }

    @Override
    protected void addInstanceRules(RuleStore rs) {
        super.addInstanceRules(rs);
        rs.addRule(new ElementSelector("configuration/appender"), new AppenderAction());
    }

    @Override
    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
        DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
    }

    @Override
    protected void buildInterpreter() {
        super.buildInterpreter();
        Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
        omap.put("APPENDER_BAG", new HashMap());
        HashMap<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.putAll(this.parentPropertyMap);
        propertiesMap.put(this.key, this.value);
        this.interpreter.setInterpretationContextPropertiesMap(propertiesMap);
    }

    @Override
    public Appender<ILoggingEvent> getAppender() {
        Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
        HashMap appenderMap = (HashMap)omap.get("APPENDER_BAG");
        this.oneAndOnlyOneCheck(appenderMap);
        Collection values = appenderMap.values();
        if (values.size() == 0) {
            return null;
        }
        return (Appender)values.iterator().next();
    }
}

