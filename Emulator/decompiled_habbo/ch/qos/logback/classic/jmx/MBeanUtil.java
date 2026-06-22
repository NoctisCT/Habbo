/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.jmx;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jmx.JMXConfigurator;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.StatusUtil;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class MBeanUtil {
    static final String DOMAIN = "ch.qos.logback.classic";

    public static String getObjectNameFor(String contextName, Class type) {
        return "ch.qos.logback.classic:Name=" + contextName + ",Type=" + type.getName();
    }

    public static ObjectName string2ObjectName(Context context, Object caller, String objectNameAsStr) {
        String msg = "Failed to convert [" + objectNameAsStr + "] to ObjectName";
        StatusUtil statusUtil = new StatusUtil(context);
        try {
            return new ObjectName(objectNameAsStr);
        }
        catch (MalformedObjectNameException e) {
            statusUtil.addError(caller, msg, e);
            return null;
        }
        catch (NullPointerException e) {
            statusUtil.addError(caller, msg, e);
            return null;
        }
    }

    public static boolean isRegistered(MBeanServer mbs, ObjectName objectName) {
        return mbs.isRegistered(objectName);
    }

    public static void createAndRegisterJMXConfigurator(MBeanServer mbs, LoggerContext loggerContext, JMXConfigurator jmxConfigurator, ObjectName objectName, Object caller) {
        try {
            mbs.registerMBean(jmxConfigurator, objectName);
        }
        catch (Exception e) {
            StatusUtil statusUtil = new StatusUtil(loggerContext);
            statusUtil.addError(caller, "Failed to create mbean", e);
        }
    }

    public static void unregister(LoggerContext loggerContext, MBeanServer mbs, ObjectName objectName, Object caller) {
        StatusUtil statusUtil = new StatusUtil(loggerContext);
        if (mbs.isRegistered(objectName)) {
            try {
                statusUtil.addInfo(caller, "Unregistering mbean [" + objectName + "]");
                mbs.unregisterMBean(objectName);
            }
            catch (InstanceNotFoundException e) {
                statusUtil.addError(caller, "Failed to unregister mbean" + objectName, e);
            }
            catch (MBeanRegistrationException e) {
                statusUtil.addError(caller, "Failed to unregister mbean" + objectName, e);
            }
        } else {
            statusUtil.addInfo(caller, "mbean [" + objectName + "] does not seem to be registered");
        }
    }
}

