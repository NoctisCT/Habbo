/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javassist.CannotCompileException
 *  javassist.ClassPath
 *  javassist.ClassPool
 *  javassist.CtClass
 *  javassist.CtMethod
 *  javassist.CtNewMethod
 *  javassist.LoaderClassPath
 *  javassist.NotFoundException
 */
package com.zaxxer.hikari.util;

import com.zaxxer.hikari.pool.ProxyCallableStatement;
import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyDatabaseMetaData;
import com.zaxxer.hikari.pool.ProxyPreparedStatement;
import com.zaxxer.hikari.pool.ProxyResultSet;
import com.zaxxer.hikari.pool.ProxyStatement;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public final class JavassistProxyFactory {
    private static ClassPool classPool;
    private static String genDirectory;

    public static void main(String ... args) throws Exception {
        classPool = new ClassPool();
        classPool.importPackage("java.sql");
        classPool.appendClassPath((ClassPath)new LoaderClassPath(JavassistProxyFactory.class.getClassLoader()));
        if (args.length > 0) {
            genDirectory = args[0];
        }
        String methodBody = "{ try { return delegate.method($$); } catch (SQLException e) { throw checkException(e); } }";
        JavassistProxyFactory.generateProxyClass(Connection.class, ProxyConnection.class.getName(), methodBody);
        JavassistProxyFactory.generateProxyClass(Statement.class, ProxyStatement.class.getName(), methodBody);
        JavassistProxyFactory.generateProxyClass(ResultSet.class, ProxyResultSet.class.getName(), methodBody);
        JavassistProxyFactory.generateProxyClass(DatabaseMetaData.class, ProxyDatabaseMetaData.class.getName(), methodBody);
        methodBody = "{ try { return ((cast) delegate).method($$); } catch (SQLException e) { throw checkException(e); } }";
        JavassistProxyFactory.generateProxyClass(PreparedStatement.class, ProxyPreparedStatement.class.getName(), methodBody);
        JavassistProxyFactory.generateProxyClass(CallableStatement.class, ProxyCallableStatement.class.getName(), methodBody);
        JavassistProxyFactory.modifyProxyFactory();
    }

    private static void modifyProxyFactory() throws NotFoundException, CannotCompileException, IOException {
        System.out.println("Generating method bodies for com.zaxxer.hikari.proxy.ProxyFactory");
        String packageName = ProxyConnection.class.getPackage().getName();
        CtClass proxyCt = classPool.getCtClass("com.zaxxer.hikari.pool.ProxyFactory");
        block16: for (CtMethod method : proxyCt.getMethods()) {
            switch (method.getName()) {
                case "getProxyConnection": {
                    method.setBody("{return new " + packageName + ".HikariProxyConnection($$);}");
                    continue block16;
                }
                case "getProxyStatement": {
                    method.setBody("{return new " + packageName + ".HikariProxyStatement($$);}");
                    continue block16;
                }
                case "getProxyPreparedStatement": {
                    method.setBody("{return new " + packageName + ".HikariProxyPreparedStatement($$);}");
                    continue block16;
                }
                case "getProxyCallableStatement": {
                    method.setBody("{return new " + packageName + ".HikariProxyCallableStatement($$);}");
                    continue block16;
                }
                case "getProxyResultSet": {
                    method.setBody("{return new " + packageName + ".HikariProxyResultSet($$);}");
                    continue block16;
                }
                case "getProxyDatabaseMetaData": {
                    method.setBody("{return new " + packageName + ".HikariProxyDatabaseMetaData($$);}");
                    continue block16;
                }
            }
        }
        proxyCt.writeFile(genDirectory + "target/classes");
    }

    private static <T> void generateProxyClass(Class<T> primaryInterface, String superClassName, String methodBody) throws Exception {
        String newClassName = superClassName.replaceAll("(.+)\\.(\\w+)", "$1.Hikari$2");
        CtClass superCt = classPool.getCtClass(superClassName);
        CtClass targetCt = classPool.makeClass(newClassName, superCt);
        targetCt.setModifiers(16);
        System.out.println("Generating " + newClassName);
        targetCt.setModifiers(1);
        HashSet<String> superSigs = new HashSet<String>();
        for (CtMethod method : superCt.getMethods()) {
            if ((method.getModifiers() & 0x10) != 16) continue;
            superSigs.add(method.getName() + method.getSignature());
        }
        HashSet<String> methods = new HashSet<String>();
        for (Class<?> intf : JavassistProxyFactory.getAllInterfaces(primaryInterface)) {
            CtClass intfCt = classPool.getCtClass(intf.getName());
            targetCt.addInterface(intfCt);
            for (CtMethod intfMethod : intfCt.getDeclaredMethods()) {
                String signature = intfMethod.getName() + intfMethod.getSignature();
                if (superSigs.contains(signature) || methods.contains(signature)) continue;
                methods.add(signature);
                CtMethod method = CtNewMethod.copy((CtMethod)intfMethod, (CtClass)targetCt, null);
                String modifiedBody = methodBody;
                CtMethod superMethod = superCt.getMethod(intfMethod.getName(), intfMethod.getSignature());
                if ((superMethod.getModifiers() & 0x400) != 1024 && !JavassistProxyFactory.isDefaultMethod(intf, intfMethod)) {
                    modifiedBody = modifiedBody.replace("((cast) ", "");
                    modifiedBody = modifiedBody.replace("delegate", "super");
                    modifiedBody = modifiedBody.replace("super)", "super");
                }
                modifiedBody = modifiedBody.replace("cast", primaryInterface.getName());
                modifiedBody = JavassistProxyFactory.isThrowsSqlException(intfMethod) ? modifiedBody.replace("method", method.getName()) : "{ return ((cast) delegate).method($$); }".replace("method", method.getName()).replace("cast", primaryInterface.getName());
                if (method.getReturnType() == CtClass.voidType) {
                    modifiedBody = modifiedBody.replace("return", "");
                }
                method.setBody(modifiedBody);
                targetCt.addMethod(method);
            }
        }
        targetCt.getClassFile().setMajorVersion(52);
        targetCt.writeFile(genDirectory + "target/classes");
    }

    private static boolean isThrowsSqlException(CtMethod method) {
        try {
            for (CtClass clazz : method.getExceptionTypes()) {
                if (!clazz.getSimpleName().equals("SQLException")) continue;
                return true;
            }
        }
        catch (NotFoundException notFoundException) {
            // empty catch block
        }
        return false;
    }

    private static boolean isDefaultMethod(Class<?> intf, CtMethod intfMethod) throws Exception {
        ArrayList paramTypes = new ArrayList();
        for (CtClass pt : intfMethod.getParameterTypes()) {
            paramTypes.add(JavassistProxyFactory.toJavaClass(pt));
        }
        return intf.getDeclaredMethod(intfMethod.getName(), paramTypes.toArray(new Class[0])).toString().contains("default ");
    }

    private static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        LinkedHashSet interfaces = new LinkedHashSet();
        for (Class<?> intf : clazz.getInterfaces()) {
            if (intf.getInterfaces().length > 0) {
                interfaces.addAll(JavassistProxyFactory.getAllInterfaces(intf));
            }
            interfaces.add(intf);
        }
        if (clazz.getSuperclass() != null) {
            interfaces.addAll(JavassistProxyFactory.getAllInterfaces(clazz.getSuperclass()));
        }
        if (clazz.isInterface()) {
            interfaces.add(clazz);
        }
        return interfaces;
    }

    private static Class<?> toJavaClass(CtClass cls) throws Exception {
        if (cls.getName().endsWith("[]")) {
            return Array.newInstance(JavassistProxyFactory.toJavaClass(cls.getName().replace("[]", "")), 0).getClass();
        }
        return JavassistProxyFactory.toJavaClass(cls.getName());
    }

    private static Class<?> toJavaClass(String cn) throws Exception {
        switch (cn) {
            case "int": {
                return Integer.TYPE;
            }
            case "long": {
                return Long.TYPE;
            }
            case "short": {
                return Short.TYPE;
            }
            case "byte": {
                return Byte.TYPE;
            }
            case "float": {
                return Float.TYPE;
            }
            case "double": {
                return Double.TYPE;
            }
            case "boolean": {
                return Boolean.TYPE;
            }
            case "char": {
                return Character.TYPE;
            }
            case "void": {
                return Void.TYPE;
            }
        }
        return Class.forName(cn);
    }

    static {
        genDirectory = "";
    }
}

