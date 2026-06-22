/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.NativeLibraryUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public final class NativeLibraryLoader {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NativeLibraryLoader.class);
    private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
    private static final File WORKDIR;
    private static final boolean DELETE_NATIVE_LIB_AFTER_LOADING;
    private static final boolean TRY_TO_PATCH_SHADED_ID;
    private static final byte[] UNIQUE_ID_BYTES;

    public static void loadFirstAvailable(ClassLoader loader, String ... names) {
        ArrayList<Throwable> suppressed = new ArrayList<Throwable>();
        for (String name : names) {
            try {
                NativeLibraryLoader.load(name, loader);
                return;
            }
            catch (Throwable t) {
                suppressed.add(t);
                logger.debug("Unable to load the library '{}', trying next name...", (Object)name, (Object)t);
            }
        }
        IllegalArgumentException iae = new IllegalArgumentException("Failed to load any of the given libraries: " + Arrays.toString(names));
        ThrowableUtil.addSuppressedAndClear(iae, suppressed);
        throw iae;
    }

    private static String calculatePackagePrefix() {
        String expected;
        String maybeShaded = NativeLibraryLoader.class.getName();
        if (!maybeShaded.endsWith(expected = "io!netty!util!internal!NativeLibraryLoader".replace('!', '.'))) {
            throw new UnsatisfiedLinkError(String.format("Could not find prefix added to %s to get %s. When shading, only adding a package prefix is supported", expected, maybeShaded));
        }
        return maybeShaded.substring(0, maybeShaded.length() - expected.length());
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void load(String originalName, ClassLoader loader) {
        String packagePrefix = NativeLibraryLoader.calculatePackagePrefix().replace('.', '_');
        String name = packagePrefix + originalName;
        ArrayList<Throwable> suppressed = new ArrayList<Throwable>();
        try {
            NativeLibraryLoader.loadLibrary(loader, name, false);
            return;
        }
        catch (Throwable ex) {
            suppressed.add(ex);
            if (logger.isDebugEnabled()) {
                logger.debug("{} cannot be loaded from java.library.path, now trying export to -Dio.netty.native.workdir: {}", name, WORKDIR, ex);
            }
            String libname = System.mapLibraryName(name);
            String path = NATIVE_RESOURCE_HOME + libname;
            InputStream in = null;
            FileOutputStream out = null;
            File tmpFile = null;
            URL url = loader == null ? ClassLoader.getSystemResource(path) : loader.getResource(path);
            try {
                if (url == null) {
                    if (!PlatformDependent.isOsx()) {
                        FileNotFoundException fnf = new FileNotFoundException(path);
                        ThrowableUtil.addSuppressedAndClear(fnf, suppressed);
                        throw fnf;
                    }
                    String fileName = path.endsWith(".jnilib") ? "META-INF/native/lib" + name + ".dynlib" : "META-INF/native/lib" + name + ".jnilib";
                    url = loader == null ? ClassLoader.getSystemResource(fileName) : loader.getResource(fileName);
                    if (url == null) {
                        FileNotFoundException fnf = new FileNotFoundException(fileName);
                        ThrowableUtil.addSuppressedAndClear(fnf, suppressed);
                        throw fnf;
                    }
                }
                int index = libname.lastIndexOf(46);
                String prefix = libname.substring(0, index);
                String suffix = libname.substring(index);
                tmpFile = File.createTempFile(prefix, suffix, WORKDIR);
                in = url.openStream();
                out = new FileOutputStream(tmpFile);
                if (NativeLibraryLoader.shouldShadedLibraryIdBePatched(packagePrefix)) {
                    NativeLibraryLoader.patchShadedLibraryId(in, out, originalName, name);
                } else {
                    int length;
                    byte[] buffer = new byte[8192];
                    while ((length = in.read(buffer)) > 0) {
                        ((OutputStream)out).write(buffer, 0, length);
                    }
                }
                out.flush();
                NativeLibraryLoader.closeQuietly(out);
                out = null;
                NativeLibraryLoader.loadLibrary(loader, tmpFile.getPath(), true);
            }
            catch (UnsatisfiedLinkError e) {
                try {
                    try {
                        if (tmpFile != null && tmpFile.isFile() && tmpFile.canRead() && !NoexecVolumeDetector.canExecuteExecutable(tmpFile)) {
                            logger.info("{} exists but cannot be executed even when execute permissions set; check volume for \"noexec\" flag; use -D{}=[path] to set native working directory separately.", (Object)tmpFile.getPath(), (Object)"io.netty.native.workdir");
                        }
                    }
                    catch (Throwable t) {
                        suppressed.add(t);
                        logger.debug("Error checking if {} is on a file store mounted with noexec", (Object)tmpFile, (Object)t);
                    }
                    ThrowableUtil.addSuppressedAndClear(e, suppressed);
                    throw e;
                    catch (Exception e2) {
                        UnsatisfiedLinkError ule = new UnsatisfiedLinkError("could not load a native library: " + name);
                        ule.initCause(e2);
                        ThrowableUtil.addSuppressedAndClear(ule, suppressed);
                        throw ule;
                    }
                }
                catch (Throwable throwable) {
                    NativeLibraryLoader.closeQuietly(in);
                    NativeLibraryLoader.closeQuietly(out);
                    if (tmpFile == null) throw throwable;
                    if (DELETE_NATIVE_LIB_AFTER_LOADING) {
                        if (tmpFile.delete()) throw throwable;
                    }
                    tmpFile.deleteOnExit();
                    throw throwable;
                }
            }
            NativeLibraryLoader.closeQuietly(in);
            NativeLibraryLoader.closeQuietly(out);
            if (tmpFile == null) return;
            if (DELETE_NATIVE_LIB_AFTER_LOADING) {
                if (tmpFile.delete()) return;
            }
            tmpFile.deleteOnExit();
            return;
        }
    }

    static boolean patchShadedLibraryId(InputStream in, OutputStream out, String originalName, String name) throws IOException {
        boolean patched;
        int length;
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(in.available());
        while ((length = in.read(buffer)) > 0) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byteArrayOutputStream.flush();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        if (!NativeLibraryLoader.patchShadedLibraryId(bytes, originalName, name)) {
            String os = PlatformDependent.normalizedOs();
            String arch = PlatformDependent.normalizedArch();
            String osArch = "_" + os + "_" + arch;
            patched = originalName.endsWith(osArch) ? NativeLibraryLoader.patchShadedLibraryId(bytes, originalName.substring(0, originalName.length() - osArch.length()), name) : false;
        } else {
            patched = true;
        }
        out.write(bytes, 0, bytes.length);
        return patched;
    }

    private static boolean shouldShadedLibraryIdBePatched(String packagePrefix) {
        return TRY_TO_PATCH_SHADED_ID && PlatformDependent.isOsx() && !packagePrefix.isEmpty();
    }

    private static boolean patchShadedLibraryId(byte[] bytes, String originalName, String name) {
        int i;
        byte[] nameBytes = originalName.getBytes(CharsetUtil.UTF_8);
        int idIdx = -1;
        block0: for (i = 0; i < bytes.length && bytes.length - i >= nameBytes.length; ++i) {
            int idx = i;
            int j = 0;
            while (j < nameBytes.length && bytes[idx++] == nameBytes[j++]) {
                if (j != nameBytes.length) continue;
                idIdx = i;
                break block0;
            }
        }
        if (idIdx == -1) {
            logger.debug("Was not able to find the ID of the shaded native library {}, can't adjust it.", (Object)name);
            return false;
        }
        for (i = 0; i < nameBytes.length; ++i) {
            bytes[idIdx + i] = UNIQUE_ID_BYTES[PlatformDependent.threadLocalRandom().nextInt(UNIQUE_ID_BYTES.length)];
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Found the ID of the shaded native library {}. Replacing ID part {} with {}", name, originalName, new String(bytes, idIdx, nameBytes.length, CharsetUtil.UTF_8));
        }
        return true;
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static void loadLibrary(ClassLoader loader, String name, boolean absolute) {
        Throwable suppressed = null;
        try {
            Class<?> newHelper = NativeLibraryLoader.tryToLoadClass(loader, NativeLibraryUtil.class);
            NativeLibraryLoader.loadLibraryByHelper(newHelper, name, absolute);
            logger.debug("Successfully loaded the library {}", (Object)name);
            return;
        }
        catch (UnsatisfiedLinkError e) {
            try {
                block5: {
                    suppressed = e;
                    logger.debug("Unable to load the library '{}', trying other loading mechanism.", (Object)name, (Object)e);
                    break block5;
                    catch (Exception e2) {
                        suppressed = e2;
                        logger.debug("Unable to load the library '{}', trying other loading mechanism.", (Object)name, (Object)e2);
                    }
                }
                NativeLibraryUtil.loadLibrary(name, absolute);
                logger.debug("Successfully loaded the library {}", (Object)name);
                return;
            }
            catch (UnsatisfiedLinkError ule) {
                if (suppressed == null) throw ule;
                ThrowableUtil.addSuppressed((Throwable)ule, suppressed);
                throw ule;
            }
        }
    }

    private static void loadLibraryByHelper(final Class<?> helper, final String name, final boolean absolute) throws UnsatisfiedLinkError {
        Object ret = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    Method method = helper.getMethod("loadLibrary", String.class, Boolean.TYPE);
                    method.setAccessible(true);
                    return method.invoke(null, name, absolute);
                }
                catch (Exception e) {
                    return e;
                }
            }
        });
        if (ret instanceof Throwable) {
            Throwable t = (Throwable)ret;
            assert (!(t instanceof UnsatisfiedLinkError)) : t + " should be a wrapper throwable";
            Throwable cause = t.getCause();
            if (cause instanceof UnsatisfiedLinkError) {
                throw (UnsatisfiedLinkError)cause;
            }
            UnsatisfiedLinkError ule = new UnsatisfiedLinkError(t.getMessage());
            ule.initCause(t);
            throw ule;
        }
    }

    private static Class<?> tryToLoadClass(final ClassLoader loader, final Class<?> helper) throws ClassNotFoundException {
        try {
            return Class.forName(helper.getName(), false, loader);
        }
        catch (ClassNotFoundException e1) {
            if (loader == null) {
                throw e1;
            }
            try {
                final byte[] classBinary = NativeLibraryLoader.classToByteArray(helper);
                return (Class)AccessController.doPrivileged(new PrivilegedAction<Class<?>>(){

                    @Override
                    public Class<?> run() {
                        try {
                            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
                            defineClass.setAccessible(true);
                            return (Class)defineClass.invoke((Object)loader, helper.getName(), classBinary, 0, classBinary.length);
                        }
                        catch (Exception e) {
                            throw new IllegalStateException("Define class failed!", e);
                        }
                    }
                });
            }
            catch (ClassNotFoundException e2) {
                ThrowableUtil.addSuppressed((Throwable)e2, e1);
                throw e2;
            }
            catch (RuntimeException e2) {
                ThrowableUtil.addSuppressed((Throwable)e2, e1);
                throw e2;
            }
            catch (Error e2) {
                ThrowableUtil.addSuppressed((Throwable)e2, e1);
                throw e2;
            }
        }
    }

    private static byte[] classToByteArray(Class<?> clazz) throws ClassNotFoundException {
        URL classUrl;
        String fileName = clazz.getName();
        int lastDot = fileName.lastIndexOf(46);
        if (lastDot > 0) {
            fileName = fileName.substring(lastDot + 1);
        }
        if ((classUrl = clazz.getResource(fileName + ".class")) == null) {
            throw new ClassNotFoundException(clazz.getName());
        }
        byte[] buf = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        InputStream in = null;
        try {
            int r22;
            in = classUrl.openStream();
            while ((r22 = in.read(buf)) != -1) {
                out.write(buf, 0, r22);
            }
            byte[] r22 = out.toByteArray();
            return r22;
        }
        catch (IOException ex) {
            throw new ClassNotFoundException(clazz.getName(), ex);
        }
        finally {
            NativeLibraryLoader.closeQuietly(in);
            NativeLibraryLoader.closeQuietly(out);
        }
    }

    private static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private NativeLibraryLoader() {
    }

    static {
        UNIQUE_ID_BYTES = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(CharsetUtil.US_ASCII);
        String workdir = SystemPropertyUtil.get("io.netty.native.workdir");
        if (workdir != null) {
            File f = new File(workdir);
            f.mkdirs();
            try {
                f = f.getAbsoluteFile();
            }
            catch (Exception exception) {
                // empty catch block
            }
            WORKDIR = f;
            logger.debug("-Dio.netty.native.workdir: " + WORKDIR);
        } else {
            WORKDIR = PlatformDependent.tmpdir();
            logger.debug("-Dio.netty.native.workdir: " + WORKDIR + " (io.netty.tmpdir)");
        }
        DELETE_NATIVE_LIB_AFTER_LOADING = SystemPropertyUtil.getBoolean("io.netty.native.deleteLibAfterLoading", true);
        logger.debug("-Dio.netty.native.deleteLibAfterLoading: {}", (Object)DELETE_NATIVE_LIB_AFTER_LOADING);
        TRY_TO_PATCH_SHADED_ID = SystemPropertyUtil.getBoolean("io.netty.native.tryPatchShadedId", true);
        logger.debug("-Dio.netty.native.tryPatchShadedId: {}", (Object)TRY_TO_PATCH_SHADED_ID);
    }

    private static final class NoexecVolumeDetector {
        @SuppressJava6Requirement(reason="Usage guarded by java version check")
        private static boolean canExecuteExecutable(File file) throws IOException {
            EnumSet<PosixFilePermission> executePermissions;
            if (PlatformDependent.javaVersion() < 7) {
                return true;
            }
            if (file.canExecute()) {
                return true;
            }
            Set<PosixFilePermission> existingFilePermissions = Files.getPosixFilePermissions(file.toPath(), new LinkOption[0]);
            if (existingFilePermissions.containsAll(executePermissions = EnumSet.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE))) {
                return false;
            }
            EnumSet<PosixFilePermission> newPermissions = EnumSet.copyOf(existingFilePermissions);
            newPermissions.addAll(executePermissions);
            Files.setPosixFilePermissions(file.toPath(), newPermissions);
            return file.canExecute();
        }

        private NoexecVolumeDetector() {
        }
    }
}

