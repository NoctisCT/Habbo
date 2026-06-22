/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.util.OptionHelper;

public class StatusListenerConfigHelper {
    public static void installIfAsked(Context context) {
        String slClass = OptionHelper.getSystemProperty("logback.statusListenerClass");
        if (!OptionHelper.isEmpty(slClass)) {
            StatusListenerConfigHelper.addStatusListener(context, slClass);
        }
    }

    private static void addStatusListener(Context context, String listenerClassName) {
        StatusListener listener = null;
        listener = "SYSOUT".equalsIgnoreCase(listenerClassName) ? new OnConsoleStatusListener() : StatusListenerConfigHelper.createListenerPerClassName(context, listenerClassName);
        StatusListenerConfigHelper.initAndAddListener(context, listener);
    }

    private static void initAndAddListener(Context context, StatusListener listener) {
        if (listener != null) {
            boolean effectivelyAdded;
            if (listener instanceof ContextAware) {
                ((ContextAware)((Object)listener)).setContext(context);
            }
            if ((effectivelyAdded = context.getStatusManager().add(listener)) && listener instanceof LifeCycle) {
                ((LifeCycle)((Object)listener)).start();
            }
        }
    }

    private static StatusListener createListenerPerClassName(Context context, String listenerClass) {
        try {
            return (StatusListener)OptionHelper.instantiateByClassName(listenerClass, StatusListener.class, context);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addOnConsoleListenerInstance(Context context, OnConsoleStatusListener onConsoleStatusListener) {
        onConsoleStatusListener.setContext(context);
        boolean effectivelyAdded = context.getStatusManager().add(onConsoleStatusListener);
        if (effectivelyAdded) {
            onConsoleStatusListener.start();
        }
    }
}

