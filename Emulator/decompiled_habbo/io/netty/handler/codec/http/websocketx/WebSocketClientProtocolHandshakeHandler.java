/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

class WebSocketClientProtocolHandshakeHandler
extends ChannelInboundHandlerAdapter {
    private static final long DEFAULT_HANDSHAKE_TIMEOUT_MS = 10000L;
    private final WebSocketClientHandshaker handshaker;
    private final long handshakeTimeoutMillis;
    private ChannelHandlerContext ctx;
    private ChannelPromise handshakePromise;

    WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker handshaker) {
        this(handshaker, 10000L);
    }

    WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker handshaker, long handshakeTimeoutMillis) {
        this.handshaker = handshaker;
        this.handshakeTimeoutMillis = ObjectUtil.checkPositive(handshakeTimeoutMillis, "handshakeTimeoutMillis");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.handshakePromise = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.handshaker.handshake(ctx.channel()).addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    WebSocketClientProtocolHandshakeHandler.this.handshakePromise.tryFailure(future.cause());
                    ctx.fireExceptionCaught(future.cause());
                } else {
                    ctx.fireUserEventTriggered((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED);
                }
            }
        });
        this.applyHandshakeTimeout();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }
        FullHttpResponse response = (FullHttpResponse)msg;
        try {
            if (!this.handshaker.isHandshakeComplete()) {
                this.handshaker.finishHandshake(ctx.channel(), response);
                this.handshakePromise.trySuccess();
                ctx.fireUserEventTriggered((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE);
                ctx.pipeline().remove(this);
                return;
            }
            throw new IllegalStateException("WebSocketClientHandshaker should have been non finished yet");
        }
        finally {
            response.release();
        }
    }

    private void applyHandshakeTimeout() {
        final ChannelPromise localHandshakePromise = this.handshakePromise;
        if (this.handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
            return;
        }
        final ScheduledFuture<?> timeoutFuture = this.ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                if (localHandshakePromise.isDone()) {
                    return;
                }
                if (localHandshakePromise.tryFailure(new WebSocketHandshakeException("handshake timed out"))) {
                    WebSocketClientProtocolHandshakeHandler.this.ctx.flush().fireUserEventTriggered((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_TIMEOUT).close();
                }
            }
        }, this.handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)new FutureListener<Void>(){

            @Override
            public void operationComplete(Future<Void> f) throws Exception {
                timeoutFuture.cancel(false);
            }
        });
    }

    ChannelFuture getHandshakeFuture() {
        return this.handshakePromise;
    }
}

