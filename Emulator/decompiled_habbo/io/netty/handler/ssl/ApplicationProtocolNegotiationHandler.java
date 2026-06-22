/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public abstract class ApplicationProtocolNegotiationHandler
extends ChannelInboundHandlerAdapter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ApplicationProtocolNegotiationHandler.class);
    private final String fallbackProtocol;

    protected ApplicationProtocolNegotiationHandler(String fallbackProtocol) {
        this.fallbackProtocol = ObjectUtil.checkNotNull(fallbackProtocol, "fallbackProtocol");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof SslHandshakeCompletionEvent) {
            try {
                SslHandshakeCompletionEvent handshakeEvent = (SslHandshakeCompletionEvent)evt;
                if (handshakeEvent.isSuccess()) {
                    SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
                    if (sslHandler == null) {
                        throw new IllegalStateException("cannot find an SslHandler in the pipeline (required for application-level protocol negotiation)");
                    }
                    String protocol = sslHandler.applicationProtocol();
                    this.configurePipeline(ctx, protocol != null ? protocol : this.fallbackProtocol);
                } else {
                    this.handshakeFailure(ctx, handshakeEvent.cause());
                }
            }
            catch (Throwable cause) {
                this.exceptionCaught(ctx, cause);
            }
            finally {
                ChannelPipeline pipeline = ctx.pipeline();
                if (pipeline.context(this) != null) {
                    pipeline.remove(this);
                }
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    protected abstract void configurePipeline(ChannelHandlerContext var1, String var2) throws Exception;

    protected void handshakeFailure(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("{} TLS handshake failed:", (Object)ctx.channel(), (Object)cause);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("{} Failed to select the application-level protocol:", (Object)ctx.channel(), (Object)cause);
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
}

