/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import java.util.ArrayList;

public class EnvUtil {
    private static boolean isJDK_N_OrHigher(int n) {
        ArrayList<String> versionList = new ArrayList<String>();
        for (int i = 0; i < 5; ++i) {
            versionList.add("1." + (n + i));
        }
        String javaVersion = System.getProperty("java.version");
        if (javaVersion == null) {
            return false;
        }
        for (String v : versionList) {
            if (!javaVersion.startsWith(v)) continue;
            return true;
        }
        return false;
    }

    public static boolean isJDK5() {
        return EnvUtil.isJDK_N_OrHigher(5);
    }

    public static boolean isJDK6OrHigher() {
        return EnvUtil.isJDK_N_OrHigher(6);
    }

    public static boolean isJDK7OrHigher() {
        return EnvUtil.isJDK_N_OrHigher(7);
    }

    public static boolean isJaninoAvailable() {
        ClassLoader classLoader = EnvUtil.class.getClassLoader();
        try {
            Class<?> bindingClass = classLoader.loadClass("org.codehaus.janino.ScriptEvaluator");
            return bindingClass != null;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.startsWith("Windows");
    }
}

