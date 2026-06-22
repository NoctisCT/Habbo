/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.util;

import ch.qos.logback.core.pattern.util.IEscapeUtil;

public class RegularEscapeUtil
implements IEscapeUtil {
    @Override
    public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
        if (escapeChars.indexOf(next) >= 0) {
            buf.append(next);
        } else {
            switch (next) {
                case '_': {
                    break;
                }
                case '\\': {
                    buf.append(next);
                    break;
                }
                case 't': {
                    buf.append('\t');
                    break;
                }
                case 'r': {
                    buf.append('\r');
                    break;
                }
                case 'n': {
                    buf.append('\n');
                    break;
                }
                default: {
                    String commaSeperatedEscapeChars = this.formatEscapeCharsForListing(escapeChars);
                    throw new IllegalArgumentException("Illegal char '" + next + " at column " + pointer + ". Only \\\\, \\_" + commaSeperatedEscapeChars + ", \\t, \\n, \\r combinations are allowed as escape characters.");
                }
            }
        }
    }

    String formatEscapeCharsForListing(String escapeChars) {
        StringBuilder commaSeperatedEscapeChars = new StringBuilder();
        for (int i = 0; i < escapeChars.length(); ++i) {
            commaSeperatedEscapeChars.append(", \\").append(escapeChars.charAt(i));
        }
        return commaSeperatedEscapeChars.toString();
    }

    public static String basicEscape(String s) {
        int len = s.length();
        StringBuilder sbuf = new StringBuilder(len);
        int i = 0;
        while (i < len) {
            int c;
            if ((c = s.charAt(i++)) == 92) {
                if ((c = s.charAt(i++)) == 110) {
                    c = 10;
                } else if (c == 114) {
                    c = 13;
                } else if (c == 116) {
                    c = 9;
                } else if (c == 102) {
                    c = 12;
                } else if (c == 8) {
                    c = 8;
                } else if (c == 34) {
                    c = 34;
                } else if (c == 39) {
                    c = 39;
                } else if (c == 92) {
                    c = 92;
                }
            }
            sbuf.append((char)c);
        }
        return sbuf.toString();
    }
}

