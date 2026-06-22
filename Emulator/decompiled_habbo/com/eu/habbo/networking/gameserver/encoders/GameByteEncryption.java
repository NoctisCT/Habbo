/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.gameserver.encoders;

import com.eu.habbo.networking.gameserver.GameServerAttributes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

public class GameByteEncryption
extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        ByteBuf data = in.readBytes(in.readableBytes());
        ReferenceCountUtil.release(in);
        ctx.channel().attr(GameServerAttributes.CRYPTO_SERVER).get().parse(data.array());
        ctx.write(data, promise);
    }
}

