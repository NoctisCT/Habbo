/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.util;

import java.util.ArrayList;
import java.util.List;

public class LoggerNameUtil {
    public static int getFirstSeparatorIndexOf(String name) {
        return LoggerNameUtil.getSeparatorIndexOf(name, 0);
    }

    public static int getSeparatorIndexOf(String name, int fromIndex) {
        int dotIndex = name.indexOf(46, fromIndex);
        int dollarIndex = name.indexOf(36, fromIndex);
        if (dotIndex == -1 && dollarIndex == -1) {
            return -1;
        }
        if (dotIndex == -1) {
            return dollarIndex;
        }
        if (dollarIndex == -1) {
            return dotIndex;
        }
        return dotIndex < dollarIndex ? dotIndex : dollarIndex;
    }

    public static List<String> computeNameParts(String loggerName) {
        ArrayList<String> partList = new ArrayList<String>();
        int fromIndex = 0;
        while (true) {
            int index;
            if ((index = LoggerNameUtil.getSeparatorIndexOf(loggerName, fromIndex)) == -1) break;
            partList.add(loggerName.substring(fromIndex, index));
            fromIndex = index + 1;
        }
        partList.add(loggerName.substring(fromIndex));
        return partList;
    }
}

