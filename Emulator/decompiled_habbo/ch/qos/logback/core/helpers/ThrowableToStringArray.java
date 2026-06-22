/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.helpers;

import java.util.LinkedList;
import java.util.List;

public class ThrowableToStringArray {
    public static String[] convert(Throwable t) {
        LinkedList<String> strList = new LinkedList<String>();
        ThrowableToStringArray.extract(strList, t, null);
        return strList.toArray(new String[0]);
    }

    private static void extract(List<String> strList, Throwable t, StackTraceElement[] parentSTE) {
        Throwable cause;
        StackTraceElement[] ste = t.getStackTrace();
        int numberOfcommonFrames = ThrowableToStringArray.findNumberOfCommonFrames(ste, parentSTE);
        strList.add(ThrowableToStringArray.formatFirstLine(t, parentSTE));
        for (int i = 0; i < ste.length - numberOfcommonFrames; ++i) {
            strList.add("\tat " + ste[i].toString());
        }
        if (numberOfcommonFrames != 0) {
            strList.add("\t... " + numberOfcommonFrames + " common frames omitted");
        }
        if ((cause = t.getCause()) != null) {
            ThrowableToStringArray.extract(strList, cause, ste);
        }
    }

    private static String formatFirstLine(Throwable t, StackTraceElement[] parentSTE) {
        String prefix = "";
        if (parentSTE != null) {
            prefix = "Caused by: ";
        }
        String result = prefix + t.getClass().getName();
        if (t.getMessage() != null) {
            result = result + ": " + t.getMessage();
        }
        return result;
    }

    private static int findNumberOfCommonFrames(StackTraceElement[] ste, StackTraceElement[] parentSTE) {
        if (parentSTE == null) {
            return 0;
        }
        int steIndex = ste.length - 1;
        int count = 0;
        for (int parentIndex = parentSTE.length - 1; steIndex >= 0 && parentIndex >= 0 && ste[steIndex].equals(parentSTE[parentIndex]); --steIndex, --parentIndex) {
            ++count;
        }
        return count;
    }
}

