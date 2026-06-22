/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.hotelview;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.hotelview.HallOfFameWinner;
import gnu.trove.map.hash.THashMap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HallOfFame {
    private static final Logger LOGGER = LoggerFactory.getLogger(HallOfFame.class);
    private final THashMap<Integer, HallOfFameWinner> winners = new THashMap();
    private String competitionName;

    public HallOfFame() {
        this.setCompetitionName("xmasRoomComp");
        this.reload();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reload() {
        this.winners.clear();
        THashMap<Integer, HallOfFameWinner> tHashMap = this.winners;
        synchronized (tHashMap) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet set = statement.executeQuery(Emulator.getConfig().getValue("hotelview.halloffame.query"));){
                while (set.next()) {
                    HallOfFameWinner winner = new HallOfFameWinner(set);
                    this.winners.put(winner.getId(), winner);
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    public THashMap<Integer, HallOfFameWinner> getWinners() {
        return this.winners;
    }

    public String getCompetitionName() {
        return this.competitionName;
    }

    void setCompetitionName(String name) {
        this.competitionName = name;
    }
}

