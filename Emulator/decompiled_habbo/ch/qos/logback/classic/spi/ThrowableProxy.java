/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.PackagingDataCalculator;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThrowableProxy
implements IThrowableProxy {
    private Throwable throwable;
    private String className;
    private String message;
    StackTraceElementProxy[] stackTraceElementProxyArray;
    int commonFrames;
    private ThrowableProxy cause;
    private ThrowableProxy[] suppressed = NO_SUPPRESSED;
    private transient PackagingDataCalculator packagingDataCalculator;
    private boolean calculatedPackageData = false;
    private static final Method GET_SUPPRESSED_METHOD;
    private static final ThrowableProxy[] NO_SUPPRESSED;

    public ThrowableProxy(Throwable throwable) {
        this.throwable = throwable;
        this.className = throwable.getClass().getName();
        this.message = throwable.getMessage();
        this.stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(throwable.getStackTrace());
        Throwable nested = throwable.getCause();
        if (nested != null) {
            this.cause = new ThrowableProxy(nested);
            this.cause.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(nested.getStackTrace(), this.stackTraceElementProxyArray);
        }
        if (GET_SUPPRESSED_METHOD != null) {
            try {
                Throwable[] throwableSuppressed;
                Object obj = GET_SUPPRESSED_METHOD.invoke((Object)throwable, new Object[0]);
                if (obj instanceof Throwable[] && (throwableSuppressed = (Throwable[])obj).length > 0) {
                    this.suppressed = new ThrowableProxy[throwableSuppressed.length];
                    for (int i = 0; i < throwableSuppressed.length; ++i) {
                        this.suppressed[i] = new ThrowableProxy(throwableSuppressed[i]);
                        this.suppressed[i].commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(throwableSuppressed[i].getStackTrace(), this.stackTraceElementProxyArray);
                    }
                }
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (InvocationTargetException invocationTargetException) {
                // empty catch block
            }
        }
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public StackTraceElementProxy[] getStackTraceElementProxyArray() {
        return this.stackTraceElementProxyArray;
    }

    @Override
    public int getCommonFrames() {
        return this.commonFrames;
    }

    @Override
    public IThrowableProxy getCause() {
        return this.cause;
    }

    @Override
    public IThrowableProxy[] getSuppressed() {
        return this.suppressed;
    }

    public PackagingDataCalculator getPackagingDataCalculator() {
        if (this.throwable != null && this.packagingDataCalculator == null) {
            this.packagingDataCalculator = new PackagingDataCalculator();
        }
        return this.packagingDataCalculator;
    }

    public void calculatePackagingData() {
        if (this.calculatedPackageData) {
            return;
        }
        PackagingDataCalculator pdc = this.getPackagingDataCalculator();
        if (pdc != null) {
            this.calculatedPackageData = true;
            pdc.calculate(this);
        }
    }

    public void fullDump() {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElementProxy step : this.stackTraceElementProxyArray) {
            String string = step.toString();
            builder.append('\t').append(string);
            ThrowableProxyUtil.subjoinPackagingData(builder, step);
            builder.append(CoreConstants.LINE_SEPARATOR);
        }
        System.out.println(builder.toString());
    }

    static {
        Method method = null;
        try {
            method = Throwable.class.getMethod("getSuppressed", new Class[0]);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        GET_SUPPRESSED_METHOD = method;
        NO_SUPPRESSED = new ThrowableProxy[0];
    }
}

