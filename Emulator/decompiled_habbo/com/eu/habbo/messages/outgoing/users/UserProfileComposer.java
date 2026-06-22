/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProfileComposer
extends MessageComposer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileComposer.class);
    private final HabboInfo habboInfo;
    private Habbo habbo;
    private GameClient viewer;

    public UserProfileComposer(HabboInfo habboInfo, GameClient viewer) {
        this.habboInfo = habboInfo;
        this.viewer = viewer;
    }

    public UserProfileComposer(Habbo habbo, GameClient viewer) {
        this.habbo = habbo;
        this.habboInfo = habbo.getHabboInfo();
        this.viewer = viewer;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected ServerMessage composeInternal() {
        if (this.habboInfo == null) {
            return null;
        }
        this.response.init(3898);
        this.response.appendInt(this.habboInfo.getId());
        this.response.appendString(this.habboInfo.getUsername());
        this.response.appendString(this.habboInfo.getLook());
        this.response.appendString(this.habboInfo.getMotto());
        this.response.appendString(new SimpleDateFormat("dd-MM-yyyy").format(new Date((long)this.habboInfo.getAccountCreated() * 1000L)));
        int achievementScore = 0;
        if (this.habbo != null) {
            achievementScore = this.habbo.getHabboStats().getAchievementScore();
        } else {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT achievement_score FROM users_settings WHERE user_id = ? LIMIT 1");){
                statement.setInt(1, this.habboInfo.getId());
                try (ResultSet resultSet = statement.executeQuery();){
                    if (resultSet.next()) {
                        achievementScore = resultSet.getInt("achievement_score");
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
        this.response.appendInt(achievementScore);
        this.response.appendInt(Messenger.getFriendCount(this.habboInfo.getId()));
        this.response.appendBoolean(this.viewer.getHabbo().getMessenger().getFriends().containsKey(this.habboInfo.getId()));
        this.response.appendBoolean(Messenger.friendRequested(this.viewer.getHabbo().getHabboInfo().getId(), this.habboInfo.getId()));
        this.response.appendBoolean(this.habboInfo.isOnline());
        List<Object> guilds = new ArrayList();
        if (this.habbo != null) {
            int i;
            void var4_10;
            ArrayList<Integer> toRemove = new ArrayList<Integer>();
            int n = this.habbo.getHabboStats().guilds.size();
            while (var4_10 > 0) {
                i = this.habbo.getHabboStats().guilds.get((int)(var4_10 - true));
                if (i != 0) {
                    Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(i);
                    if (guild != null) {
                        guilds.add(guild);
                    } else {
                        toRemove.add(i);
                    }
                }
                --var4_10;
            }
            Iterator iterator = toRemove.iterator();
            while (iterator.hasNext()) {
                i = (Integer)iterator.next();
                this.habbo.getHabboStats().removeGuild(i);
            }
        } else {
            guilds = Emulator.getGameEnvironment().getGuildManager().getGuilds(this.habboInfo.getId());
        }
        this.response.appendInt(guilds.size());
        for (Guild guild : guilds) {
            this.response.appendInt(guild.getId());
            this.response.appendString(guild.getName());
            this.response.appendString(guild.getBadge());
            this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getSymbolColor((int)guild.getColorOne()).valueA);
            this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getSymbolColor((int)guild.getColorTwo()).valueA);
            this.response.appendBoolean(this.habbo != null && guild.getId() == this.habbo.getHabboStats().guild);
            this.response.appendInt(guild.getOwnerId());
            this.response.appendBoolean(guild.getOwnerId() == this.habboInfo.getId());
        }
        this.response.appendInt(Emulator.getIntUnixTimestamp() - this.habboInfo.getLastOnline());
        this.response.appendBoolean(true);
        return this.response;
    }

    public HabboInfo getHabboInfo() {
        return this.habboInfo;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public GameClient getViewer() {
        return this.viewer;
    }
}

