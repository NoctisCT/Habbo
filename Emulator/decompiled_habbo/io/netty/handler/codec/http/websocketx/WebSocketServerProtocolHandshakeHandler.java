/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

class WebSocketServerProtocolHandshakeHandler
extends ChannelInboundHandlerAdapter {
    private final WebSocketServerProtocolConfig serverConfig;
    private ChannelHandlerContext ctx;
    private ChannelPromise handshakePromise;

    WebSocketServerProtocolHandshakeHandler(WebSocketServerProtocolConfig serverConfig) {
        this.serverConfig = ObjectUtil.checkNotNull(serverConfig, "serverConfig");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.handshakePromise = ctx.newPromise();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        final FullHttpRequest req = (FullHttpRequest)msg;
        if (this.isNotWebSocketPath(req)) {
            ctx.fireChannelRead(msg);
            return;
        }
        try {
            if (!HttpMethod.GET.equals(req.method())) {
                WebSocketServerProtocolHandshakeHandler.sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, ctx.alloc().buffer(0)));
                return;
            }
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WebSocketServerProtocolHandshakeHandler.getWebSocketLocation(ctx.pipeline(), req, this.serverConfig.websocketPath()), this.serverConfig.subprotocols(), this.serverConfig.decoderConfig());
            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            final ChannelPromise localHandshakePromise = this.handshakePromise;
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                WebSocketServerProtocolHandler.setHandshaker(ctx.channel(), handshaker);
                ctx.pipeline().remove(this);
                ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                handshakeFuture.addListener(new ChannelFutureListener(){

                    @Override
                    public void operationComplete(ChannelFuture future) {
                        if (!future.isSuccess()) {
                            localHandshakePromise.tryFailure(future.cause());
                            ctx.fireExceptionCaught(future.cause());
                        } else {
                            localHandshakePromise.trySuccess();
                            ctx.fireUserEventTriggered((Object)WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                            ctx.fireUserEventTriggered(new WebSocketServerProtocolHandler.HandshakeComplete(req.uri(), req.headers(), handshaker.selectedSubprotocol()));
                        }
                    }
                });
                this.applyHandshakeTimeout();
            }
        }
        finally {
            req.release();
        }
    }

    private boolean isNotWebSocketPath(FullHttpRequest req) {
        String websocketPath = this.serverConfig.websocketPath();
        return this.serverConfig.checkStartsWith() ? !req.uri().startsWith(websocketPath) : !req.uri().equals(websocketPath);
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            protocol = "wss";
        }
        String host = req.headers().get(HttpHeaderNames.HOST);
        return protocol + "://" + host + path;
    }

    private void applyHandshakeTimeout() {
        final ChannelPromise localHandshakePromise = this.handshakePromise;
        long handshakeTimeoutMillis = this.serverConfig.handshakeTimeoutMillis();
        if (handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
            return;
        }
        final ScheduledFuture<?> timeoutFuture = this.ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                if (!localHandshakePromise.isDone() && localHandshakePromise.tryFailure(new WebSocketHandshakeException("handshake timed out"))) {
                    WebSocketServerProtocolHandshakeHandler.this.ctx.flush().fireUserEventTriggered((Object)WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_TIMEOUT).close();
                }
            }
        }, handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)new FutureListener<Void>(){

            @Override
            public void operationComplete(Future<Void> f) {
                timeoutFuture.cancel(false);
            }
        });
    }
}

