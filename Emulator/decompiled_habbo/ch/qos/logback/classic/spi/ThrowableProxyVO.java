/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import java.io.Serializable;
import java.util.Arrays;

public class ThrowableProxyVO
implements IThrowableProxy,
Serializable {
    private static final long serialVersionUID = -773438177285807139L;
    private String className;
    private String message;
    private int commonFramesCount;
    private StackTraceElementProxy[] stackTraceElementProxyArray;
    private IThrowableProxy cause;
    private IThrowableProxy[] suppressed;

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public int getCommonFrames() {
        return this.commonFramesCount;
    }

    @Override
    public IThrowableProxy getCause() {
        return this.cause;
    }

    @Override
    public StackTraceElementProxy[] getStackTraceElementProxyArray() {
        return this.stackTraceElementProxyArray;
    }

    @Override
    public IThrowableProxy[] getSuppressed() {
        return this.suppressed;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.className == null ? 0 : this.className.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ThrowableProxyVO other = (ThrowableProxyVO)obj;
        if (this.className == null ? other.className != null : !this.className.equals(other.className)) {
            return false;
        }
        if (!Arrays.equals(this.stackTraceElementProxyArray, other.stackTraceElementProxyArray)) {
            return false;
        }
        if (!Arrays.equals(this.suppressed, other.suppressed)) {
            return false;
        }
        return !(this.cause == null ? other.cause != null : !this.cause.equals(other.cause));
    }

    public static ThrowableProxyVO build(IThrowableProxy throwableProxy) {
        IThrowableProxy[] suppressed;
        if (throwableProxy == null) {
            return null;
        }
        ThrowableProxyVO tpvo = new ThrowableProxyVO();
        tpvo.className = throwableProxy.getClassName();
        tpvo.message = throwableProxy.getMessage();
        tpvo.commonFramesCount = throwableProxy.getCommonFrames();
        tpvo.stackTraceElementProxyArray = throwableProxy.getStackTraceElementProxyArray();
        IThrowableProxy cause = throwableProxy.getCause();
        if (cause != null) {
            tpvo.cause = ThrowableProxyVO.build(cause);
        }
        if ((suppressed = throwableProxy.getSuppressed()) != null) {
            tpvo.suppressed = new IThrowableProxy[suppressed.length];
            for (int i = 0; i < suppressed.length; ++i) {
                tpvo.suppressed[i] = ThrowableProxyVO.build(suppressed[i]);
            }
        }
        return tpvo;
    }
}

