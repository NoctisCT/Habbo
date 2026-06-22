/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContainerInitializer
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 */
package ch.qos.logback.classic.servlet;

import ch.qos.logback.classic.servlet.LogbackServletContextListener;
import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import ch.qos.logback.core.util.OptionHelper;
import java.util.EventListener;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class LogbackServletContainerInitializer
implements ServletContainerInitializer {
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        if (this.isDisabledByConfiguration(ctx)) {
            StatusViaSLF4JLoggerFactory.addInfo("Due to deployment instructions will NOT register an instance of " + LogbackServletContextListener.class + " to the current web-app", this);
            return;
        }
        StatusViaSLF4JLoggerFactory.addInfo("Adding an instance of  " + LogbackServletContextListener.class + " to the current web-app", this);
        LogbackServletContextListener lscl = new LogbackServletContextListener();
        ctx.addListener((EventListener)((Object)lscl));
    }

    boolean isDisabledByConfiguration(ServletContext ctx) {
        String disableAttributeStr = null;
        String disableAttribute = ctx.getInitParameter("logbackDisableServletContainerInitializer");
        if (disableAttribute instanceof String) {
            disableAttributeStr = disableAttribute;
        }
        if (OptionHelper.isEmpty(disableAttributeStr)) {
            disableAttributeStr = OptionHelper.getSystemProperty("logbackDisableServletContainerInitializer");
        }
        if (OptionHelper.isEmpty(disableAttributeStr)) {
            disableAttributeStr = OptionHelper.getEnv("logbackDisableServletContainerInitializer");
        }
        if (OptionHelper.isEmpty(disableAttributeStr)) {
            return false;
        }
        return disableAttributeStr.equalsIgnoreCase("true");
    }
}

