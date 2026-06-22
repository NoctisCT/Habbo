/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.ScheduledFuture;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.concurrent.TimeUnit;

abstract class WebSocketProtocolHandler
extends MessageToMessageDecoder<WebSocketFrame>
implements ChannelOutboundHandler {
    private final boolean dropPongFrames;
    private final WebSocketCloseStatus closeStatus;
    private final long forceCloseTimeoutMillis;
    private ChannelPromise closeSent;

    WebSocketProtocolHandler() {
        this(true);
    }

    WebSocketProtocolHandler(boolean dropPongFrames) {
        this(dropPongFrames, null, 0L);
    }

    WebSocketProtocolHandler(boolean dropPongFrames, WebSocketCloseStatus closeStatus, long forceCloseTimeoutMillis) {
        this.dropPongFrames = dropPongFrames;
        this.closeStatus = closeStatus;
        this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (frame instanceof PingWebSocketFrame) {
            frame.content().retain();
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
            WebSocketProtocolHandler.readIfNeeded(ctx);
            return;
        }
        if (frame instanceof PongWebSocketFrame && this.dropPongFrames) {
            WebSocketProtocolHandler.readIfNeeded(ctx);
            return;
        }
        out.add(frame.retain());
    }

    private static void readIfNeeded(ChannelHandlerContext ctx) {
        if (!ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
    }

    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        if (this.closeStatus == null || !ctx.channel().isActive()) {
            ctx.close(promise);
        } else {
            if (this.closeSent == null) {
                this.write(ctx, new CloseWebSocketFrame(this.closeStatus), ctx.newPromise());
            }
            this.flush(ctx);
            this.applyCloseSentTimeout(ctx);
            this.closeSent.addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture future) {
                    ctx.close(promise);
                }
            });
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.closeSent != null) {
            ReferenceCountUtil.release(msg);
            promise.setFailure(new ClosedChannelException());
            return;
        }
        if (msg instanceof CloseWebSocketFrame) {
            this.closeSent = promise = promise.unvoid();
        }
        ctx.write(msg, promise);
    }

    private void applyCloseSentTimeout(ChannelHandlerContext ctx) {
        if (this.closeSent.isDone() || this.forceCloseTimeoutMillis < 0L) {
            return;
        }
        final ScheduledFuture<?> timeoutTask = ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                if (!WebSocketProtocolHandler.this.closeSent.isDone()) {
                    WebSocketProtocolHandler.this.closeSent.tryFailure(new WebSocketHandshakeException("send close frame timed out"));
                }
            }
        }, this.forceCloseTimeoutMillis, TimeUnit.MILLISECONDS);
        this.closeSent.addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) {
                timeoutTask.cancel(false);
            }
        });
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
}

