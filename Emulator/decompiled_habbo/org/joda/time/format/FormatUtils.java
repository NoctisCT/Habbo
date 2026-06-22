/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.io.IOException;
import java.io.Writer;

public class FormatUtils {
    private static final double LOG_10 = Math.log(10.0);

    private FormatUtils() {
    }

    public static void appendPaddedInteger(StringBuffer stringBuffer, int n, int n2) {
        try {
            FormatUtils.appendPaddedInteger((Appendable)stringBuffer, n, n2);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void appendPaddedInteger(Appendable appendable, int n, int n2) throws IOException {
        if (n < 0) {
            appendable.append('-');
            if (n != Integer.MIN_VALUE) {
                n = -n;
            } else {
                while (n2 > 10) {
                    appendable.append('0');
                    --n2;
                }
                appendable.append("2147483648");
                return;
            }
        }
        if (n < 10) {
            while (n2 > 1) {
                appendable.append('0');
                --n2;
            }
            appendable.append((char)(n + 48));
        } else if (n < 100) {
            while (n2 > 2) {
                appendable.append('0');
                --n2;
            }
            int n3 = (n + 1) * 0xCCCCCC >> 27;
            appendable.append((char)(n3 + 48));
            appendable.append((char)(n - (n3 << 3) - (n3 << 1) + 48));
        } else {
            int n4 = n < 1000 ? 3 : (n < 10000 ? 4 : (int)(Math.log(n) / LOG_10) + 1);
            while (n2 > n4) {
                appendable.append('0');
                --n2;
            }
            appendable.append(Integer.toString(n));
        }
    }

    public static void appendPaddedInteger(StringBuffer stringBuffer, long l, int n) {
        try {
            FormatUtils.appendPaddedInteger((Appendable)stringBuffer, l, n);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void appendPaddedInteger(Appendable appendable, long l, int n) throws IOException {
        int n2 = (int)l;
        if ((long)n2 == l) {
            FormatUtils.appendPaddedInteger(appendable, n2, n);
        } else if (n <= 19) {
            appendable.append(Long.toString(l));
        } else {
            if (l < 0L) {
                appendable.append('-');
                if (l != Long.MIN_VALUE) {
                    l = -l;
                } else {
                    while (n > 19) {
                        appendable.append('0');
                        --n;
                    }
                    appendable.append("9223372036854775808");
                    return;
                }
            }
            int n3 = (int)(Math.log(l) / LOG_10) + 1;
            while (n > n3) {
                appendable.append('0');
                --n;
            }
            appendable.append(Long.toString(l));
        }
    }

    public static void writePaddedInteger(Writer writer, int n, int n2) throws IOException {
        if (n < 0) {
            writer.write(45);
            if (n != Integer.MIN_VALUE) {
                n = -n;
            } else {
                while (n2 > 10) {
                    writer.write(48);
                    --n2;
                }
                writer.write("2147483648");
                return;
            }
        }
        if (n < 10) {
            while (n2 > 1) {
                writer.write(48);
                --n2;
            }
            writer.write(n + 48);
        } else if (n < 100) {
            while (n2 > 2) {
                writer.write(48);
                --n2;
            }
            int n3 = (n + 1) * 0xCCCCCC >> 27;
            writer.write(n3 + 48);
            writer.write(n - (n3 << 3) - (n3 << 1) + 48);
        } else {
            int n4 = n < 1000 ? 3 : (n < 10000 ? 4 : (int)(Math.log(n) / LOG_10) + 1);
            while (n2 > n4) {
                writer.write(48);
                --n2;
            }
            writer.write(Integer.toString(n));
        }
    }

    public static void writePaddedInteger(Writer writer, long l, int n) throws IOException {
        int n2 = (int)l;
        if ((long)n2 == l) {
            FormatUtils.writePaddedInteger(writer, n2, n);
        } else if (n <= 19) {
            writer.write(Long.toString(l));
        } else {
            if (l < 0L) {
                writer.write(45);
                if (l != Long.MIN_VALUE) {
                    l = -l;
                } else {
                    while (n > 19) {
                        writer.write(48);
                        --n;
                    }
                    writer.write("9223372036854775808");
                    return;
                }
            }
            int n3 = (int)(Math.log(l) / LOG_10) + 1;
            while (n > n3) {
                writer.write(48);
                --n;
            }
            writer.write(Long.toString(l));
        }
    }

    public static void appendUnpaddedInteger(StringBuffer stringBuffer, int n) {
        try {
            FormatUtils.appendUnpaddedInteger((Appendable)stringBuffer, n);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void appendUnpaddedInteger(Appendable appendable, int n) throws IOException {
        if (n < 0) {
            appendable.append('-');
            if (n != Integer.MIN_VALUE) {
                n = -n;
            } else {
                appendable.append("2147483648");
                return;
            }
        }
        if (n < 10) {
            appendable.append((char)(n + 48));
        } else if (n < 100) {
            int n2 = (n + 1) * 0xCCCCCC >> 27;
            appendable.append((char)(n2 + 48));
            appendable.append((char)(n - (n2 << 3) - (n2 << 1) + 48));
        } else {
            appendable.append(Integer.toString(n));
        }
    }

    public static void appendUnpaddedInteger(StringBuffer stringBuffer, long l) {
        try {
            FormatUtils.appendUnpaddedInteger((Appendable)stringBuffer, l);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void appendUnpaddedInteger(Appendable appendable, long l) throws IOException {
        int n = (int)l;
        if ((long)n == l) {
            FormatUtils.appendUnpaddedInteger(appendable, n);
        } else {
            appendable.append(Long.toString(l));
        }
    }

    public static void writeUnpaddedInteger(Writer writer, int n) throws IOException {
        if (n < 0) {
            writer.write(45);
            if (n != Integer.MIN_VALUE) {
                n = -n;
            } else {
                writer.write("2147483648");
                return;
            }
        }
        if (n < 10) {
            writer.write(n + 48);
        } else if (n < 100) {
            int n2 = (n + 1) * 0xCCCCCC >> 27;
            writer.write(n2 + 48);
            writer.write(n - (n2 << 3) - (n2 << 1) + 48);
        } else {
            writer.write(Integer.toString(n));
        }
    }

    public static void writeUnpaddedInteger(Writer writer, long l) throws IOException {
        int n = (int)l;
        if ((long)n == l) {
            FormatUtils.writeUnpaddedInteger(writer, n);
        } else {
            writer.write(Long.toString(l));
        }
    }

    public static int calculateDigitCount(long l) {
        if (l < 0L) {
            if (l != Long.MIN_VALUE) {
                return FormatUtils.calculateDigitCount(-l) + 1;
            }
            return 20;
        }
        return l < 10L ? 1 : (l < 100L ? 2 : (l < 1000L ? 3 : (l < 10000L ? 4 : (int)(Math.log(l) / LOG_10) + 1)));
    }

    static int parseTwoDigits(CharSequence charSequence, int n) {
        int n2 = charSequence.charAt(n) - 48;
        return (n2 << 3) + (n2 << 1) + charSequence.charAt(n + 1) - 48;
    }

    static String createErrorMessage(String string, int n) {
        int n2 = n + 32;
        String string2 = string.length() <= n2 + 3 ? string : string.substring(0, n2).concat("...");
        if (n <= 0) {
            return "Invalid format: \"" + string2 + '\"';
        }
        if (n >= string.length()) {
            return "Invalid format: \"" + string2 + "\" is too short";
        }
        return "Invalid format: \"" + string2 + "\" is malformed at \"" + string2.substring(n) + '\"';
    }
}

