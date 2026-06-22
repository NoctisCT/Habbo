/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.jmx;

import ch.qos.logback.core.joran.spi.JoranException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

public interface JMXConfiguratorMBean {
    public void reloadDefaultConfiguration() throws JoranException;

    public void reloadByFileName(String var1) throws JoranException, FileNotFoundException;

    public void reloadByURL(URL var1) throws JoranException;

    public void setLoggerLevel(String var1, String var2);

    public String getLoggerLevel(String var1);

    public String getLoggerEffectiveLevel(String var1);

    public List<String> getLoggerList();

    public List<String> getStatuses();
}

