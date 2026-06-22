/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendRequestComposer;
import com.eu.habbo.messages.outgoing.friends.FriendRequestErrorComposer;
import com.eu.habbo.plugin.events.users.friends.UserRequestFriendshipEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendRequestEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendRequestEvent.class);

    @Override
    public void handle() throws Exception {
        String username = this.packet.readString();
        if (this.client == null || username == null || username.isEmpty()) {
            return;
        }
        Habbo targetHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(username);
        if (targetHabbo == null) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT users.*, users_settings.block_friendrequests FROM users INNER JOIN users_settings ON users.id = users_settings.user_id WHERE username = ? LIMIT 1");){
                statement.setString(1, username);
                try (ResultSet set = statement.executeQuery();){
                    while (set.next()) {
                        targetHabbo = new Habbo(set);
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
                return;
            }
        }
        if (targetHabbo == null) {
            this.client.sendResponse(new FriendRequestErrorComposer(4));
            return;
        }
        int targetId = targetHabbo.getHabboInfo().getId();
        boolean targetBlocksFriendRequests = targetHabbo.getHabboStats().blockFriendRequests;
        if (targetId == this.client.getHabbo().getHabboInfo().getId()) {
            return;
        }
        if (targetBlocksFriendRequests) {
            this.client.sendResponse(new FriendRequestErrorComposer(3));
            return;
        }
        if (this.client.getHabbo().getMessenger().getFriends().values().size() >= this.client.getHabbo().getHabboStats().maxFriends && !this.client.getHabbo().hasPermission("acc_infinite_friends")) {
            this.client.sendResponse(new FriendRequestErrorComposer(1));
            return;
        }
        if (targetHabbo.getMessenger().getFriends().values().size() >= targetHabbo.getHabboStats().maxFriends && !targetHabbo.hasPermission("acc_infinite_friends")) {
            this.client.sendResponse(new FriendRequestErrorComposer(2));
            return;
        }
        if (Emulator.getPluginManager().fireEvent(new UserRequestFriendshipEvent(this.client.getHabbo(), username, targetHabbo)).isCancelled()) {
            this.client.sendResponse(new FriendRequestErrorComposer(2));
            return;
        }
        if (targetHabbo.isOnline()) {
            targetHabbo.getClient().sendResponse(new FriendRequestComposer(this.client.getHabbo()));
        }
        Messenger.makeFriendRequest(this.client.getHabbo().getHabboInfo().getId(), targetId);
    }
}

