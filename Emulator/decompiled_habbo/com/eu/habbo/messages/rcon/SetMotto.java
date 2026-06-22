/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetMotto
extends RCONMessage<SetMottoJSON> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetMotto.class);

    public SetMotto() {
        super(SetMottoJSON.class);
    }

    @Override
    public void handle(Gson gson, SetMottoJSON json) {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);
        if (habbo != null) {
            habbo.getHabboInfo().setMotto(json.motto);
            habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDataComposer(habbo).compose());
        } else {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE users SET motto = ? WHERE id = ? LIMIT 1");){
                statement.setString(1, json.motto);
                statement.setInt(2, json.user_id);
                statement.execute();
            }
            catch (SQLException e) {
                LOGGER.error("Caught exception", e);
            }
        }
    }

    static class SetMottoJSON {
        public int user_id;
        public String motto;

        SetMottoJSON() {
        }
    }
}

