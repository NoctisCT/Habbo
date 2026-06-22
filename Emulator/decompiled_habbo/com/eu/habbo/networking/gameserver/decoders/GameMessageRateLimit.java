/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.gameserver.decoders;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.networking.gameserver.GameServerAttributes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class GameMessageRateLimit
extends MessageToMessageDecoder<ClientMessage> {
    private static final int RESET_TIME = 1;
    private static final int MAX_COUNTER = 10;

    @Override
    protected void decode(ChannelHandlerContext ctx, ClientMessage message, List<Object> out) throws Exception {
        GameClient client = ctx.channel().attr(GameServerAttributes.CLIENT).get();
        if (client == null) {
            return;
        }
        int count = 0;
        int timestamp = Emulator.getIntUnixTimestamp();
        if ((long)timestamp - client.lastPacketCounterCleared > 1L) {
            client.incomingPacketCounter.clear();
            client.lastPacketCounterCleared = timestamp;
        } else {
            count = client.incomingPacketCounter.getOrDefault(message.getMessageId(), 0);
        }
        if (count > 10) {
            return;
        }
        client.incomingPacketCounter.put(message.getMessageId(), ++count);
        out.add(message);
    }
}

