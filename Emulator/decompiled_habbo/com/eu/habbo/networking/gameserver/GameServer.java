/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.gameserver;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClientManager;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.networking.Server;
import com.eu.habbo.networking.gameserver.decoders.GameByteDecoder;
import com.eu.habbo.networking.gameserver.decoders.GameByteFrameDecoder;
import com.eu.habbo.networking.gameserver.decoders.GameClientMessageLogger;
import com.eu.habbo.networking.gameserver.decoders.GameMessageHandler;
import com.eu.habbo.networking.gameserver.decoders.GameMessageRateLimit;
import com.eu.habbo.networking.gameserver.decoders.GamePolicyDecoder;
import com.eu.habbo.networking.gameserver.encoders.GameServerMessageEncoder;
import com.eu.habbo.networking.gameserver.encoders.GameServerMessageLogger;
import com.eu.habbo.networking.gameserver.handlers.IdleTimeoutHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;

public class GameServer
extends Server {
    private final PacketManager packetManager = new PacketManager();
    private final GameClientManager gameClientManager = new GameClientManager();

    public GameServer(String host, int port) throws Exception {
        super("Game Server", host, port, Emulator.getConfig().getInt("io.bossgroup.threads"), Emulator.getConfig().getInt("io.workergroup.threads"));
    }

    @Override
    public void initializePipeline() {
        super.initializePipeline();
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>(){

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("logger", (ChannelHandler)new LoggingHandler());
                ch.pipeline().addLast(new GamePolicyDecoder());
                ch.pipeline().addLast(new GameByteFrameDecoder());
                ch.pipeline().addLast(new GameByteDecoder());
                if (PacketManager.DEBUG_SHOW_PACKETS) {
                    ch.pipeline().addLast(new GameClientMessageLogger());
                }
                ch.pipeline().addLast("idleEventHandler", (ChannelHandler)new IdleTimeoutHandler(30, 60));
                ch.pipeline().addLast(new GameMessageRateLimit());
                ch.pipeline().addLast(new GameMessageHandler());
                ch.pipeline().addLast(new GameServerMessageEncoder());
                if (PacketManager.DEBUG_SHOW_PACKETS) {
                    ch.pipeline().addLast(new GameServerMessageLogger());
                }
            }
        });
    }

    public PacketManager getPacketManager() {
        return this.packetManager;
    }

    public GameClientManager getGameClientManager() {
        return this.gameClientManager;
    }
}

