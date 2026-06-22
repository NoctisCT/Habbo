/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.blockhound.BlockHound$Builder
 *  reactor.blockhound.integration.BlockHoundIntegration
 */
package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.SuppressJava6Requirement;
import java.util.function.Function;
import java.util.function.Predicate;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

class Hidden {
    Hidden() {
    }

    @SuppressJava6Requirement(reason="BlockHound is Java 8+, but this class is only loaded by it's SPI")
    public static final class NettyBlockHoundIntegration
    implements BlockHoundIntegration {
        public void applyTo(BlockHound.Builder builder) {
            builder.allowBlockingCallsInside("io.netty.channel.nio.NioEventLoop", "handleLoopException");
            builder.allowBlockingCallsInside("io.netty.channel.kqueue.KQueueEventLoop", "handleLoopException");
            builder.allowBlockingCallsInside("io.netty.channel.epoll.EpollEventLoop", "handleLoopException");
            builder.allowBlockingCallsInside("io.netty.util.HashedWheelTimer$Worker", "waitForNextTick");
            builder.allowBlockingCallsInside("io.netty.util.concurrent.SingleThreadEventExecutor", "confirmShutdown");
            builder.allowBlockingCallsInside("io.netty.handler.ssl.SslHandler", "handshake");
            builder.allowBlockingCallsInside("io.netty.handler.ssl.SslHandler", "runAllDelegatedTasks");
            builder.allowBlockingCallsInside("io.netty.util.concurrent.GlobalEventExecutor", "takeTask");
            builder.allowBlockingCallsInside("io.netty.util.concurrent.SingleThreadEventExecutor", "takeTask");
            builder.nonBlockingThreadPredicate((Function)new Function<Predicate<Thread>, Predicate<Thread>>(){

                @Override
                public Predicate<Thread> apply(final Predicate<Thread> p) {
                    return new Predicate<Thread>(){

                        @Override
                        @SuppressJava6Requirement(reason="Predicate#test")
                        public boolean test(Thread thread) {
                            return p.test(thread) || thread instanceof FastThreadLocalThread;
                        }
                    };
                }
            });
        }

        public int compareTo(BlockHoundIntegration o) {
            return 0;
        }
    }
}

