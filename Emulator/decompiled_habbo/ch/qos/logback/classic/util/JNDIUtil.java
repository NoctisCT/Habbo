/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JNDIUtil {
    public static Context getInitialContext() throws NamingException {
        return new InitialContext();
    }

    public static String lookup(Context ctx, String name) {
        if (ctx == null) {
            return null;
        }
        try {
            Object lookup = ctx.lookup(name);
            return lookup == null ? null : lookup.toString();
        }
        catch (NamingException e) {
            return null;
        }
    }
}

