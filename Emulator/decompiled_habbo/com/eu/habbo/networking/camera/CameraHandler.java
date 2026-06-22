/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.camera;

import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.networking.camera.CameraPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class CameraHandler
extends ChannelInboundHandlerAdapter {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf message = (ByteBuf)msg;
            ((ByteBuf)msg).readerIndex(0);
            int length = message.readInt();
            ByteBuf b = Unpooled.wrappedBuffer(message.readBytes(length));
            short header = b.readShort();
            try {
                CameraPacketHandler.instance().handle(ctx.channel(), header, b);
            }
            catch (Exception exception) {
            }
            finally {
                try {
                    b.release();
                }
                catch (Exception exception) {}
                try {
                    ((ByteBuf)msg).release();
                }
                catch (Exception exception) {}
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        CameraClient.attemptReconnect = true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}

