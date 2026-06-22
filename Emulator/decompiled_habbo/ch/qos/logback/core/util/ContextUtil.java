/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ContextUtil
extends ContextAwareBase {
    public ContextUtil(Context context) {
        this.setContext(context);
    }

    public static String getLocalHostName() throws UnknownHostException, SocketException {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostName();
        }
        catch (UnknownHostException e) {
            return ContextUtil.getLocalAddressAsString();
        }
    }

    private static String getLocalAddressAsString() throws UnknownHostException, SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces != null && interfaces.hasMoreElements()) {
            Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
            while (addresses != null && addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (!ContextUtil.acceptableAddress(address)) continue;
                return address.getHostAddress();
            }
        }
        throw new UnknownHostException();
    }

    private static boolean acceptableAddress(InetAddress address) {
        return address != null && !address.isLoopbackAddress() && !address.isAnyLocalAddress() && !address.isLinkLocalAddress();
    }

    public String safelyGetLocalHostName() {
        try {
            String localhostName = ContextUtil.getLocalHostName();
            return localhostName;
        }
        catch (UnknownHostException e) {
            this.addError("Failed to get local hostname", e);
        }
        catch (SocketException e) {
            this.addError("Failed to get local hostname", e);
        }
        catch (SecurityException e) {
            this.addError("Failed to get local hostname", e);
        }
        return "UNKNOWN_LOCALHOST";
    }

    public void addProperties(Properties props) {
        if (props == null) {
            return;
        }
        for (String string : props.keySet()) {
            this.context.putProperty(string, props.getProperty(string));
        }
    }

    public static Map<String, String> getFilenameCollisionMap(Context context) {
        if (context == null) {
            return null;
        }
        Map map = (Map)context.getObject("FA_FILENAME_COLLISION_MAP");
        return map;
    }

    public static Map<String, FileNamePattern> getFilenamePatternCollisionMap(Context context) {
        if (context == null) {
            return null;
        }
        Map map = (Map)context.getObject("RFA_FILENAME_PATTERN_COLLISION_MAP");
        return map;
    }

    public void addGroovyPackages(List<String> frameworkPackages) {
        this.addFrameworkPackage(frameworkPackages, "org.codehaus.groovy.runtime");
    }

    public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
        if (!frameworkPackages.contains(packageName)) {
            frameworkPackages.add(packageName);
        }
    }
}

