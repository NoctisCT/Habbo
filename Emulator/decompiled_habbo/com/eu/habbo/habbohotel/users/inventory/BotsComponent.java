/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotsComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotsComponent.class);
    private final THashMap<Integer, Bot> bots = new THashMap();

    public BotsComponent(Habbo habbo) {
        this.loadBots(habbo);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadBots(Habbo habbo) {
        THashMap<Integer, Bot> tHashMap = this.bots;
        synchronized (tHashMap) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT users.username AS owner_name, bots.* FROM bots INNER JOIN users ON users.id = bots.user_id WHERE user_id = ? AND room_id = 0 ORDER BY id ASC");){
                statement.setInt(1, habbo.getHabboInfo().getId());
                try (ResultSet set = statement.executeQuery();){
                    while (set.next()) {
                        Bot bot = Emulator.getGameEnvironment().getBotManager().loadBot(set);
                        if (bot == null) continue;
                        this.bots.put(set.getInt("id"), bot);
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    public Bot getBot(int botId) {
        return this.bots.get(botId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addBot(Bot bot) {
        THashMap<Integer, Bot> tHashMap = this.bots;
        synchronized (tHashMap) {
            this.bots.put(bot.getId(), bot);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeBot(Bot bot) {
        THashMap<Integer, Bot> tHashMap = this.bots;
        synchronized (tHashMap) {
            this.bots.remove(bot.getId());
        }
    }

    public THashMap<Integer, Bot> getBots() {
        return this.bots;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        THashMap<Integer, Bot> tHashMap = this.bots;
        synchronized (tHashMap) {
            for (Map.Entry<Integer, Bot> map : this.bots.entrySet()) {
                if (!map.getValue().needsUpdate()) continue;
                Emulator.getThreading().run(map.getValue());
            }
            this.bots.clear();
        }
    }
}

