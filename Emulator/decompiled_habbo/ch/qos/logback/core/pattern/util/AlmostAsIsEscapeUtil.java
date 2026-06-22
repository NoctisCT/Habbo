/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.util;

import ch.qos.logback.core.pattern.util.RestrictedEscapeUtil;

public class AlmostAsIsEscapeUtil
extends RestrictedEscapeUtil {
    @Override
    public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
        super.escape("%)", buf, next, pointer);
    }
}

