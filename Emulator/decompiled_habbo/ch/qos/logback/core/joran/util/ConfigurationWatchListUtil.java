/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import java.net.URL;

public class ConfigurationWatchListUtil {
    static final ConfigurationWatchListUtil origin = new ConfigurationWatchListUtil();

    private ConfigurationWatchListUtil() {
    }

    public static void registerConfigurationWatchList(Context context, ConfigurationWatchList cwl) {
        context.putObject("CONFIGURATION_WATCH_LIST", cwl);
    }

    public static void setMainWatchURL(Context context, URL url) {
        ConfigurationWatchList cwl = ConfigurationWatchListUtil.getConfigurationWatchList(context);
        if (cwl == null) {
            cwl = new ConfigurationWatchList();
            cwl.setContext(context);
            context.putObject("CONFIGURATION_WATCH_LIST", cwl);
        } else {
            cwl.clear();
        }
        cwl.setMainURL(url);
    }

    public static URL getMainWatchURL(Context context) {
        ConfigurationWatchList cwl = ConfigurationWatchListUtil.getConfigurationWatchList(context);
        if (cwl == null) {
            return null;
        }
        return cwl.getMainURL();
    }

    public static void addToWatchList(Context context, URL url) {
        ConfigurationWatchList cwl = ConfigurationWatchListUtil.getConfigurationWatchList(context);
        if (cwl == null) {
            ConfigurationWatchListUtil.addWarn(context, "Null ConfigurationWatchList. Cannot add " + url);
        } else {
            ConfigurationWatchListUtil.addInfo(context, "Adding [" + url + "] to configuration watch list.");
            cwl.addToWatchList(url);
        }
    }

    public static ConfigurationWatchList getConfigurationWatchList(Context context) {
        return (ConfigurationWatchList)context.getObject("CONFIGURATION_WATCH_LIST");
    }

    static void addStatus(Context context, Status s) {
        if (context == null) {
            System.out.println("Null context in " + ConfigurationWatchList.class.getName());
            return;
        }
        StatusManager sm = context.getStatusManager();
        if (sm == null) {
            return;
        }
        sm.add(s);
    }

    static void addInfo(Context context, String msg) {
        ConfigurationWatchListUtil.addStatus(context, new InfoStatus(msg, origin));
    }

    static void addWarn(Context context, String msg) {
        ConfigurationWatchListUtil.addStatus(context, new WarnStatus(msg, origin));
    }
}

