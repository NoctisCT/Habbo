/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.Abbreviator;

public class ClassNameOnlyAbbreviator
implements Abbreviator {
    @Override
    public String abbreviate(String fqClassName) {
        int lastIndex = fqClassName.lastIndexOf(46);
        if (lastIndex != -1) {
            return fqClassName.substring(lastIndex + 1, fqClassName.length());
        }
        return fqClassName;
    }
}

