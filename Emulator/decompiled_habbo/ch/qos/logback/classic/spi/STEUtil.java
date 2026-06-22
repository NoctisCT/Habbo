/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.spi.StackTraceElementProxy;

public class STEUtil {
    static int UNUSED_findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElement[] otherSTEArray) {
        if (otherSTEArray == null) {
            return 0;
        }
        int steIndex = steArray.length - 1;
        int count = 0;
        for (int parentIndex = otherSTEArray.length - 1; steIndex >= 0 && parentIndex >= 0 && steArray[steIndex].equals(otherSTEArray[parentIndex]); --steIndex, --parentIndex) {
            ++count;
        }
        return count;
    }

    static int findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElementProxy[] otherSTEPArray) {
        if (otherSTEPArray == null) {
            return 0;
        }
        int steIndex = steArray.length - 1;
        int count = 0;
        for (int parentIndex = otherSTEPArray.length - 1; steIndex >= 0 && parentIndex >= 0 && steArray[steIndex].equals(otherSTEPArray[parentIndex].ste); --steIndex, --parentIndex) {
            ++count;
        }
        return count;
    }
}

