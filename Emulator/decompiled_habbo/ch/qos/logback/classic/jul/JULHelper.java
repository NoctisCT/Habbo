/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.jul;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class JULHelper {
    public static final boolean isRegularNonRootLogger(java.util.logging.Logger julLogger) {
        if (julLogger == null) {
            return false;
        }
        return !julLogger.getName().equals("");
    }

    public static final boolean isRoot(java.util.logging.Logger julLogger) {
        if (julLogger == null) {
            return false;
        }
        return julLogger.getName().equals("");
    }

    public static java.util.logging.Level asJULLevel(Level lbLevel) {
        if (lbLevel == null) {
            throw new IllegalArgumentException("Unexpected level [null]");
        }
        switch (lbLevel.levelInt) {
            case -2147483648: {
                return java.util.logging.Level.ALL;
            }
            case 5000: {
                return java.util.logging.Level.FINEST;
            }
            case 10000: {
                return java.util.logging.Level.FINE;
            }
            case 20000: {
                return java.util.logging.Level.INFO;
            }
            case 30000: {
                return java.util.logging.Level.WARNING;
            }
            case 40000: {
                return java.util.logging.Level.SEVERE;
            }
            case 0x7FFFFFFF: {
                return java.util.logging.Level.OFF;
            }
        }
        throw new IllegalArgumentException("Unexpected level [" + lbLevel + "]");
    }

    public static String asJULLoggerName(String loggerName) {
        if ("ROOT".equals(loggerName)) {
            return "";
        }
        return loggerName;
    }

    public static java.util.logging.Logger asJULLogger(String loggerName) {
        String julLoggerName = JULHelper.asJULLoggerName(loggerName);
        return java.util.logging.Logger.getLogger(julLoggerName);
    }

    public static java.util.logging.Logger asJULLogger(Logger logger) {
        return JULHelper.asJULLogger(logger.getName());
    }
}

