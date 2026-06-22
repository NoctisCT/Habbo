/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import org.jsoup.helper.Validate;

public final class StringUtil {
    static final String[] padding = new String[]{"", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          ", "           ", "            ", "             ", "              ", "               ", "                ", "                 ", "                  ", "                   ", "                    "};
    private static final Stack<StringBuilder> builders = new Stack();
    private static final int MaxCachedBuilderSize = 8192;
    private static final int MaxIdleBuilders = 8;

    public static String join(Collection strings, String sep) {
        return StringUtil.join(strings.iterator(), sep);
    }

    public static String join(Iterator strings, String sep) {
        if (!strings.hasNext()) {
            return "";
        }
        String start = strings.next().toString();
        if (!strings.hasNext()) {
            return start;
        }
        StringBuilder sb = StringUtil.borrowBuilder().append(start);
        while (strings.hasNext()) {
            sb.append(sep);
            sb.append(strings.next());
        }
        return StringUtil.releaseBuilder(sb);
    }

    public static String join(String[] strings, String sep) {
        return StringUtil.join(Arrays.asList(strings), sep);
    }

    public static String padding(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("width must be > 0");
        }
        if (width < padding.length) {
            return padding[width];
        }
        char[] out = new char[width];
        for (int i = 0; i < width; ++i) {
            out[i] = 32;
        }
        return String.valueOf(out);
    }

    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0) {
            return true;
        }
        int l = string.length();
        for (int i = 0; i < l; ++i) {
            if (StringUtil.isWhitespace(string.codePointAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        int l = string.length();
        for (int i = 0; i < l; ++i) {
            if (Character.isDigit(string.codePointAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isWhitespace(int c) {
        return c == 32 || c == 9 || c == 10 || c == 12 || c == 13;
    }

    public static boolean isActuallyWhitespace(int c) {
        return c == 32 || c == 9 || c == 10 || c == 12 || c == 13 || c == 160;
    }

    public static boolean isInvisibleChar(int c) {
        return c == 8203 || c == 173;
    }

    public static String normaliseWhitespace(String string) {
        StringBuilder sb = StringUtil.borrowBuilder();
        StringUtil.appendNormalisedWhitespace(sb, string, false);
        return StringUtil.releaseBuilder(sb);
    }

    public static void appendNormalisedWhitespace(StringBuilder accum, String string, boolean stripLeading) {
        int c;
        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;
        int len = string.length();
        for (int i = 0; i < len; i += Character.charCount(c)) {
            c = string.codePointAt(i);
            if (StringUtil.isActuallyWhitespace(c)) {
                if (stripLeading && !reachedNonWhite || lastWasWhite) continue;
                accum.append(' ');
                lastWasWhite = true;
                continue;
            }
            if (StringUtil.isInvisibleChar(c)) continue;
            accum.appendCodePoint(c);
            lastWasWhite = false;
            reachedNonWhite = true;
        }
    }

    public static boolean in(String needle, String ... haystack) {
        int len = haystack.length;
        for (int i = 0; i < len; ++i) {
            if (!haystack[i].equals(needle)) continue;
            return true;
        }
        return false;
    }

    public static boolean inSorted(String needle, String[] haystack) {
        return Arrays.binarySearch(haystack, needle) >= 0;
    }

    public static URL resolve(URL base, String relUrl) throws MalformedURLException {
        if (relUrl.startsWith("?")) {
            relUrl = base.getPath() + relUrl;
        }
        if (relUrl.indexOf(46) == 0 && base.getFile().indexOf(47) != 0) {
            base = new URL(base.getProtocol(), base.getHost(), base.getPort(), "/" + base.getFile());
        }
        return new URL(base, relUrl);
    }

    public static String resolve(String baseUrl, String relUrl) {
        try {
            URL base;
            try {
                base = new URL(baseUrl);
            }
            catch (MalformedURLException e) {
                URL abs = new URL(relUrl);
                return abs.toExternalForm();
            }
            return StringUtil.resolve(base, relUrl).toExternalForm();
        }
        catch (MalformedURLException e) {
            return "";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static StringBuilder borrowBuilder() {
        Stack<StringBuilder> stack = builders;
        synchronized (stack) {
            return builders.empty() ? new StringBuilder(8192) : builders.pop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String releaseBuilder(StringBuilder sb) {
        Validate.notNull(sb);
        String string = sb.toString();
        if (sb.length() > 8192) {
            sb = new StringBuilder(8192);
        } else {
            sb.delete(0, sb.length());
        }
        Stack<StringBuilder> stack = builders;
        synchronized (stack) {
            builders.push(sb);
            while (builders.size() > 8) {
                builders.pop();
            }
        }
        return string;
    }
}

