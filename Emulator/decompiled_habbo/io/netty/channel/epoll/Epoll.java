/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.epoll.Native;
import io.netty.channel.unix.FileDescriptor;
import io.netty.util.internal.SystemPropertyUtil;

public final class Epoll {
    private static final Throwable UNAVAILABILITY_CAUSE;

    public static boolean isAvailable() {
        return UNAVAILABILITY_CAUSE == null;
    }

    public static void ensureAvailability() {
        if (UNAVAILABILITY_CAUSE != null) {
            throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(UNAVAILABILITY_CAUSE);
        }
    }

    public static Throwable unavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    private Epoll() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        Throwable cause = null;
        if (SystemPropertyUtil.getBoolean("io.netty.transport.noNative", false)) {
            cause = new UnsupportedOperationException("Native transport was explicit disabled with -Dio.netty.transport.noNative=true");
        } else {
            FileDescriptor epollFd = null;
            FileDescriptor eventFd = null;
            try {
                epollFd = Native.newEpollCreate();
                eventFd = Native.newEventFd();
            }
            catch (Throwable t) {
                cause = t;
            }
            finally {
                if (epollFd != null) {
                    try {
                        epollFd.close();
                    }
                    catch (Exception exception) {}
                }
                if (eventFd != null) {
                    try {
                        eventFd.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
        UNAVAILABILITY_CAUSE = cause;
    }
}

