/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;

public class SpdyProtocolException
extends Exception {
    private static final long serialVersionUID = 7870000537743847264L;

    public SpdyProtocolException() {
    }

    public SpdyProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpdyProtocolException(String message) {
        super(message);
    }

    public SpdyProtocolException(Throwable cause) {
        super(cause);
    }

    static SpdyProtocolException newStatic(String message) {
        if (PlatformDependent.javaVersion() >= 7) {
            return new SpdyProtocolException(message, true);
        }
        return new SpdyProtocolException(message);
    }

    @SuppressJava6Requirement(reason="uses Java 7+ Exception.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
    private SpdyProtocolException(String message, boolean shared) {
        super(message, null, false, true);
        assert (shared);
    }
}

