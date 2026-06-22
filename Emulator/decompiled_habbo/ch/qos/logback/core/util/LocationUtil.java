/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import ch.qos.logback.core.util.Loader;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

public class LocationUtil {
    public static final String SCHEME_PATTERN = "^\\p{Alpha}[\\p{Alnum}+.-]*:.*$";
    public static final String CLASSPATH_SCHEME = "classpath:";

    public static URL urlForResource(String location) throws MalformedURLException, FileNotFoundException {
        if (location == null) {
            throw new NullPointerException("location is required");
        }
        URL url = null;
        if (!location.matches(SCHEME_PATTERN)) {
            url = Loader.getResourceBySelfClassLoader(location);
        } else if (location.startsWith(CLASSPATH_SCHEME)) {
            String path = location.substring(CLASSPATH_SCHEME.length());
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.length() == 0) {
                throw new MalformedURLException("path is required");
            }
            url = Loader.getResourceBySelfClassLoader(path);
        } else {
            url = new URL(location);
        }
        if (url == null) {
            throw new FileNotFoundException(location);
        }
        return url;
    }
}

