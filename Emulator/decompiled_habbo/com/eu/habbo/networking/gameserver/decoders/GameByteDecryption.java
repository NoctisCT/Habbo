/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.gameserver.decoders;

import com.eu.habbo.networking.gameserver.GameServerAttributes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class GameByteDecryption
extends ByteToMessageDecoder {
    public GameByteDecryption() {
        this.setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        ByteBuf data = in.readBytes(in.readableBytes());
        ctx.channel().attr(GameServerAttributes.CRYPTO_CLIENT).get().parse(data.array());
        out.add(data);
    }
}

