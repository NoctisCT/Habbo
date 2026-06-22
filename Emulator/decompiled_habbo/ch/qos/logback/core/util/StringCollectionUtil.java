/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class StringCollectionUtil {
    public static void retainMatching(Collection<String> values, String ... patterns) {
        StringCollectionUtil.retainMatching(values, Arrays.asList(patterns));
    }

    public static void retainMatching(Collection<String> values, Collection<String> patterns) {
        if (patterns.isEmpty()) {
            return;
        }
        ArrayList<String> matches = new ArrayList<String>(values.size());
        for (String p : patterns) {
            Pattern pattern = Pattern.compile(p);
            for (String value : values) {
                if (!pattern.matcher(value).matches()) continue;
                matches.add(value);
            }
        }
        values.retainAll(matches);
    }

    public static void removeMatching(Collection<String> values, String ... patterns) {
        StringCollectionUtil.removeMatching(values, Arrays.asList(patterns));
    }

    public static void removeMatching(Collection<String> values, Collection<String> patterns) {
        ArrayList<String> matches = new ArrayList<String>(values.size());
        for (String p : patterns) {
            Pattern pattern = Pattern.compile(p);
            for (String value : values) {
                if (!pattern.matcher(value).matches()) continue;
                matches.add(value);
            }
        }
        values.removeAll(matches);
    }
}

