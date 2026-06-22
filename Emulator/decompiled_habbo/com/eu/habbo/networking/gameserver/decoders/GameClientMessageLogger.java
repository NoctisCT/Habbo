/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.gameserver.decoders;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.PacketNames;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameClientMessageLogger
extends MessageToMessageDecoder<ClientMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameClientMessageLogger.class);
    private final PacketNames names = Emulator.getGameServer().getPacketManager().getNames();

    @Override
    protected void decode(ChannelHandlerContext ctx, ClientMessage message, List<Object> out) {
        LOGGER.debug(String.format("[\u001b[32mCLIENT\u001b[39m][%-4d][%-41s] => %s", message.getMessageId(), this.names.getIncomingName(message.getMessageId()), message.getMessageBody()));
        out.add(message);
    }
}

