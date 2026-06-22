/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;
import com.eu.habbo.messages.outgoing.users.MeMenuSettingsComposer;
import com.eu.habbo.messages.outgoing.users.UpdateUserLookComposer;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateUser
extends RCONMessage<JSON> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUser.class);

    public UpdateUser() {
        super(JSON.class);
    }

    @Override
    public void handle(Gson gson, JSON json) {
        block37: {
            if (json.user_id > 0) {
                Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);
                if (habbo != null) {
                    habbo.getHabboStats().addAchievementScore(json.achievement_score);
                    if (json.block_following != -1) {
                        boolean bl = habbo.getHabboStats().blockFollowing = json.block_following == 1;
                    }
                    if (json.block_friendrequests != -1) {
                        boolean bl = habbo.getHabboStats().blockFriendRequests = json.block_friendrequests == 1;
                    }
                    if (json.block_roominvites != -1) {
                        boolean bl = habbo.getHabboStats().blockRoomInvites = json.block_roominvites == 1;
                    }
                    if (json.old_chat != -1) {
                        boolean bl = habbo.getHabboStats().preferOldChat = json.old_chat == 1;
                    }
                    if (json.block_camera_follow != -1) {
                        boolean bl = habbo.getHabboStats().blockCameraFollow = json.block_camera_follow == 1;
                    }
                    if (!json.look.isEmpty()) {
                        habbo.getHabboInfo().setLook(json.look);
                        if (habbo.getClient() != null) {
                            habbo.getClient().sendResponse(new UpdateUserLookComposer(habbo).compose());
                        }
                        if (habbo.getHabboInfo().getCurrentRoom() != null) {
                            habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDataComposer(habbo).compose());
                        }
                    }
                    habbo.getHabboStats().run();
                    habbo.getClient().sendResponse(new MeMenuSettingsComposer(habbo));
                } else {
                    try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();){
                        try (PreparedStatement statement = connection.prepareStatement("UPDATE users_settings SET achievement_score = achievement_score + ? " + (json.block_following != -1 ? ", block_following = ?" : "") + (json.block_friendrequests != -1 ? ", block_friendrequests = ?" : "") + (json.block_roominvites != -1 ? ", block_roominvites = ?" : "") + (json.old_chat != -1 ? ", old_chat = ?" : "") + (json.block_camera_follow != -1 ? ", block_camera_follow = ?" : "") + " WHERE user_id = ? LIMIT 1");){
                            int index = 1;
                            statement.setInt(index, json.achievement_score);
                            ++index;
                            if (json.block_following != -1) {
                                statement.setString(index, json.block_following == 1 ? "1" : "0");
                                ++index;
                            }
                            if (json.block_friendrequests != -1) {
                                statement.setString(index, json.block_friendrequests == 1 ? "1" : "0");
                                ++index;
                            }
                            if (json.block_roominvites != -1) {
                                statement.setString(index, json.block_roominvites == 1 ? "1" : "0");
                                ++index;
                            }
                            if (json.old_chat != -1) {
                                statement.setString(index, json.old_chat == 1 ? "1" : "0");
                                ++index;
                            }
                            if (json.block_camera_follow != -1) {
                                statement.setString(index, json.block_camera_follow == 1 ? "1" : "0");
                                ++index;
                            }
                            statement.setInt(index, json.user_id);
                            statement.execute();
                        }
                        if (json.look.isEmpty()) break block37;
                        statement = connection.prepareStatement("UPDATE users SET look = ? WHERE id = ? LIMIT 1");
                        try {
                            statement.setString(1, json.look);
                            statement.setInt(2, json.user_id);
                            statement.execute();
                        }
                        finally {
                            if (statement != null) {
                                statement.close();
                            }
                        }
                    }
                    catch (SQLException e) {
                        LOGGER.error("Caught SQL exception", e);
                    }
                }
            }
        }
    }

    static class JSON {
        public int user_id;
        public int achievement_score = 0;
        public int block_following = -1;
        public int block_friendrequests = -1;
        public int block_roominvites = -1;
        public int old_chat = -1;
        public int block_camera_follow = -1;
        public String look = "";
        public boolean strip_unredeemed_clothing = false;

        JSON() {
        }
    }
}

