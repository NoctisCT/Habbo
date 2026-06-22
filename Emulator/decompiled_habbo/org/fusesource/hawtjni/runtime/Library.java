/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.hawtjni.runtime;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class Library {
    public static final String STRATEGY_PROPERTY = "hawtjni.strategy";
    public static final String STRATEGY_SHA1 = "sha1";
    public static final String STRATEGY_TEMP = "temp";
    static final String SLASH = System.getProperty("file.separator");
    static final String STRATEGY = System.getProperty("hawtjni.strategy", "windows".equals(Library.getOperatingSystem()) ? "sha1" : "temp");
    private final String name;
    private final String version;
    private final ClassLoader classLoader;
    private boolean loaded;
    private String nativeLibraryPath;
    private URL nativeLibrarySourceUrl;

    public Library(String name) {
        this(name, null, null);
    }

    public Library(String name, Class<?> clazz) {
        this(name, Library.version(clazz), clazz.getClassLoader());
    }

    public Library(String name, String version) {
        this(name, version, null);
    }

    public Library(String name, String version, ClassLoader classLoader) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
        this.version = version;
        this.classLoader = classLoader;
    }

    private static String version(Class<?> clazz) {
        try {
            return clazz.getPackage().getImplementationVersion();
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public String getNativeLibraryPath() {
        return this.nativeLibraryPath;
    }

    public URL getNativeLibrarySourceUrl() {
        return this.nativeLibrarySourceUrl;
    }

    public static String getOperatingSystem() {
        String name = System.getProperty("os.name").toLowerCase().trim();
        if (name.startsWith("linux")) {
            return "linux";
        }
        if (name.startsWith("mac os x")) {
            return "osx";
        }
        if (name.startsWith("win")) {
            return "windows";
        }
        return name.replaceAll("\\W+", "_");
    }

    public static String getPlatform() {
        return Library.getOperatingSystem() + Library.getBitModel();
    }

    public static int getBitModel() {
        String prop = System.getProperty("sun.arch.data.model");
        if (prop == null) {
            prop = System.getProperty("com.ibm.vm.bitmode");
        }
        if (prop != null) {
            return Integer.parseInt(prop);
        }
        return -1;
    }

    public synchronized void load() {
        if (this.loaded) {
            return;
        }
        this.doLoad();
        this.loaded = true;
    }

    private void doLoad() {
        String version = System.getProperty("library." + this.name + ".version");
        if (version == null) {
            version = this.version;
        }
        ArrayList<Throwable> errors = new ArrayList<Throwable>();
        String[] specificDirs = this.getSpecificSearchDirs();
        String libFilename = this.map(this.name);
        String versionlibFilename = version == null ? null : this.map(this.name + "-" + version);
        String customPath = System.getProperty("library." + this.name + ".path");
        if (customPath != null) {
            for (String dir : specificDirs) {
                if (version != null && this.load(errors, this.file(customPath, dir, versionlibFilename))) {
                    return;
                }
                if (!this.load(errors, this.file(customPath, dir, libFilename))) continue;
                return;
            }
        }
        if (version != null && this.loadLibrary(errors, this.name + Library.getBitModel() + "-" + version)) {
            return;
        }
        if (version != null && this.loadLibrary(errors, this.name + "-" + version)) {
            return;
        }
        if (this.loadLibrary(errors, this.name)) {
            return;
        }
        if (this.classLoader != null) {
            String targetLibName = version != null ? versionlibFilename : libFilename;
            for (String dir : specificDirs) {
                if (version != null && this.extractAndLoad(errors, customPath, dir, versionlibFilename, targetLibName)) {
                    return;
                }
                if (!this.extractAndLoad(errors, customPath, dir, libFilename, targetLibName)) continue;
                return;
            }
        }
        UnsatisfiedLinkError e = new UnsatisfiedLinkError("Could not load library. Reasons: " + errors.toString());
        try {
            Method method = Throwable.class.getMethod("addSuppressed", Throwable.class);
            for (Throwable t : errors) {
                method.invoke((Object)e, t);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        throw e;
    }

    @Deprecated
    public final String getArchSpecifcResourcePath() {
        return this.getArchSpecificResourcePath();
    }

    public final String getArchSpecificResourcePath() {
        return "META-INF/native/" + Library.getPlatform() + "/" + System.getProperty("os.arch") + "/" + this.map(this.name);
    }

    @Deprecated
    public final String getOperatingSystemSpecifcResourcePath() {
        return this.getOperatingSystemSpecificResourcePath();
    }

    public final String getOperatingSystemSpecificResourcePath() {
        return this.getPlatformSpecificResourcePath(Library.getOperatingSystem());
    }

    @Deprecated
    public final String getPlatformSpecifcResourcePath() {
        return this.getPlatformSpecificResourcePath();
    }

    public final String getPlatformSpecificResourcePath() {
        return this.getPlatformSpecificResourcePath(Library.getPlatform());
    }

    @Deprecated
    public final String getPlatformSpecifcResourcePath(String platform) {
        return this.getPlatformSpecificResourcePath(platform);
    }

    public final String getPlatformSpecificResourcePath(String platform) {
        return "META-INF/native/" + platform + "/" + this.map(this.name);
    }

    @Deprecated
    public final String getResorucePath() {
        return this.getResourcePath();
    }

    public final String getResourcePath() {
        return "META-INF/native/" + this.map(this.name);
    }

    public final String getLibraryFileName() {
        return this.map(this.name);
    }

    public final String[] getSpecificSearchDirs() {
        return new String[]{Library.getPlatform() + "/" + System.getProperty("os.arch"), Library.getPlatform(), Library.getOperatingSystem(), "."};
    }

    private boolean extractAndLoad(ArrayList<Throwable> errors, String customPath, String dir, String libName, String targetLibName) {
        String resourcePath = "META-INF/native/" + (dir == null ? "" : dir + '/') + libName;
        URL resource = this.classLoader.getResource(resourcePath);
        if (resource != null) {
            int idx = targetLibName.lastIndexOf(46);
            String prefix = targetLibName.substring(0, idx) + "-";
            String suffix = targetLibName.substring(idx);
            for (File path : Arrays.asList(customPath != null ? this.file(customPath) : null, this.file(System.getProperty("java.io.tmpdir")), this.file(System.getProperty("user.home"), ".hawtjni", this.name))) {
                File target;
                if (path == null || (target = STRATEGY_SHA1.equals(STRATEGY) ? this.extractSha1(errors, resource, prefix, suffix, path) : this.extractTemp(errors, resource, prefix, suffix, path)) == null || !this.load(errors, target)) continue;
                this.nativeLibrarySourceUrl = resource;
                return true;
            }
        }
        return false;
    }

    private File file(String ... paths) {
        File rc = null;
        for (String path : paths) {
            if (rc == null) {
                rc = new File(path);
                continue;
            }
            if (path == null) continue;
            rc = new File(rc, path);
        }
        return rc;
    }

    private String map(String libName) {
        String ext;
        if ((libName = System.mapLibraryName(libName)).endsWith(ext = ".dylib")) {
            libName = libName.substring(0, libName.length() - ext.length()) + ".jnilib";
        }
        return libName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File extractSha1(ArrayList<Throwable> errors, URL source, String prefix, String suffix, File directory) {
        File file;
        File target = null;
        if (!(directory = directory.getAbsoluteFile()).exists() && !directory.mkdirs()) {
            errors.add(new IOException("Unable to create directory: " + directory));
            return null;
        }
        String sha1 = this.computeSha1(source.openStream());
        String sha1f = "";
        target = new File(directory, prefix + sha1 + suffix);
        if (target.isFile() && target.canRead()) {
            sha1f = this.computeSha1(new FileInputStream(target));
        }
        if (sha1f.equals(sha1)) {
            return target;
        }
        FileOutputStream os = null;
        InputStream is = null;
        try {
            is = source.openStream();
            if (is != null) {
                int read;
                byte[] buffer = new byte[4096];
                os = new FileOutputStream(target);
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                this.chmod755(target);
            }
            file = target;
        }
        catch (Throwable throwable) {
            try {
                Library.close(os);
                Library.close(is);
                throw throwable;
            }
            catch (Throwable e) {
                IOException io;
                if (target != null) {
                    target.delete();
                    io = new IOException("Unable to extract library from " + source + " to " + target);
                } else {
                    io = new IOException("Unable to create temporary file in " + directory);
                }
                io.initCause(e);
                errors.add(io);
                return null;
            }
        }
        Library.close(os);
        Library.close(is);
        return file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String computeSha1(InputStream is) throws NoSuchAlgorithmException, IOException {
        String sha1;
        try {
            int read;
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] buffer = new byte[4096];
            while ((read = is.read(buffer)) != -1) {
                mDigest.update(buffer, 0, read);
            }
            byte[] result = mDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(Integer.toString((b & 0xFF) + 256, 16).substring(1));
            }
            sha1 = sb.toString();
        }
        finally {
            Library.close(is);
        }
        return sha1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File extractTemp(ArrayList<Throwable> errors, URL source, String prefix, String suffix, File directory) {
        File file;
        File target = null;
        if (!(directory = directory.getAbsoluteFile()).exists() && !directory.mkdirs()) {
            errors.add(new IOException("Unable to create directory: " + directory));
            return null;
        }
        FileOutputStream os = null;
        InputStream is = null;
        try {
            target = File.createTempFile(prefix, suffix, directory);
            is = source.openStream();
            if (is != null) {
                int read;
                byte[] buffer = new byte[4096];
                os = new FileOutputStream(target);
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                this.chmod755(target);
            }
            target.deleteOnExit();
            file = target;
        }
        catch (Throwable throwable) {
            try {
                Library.close(os);
                Library.close(is);
                throw throwable;
            }
            catch (Throwable e) {
                IOException io;
                if (target != null) {
                    target.delete();
                    io = new IOException("Unable to extract library from " + source + " to " + target);
                } else {
                    io = new IOException("Unable to create temporary file in " + directory);
                }
                io.initCause(e);
                errors.add(io);
                return null;
            }
        }
        Library.close(os);
        Library.close(is);
        return file;
    }

    private static void close(Closeable file) {
        if (file != null) {
            try {
                file.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private void chmod755(File file) {
        if (Library.getPlatform().startsWith("windows")) {
            return;
        }
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            Class<?> posixFilePermissionsClass = classLoader.loadClass("java.nio.file.attribute.PosixFilePermissions");
            Method fromStringMethod = posixFilePermissionsClass.getMethod("fromString", String.class);
            Object permissionSet = fromStringMethod.invoke(null, "rwxr-xr-x");
            Object path = file.getClass().getMethod("toPath", new Class[0]).invoke((Object)file, new Object[0]);
            Class<?> pathClass = classLoader.loadClass("java.nio.file.Path");
            Class<?> filesClass = classLoader.loadClass("java.nio.file.Files");
            Method setPosixFilePermissionsMethod = filesClass.getMethod("setPosixFilePermissions", pathClass, Set.class);
            setPosixFilePermissionsMethod.invoke(null, path, permissionSet);
        }
        catch (Throwable ignored) {
            try {
                Runtime.getRuntime().exec(new String[]{"chmod", "755", file.getCanonicalPath()}).waitFor();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    private boolean load(ArrayList<Throwable> errors, File lib) {
        try {
            System.load(lib.getPath());
            this.nativeLibraryPath = lib.getPath();
            return true;
        }
        catch (UnsatisfiedLinkError e) {
            LinkageError le = new LinkageError("Unable to load library from " + lib);
            le.initCause(e);
            errors.add(le);
            return false;
        }
    }

    private boolean loadLibrary(ArrayList<Throwable> errors, String lib) {
        try {
            System.loadLibrary(lib);
            this.nativeLibraryPath = "java.library.path,sun.boot.library.pathlib:" + lib;
            return true;
        }
        catch (UnsatisfiedLinkError e) {
            LinkageError le = new LinkageError("Unable to load library " + lib);
            le.initCause(e);
            errors.add(le);
            return false;
        }
    }
}

