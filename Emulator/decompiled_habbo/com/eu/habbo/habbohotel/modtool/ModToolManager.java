/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.CfhCategory;
import com.eu.habbo.habbohotel.modtool.CfhTopic;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.modtool.ModToolCategory;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolPreset;
import com.eu.habbo.habbohotel.modtool.ModToolRoomVisit;
import com.eu.habbo.habbohotel.modtool.ModToolTicketState;
import com.eu.habbo.habbohotel.modtool.ModToolTicketType;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomState;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueHandledComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueInfoComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolUserInfoComposer;
import com.eu.habbo.plugin.events.support.SupportRoomActionEvent;
import com.eu.habbo.plugin.events.support.SupportTicketEvent;
import com.eu.habbo.plugin.events.support.SupportTicketStatusChangedEvent;
import com.eu.habbo.plugin.events.support.SupportUserAlertedEvent;
import com.eu.habbo.plugin.events.support.SupportUserAlertedReason;
import com.eu.habbo.plugin.events.support.SupportUserBannedEvent;
import com.eu.habbo.threading.runnables.InsertModToolIssue;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModToolManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModToolManager.class);
    private final TIntObjectMap<ModToolCategory> category;
    private final THashMap<String, THashSet<String>> presets;
    private final THashMap<Integer, ModToolIssue> tickets;
    private final TIntObjectMap<CfhCategory> cfhCategories;

    public ModToolManager() {
        long millis = System.currentTimeMillis();
        this.category = TCollections.synchronizedMap(new TIntObjectHashMap());
        this.presets = new THashMap();
        this.tickets = new THashMap();
        this.cfhCategories = new TIntObjectHashMap<CfhCategory>();
        this.loadModTool();
        LOGGER.info("ModTool Manager -> Loaded! ({} MS)", (Object)(System.currentTimeMillis() - millis));
    }

    public static void requestUserInfo(GameClient client, ClientMessage packet) {
        int userId = packet.readInt();
        if (userId <= 0) {
            return;
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT users.*, users_settings.*, permissions.rank_name, permissions.acc_hide_mail AS hide_mail, permissions.id AS rank_id FROM users INNER JOIN users_settings ON users.id = users_settings.user_id INNER JOIN permissions ON permissions.id = users.rank WHERE users.id = ? LIMIT 1");){
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    client.sendResponse(new ModToolUserInfoComposer(set));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    public synchronized void loadModTool() {
        this.category.clear();
        this.presets.clear();
        this.cfhCategories.clear();
        this.presets.put("user", new THashSet());
        this.presets.put("room", new THashSet());
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();){
            this.loadCategory(connection);
            this.loadPresets(connection);
            this.loadTickets(connection);
            this.loadCfhCategories(connection);
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void loadCategory(Connection connection) {
        try (Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery("SELECT * FROM support_issue_categories");){
            block22: while (set.next()) {
                this.category.put(set.getInt("id"), new ModToolCategory(set.getString("name")));
                PreparedStatement settings = connection.prepareStatement("SELECT * FROM support_issue_presets WHERE category = ?");
                try {
                    settings.setInt(1, set.getInt("id"));
                    ResultSet presets = settings.executeQuery();
                    try {
                        while (true) {
                            if (!presets.next()) continue block22;
                            this.category.get(set.getInt("id")).addPreset(new ModToolPreset(presets));
                        }
                    }
                    finally {
                        if (presets == null) continue;
                        presets.close();
                    }
                }
                finally {
                    if (settings == null) continue;
                    settings.close();
                }
            }
            return;
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadPresets(Connection connection) {
        THashMap<String, THashSet<String>> tHashMap = this.presets;
        synchronized (tHashMap) {
            try (Statement statement = connection.createStatement();
                 ResultSet set = statement.executeQuery("SELECT * FROM support_presets");){
                while (set.next()) {
                    this.presets.get(set.getString("type")).add(set.getString("preset"));
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadTickets(Connection connection) {
        THashMap<Integer, ModToolIssue> tHashMap = this.tickets;
        synchronized (tHashMap) {
            try (Statement statement = connection.createStatement();
                 ResultSet set = statement.executeQuery("SELECT S.username as sender_username, R.username AS reported_username, M.username as mod_username, support_tickets.* FROM support_tickets INNER JOIN users as S ON S.id = sender_id INNER JOIN users AS R ON R.id = reported_id INNER JOIN users AS M ON M.id = mod_id WHERE state != 0");){
                while (set.next()) {
                    this.tickets.put(set.getInt("id"), new ModToolIssue(set));
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    private void loadCfhCategories(Connection connection) {
        try (Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery("SELECT support_cfh_topics.id, support_cfh_topics.category_id, support_cfh_topics.name_internal, support_cfh_topics.action, support_cfh_topics.auto_reply,support_cfh_topics.ignore_target, support_cfh_categories.name_internal AS category_name_internal, support_cfh_categories.id AS support_cfh_category_id, support_cfh_topics.default_sanction AS default_sanction FROM support_cfh_topics LEFT JOIN support_cfh_categories ON support_cfh_categories.id = support_cfh_topics.category_id");){
            while (set.next()) {
                if (!this.cfhCategories.containsKey(set.getInt("support_cfh_category_id"))) {
                    this.cfhCategories.put(set.getInt("support_cfh_category_id"), new CfhCategory(set.getInt("id"), set.getString("category_name_internal")));
                }
                this.cfhCategories.get(set.getInt("support_cfh_category_id")).addTopic(new CfhTopic(set, this.getIssuePreset(set.getInt("default_sanction"))));
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public CfhTopic getCfhTopic(int topicId) {
        for (CfhCategory category : this.getCfhCategories().valueCollection()) {
            for (CfhTopic topic : category.getTopics().valueCollection()) {
                if (topic.id != topicId) continue;
                return topic;
            }
        }
        return null;
    }

    public ModToolPreset getIssuePreset(final int id) {
        if (id == 0) {
            return null;
        }
        final ModToolPreset[] preset = new ModToolPreset[]{null};
        this.category.forEachValue(new TObjectProcedure<ModToolCategory>(){
            final /* synthetic */ ModToolManager this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public boolean execute(ModToolCategory object) {
                preset[0] = object.getPresets().get(id);
                return preset[0] == null;
            }
        });
        return preset[0];
    }

    public void quickTicket(Habbo reported, String reason, String message) {
        ModToolIssue issue = new ModToolIssue(0, reason, reported.getHabboInfo().getId(), reported.getHabboInfo().getUsername(), 0, message, ModToolTicketType.AUTOMATIC);
        Emulator.getGameEnvironment().getModToolManager().addTicket(issue);
        Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
    }

    public ArrayList<ModToolChatLog> getRoomChatlog(int roomId) {
        ArrayList<ModToolChatLog> chatlogs = new ArrayList<ModToolChatLog>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT users.username, users.id, chatlogs_room.* FROM chatlogs_room INNER JOIN users ON users.id = chatlogs_room.user_from_id WHERE room_id = ? ORDER BY timestamp DESC LIMIT 150");){
            statement.setInt(1, roomId);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    chatlogs.add(new ModToolChatLog(set.getInt("timestamp"), set.getInt("id"), set.getString("username"), set.getString("message")));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return chatlogs;
    }

    public ArrayList<ModToolChatLog> getUserChatlog(int userId) {
        ArrayList<ModToolChatLog> chatlogs = new ArrayList<ModToolChatLog>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT users.username, users.id, chatlogs_room.* FROM chatlogs_room INNER JOIN users ON users.id = chatlogs_room.user_from_id WHERE user_from_id = ? ORDER BY chatlogs_room.timestamp DESC LIMIT 150");){
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    chatlogs.add(new ModToolChatLog(set.getInt("timestamp"), set.getInt("id"), set.getString("username"), set.getString("message")));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return chatlogs;
    }

    public ArrayList<ModToolChatLog> getMessengerChatlog(int userOneId, int userTwoId) {
        ArrayList<ModToolChatLog> chatLogs = new ArrayList<ModToolChatLog>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT users.username, chatlogs_private.* FROM chatlogs_private INNER JOIN users ON users.id = user_from_id WHERE (user_from_id = ? AND user_to_id = ?) OR (user_from_id = ? AND user_to_id = ?) ORDER BY chatlogs_private.timestamp DESC LIMIT 50");){
            statement.setInt(1, userOneId);
            statement.setInt(2, userTwoId);
            statement.setInt(3, userTwoId);
            statement.setInt(4, userOneId);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    chatLogs.add(new ModToolChatLog(set.getInt("timestamp"), set.getInt("chatlogs_private.user_from_id"), set.getString("users.username"), set.getString("message")));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return chatLogs;
    }

    public ArrayList<ModToolRoomVisit> getUserRoomVisitsAndChatlogs(int userId) {
        ArrayList<ModToolRoomVisit> chatlogs = new ArrayList<ModToolRoomVisit>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT rooms.name, users.username, room_enter_log.timestamp AS enter_timestamp, room_enter_log.exit_timestamp, chatlogs_room.* FROM room_enter_log INNER JOIN rooms ON room_enter_log.room_id = rooms.id INNER JOIN users ON room_enter_log.user_id = users.id LEFT JOIN chatlogs_room ON room_enter_log.user_id = chatlogs_room.user_from_id AND room_enter_log.room_id = chatlogs_room.room_id AND chatlogs_room.timestamp >= room_enter_log.timestamp AND chatlogs_room.timestamp < room_enter_log.exit_timestamp WHERE chatlogs_room.user_from_id = ? ORDER BY room_enter_log.timestamp DESC LIMIT 500");){
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery();){
                int userid = 0;
                String username = "unknown";
                while (set.next()) {
                    ModToolRoomVisit visit = null;
                    for (ModToolRoomVisit v : chatlogs) {
                        if (v.timestamp != set.getInt("enter_timestamp") || v.exitTimestamp != set.getInt("exit_timestamp")) continue;
                        visit = v;
                    }
                    if (visit == null) {
                        visit = new ModToolRoomVisit(set.getInt("room_id"), set.getString("name"), set.getInt("enter_timestamp"), set.getInt("exit_timestamp"));
                        chatlogs.add(visit);
                    }
                    visit.chat.add(new ModToolChatLog(set.getInt("timestamp"), set.getInt("user_from_id"), set.getString("username"), set.getString("message")));
                    if (userid == 0) {
                        userid = set.getInt("user_from_id");
                    }
                    if (!username.equals("unknown")) continue;
                    username = set.getString("username");
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return chatlogs;
    }

    public THashSet<ModToolRoomVisit> getUserRoomVisits(int userId) {
        THashSet<ModToolRoomVisit> roomVisits = new THashSet<ModToolRoomVisit>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT rooms.name, room_enter_log.* FROM room_enter_log INNER JOIN rooms ON rooms.id = room_enter_log.room_id WHERE user_id = ? ORDER BY timestamp DESC LIMIT 50");){
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    roomVisits.add(new ModToolRoomVisit(set));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return roomVisits;
    }

    public THashSet<ModToolRoomVisit> getVisitsForRoom(Room room, int amount, boolean groupUser, int fromTimestamp, int toTimestamp) {
        return this.getVisitsForRoom(room, amount, groupUser, fromTimestamp, toTimestamp, "");
    }

    public THashSet<ModToolRoomVisit> getVisitsForRoom(Room room, int amount, boolean groupUser, int fromTimestamp, int toTimestamp, String excludeUsername) {
        THashSet<ModToolRoomVisit> roomVisits = new THashSet<ModToolRoomVisit>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM (SELECT users.username as name, room_enter_log.* FROM`room_enter_log` INNER JOIN users ON users.id = room_enter_log.user_id WHERE room_enter_log.room_id = ? " + (fromTimestamp > 0 ? "AND timestamp >= ? " : "") + (toTimestamp > 0 ? "AND exit_timestamp <= ? " : "") + "AND users.username != ? ORDER BY timestamp DESC LIMIT ?) x " + (groupUser ? "GROUP BY user_id" : "") + ";");){
            statement.setInt(1, room.getId());
            if (fromTimestamp > 0) {
                statement.setInt(2, fromTimestamp);
            }
            if (toTimestamp > 0) {
                statement.setInt(fromTimestamp > 0 ? 3 : 2, toTimestamp);
            }
            statement.setString(toTimestamp > 0 ? (fromTimestamp > 0 ? 4 : 3) : 2, excludeUsername);
            int columnAmount = 3;
            if (fromTimestamp > 0) {
                ++columnAmount;
            }
            if (toTimestamp > 0) {
                ++columnAmount;
            }
            statement.setInt(columnAmount, amount);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    roomVisits.add(new ModToolRoomVisit(set));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return roomVisits;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public ModToolBan createOfflineUserBan(int userId, int staffId, int duration, String reason, ModToolBanType type) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO bans (user_id, ip, machine_id, user_staff_id, ban_expire, ban_reason, type) VALUES (?, (SELECT ip_current FROM users WHERE id = ?), (SELECT machine_id FROM users WHERE id = ?), ?, ?, ?, ?)", 1);){
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            statement.setInt(3, userId);
            statement.setInt(4, staffId);
            statement.setInt(5, Emulator.getIntUnixTimestamp() + duration);
            statement.setString(6, reason);
            statement.setString(7, type.getType());
            try (ResultSet set = statement.executeQuery();){
                if (!set.next()) return null;
                try (PreparedStatement selectBanStatement = connection.prepareStatement("SELECT * FROM bans WHERE id = ? LIMIT 1");){
                    selectBanStatement.setInt(1, set.getInt(1));
                    try (ResultSet selectSet = selectBanStatement.executeQuery();){
                        if (!selectSet.next()) return null;
                        ModToolBan modToolBan = new ModToolBan(selectSet);
                        return modToolBan;
                    }
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }

    public void alert(Habbo moderator, Habbo target, String message) {
        this.alert(moderator, target, message, SupportUserAlertedReason.ALERT);
    }

    public void alert(Habbo moderator, Habbo target, String message, SupportUserAlertedReason reason) {
        if (!moderator.hasPermission(Permission.ACC_SUPPORTTOOL)) {
            ScripterManager.scripterDetected(moderator.getClient(), Emulator.getTexts().getValue("scripter.warning.modtools.alert").replace("%username%", moderator.getHabboInfo().getUsername()).replace("%message%", message));
            return;
        }
        SupportUserAlertedEvent alertedEvent = new SupportUserAlertedEvent(moderator, target, message, reason);
        if (Emulator.getPluginManager().fireEvent(alertedEvent).isCancelled()) {
            return;
        }
        if (target != null) {
            alertedEvent.target.getClient().sendResponse(new ModToolIssueHandledComposer(alertedEvent.message));
        }
    }

    public void kick(Habbo moderator, Habbo target, String message) {
        if (moderator.hasPermission(Permission.ACC_SUPPORTTOOL) && !target.hasPermission(Permission.ACC_UNKICKABLE)) {
            if (target.getHabboInfo().getCurrentRoom() != null) {
                Emulator.getGameEnvironment().getRoomManager().leaveRoom(target, target.getHabboInfo().getCurrentRoom());
            }
            this.alert(moderator, target, message, SupportUserAlertedReason.KICKED);
        }
    }

    public List<ModToolBan> ban(int targetUserId, Habbo moderator, String reason, int duration, ModToolBanType type, int cfhTopic) {
        HabboInfo offlineInfo;
        if (moderator == null) {
            return null;
        }
        ArrayList<ModToolBan> bans = new ArrayList<ModToolBan>();
        Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUserId);
        HabboInfo habboInfo = offlineInfo = target != null ? target.getHabboInfo() : HabboManager.getOfflineHabboInfo(targetUserId);
        if (moderator.getHabboInfo().getRank().getId() < offlineInfo.getRank().getId()) {
            return bans;
        }
        if (!(type != ModToolBanType.MACHINE && type != ModToolBanType.SUPER || offlineInfo != null && !offlineInfo.getMachineID().isEmpty())) {
            type = ModToolBanType.IP;
        }
        if (!(type != ModToolBanType.IP && type != ModToolBanType.SUPER || offlineInfo != null && !offlineInfo.getIpLogin().isEmpty())) {
            type = ModToolBanType.ACCOUNT;
        }
        ModToolBan ban = new ModToolBan(targetUserId, offlineInfo != null ? offlineInfo.getIpLogin() : "offline", offlineInfo != null ? offlineInfo.getMachineID() : "offline", moderator.getHabboInfo().getId(), Emulator.getIntUnixTimestamp() + duration, reason, type, cfhTopic);
        Emulator.getPluginManager().fireEvent(new SupportUserBannedEvent(moderator, target, ban));
        Emulator.getThreading().run(ban);
        bans.add(ban);
        if (target != null) {
            Emulator.getGameServer().getGameClientManager().disposeClient(target.getClient());
        }
        if (!(type != ModToolBanType.IP && type != ModToolBanType.SUPER || target == null || ban.ip.equals("offline"))) {
            for (Habbo h : Emulator.getGameServer().getGameClientManager().getHabbosWithIP(ban.ip)) {
                if (h.getHabboInfo().getRank().getId() >= moderator.getHabboInfo().getRank().getId()) continue;
                ban = new ModToolBan(h.getHabboInfo().getId(), h != null ? h.getHabboInfo().getIpLogin() : "offline", h != null ? h.getClient().getMachineId() : "offline", moderator.getHabboInfo().getId(), Emulator.getIntUnixTimestamp() + duration, reason, type, cfhTopic);
                Emulator.getPluginManager().fireEvent(new SupportUserBannedEvent(moderator, h, ban));
                Emulator.getThreading().run(ban);
                bans.add(ban);
                Emulator.getGameServer().getGameClientManager().disposeClient(h.getClient());
            }
        }
        if (!(type != ModToolBanType.MACHINE && type != ModToolBanType.SUPER || target == null || ban.machineId.equals("offline"))) {
            for (Habbo h : Emulator.getGameServer().getGameClientManager().getHabbosWithMachineId(ban.machineId)) {
                if (h.getHabboInfo().getRank().getId() >= moderator.getHabboInfo().getRank().getId()) continue;
                ban = new ModToolBan(h.getHabboInfo().getId(), h != null ? h.getHabboInfo().getIpLogin() : "offline", h != null ? h.getClient().getMachineId() : "offline", moderator.getHabboInfo().getId(), Emulator.getIntUnixTimestamp() + duration, reason, type, cfhTopic);
                Emulator.getPluginManager().fireEvent(new SupportUserBannedEvent(moderator, h, ban));
                Emulator.getThreading().run(ban);
                bans.add(ban);
                Emulator.getGameServer().getGameClientManager().disposeClient(h.getClient());
            }
        }
        return bans;
    }

    public void roomAction(Room room, Habbo moderator, boolean kickUsers, boolean lockDoor, boolean changeTitle) {
        SupportRoomActionEvent roomActionEvent = new SupportRoomActionEvent(moderator, room, kickUsers, lockDoor, changeTitle);
        Emulator.getPluginManager().fireEvent(roomActionEvent);
        if (roomActionEvent.changeTitle) {
            room.setName(Emulator.getTexts().getValue("hotel.room.inappropriate.title"));
            room.setNeedsUpdate(true);
        }
        if (roomActionEvent.lockDoor) {
            room.setState(RoomState.LOCKED);
            room.setNeedsUpdate(true);
        }
        if (roomActionEvent.kickUsers) {
            for (Habbo habbo : room.getHabbos()) {
                if (habbo.hasPermission(Permission.ACC_UNKICKABLE) || habbo.hasPermission(Permission.ACC_SUPPORTTOOL) || room.isOwner(habbo)) continue;
                room.kickHabbo(habbo, false);
            }
        }
    }

    public ModToolBan checkForBan(int userId) {
        ModToolBan ban = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bans WHERE user_id = ? AND ban_expire >= ? AND (type = 'account' OR type = 'super') ORDER BY timestamp LIMIT 1");){
            statement.setInt(1, userId);
            statement.setInt(2, Emulator.getIntUnixTimestamp());
            try (ResultSet set = statement.executeQuery();){
                if (set.next()) {
                    ban = new ModToolBan(set);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return ban;
    }

    @Deprecated
    public boolean hasIPBan(Channel habbo) {
        if (habbo == null) {
            return false;
        }
        if (habbo.remoteAddress() == null || ((InetSocketAddress)habbo.remoteAddress()).getAddress() == null) {
            return false;
        }
        return this.hasIPBan(((InetSocketAddress)habbo.remoteAddress()).getAddress().getHostAddress());
    }

    public boolean hasIPBan(String ipAddress) {
        boolean banned = false;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bans WHERE ip = ? AND (type = 'ip' OR type = 'super')  AND ban_expire > ? LIMIT 1");){
            statement.setString(1, ipAddress);
            statement.setInt(2, Emulator.getIntUnixTimestamp());
            try (ResultSet set = statement.executeQuery();){
                if (set.next()) {
                    banned = true;
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return banned;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean hasMACBan(GameClient habbo) {
        if (habbo == null) {
            return false;
        }
        if (habbo.getMachineId().isEmpty()) {
            return false;
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bans WHERE machine_id = ? AND (type = 'machine' OR type = 'super') AND ban_expire > ? LIMIT 1");){
            statement.setString(1, habbo.getMachineId());
            statement.setInt(2, Emulator.getIntUnixTimestamp());
            try (ResultSet set = statement.executeQuery();){
                if (!set.next()) return false;
                boolean bl = true;
                return bl;
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return false;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public boolean unban(String username) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();){
            boolean bl;
            block14: {
                PreparedStatement statement = connection.prepareStatement("UPDATE bans INNER JOIN users ON bans.user_id = users.id SET ban_expire = ?, ban_reason = CONCAT('" + Emulator.getTexts().getValue("unbanned") + ": ', ban_reason) WHERE users.username LIKE ? AND ban_expire > ?");
                try {
                    statement.setInt(1, Emulator.getIntUnixTimestamp());
                    statement.setString(2, username);
                    statement.setInt(3, Emulator.getIntUnixTimestamp());
                    statement.execute();
                    boolean bl2 = bl = statement.getUpdateCount() > 0;
                    if (statement == null) break block14;
                }
                catch (Throwable throwable) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                statement.close();
            }
            return bl;
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return false;
        }
    }

    public void pickTicket(ModToolIssue issue, Habbo habbo) {
        issue.modId = habbo.getHabboInfo().getId();
        issue.modName = habbo.getHabboInfo().getUsername();
        issue.state = ModToolTicketState.PICKED;
        this.updateTicketToMods(issue);
        issue.updateInDatabase();
    }

    public void updateTicketToMods(ModToolIssue issue) {
        SupportTicketStatusChangedEvent event = new SupportTicketStatusChangedEvent(null, issue);
        if (!Emulator.getPluginManager().fireEvent(event).isCancelled()) {
            Emulator.getGameEnvironment().getHabboManager().sendPacketToHabbosWithPermission(new ModToolIssueInfoComposer(issue).compose(), Permission.ACC_SUPPORTTOOL);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addTicket(ModToolIssue issue) {
        if (Emulator.getPluginManager().fireEvent(new SupportTicketEvent(null, issue)).isCancelled()) {
            return;
        }
        if (issue.id == 0) {
            new InsertModToolIssue(issue).run();
        }
        THashMap<Integer, ModToolIssue> tHashMap = this.tickets;
        synchronized (tHashMap) {
            this.tickets.put(issue.id, issue);
        }
    }

    public void removeTicket(ModToolIssue issue) {
        this.removeTicket(issue.id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeTicket(int issueId) {
        THashMap<Integer, ModToolIssue> tHashMap = this.tickets;
        synchronized (tHashMap) {
            this.tickets.remove(issueId);
        }
    }

    public void closeTicketAsUseless(ModToolIssue issue, Habbo sender) {
        issue.state = ModToolTicketState.CLOSED;
        issue.updateInDatabase();
        if (sender != null) {
            sender.getClient().sendResponse(new ModToolIssueHandledComposer(1));
        }
        this.updateTicketToMods(issue);
        this.removeTicket(issue);
    }

    public void closeTicketAsAbusive(ModToolIssue issue, Habbo sender) {
        issue.state = ModToolTicketState.CLOSED;
        issue.updateInDatabase();
        if (sender != null) {
            sender.getClient().sendResponse(new ModToolIssueHandledComposer(2));
        }
        this.updateTicketToMods(issue);
        this.removeTicket(issue);
    }

    public void closeTicketAsHandled(ModToolIssue issue, Habbo sender) {
        issue.state = ModToolTicketState.CLOSED;
        issue.updateInDatabase();
        if (sender != null) {
            sender.getClient().sendResponse(new ModToolIssueHandledComposer(0));
        }
        this.updateTicketToMods(issue);
        this.removeTicket(issue);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasPendingTickets(int userId) {
        THashMap<Integer, ModToolIssue> tHashMap = this.tickets;
        synchronized (tHashMap) {
            for (Map.Entry<Integer, ModToolIssue> map : this.tickets.entrySet()) {
                if (map.getValue().senderId != userId) continue;
                return true;
            }
        }
        return false;
    }

    public int totalBans(int userId) {
        int total = 0;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as total FROM bans WHERE user_id = ?");){
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery();){
                if (set.next()) {
                    total = set.getInt("total");
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return total;
    }

    public TIntObjectMap<ModToolCategory> getCategory() {
        return this.category;
    }

    public ModToolCategory getCategory(int id) {
        return this.category.get(id);
    }

    public THashMap<String, THashSet<String>> getPresets() {
        return this.presets;
    }

    public THashMap<Integer, ModToolIssue> getTickets() {
        return this.tickets;
    }

    public ModToolIssue getTicket(int ticketId) {
        return this.tickets.get(ticketId);
    }

    public TIntObjectMap<CfhCategory> getCfhCategories() {
        return this.cfhCategories;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<ModToolIssue> openTicketsForHabbo(final Habbo habbo) {
        final ArrayList<ModToolIssue> issues = new ArrayList<ModToolIssue>();
        THashMap<Integer, ModToolIssue> tHashMap = this.tickets;
        synchronized (tHashMap) {
            this.tickets.forEachValue(new TObjectProcedure<ModToolIssue>(){
                final /* synthetic */ ModToolManager this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean execute(ModToolIssue object) {
                    if (object.senderId == habbo.getHabboInfo().getId() && object.state == ModToolTicketState.OPEN) {
                        issues.add(object);
                    }
                    return true;
                }
            });
        }
        return issues;
    }
}

