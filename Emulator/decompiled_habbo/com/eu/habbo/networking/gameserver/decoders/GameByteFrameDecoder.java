/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.gameserver.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class GameByteFrameDecoder
extends LengthFieldBasedFrameDecoder {
    private static final int MAX_PACKET_LENGTH = 417792;
    private static final int LENGTH_FIELD_OFFSET = 0;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_FIELD_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 4;

    public GameByteFrameDecoder() {
        super(417792, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}

