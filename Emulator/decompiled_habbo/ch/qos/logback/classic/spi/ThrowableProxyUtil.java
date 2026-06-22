/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.spi.ClassPackagingData;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.CoreConstants;

public class ThrowableProxyUtil {
    public static final int REGULAR_EXCEPTION_INDENT = 1;
    public static final int SUPPRESSED_EXCEPTION_INDENT = 1;
    private static final int BUILDER_CAPACITY = 2048;

    public static void build(ThrowableProxy nestedTP, Throwable nestedThrowable, ThrowableProxy parentTP) {
        StackTraceElement[] nestedSTE = nestedThrowable.getStackTrace();
        int commonFramesCount = -1;
        if (parentTP != null) {
            commonFramesCount = ThrowableProxyUtil.findNumberOfCommonFrames(nestedSTE, parentTP.getStackTraceElementProxyArray());
        }
        nestedTP.commonFrames = commonFramesCount;
        nestedTP.stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(nestedSTE);
    }

    static StackTraceElementProxy[] steArrayToStepArray(StackTraceElement[] stea) {
        if (stea == null) {
            return new StackTraceElementProxy[0];
        }
        StackTraceElementProxy[] stepa = new StackTraceElementProxy[stea.length];
        for (int i = 0; i < stepa.length; ++i) {
            stepa[i] = new StackTraceElementProxy(stea[i]);
        }
        return stepa;
    }

    static int findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElementProxy[] parentSTEPArray) {
        StackTraceElement otherSte;
        StackTraceElement ste;
        if (parentSTEPArray == null || steArray == null) {
            return 0;
        }
        int steIndex = steArray.length - 1;
        int count = 0;
        for (int parentIndex = parentSTEPArray.length - 1; steIndex >= 0 && parentIndex >= 0 && (ste = steArray[steIndex]).equals(otherSte = parentSTEPArray[parentIndex].ste); --steIndex, --parentIndex) {
            ++count;
        }
        return count;
    }

    public static String asString(IThrowableProxy tp) {
        StringBuilder sb = new StringBuilder(2048);
        ThrowableProxyUtil.recursiveAppend(sb, null, 1, tp);
        return sb.toString();
    }

    private static void recursiveAppend(StringBuilder sb, String prefix, int indent, IThrowableProxy tp) {
        if (tp == null) {
            return;
        }
        ThrowableProxyUtil.subjoinFirstLine(sb, prefix, indent, tp);
        sb.append(CoreConstants.LINE_SEPARATOR);
        ThrowableProxyUtil.subjoinSTEPArray(sb, indent, tp);
        IThrowableProxy[] suppressed = tp.getSuppressed();
        if (suppressed != null) {
            for (IThrowableProxy current : suppressed) {
                ThrowableProxyUtil.recursiveAppend(sb, "Suppressed: ", indent + 1, current);
            }
        }
        ThrowableProxyUtil.recursiveAppend(sb, "Caused by: ", indent, tp.getCause());
    }

    public static void indent(StringBuilder buf, int indent) {
        for (int j = 0; j < indent; ++j) {
            buf.append('\t');
        }
    }

    private static void subjoinFirstLine(StringBuilder buf, String prefix, int indent, IThrowableProxy tp) {
        ThrowableProxyUtil.indent(buf, indent - 1);
        if (prefix != null) {
            buf.append(prefix);
        }
        ThrowableProxyUtil.subjoinExceptionMessage(buf, tp);
    }

    public static void subjoinPackagingData(StringBuilder builder, StackTraceElementProxy step) {
        ClassPackagingData cpd;
        if (step != null && (cpd = step.getClassPackagingData()) != null) {
            if (!cpd.isExact()) {
                builder.append(" ~[");
            } else {
                builder.append(" [");
            }
            builder.append(cpd.getCodeLocation()).append(':').append(cpd.getVersion()).append(']');
        }
    }

    public static void subjoinSTEP(StringBuilder sb, StackTraceElementProxy step) {
        sb.append(step.toString());
        ThrowableProxyUtil.subjoinPackagingData(sb, step);
    }

    public static void subjoinSTEPArray(StringBuilder sb, IThrowableProxy tp) {
        ThrowableProxyUtil.subjoinSTEPArray(sb, 1, tp);
    }

    public static void subjoinSTEPArray(StringBuilder sb, int indentLevel, IThrowableProxy tp) {
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        int commonFrames = tp.getCommonFrames();
        for (int i = 0; i < stepArray.length - commonFrames; ++i) {
            StackTraceElementProxy step = stepArray[i];
            ThrowableProxyUtil.indent(sb, indentLevel);
            ThrowableProxyUtil.subjoinSTEP(sb, step);
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        if (commonFrames > 0) {
            ThrowableProxyUtil.indent(sb, indentLevel);
            sb.append("... ").append(commonFrames).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }
    }

    public static void subjoinFirstLine(StringBuilder buf, IThrowableProxy tp) {
        int commonFrames = tp.getCommonFrames();
        if (commonFrames > 0) {
            buf.append("Caused by: ");
        }
        ThrowableProxyUtil.subjoinExceptionMessage(buf, tp);
    }

    public static void subjoinFirstLineRootCauseFirst(StringBuilder buf, IThrowableProxy tp) {
        if (tp.getCause() != null) {
            buf.append("Wrapped by: ");
        }
        ThrowableProxyUtil.subjoinExceptionMessage(buf, tp);
    }

    private static void subjoinExceptionMessage(StringBuilder buf, IThrowableProxy tp) {
        buf.append(tp.getClassName()).append(": ").append(tp.getMessage());
    }
}

