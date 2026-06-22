/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.sift.AppenderFactory;
import ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;
import java.util.List;
import java.util.Map;

public abstract class AbstractAppenderFactoryUsingJoran<E>
implements AppenderFactory<E> {
    final List<SaxEvent> eventList;
    protected String key;
    protected Map<String, String> parentPropertyMap;

    protected AbstractAppenderFactoryUsingJoran(List<SaxEvent> eventList, String key, Map<String, String> parentPropertyMap) {
        this.eventList = this.removeSiftElement(eventList);
        this.key = key;
        this.parentPropertyMap = parentPropertyMap;
    }

    List<SaxEvent> removeSiftElement(List<SaxEvent> eventList) {
        return eventList.subList(1, eventList.size() - 1);
    }

    public abstract SiftingJoranConfiguratorBase<E> getSiftingJoranConfigurator(String var1);

    @Override
    public Appender<E> buildAppender(Context context, String discriminatingValue) throws JoranException {
        SiftingJoranConfiguratorBase<E> sjc = this.getSiftingJoranConfigurator(discriminatingValue);
        sjc.setContext(context);
        sjc.doConfigure(this.eventList);
        return sjc.getAppender();
    }

    public List<SaxEvent> getEventList() {
        return this.eventList;
    }
}

