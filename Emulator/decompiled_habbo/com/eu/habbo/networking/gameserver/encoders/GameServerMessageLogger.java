/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.gameserver.encoders;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.PacketNames;
import com.eu.habbo.messages.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerMessageLogger
extends MessageToMessageEncoder<ServerMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerMessageLogger.class);
    private final PacketNames names = Emulator.getGameServer().getPacketManager().getNames();

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerMessage message, List<Object> out) {
        LOGGER.debug(String.format("[\u001b[34mSERVER\u001b[39m][%-4d][%-41s] => %s", message.getHeader(), this.names.getOutgoingName(message.getHeader()), message.getBodyString()));
        out.add(message);
    }
}

