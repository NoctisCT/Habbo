/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.helpers;

import java.util.regex.Pattern;

public class Transform {
    private static final String CDATA_START = "<![CDATA[";
    private static final String CDATA_END = "]]>";
    private static final String CDATA_PSEUDO_END = "]]&gt;";
    private static final String CDATA_EMBEDED_END = "]]>]]&gt;<![CDATA[";
    private static final int CDATA_END_LEN = "]]>".length();
    private static final Pattern UNSAFE_XML_CHARS = Pattern.compile("[\u0000-\b\u000b\f\u000e-\u001f<>&'\"]");

    public static String escapeTags(String input) {
        if (input == null || input.length() == 0 || !UNSAFE_XML_CHARS.matcher(input).find()) {
            return input;
        }
        StringBuffer buf = new StringBuffer(input);
        return Transform.escapeTags(buf);
    }

    public static String escapeTags(StringBuffer buf) {
        block8: for (int i = 0; i < buf.length(); ++i) {
            char ch = buf.charAt(i);
            switch (ch) {
                case '\t': 
                case '\n': 
                case '\r': {
                    continue block8;
                }
                case '&': {
                    buf.replace(i, i + 1, "&amp;");
                    continue block8;
                }
                case '<': {
                    buf.replace(i, i + 1, "&lt;");
                    continue block8;
                }
                case '>': {
                    buf.replace(i, i + 1, "&gt;");
                    continue block8;
                }
                case '\"': {
                    buf.replace(i, i + 1, "&quot;");
                    continue block8;
                }
                case '\'': {
                    buf.replace(i, i + 1, "&#39;");
                    continue block8;
                }
                default: {
                    if (ch >= ' ') continue block8;
                    buf.replace(i, i + 1, "\ufffd");
                }
            }
        }
        return buf.toString();
    }

    public static void appendEscapingCDATA(StringBuilder output, String str) {
        if (str == null) {
            return;
        }
        int end = str.indexOf(CDATA_END);
        if (end < 0) {
            output.append(str);
            return;
        }
        int start = 0;
        while (end > -1) {
            output.append(str.substring(start, end));
            output.append(CDATA_EMBEDED_END);
            start = end + CDATA_END_LEN;
            if (start < str.length()) {
                end = str.indexOf(CDATA_END, start);
                continue;
            }
            return;
        }
        output.append(str.substring(start));
    }
}

