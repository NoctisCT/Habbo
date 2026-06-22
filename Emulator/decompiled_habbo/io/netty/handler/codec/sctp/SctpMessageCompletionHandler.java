/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import java.util.List;

public class SctpMessageCompletionHandler
extends MessageToMessageDecoder<SctpMessage> {
    private final IntObjectMap<ByteBuf> fragments = new IntObjectHashMap<ByteBuf>();

    @Override
    protected void decode(ChannelHandlerContext ctx, SctpMessage msg, List<Object> out) throws Exception {
        ByteBuf byteBuf = msg.content();
        int protocolIdentifier = msg.protocolIdentifier();
        int streamIdentifier = msg.streamIdentifier();
        boolean isComplete = msg.isComplete();
        boolean isUnordered = msg.isUnordered();
        ByteBuf frag = this.fragments.remove(streamIdentifier);
        if (frag == null) {
            frag = Unpooled.EMPTY_BUFFER;
        }
        if (isComplete && !frag.isReadable()) {
            out.add(msg);
        } else if (!isComplete && frag.isReadable()) {
            this.fragments.put(streamIdentifier, Unpooled.wrappedBuffer(frag, byteBuf));
        } else if (isComplete && frag.isReadable()) {
            SctpMessage assembledMsg = new SctpMessage(protocolIdentifier, streamIdentifier, isUnordered, Unpooled.wrappedBuffer(frag, byteBuf));
            out.add(assembledMsg);
        } else {
            this.fragments.put(streamIdentifier, byteBuf);
        }
        byteBuf.retain();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        for (ByteBuf buffer : this.fragments.values()) {
            buffer.release();
        }
        this.fragments.clear();
        super.handlerRemoved(ctx);
    }
}

