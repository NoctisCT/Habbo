/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.Abbreviator;

public class TargetLengthBasedClassNameAbbreviator
implements Abbreviator {
    final int targetLength;

    public TargetLengthBasedClassNameAbbreviator(int targetLength) {
        this.targetLength = targetLength;
    }

    @Override
    public String abbreviate(String fqClassName) {
        StringBuilder buf = new StringBuilder(this.targetLength);
        if (fqClassName == null) {
            throw new IllegalArgumentException("Class name may not be null");
        }
        int inLen = fqClassName.length();
        if (inLen < this.targetLength) {
            return fqClassName;
        }
        int[] dotIndexesArray = new int[16];
        int[] lengthArray = new int[17];
        int dotCount = TargetLengthBasedClassNameAbbreviator.computeDotIndexes(fqClassName, dotIndexesArray);
        if (dotCount == 0) {
            return fqClassName;
        }
        this.computeLengthArray(fqClassName, dotIndexesArray, lengthArray, dotCount);
        for (int i = 0; i <= dotCount; ++i) {
            if (i == 0) {
                buf.append(fqClassName.substring(0, lengthArray[i] - 1));
                continue;
            }
            buf.append(fqClassName.substring(dotIndexesArray[i - 1], dotIndexesArray[i - 1] + lengthArray[i]));
        }
        return buf.toString();
    }

    static int computeDotIndexes(String className, int[] dotArray) {
        int dotCount;
        int k = 0;
        for (dotCount = 0; (k = className.indexOf(46, k)) != -1 && dotCount < 16; ++dotCount) {
            dotArray[dotCount] = k++;
        }
        return dotCount;
    }

    void computeLengthArray(String className, int[] dotArray, int[] lengthArray, int dotCount) {
        int toTrim = className.length() - this.targetLength;
        for (int i = 0; i < dotCount; ++i) {
            int available;
            int len;
            int previousDotPosition = -1;
            if (i > 0) {
                previousDotPosition = dotArray[i - 1];
            }
            int n = len = (available = dotArray[i] - previousDotPosition - 1) < 1 ? available : 1;
            len = toTrim > 0 ? (available < 1 ? available : 1) : available;
            toTrim -= available - len;
            lengthArray[i] = len + 1;
        }
        int lastDotIndex = dotCount - 1;
        lengthArray[dotCount] = className.length() - dotArray[lastDotIndex];
    }

    static void printArray(String msg, int[] ia) {
        System.out.print(msg);
        for (int i = 0; i < ia.length; ++i) {
            if (i == 0) {
                System.out.print(ia[i]);
                continue;
            }
            System.out.print(", " + ia[i]);
        }
        System.out.println();
    }
}

