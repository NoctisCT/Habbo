/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  sun.reflect.Reflection
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.spi.ClassPackagingData;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.STEUtil;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import sun.reflect.Reflection;

public class PackagingDataCalculator {
    static final StackTraceElementProxy[] STEP_ARRAY_TEMPLATE = new StackTraceElementProxy[0];
    HashMap<String, ClassPackagingData> cache = new HashMap();
    private static boolean GET_CALLER_CLASS_METHOD_AVAILABLE = false;

    public void calculate(IThrowableProxy tp) {
        while (tp != null) {
            this.populateFrames(tp.getStackTraceElementProxyArray());
            IThrowableProxy[] suppressed = tp.getSuppressed();
            if (suppressed != null) {
                for (IThrowableProxy current : suppressed) {
                    this.populateFrames(current.getStackTraceElementProxyArray());
                }
            }
            tp = tp.getCause();
        }
    }

    void populateFrames(StackTraceElementProxy[] stepArray) {
        Throwable t = new Throwable("local stack reference");
        StackTraceElement[] localteSTEArray = t.getStackTrace();
        int commonFrames = STEUtil.findNumberOfCommonFrames(localteSTEArray, stepArray);
        int localFirstCommon = localteSTEArray.length - commonFrames;
        int stepFirstCommon = stepArray.length - commonFrames;
        ClassLoader lastExactClassLoader = null;
        ClassLoader firsExactClassLoader = null;
        int missfireCount = 0;
        for (int i = 0; i < commonFrames; ++i) {
            ClassPackagingData pi;
            Class callerClass = null;
            if (GET_CALLER_CLASS_METHOD_AVAILABLE) {
                callerClass = Reflection.getCallerClass((int)(localFirstCommon + i - missfireCount + 1));
            }
            StackTraceElementProxy step = stepArray[stepFirstCommon + i];
            String stepClassname = step.ste.getClassName();
            if (callerClass != null && stepClassname.equals(callerClass.getName())) {
                lastExactClassLoader = callerClass.getClassLoader();
                if (firsExactClassLoader == null) {
                    firsExactClassLoader = lastExactClassLoader;
                }
                pi = this.calculateByExactType(callerClass);
                step.setClassPackagingData(pi);
                continue;
            }
            ++missfireCount;
            pi = this.computeBySTEP(step, lastExactClassLoader);
            step.setClassPackagingData(pi);
        }
        this.populateUncommonFrames(commonFrames, stepArray, firsExactClassLoader);
    }

    void populateUncommonFrames(int commonFrames, StackTraceElementProxy[] stepArray, ClassLoader firstExactClassLoader) {
        int uncommonFrames = stepArray.length - commonFrames;
        for (int i = 0; i < uncommonFrames; ++i) {
            StackTraceElementProxy step = stepArray[i];
            ClassPackagingData pi = this.computeBySTEP(step, firstExactClassLoader);
            step.setClassPackagingData(pi);
        }
    }

    private ClassPackagingData calculateByExactType(Class type) {
        String className = type.getName();
        ClassPackagingData cpd = this.cache.get(className);
        if (cpd != null) {
            return cpd;
        }
        String version = this.getImplementationVersion(type);
        String codeLocation = this.getCodeLocation(type);
        cpd = new ClassPackagingData(codeLocation, version);
        this.cache.put(className, cpd);
        return cpd;
    }

    private ClassPackagingData computeBySTEP(StackTraceElementProxy step, ClassLoader lastExactClassLoader) {
        String className = step.ste.getClassName();
        ClassPackagingData cpd = this.cache.get(className);
        if (cpd != null) {
            return cpd;
        }
        Class type = this.bestEffortLoadClass(lastExactClassLoader, className);
        String version = this.getImplementationVersion(type);
        String codeLocation = this.getCodeLocation(type);
        cpd = new ClassPackagingData(codeLocation, version, false);
        this.cache.put(className, cpd);
        return cpd;
    }

    String getImplementationVersion(Class type) {
        if (type == null) {
            return "na";
        }
        Package aPackage = type.getPackage();
        if (aPackage != null) {
            String v = aPackage.getImplementationVersion();
            if (v == null) {
                return "na";
            }
            return v;
        }
        return "na";
    }

    String getCodeLocation(Class type) {
        try {
            URL resource;
            CodeSource codeSource;
            if (type != null && (codeSource = type.getProtectionDomain().getCodeSource()) != null && (resource = codeSource.getLocation()) != null) {
                String locationStr = resource.toString();
                String result = this.getCodeLocation(locationStr, '/');
                if (result != null) {
                    return result;
                }
                return this.getCodeLocation(locationStr, '\\');
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return "na";
    }

    private String getCodeLocation(String locationStr, char separator) {
        int idx = locationStr.lastIndexOf(separator);
        if (this.isFolder(idx, locationStr)) {
            idx = locationStr.lastIndexOf(separator, idx - 1);
            return locationStr.substring(idx + 1);
        }
        if (idx > 0) {
            return locationStr.substring(idx + 1);
        }
        return null;
    }

    private boolean isFolder(int idx, String text) {
        return idx != -1 && idx + 1 == text.length();
    }

    private Class loadClass(ClassLoader cl, String className) {
        if (cl == null) {
            return null;
        }
        try {
            return cl.loadClass(className);
        }
        catch (ClassNotFoundException e1) {
            return null;
        }
        catch (NoClassDefFoundError e1) {
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Class bestEffortLoadClass(ClassLoader lastGuaranteedClassLoader, String className) {
        Class result = this.loadClass(lastGuaranteedClassLoader, className);
        if (result != null) {
            return result;
        }
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != lastGuaranteedClassLoader) {
            result = this.loadClass(tccl, className);
        }
        if (result != null) {
            return result;
        }
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e1) {
            return null;
        }
        catch (NoClassDefFoundError e1) {
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static {
        try {
            Reflection.getCallerClass((int)2);
            GET_CALLER_CLASS_METHOD_AVAILABLE = true;
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
        }
        catch (NoSuchMethodError noSuchMethodError) {
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
        }
        catch (Throwable e) {
            System.err.println("Unexpected exception");
            e.printStackTrace();
        }
    }
}

