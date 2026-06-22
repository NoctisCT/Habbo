/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;

public class ChannelException
extends RuntimeException {
    private static final long serialVersionUID = 2908618315971075004L;

    public ChannelException() {
    }

    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(Throwable cause) {
        super(cause);
    }

    @SuppressJava6Requirement(reason="uses Java 7+ RuntimeException.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
    protected ChannelException(String message, Throwable cause, boolean shared) {
        super(message, cause, false, true);
        assert (shared);
    }

    static ChannelException newStatic(String message, Throwable cause) {
        if (PlatformDependent.javaVersion() >= 7) {
            return new ChannelException(message, cause, true);
        }
        return new ChannelException(message, cause);
    }
}

