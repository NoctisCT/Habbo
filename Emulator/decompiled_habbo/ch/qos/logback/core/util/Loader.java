/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.util.OptionHelper;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class Loader {
    static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
    private static boolean ignoreTCL = false;
    public static final String IGNORE_TCL_PROPERTY_NAME = "logback.ignoreTCL";
    private static boolean HAS_GET_CLASS_LOADER_PERMISSION = false;

    public static Set<URL> getResources(String resource, ClassLoader classLoader) throws IOException {
        HashSet<URL> urlSet = new HashSet<URL>();
        Enumeration<URL> urlEnum = classLoader.getResources(resource);
        while (urlEnum.hasMoreElements()) {
            URL url = urlEnum.nextElement();
            urlSet.add(url);
        }
        return urlSet;
    }

    public static URL getResource(String resource, ClassLoader classLoader) {
        try {
            return classLoader.getResource(resource);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static URL getResourceBySelfClassLoader(String resource) {
        return Loader.getResource(resource, Loader.getClassLoaderOfClass(Loader.class));
    }

    public static ClassLoader getTCL() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String clazz, Context context) throws ClassNotFoundException {
        ClassLoader cl = Loader.getClassLoaderOfObject(context);
        return cl.loadClass(clazz);
    }

    public static ClassLoader getClassLoaderOfObject(Object o) {
        if (o == null) {
            throw new NullPointerException("Argument cannot be null");
        }
        return Loader.getClassLoaderOfClass(o.getClass());
    }

    public static ClassLoader getClassLoaderAsPrivileged(final Class<?> clazz) {
        if (!HAS_GET_CLASS_LOADER_PERMISSION) {
            return null;
        }
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return clazz.getClassLoader();
            }
        });
    }

    public static ClassLoader getClassLoaderOfClass(Class<?> clazz) {
        ClassLoader cl = clazz.getClassLoader();
        if (cl == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return cl;
    }

    public static Class<?> loadClass(String clazz) throws ClassNotFoundException {
        if (ignoreTCL) {
            return Class.forName(clazz);
        }
        try {
            return Loader.getTCL().loadClass(clazz);
        }
        catch (Throwable e) {
            return Class.forName(clazz);
        }
    }

    static {
        String ignoreTCLProp = OptionHelper.getSystemProperty(IGNORE_TCL_PROPERTY_NAME, null);
        if (ignoreTCLProp != null) {
            ignoreTCL = OptionHelper.toBoolean(ignoreTCLProp, true);
        }
        HAS_GET_CLASS_LOADER_PERMISSION = AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

            @Override
            public Boolean run() {
                try {
                    AccessController.checkPermission(new RuntimePermission("getClassLoader"));
                    return true;
                }
                catch (SecurityException e) {
                    return false;
                }
            }
        });
    }
}

