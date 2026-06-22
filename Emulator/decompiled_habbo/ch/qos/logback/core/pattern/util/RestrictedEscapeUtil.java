/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.util;

import ch.qos.logback.core.pattern.util.IEscapeUtil;

public class RestrictedEscapeUtil
implements IEscapeUtil {
    @Override
    public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
        if (escapeChars.indexOf(next) >= 0) {
            buf.append(next);
        } else {
            buf.append("\\");
            buf.append(next);
        }
    }
}

