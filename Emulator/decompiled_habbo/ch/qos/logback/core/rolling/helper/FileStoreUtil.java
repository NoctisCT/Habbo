/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RolloverFailure;
import java.io.File;
import java.lang.reflect.Method;

public class FileStoreUtil {
    static final String PATH_CLASS_STR = "java.nio.file.Path";
    static final String FILES_CLASS_STR = "java.nio.file.Files";

    public static boolean areOnSameFileStore(File a, File b) throws RolloverFailure {
        if (!a.exists()) {
            throw new IllegalArgumentException("File [" + a + "] does not exist.");
        }
        if (!b.exists()) {
            throw new IllegalArgumentException("File [" + b + "] does not exist.");
        }
        try {
            Class<?> pathClass = Class.forName(PATH_CLASS_STR);
            Class<?> filesClass = Class.forName(FILES_CLASS_STR);
            Method toPath = File.class.getMethod("toPath", new Class[0]);
            Method getFileStoreMethod = filesClass.getMethod("getFileStore", pathClass);
            Object pathA = toPath.invoke((Object)a, new Object[0]);
            Object pathB = toPath.invoke((Object)b, new Object[0]);
            Object fileStoreA = getFileStoreMethod.invoke(null, pathA);
            Object fileStoreB = getFileStoreMethod.invoke(null, pathB);
            return fileStoreA.equals(fileStoreB);
        }
        catch (Exception e) {
            throw new RolloverFailure("Failed to check file store equality for [" + a + "] and [" + b + "]", e);
        }
    }
}

