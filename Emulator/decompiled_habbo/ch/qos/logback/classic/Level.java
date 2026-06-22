/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic;

import java.io.Serializable;

public final class Level
implements Serializable {
    private static final long serialVersionUID = -814092767334282137L;
    public static final int OFF_INT = Integer.MAX_VALUE;
    public static final int ERROR_INT = 40000;
    public static final int WARN_INT = 30000;
    public static final int INFO_INT = 20000;
    public static final int DEBUG_INT = 10000;
    public static final int TRACE_INT = 5000;
    public static final int ALL_INT = Integer.MIN_VALUE;
    public static final Integer OFF_INTEGER = Integer.MAX_VALUE;
    public static final Integer ERROR_INTEGER = 40000;
    public static final Integer WARN_INTEGER = 30000;
    public static final Integer INFO_INTEGER = 20000;
    public static final Integer DEBUG_INTEGER = 10000;
    public static final Integer TRACE_INTEGER = 5000;
    public static final Integer ALL_INTEGER = Integer.MIN_VALUE;
    public static final Level OFF = new Level(Integer.MAX_VALUE, "OFF");
    public static final Level ERROR = new Level(40000, "ERROR");
    public static final Level WARN = new Level(30000, "WARN");
    public static final Level INFO = new Level(20000, "INFO");
    public static final Level DEBUG = new Level(10000, "DEBUG");
    public static final Level TRACE = new Level(5000, "TRACE");
    public static final Level ALL = new Level(Integer.MIN_VALUE, "ALL");
    public final int levelInt;
    public final String levelStr;

    private Level(int levelInt, String levelStr) {
        this.levelInt = levelInt;
        this.levelStr = levelStr;
    }

    public String toString() {
        return this.levelStr;
    }

    public int toInt() {
        return this.levelInt;
    }

    public Integer toInteger() {
        switch (this.levelInt) {
            case -2147483648: {
                return ALL_INTEGER;
            }
            case 5000: {
                return TRACE_INTEGER;
            }
            case 10000: {
                return DEBUG_INTEGER;
            }
            case 20000: {
                return INFO_INTEGER;
            }
            case 30000: {
                return WARN_INTEGER;
            }
            case 40000: {
                return ERROR_INTEGER;
            }
            case 0x7FFFFFFF: {
                return OFF_INTEGER;
            }
        }
        throw new IllegalStateException("Level " + this.levelStr + ", " + this.levelInt + " is unknown.");
    }

    public boolean isGreaterOrEqual(Level r) {
        return this.levelInt >= r.levelInt;
    }

    public static Level toLevel(String sArg) {
        return Level.toLevel(sArg, DEBUG);
    }

    public static Level valueOf(String sArg) {
        return Level.toLevel(sArg, DEBUG);
    }

    public static Level toLevel(int val) {
        return Level.toLevel(val, DEBUG);
    }

    public static Level toLevel(int val, Level defaultLevel) {
        switch (val) {
            case -2147483648: {
                return ALL;
            }
            case 5000: {
                return TRACE;
            }
            case 10000: {
                return DEBUG;
            }
            case 20000: {
                return INFO;
            }
            case 30000: {
                return WARN;
            }
            case 40000: {
                return ERROR;
            }
            case 0x7FFFFFFF: {
                return OFF;
            }
        }
        return defaultLevel;
    }

    public static Level toLevel(String sArg, Level defaultLevel) {
        if (sArg == null) {
            return defaultLevel;
        }
        if (sArg.equalsIgnoreCase("ALL")) {
            return ALL;
        }
        if (sArg.equalsIgnoreCase("TRACE")) {
            return TRACE;
        }
        if (sArg.equalsIgnoreCase("DEBUG")) {
            return DEBUG;
        }
        if (sArg.equalsIgnoreCase("INFO")) {
            return INFO;
        }
        if (sArg.equalsIgnoreCase("WARN")) {
            return WARN;
        }
        if (sArg.equalsIgnoreCase("ERROR")) {
            return ERROR;
        }
        if (sArg.equalsIgnoreCase("OFF")) {
            return OFF;
        }
        return defaultLevel;
    }

    private Object readResolve() {
        return Level.toLevel(this.levelInt);
    }

    public static Level fromLocationAwareLoggerInteger(int levelInt) {
        Level level;
        switch (levelInt) {
            case 0: {
                level = TRACE;
                break;
            }
            case 10: {
                level = DEBUG;
                break;
            }
            case 20: {
                level = INFO;
                break;
            }
            case 30: {
                level = WARN;
                break;
            }
            case 40: {
                level = ERROR;
                break;
            }
            default: {
                throw new IllegalArgumentException(levelInt + " not a valid level value");
            }
        }
        return level;
    }

    public static int toLocationAwareLoggerInteger(Level level) {
        if (level == null) {
            throw new IllegalArgumentException("null level parameter is not admitted");
        }
        switch (level.toInt()) {
            case 5000: {
                return 0;
            }
            case 10000: {
                return 10;
            }
            case 20000: {
                return 20;
            }
            case 30000: {
                return 30;
            }
            case 40000: {
                return 40;
            }
        }
        throw new IllegalArgumentException(level + " not a valid level value");
    }
}

