/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSize {
    private static final String LENGTH_PART = "([0-9]+)";
    private static final int DOUBLE_GROUP = 1;
    private static final String UNIT_PART = "(|kb|mb|gb)s?";
    private static final int UNIT_GROUP = 2;
    private static final Pattern FILE_SIZE_PATTERN = Pattern.compile("([0-9]+)\\s*(|kb|mb|gb)s?", 2);
    public static final long KB_COEFFICIENT = 1024L;
    public static final long MB_COEFFICIENT = 0x100000L;
    public static final long GB_COEFFICIENT = 0x40000000L;
    final long size;

    public FileSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return this.size;
    }

    public static FileSize valueOf(String fileSizeStr) {
        Matcher matcher = FILE_SIZE_PATTERN.matcher(fileSizeStr);
        if (matcher.matches()) {
            long coefficient;
            String lenStr = matcher.group(1);
            String unitStr = matcher.group(2);
            long lenValue = Long.valueOf(lenStr);
            if (unitStr.equalsIgnoreCase("")) {
                coefficient = 1L;
            } else if (unitStr.equalsIgnoreCase("kb")) {
                coefficient = 1024L;
            } else if (unitStr.equalsIgnoreCase("mb")) {
                coefficient = 0x100000L;
            } else if (unitStr.equalsIgnoreCase("gb")) {
                coefficient = 0x40000000L;
            } else {
                throw new IllegalStateException("Unexpected " + unitStr);
            }
            return new FileSize(lenValue * coefficient);
        }
        throw new IllegalArgumentException("String value [" + fileSizeStr + "] is not in the expected format.");
    }

    public String toString() {
        long inKB = this.size / 1024L;
        if (inKB == 0L) {
            return this.size + " Bytes";
        }
        long inMB = this.size / 0x100000L;
        if (inMB == 0L) {
            return inKB + " KB";
        }
        long inGB = this.size / 0x40000000L;
        if (inGB == 0L) {
            return inMB + " MB";
        }
        return inGB + " GB";
    }
}

