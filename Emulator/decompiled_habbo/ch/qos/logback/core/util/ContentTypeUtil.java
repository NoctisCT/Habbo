/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

public class ContentTypeUtil {
    public static boolean isTextual(String contextType) {
        if (contextType == null) {
            return false;
        }
        return contextType.startsWith("text");
    }

    public static String getSubType(String contextType) {
        if (contextType == null) {
            return null;
        }
        int index = contextType.indexOf(47);
        if (index == -1) {
            return null;
        }
        int subTypeStartIndex = index + 1;
        if (subTypeStartIndex < contextType.length()) {
            return contextType.substring(subTypeStartIndex);
        }
        return null;
    }
}

