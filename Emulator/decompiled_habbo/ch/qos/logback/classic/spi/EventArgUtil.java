/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

public class EventArgUtil {
    public static final Throwable extractThrowable(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return null;
        }
        Object lastEntry = argArray[argArray.length - 1];
        if (lastEntry instanceof Throwable) {
            return (Throwable)lastEntry;
        }
        return null;
    }

    public static Object[] trimmedCopy(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
        int trimemdLen = argArray.length - 1;
        Object[] trimmed = new Object[trimemdLen];
        System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
        return trimmed;
    }

    public static Object[] arrangeArguments(Object[] argArray) {
        return argArray;
    }

    public static boolean successfulExtraction(Throwable throwable) {
        return throwable != null;
    }
}

