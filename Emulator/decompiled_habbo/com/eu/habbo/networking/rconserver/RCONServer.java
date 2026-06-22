/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.rconserver;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.rcon.AlertUser;
import com.eu.habbo.messages.rcon.ChangeRoomOwner;
import com.eu.habbo.messages.rcon.ChangeUsername;
import com.eu.habbo.messages.rcon.CreateModToolTicket;
import com.eu.habbo.messages.rcon.DisconnectUser;
import com.eu.habbo.messages.rcon.ExecuteCommand;
import com.eu.habbo.messages.rcon.ForwardUser;
import com.eu.habbo.messages.rcon.FriendRequest;
import com.eu.habbo.messages.rcon.GiveBadge;
import com.eu.habbo.messages.rcon.GiveCredits;
import com.eu.habbo.messages.rcon.GivePixels;
import com.eu.habbo.messages.rcon.GivePoints;
import com.eu.habbo.messages.rcon.GiveRespect;
import com.eu.habbo.messages.rcon.GiveUserClothing;
import com.eu.habbo.messages.rcon.HotelAlert;
import com.eu.habbo.messages.rcon.IgnoreUser;
import com.eu.habbo.messages.rcon.ImageAlertUser;
import com.eu.habbo.messages.rcon.ImageHotelAlert;
import com.eu.habbo.messages.rcon.ModifyUserSubscription;
import com.eu.habbo.messages.rcon.MuteUser;
import com.eu.habbo.messages.rcon.ProgressAchievement;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.eu.habbo.messages.rcon.SendGift;
import com.eu.habbo.messages.rcon.SendRoomBundle;
import com.eu.habbo.messages.rcon.SetMotto;
import com.eu.habbo.messages.rcon.SetRank;
import com.eu.habbo.messages.rcon.StaffAlert;
import com.eu.habbo.messages.rcon.StalkUser;
import com.eu.habbo.messages.rcon.TalkUser;
import com.eu.habbo.messages.rcon.UpdateCatalog;
import com.eu.habbo.messages.rcon.UpdateUser;
import com.eu.habbo.messages.rcon.UpdateWordfilter;
import com.eu.habbo.networking.Server;
import com.eu.habbo.networking.rconserver.RCONServerHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gnu.trove.map.hash.THashMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RCONServer
extends Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(RCONServer.class);
    private final THashMap<String, Class<? extends RCONMessage>> messages;
    private final GsonBuilder gsonBuilder;
    List<String> allowedAdresses = new ArrayList<String>();

    public RCONServer(String host, int port) throws Exception {
        super("RCON Server", host, port, 1, 2);
        this.messages = new THashMap();
        this.gsonBuilder = new GsonBuilder();
        this.gsonBuilder.registerTypeAdapter((Type)((Object)RCONMessage.class), new RCONMessage.RCONMessageSerializer());
        this.addRCONMessage("alertuser", AlertUser.class);
        this.addRCONMessage("disconnect", DisconnectUser.class);
        this.addRCONMessage("forwarduser", ForwardUser.class);
        this.addRCONMessage("givebadge", GiveBadge.class);
        this.addRCONMessage("givecredits", GiveCredits.class);
        this.addRCONMessage("givepixels", GivePixels.class);
        this.addRCONMessage("givepoints", GivePoints.class);
        this.addRCONMessage("hotelalert", HotelAlert.class);
        this.addRCONMessage("sendgift", SendGift.class);
        this.addRCONMessage("sendroombundle", SendRoomBundle.class);
        this.addRCONMessage("setrank", SetRank.class);
        this.addRCONMessage("updatewordfilter", UpdateWordfilter.class);
        this.addRCONMessage("updatecatalog", UpdateCatalog.class);
        this.addRCONMessage("executecommand", ExecuteCommand.class);
        this.addRCONMessage("progressachievement", ProgressAchievement.class);
        this.addRCONMessage("updateuser", UpdateUser.class);
        this.addRCONMessage("friendrequest", FriendRequest.class);
        this.addRCONMessage("imagehotelalert", ImageHotelAlert.class);
        this.addRCONMessage("imagealertuser", ImageAlertUser.class);
        this.addRCONMessage("stalkuser", StalkUser.class);
        this.addRCONMessage("staffalert", StaffAlert.class);
        this.addRCONMessage("modticket", CreateModToolTicket.class);
        this.addRCONMessage("talkuser", TalkUser.class);
        this.addRCONMessage("changeroomowner", ChangeRoomOwner.class);
        this.addRCONMessage("muteuser", MuteUser.class);
        this.addRCONMessage("giverespect", GiveRespect.class);
        this.addRCONMessage("ignoreuser", IgnoreUser.class);
        this.addRCONMessage("setmotto", SetMotto.class);
        this.addRCONMessage("giveuserclothing", GiveUserClothing.class);
        this.addRCONMessage("modifysubscription", ModifyUserSubscription.class);
        this.addRCONMessage("changeusername", ChangeUsername.class);
        Collections.addAll(this.allowedAdresses, Emulator.getConfig().getValue("rcon.allowed", "127.0.0.1").split(";"));
    }

    @Override
    public void initializePipeline() {
        super.initializePipeline();
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>(){

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RCONServerHandler());
            }
        });
    }

    public void addRCONMessage(String key, Class<? extends RCONMessage> clazz) {
        this.messages.put(key, clazz);
    }

    public String handle(ChannelHandlerContext ctx, String key, String body) throws Exception {
        Class<? extends RCONMessage> message = this.messages.get(key.replace("_", "").toLowerCase());
        if (message != null) {
            try {
                RCONMessage rcon = message.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                Gson gson = this.gsonBuilder.create();
                rcon.handle(gson, gson.fromJson(body, rcon.type));
                LOGGER.info("Handled RCON Message: {}", (Object)message.getSimpleName());
                String result = gson.toJson((Object)rcon, (Type)((Object)RCONMessage.class));
                if (Emulator.debugging) {
                    LOGGER.debug("RCON Data {} RCON Result {}", (Object)body, (Object)result);
                }
                return result;
            }
            catch (Exception ex) {
                LOGGER.error("Failed to handle RCONMessage", ex);
            }
        } else {
            LOGGER.error("Couldn't find: {}", (Object)key);
        }
        throw new ArrayIndexOutOfBoundsException("Unhandled RCON Message");
    }

    public List<String> getCommands() {
        return new ArrayList<String>(this.messages.keySet());
    }
}

