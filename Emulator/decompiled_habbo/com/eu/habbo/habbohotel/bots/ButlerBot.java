/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.plugin.events.bots.BotServerItemEvent;
import com.eu.habbo.threading.runnables.RoomUnitGiveHanditem;
import com.eu.habbo.threading.runnables.RoomUnitWalkToRoomUnit;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButlerBot
extends Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(ButlerBot.class);
    public static THashMap<THashSet<String>, Integer> serveItems = new THashMap();

    public ButlerBot(ResultSet set) throws SQLException {
        super(set);
    }

    public ButlerBot(Bot bot) {
        super(bot);
    }

    public static void initialise() {
        if (serveItems == null) {
            serveItems = new THashMap();
        }
        serveItems.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery("SELECT * FROM bot_serves");){
            while (set.next()) {
                String[] keys = set.getString("keys").split(";");
                THashSet ks = new THashSet();
                Collections.addAll(ks, keys);
                serveItems.put(ks, set.getInt("item"));
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public static void dispose() {
        serveItems.clear();
    }

    @Override
    public void onUserSay(RoomChatMessage message) {
        if (this.getRoomUnit().hasStatus(RoomUnitStatus.MOVE) || this.getRoom() == null) {
            return;
        }
        double distanceBetweenBotAndHabbo = this.getRoomUnit().getCurrentLocation().distance(message.getHabbo().getRoomUnit().getCurrentLocation());
        if (distanceBetweenBotAndHabbo <= (double)Emulator.getConfig().getInt("hotel.bot.butler.commanddistance") && message.getUnfilteredMessage() != null) {
            for (Map.Entry<THashSet<String>, Integer> set : serveItems.entrySet()) {
                for (String keyword : set.getKey()) {
                    if (!message.getUnfilteredMessage().toLowerCase().matches("\\b" + keyword + "\\b")) continue;
                    BotServerItemEvent serveEvent = new BotServerItemEvent(this, message.getHabbo(), set.getValue());
                    if (Emulator.getPluginManager().fireEvent(serveEvent).isCancelled()) {
                        return;
                    }
                    if (this.getRoomUnit().canWalk()) {
                        String key = keyword;
                        ButlerBot bot = this;
                        bot.lookAt(serveEvent.habbo);
                        ArrayList<Runnable> tasks = new ArrayList<Runnable>();
                        tasks.add(new RoomUnitGiveHanditem(serveEvent.habbo.getRoomUnit(), serveEvent.habbo.getHabboInfo().getCurrentRoom(), serveEvent.itemId));
                        tasks.add(new RoomUnitGiveHanditem(this.getRoomUnit(), serveEvent.habbo.getHabboInfo().getCurrentRoom(), 0));
                        tasks.add(() -> {
                            if (this.getRoom() != null) {
                                String botMessage = Emulator.getTexts().getValue("bots.butler.given").replace("%key%", key).replace("%username%", serveEvent.habbo.getHabboInfo().getUsername());
                                if (!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, this.getRoomUnit(), this.getRoom(), new Object[]{botMessage})) {
                                    bot.talk(botMessage);
                                }
                            }
                        });
                        ArrayList<Runnable> failedReached = new ArrayList<Runnable>();
                        failedReached.add(() -> {
                            if (distanceBetweenBotAndHabbo <= (double)Emulator.getConfig().getInt("hotel.bot.butler.servedistance", 8)) {
                                for (Runnable task : tasks) {
                                    task.run();
                                }
                            }
                        });
                        Emulator.getThreading().run(new RoomUnitGiveHanditem(this.getRoomUnit(), serveEvent.habbo.getHabboInfo().getCurrentRoom(), serveEvent.itemId));
                        if (distanceBetweenBotAndHabbo > (double)Emulator.getConfig().getInt("hotel.bot.butler.reachdistance", 3)) {
                            Emulator.getThreading().run(new RoomUnitWalkToRoomUnit(this.getRoomUnit(), serveEvent.habbo.getRoomUnit(), serveEvent.habbo.getHabboInfo().getCurrentRoom(), tasks, failedReached, Emulator.getConfig().getInt("hotel.bot.butler.reachdistance", 3)));
                        } else {
                            Emulator.getThreading().run((Runnable)failedReached.get(0), 1000L);
                        }
                    } else if (this.getRoom() != null) {
                        this.getRoom().giveHandItem(serveEvent.habbo, serveEvent.itemId);
                        String msg = Emulator.getTexts().getValue("bots.butler.given").replace("%key%", keyword).replace("%username%", serveEvent.habbo.getHabboInfo().getUsername());
                        if (!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, this.getRoomUnit(), this.getRoom(), new Object[]{msg})) {
                            this.talk(msg);
                        }
                    }
                    return;
                }
            }
        }
    }
}

