/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.gameclients;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.networking.gameserver.GameServerAttributes;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameClientManager {
    private final ConcurrentMap<ChannelId, GameClient> clients = new ConcurrentHashMap<ChannelId, GameClient>();

    public ConcurrentMap<ChannelId, GameClient> getSessions() {
        return this.clients;
    }

    public boolean addClient(final ChannelHandlerContext ctx) {
        GameClient client = new GameClient(ctx.channel());
        ctx.channel().closeFuture().addListener(new ChannelFutureListener(){
            final /* synthetic */ GameClientManager this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                this.this$0.disposeClient(ctx.channel());
            }
        });
        ctx.channel().attr(GameServerAttributes.CLIENT).set(client);
        ctx.fireChannelRegistered();
        return this.clients.putIfAbsent(ctx.channel().id(), client) == null;
    }

    public void disposeClient(GameClient client) {
        this.disposeClient(client.getChannel());
    }

    private void disposeClient(Channel channel) {
        GameClient client = channel.attr(GameServerAttributes.CLIENT).get();
        if (client != null) {
            client.dispose();
        }
        channel.deregister();
        channel.attr(GameServerAttributes.CLIENT).set(null);
        channel.closeFuture();
        channel.close();
        this.clients.remove(channel.id());
    }

    public boolean containsHabbo(Integer id) {
        if (!this.clients.isEmpty()) {
            for (GameClient client : this.clients.values()) {
                if (client.getHabbo() == null || client.getHabbo().getHabboInfo() == null || client.getHabbo().getHabboInfo().getId() != id.intValue()) continue;
                return true;
            }
        }
        return false;
    }

    public Habbo getHabbo(int id) {
        for (GameClient client : this.clients.values()) {
            if (client.getHabbo() == null || client.getHabbo().getHabboInfo().getId() != id) continue;
            return client.getHabbo();
        }
        return null;
    }

    public Habbo getHabbo(String username) {
        for (GameClient client : this.clients.values()) {
            if (client.getHabbo() == null || !client.getHabbo().getHabboInfo().getUsername().equalsIgnoreCase(username)) continue;
            return client.getHabbo();
        }
        return null;
    }

    public List<Habbo> getHabbosWithIP(String ip) {
        ArrayList<Habbo> habbos = new ArrayList<Habbo>();
        for (GameClient client : this.clients.values()) {
            if (client.getHabbo() == null || client.getHabbo().getHabboInfo() == null || !client.getHabbo().getHabboInfo().getIpLogin().equalsIgnoreCase(ip)) continue;
            habbos.add(client.getHabbo());
        }
        return habbos;
    }

    public List<Habbo> getHabbosWithMachineId(String machineId) {
        ArrayList<Habbo> habbos = new ArrayList<Habbo>();
        for (GameClient client : this.clients.values()) {
            if (client.getHabbo() == null || client.getHabbo().getHabboInfo() == null || !client.getMachineId().equalsIgnoreCase(machineId)) continue;
            habbos.add(client.getHabbo());
        }
        return habbos;
    }

    public void sendBroadcastResponse(MessageComposer composer) {
        this.sendBroadcastResponse(composer.compose());
    }

    public void sendBroadcastResponse(ServerMessage message) {
        for (GameClient client : this.clients.values()) {
            client.sendResponse(message);
        }
    }

    public void sendBroadcastResponse(ServerMessage message, GameClient exclude) {
        for (GameClient client : this.clients.values()) {
            if (client.equals(exclude)) continue;
            client.sendResponse(message);
        }
    }

    public void sendBroadcastResponse(ServerMessage message, String minPermission, GameClient exclude) {
        for (GameClient client : this.clients.values()) {
            if (client.equals(exclude) || client.getHabbo() == null || !client.getHabbo().hasPermission(minPermission)) continue;
            client.sendResponse(message);
        }
    }
}

