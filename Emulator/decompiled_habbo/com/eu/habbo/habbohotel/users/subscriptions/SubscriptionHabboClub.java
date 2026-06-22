/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users.subscriptions;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.Database;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.rooms.RoomManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboStats;
import com.eu.habbo.habbohotel.users.clothingvalidation.ClothingValidationManager;
import com.eu.habbo.habbohotel.users.subscriptions.HcPayDayLogEntry;
import com.eu.habbo.habbohotel.users.subscriptions.Subscription;
import com.eu.habbo.habbohotel.users.subscriptions.SubscriptionManager;
import com.eu.habbo.messages.outgoing.catalog.ClubCenterDataComposer;
import com.eu.habbo.messages.outgoing.generic.PickMonthlyClubGiftNotificationComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;
import com.eu.habbo.messages.outgoing.users.UpdateUserLookComposer;
import com.eu.habbo.messages.outgoing.users.UserClubComposer;
import com.eu.habbo.messages.outgoing.users.UserPermissionsComposer;
import gnu.trove.map.hash.THashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionHabboClub
extends Subscription {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionHabboClub.class);
    public static boolean HC_PAYDAY_ENABLED = false;
    public static int HC_PAYDAY_NEXT_DATE = Integer.MAX_VALUE;
    public static String HC_PAYDAY_INTERVAL = "";
    public static String HC_PAYDAY_QUERY = "";
    public static TreeMap<Integer, Integer> HC_PAYDAY_STREAK = new TreeMap();
    public static String HC_PAYDAY_CURRENCY = "";
    public static Double HC_PAYDAY_KICKBACK_PERCENTAGE = 0.1;
    public static String ACHIEVEMENT_NAME = "";
    public static boolean DISCOUNT_ENABLED = false;
    public static int DISCOUNT_DAYS_BEFORE_END = 7;
    public static boolean HC_PAYDAY_COINSSPENT_RESET_ON_EXPIRE = false;
    public static boolean isExecuting = false;

    public SubscriptionHabboClub(Integer id, Integer userId, String subscriptionType, Integer timestampStart, Integer duration, Boolean active) {
        super(id, userId, subscriptionType, timestampStart, duration, active);
    }

    @Override
    public void onCreated() {
        super.onCreated();
        HabboInfo habboInfo = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(this.getUserId());
        HabboStats stats = habboInfo.getHabboStats();
        stats.maxFriends = Messenger.MAXIMUM_FRIENDS_HC;
        stats.maxRooms = RoomManager.MAXIMUM_ROOMS_HC;
        stats.lastHCPayday = HC_PAYDAY_COINSSPENT_RESET_ON_EXPIRE ? Emulator.getIntUnixTimestamp() : HC_PAYDAY_NEXT_DATE - Emulator.timeStringToSeconds(HC_PAYDAY_INTERVAL);
        Emulator.getThreading().run(stats);
        SubscriptionHabboClub.progressAchievement(habboInfo);
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId());
        if (habbo != null && habbo.getClient() != null) {
            if (habbo.getHabboStats().getRemainingClubGifts() > 0) {
                habbo.getClient().sendResponse(new PickMonthlyClubGiftNotificationComposer(habbo.getHabboStats().getRemainingClubGifts()));
            }
            if (Emulator.getIntUnixTimestamp() - habbo.getHabboStats().hcMessageLastModified < 60) {
                Emulator.getThreading().run(() -> {
                    habbo.getClient().sendResponse(new UserClubComposer(habbo));
                    habbo.getClient().sendResponse(new UserPermissionsComposer(habbo));
                }, Emulator.getIntUnixTimestamp() - habbo.getHabboStats().hcMessageLastModified);
            } else {
                habbo.getClient().sendResponse(new UserClubComposer(habbo, "HABBO_CLUB", UserClubComposer.RESPONSE_TYPE_NORMAL));
                habbo.getClient().sendResponse(new UserPermissionsComposer(habbo));
            }
        }
    }

    @Override
    public void addDuration(int amount) {
        Habbo habbo;
        super.addDuration(amount);
        if (amount < 0 && (habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId())) != null && habbo.getClient() != null) {
            habbo.getClient().sendResponse(new UserClubComposer(habbo, "HABBO_CLUB", UserClubComposer.RESPONSE_TYPE_NORMAL));
            habbo.getClient().sendResponse(new UserPermissionsComposer(habbo));
        }
    }

    @Override
    public void onExtended(int duration) {
        super.onExtended(duration);
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId());
        if (habbo != null && habbo.getClient() != null) {
            habbo.getClient().sendResponse(new UserClubComposer(habbo, "HABBO_CLUB", UserClubComposer.RESPONSE_TYPE_NORMAL));
            habbo.getClient().sendResponse(new UserPermissionsComposer(habbo));
        }
    }

    @Override
    public void onExpired() {
        super.onExpired();
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId());
        HabboInfo habboInfo = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(this.getUserId());
        HabboStats stats = habboInfo.getHabboStats();
        stats.maxFriends = Messenger.MAXIMUM_FRIENDS;
        stats.maxRooms = RoomManager.MAXIMUM_ROOMS_USER;
        Emulator.getThreading().run(stats);
        if (habbo != null && ClothingValidationManager.VALIDATE_ON_HC_EXPIRE) {
            habboInfo.setLook(ClothingValidationManager.validateLook(habbo, habboInfo.getLook(), habboInfo.getGender().name()));
            Emulator.getThreading().run(habbo.getHabboInfo());
            if (habbo.getClient() != null) {
                habbo.getClient().sendResponse(new UpdateUserLookComposer(habbo));
            }
            if (habbo.getHabboInfo().getCurrentRoom() != null) {
                habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDataComposer(habbo).compose());
            }
        }
        if (habbo != null && habbo.getClient() != null) {
            habbo.getClient().sendResponse(new UserClubComposer(habbo, "HABBO_CLUB", UserClubComposer.RESPONSE_TYPE_NORMAL));
            habbo.getClient().sendResponse(new UserPermissionsComposer(habbo));
        }
    }

    public static ClubCenterDataComposer calculatePayday(HabboInfo habbo) {
        Subscription activeSub = null;
        Subscription firstEverSub = null;
        int currentHcStreak = 0;
        int totalCreditsSpent = 0;
        int creditRewardForStreakBonus = 0;
        int creditRewardForMonthlySpent = 0;
        int timeUntilPayday = 0;
        for (Subscription sub : habbo.getHabboStats().subscriptions) {
            if (!sub.getSubscriptionType().equalsIgnoreCase("HABBO_CLUB")) continue;
            if (firstEverSub == null || sub.getTimestampStart() < firstEverSub.getTimestampStart()) {
                firstEverSub = sub;
            }
            if (!sub.isActive()) continue;
            activeSub = sub;
        }
        if (HC_PAYDAY_ENABLED && activeSub != null) {
            currentHcStreak = (int)Math.floor((double)(Emulator.getIntUnixTimestamp() - activeSub.getTimestampStart()) / 86400.0);
            if (currentHcStreak < 1) {
                currentHcStreak = 0;
            }
            for (Map.Entry<Integer, Integer> set : HC_PAYDAY_STREAK.entrySet()) {
                if (currentHcStreak < set.getKey() || set.getValue() <= creditRewardForStreakBonus) continue;
                creditRewardForStreakBonus = set.getValue();
            }
            THashMap<String, Object> queryParams = new THashMap<String, Object>();
            queryParams.put("@user_id", habbo.getId());
            queryParams.put("@timestamp_start", habbo.getHabboStats().lastHCPayday);
            queryParams.put("@timestamp_end", HC_PAYDAY_NEXT_DATE);
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = Database.preparedStatementWithParams(connection, HC_PAYDAY_QUERY, queryParams);
                 ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    totalCreditsSpent = set.getInt("amount_spent");
                }
            }
            catch (SQLException e) {
                SubscriptionManager.LOGGER.error("Caught SQL exception", e);
            }
            creditRewardForMonthlySpent = (int)Math.floor((double)totalCreditsSpent * HC_PAYDAY_KICKBACK_PERCENTAGE);
            timeUntilPayday = (HC_PAYDAY_NEXT_DATE - Emulator.getIntUnixTimestamp()) / 60;
        }
        return new ClubCenterDataComposer(currentHcStreak, firstEverSub != null ? new SimpleDateFormat("dd-MM-yyyy").format(new Date((long)firstEverSub.getTimestampStart() * 1000L)) : "", HC_PAYDAY_KICKBACK_PERCENTAGE, 0, 0, totalCreditsSpent, creditRewardForStreakBonus, creditRewardForMonthlySpent, timeUntilPayday);
    }

    public static void executePayDay() {
        isExecuting = true;
        int timestampNow = Emulator.getIntUnixTimestamp();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM `users_subscriptions` WHERE subscription_type = 'HABBO_CLUB' AND `active` = 1 AND `timestamp_start` < ? AND (`timestamp_start` + `duration`) > ? GROUP BY user_id");){
            block45: {
                statement.setInt(1, timestampNow);
                statement.setInt(2, timestampNow);
                ResultSet set = statement.executeQuery();
                block36: while (true) {
                    while (set.next()) {
                        int userId = set.getInt("user_id");
                        try {
                            HabboInfo habboInfo = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(userId);
                            if (habboInfo == null) {
                                SubscriptionManager.LOGGER.error("HabboInfo is null for user #" + userId + ". Removing subscription.");
                                try {
                                    PreparedStatement removeStatement = connection.prepareStatement("DELETE FROM users_subscriptions WHERE user_id = ? AND subscription_type = ?");
                                    try {
                                        removeStatement.setInt(1, userId);
                                        removeStatement.setString(2, "HABBO_CLUB");
                                        removeStatement.executeUpdate();
                                        continue block36;
                                    }
                                    finally {
                                        if (removeStatement == null) continue block36;
                                        removeStatement.close();
                                        continue block36;
                                    }
                                }
                                catch (SQLException e) {
                                    SubscriptionManager.LOGGER.error("SQL exception when trying to remove subscription for user #" + userId, e);
                                    continue;
                                }
                            }
                            HabboStats stats = habboInfo.getHabboStats();
                            ClubCenterDataComposer calculated = SubscriptionHabboClub.calculatePayday(habboInfo);
                            int totalReward = calculated.creditRewardForMonthlySpent + calculated.creditRewardForStreakBonus;
                            if (totalReward > 0) {
                                boolean claimed = SubscriptionHabboClub.claimPayDay(Emulator.getGameEnvironment().getHabboManager().getHabbo(userId), totalReward, HC_PAYDAY_CURRENCY);
                                HcPayDayLogEntry le = new HcPayDayLogEntry(timestampNow, userId, calculated.currentHcStreak, calculated.totalCreditsSpent, calculated.creditRewardForMonthlySpent, calculated.creditRewardForStreakBonus, totalReward, HC_PAYDAY_CURRENCY, claimed);
                                Emulator.getThreading().run(le);
                            }
                            stats.lastHCPayday = timestampNow;
                            Emulator.getThreading().run(stats);
                            continue block36;
                        }
                        catch (Exception e) {
                            SubscriptionManager.LOGGER.error("Exception processing HC payday for user #" + userId, e);
                        }
                    }
                    break block45;
                    {
                        continue block36;
                        break;
                    }
                    break;
                }
                finally {
                    if (set != null) {
                        set.close();
                    }
                }
            }
            Date date = new Date((long)HC_PAYDAY_NEXT_DATE * 1000L);
            date = Emulator.modifyDate(date, HC_PAYDAY_INTERVAL);
            HC_PAYDAY_NEXT_DATE = (int)(date.getTime() / 1000L);
            try (PreparedStatement stm2 = connection.prepareStatement("UPDATE `emulator_settings` SET `value` = ? WHERE `key` = ?");){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                stm2.setString(1, sdf.format(date));
                stm2.setString(2, "subscriptions.hc.payday.next_date");
                stm2.execute();
            }
            stm2 = connection.prepareStatement("UPDATE users_settings SET last_hc_payday = ? WHERE user_id IN (SELECT user_id FROM `users_subscriptions` WHERE subscription_type = 'HABBO_CLUB' AND `active` = 1 AND `timestamp_start` < ? AND (`timestamp_start` + `duration`) > ? GROUP BY user_id)");
            try {
                stm2.setInt(1, timestampNow);
                stm2.setInt(2, timestampNow);
                stm2.setInt(3, timestampNow);
                stm2.execute();
            }
            finally {
                if (stm2 != null) {
                    stm2.close();
                }
            }
        }
        catch (SQLException e) {
            SubscriptionManager.LOGGER.error("Caught SQL exception", e);
        }
        isExecuting = false;
    }

    public static void processUnclaimed(Habbo habbo) {
        block31: {
            SubscriptionHabboClub.progressAchievement(habbo.getHabboInfo());
            if (habbo.getHabboStats().getRemainingClubGifts() > 0) {
                habbo.getClient().sendResponse(new PickMonthlyClubGiftNotificationComposer(habbo.getHabboStats().getRemainingClubGifts()));
            }
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM `logs_hc_payday` WHERE user_id = ? AND claimed = 0");){
                statement.setInt(1, habbo.getHabboInfo().getId());
                ResultSet set = statement.executeQuery();
                block24: while (true) {
                    while (set.next()) {
                        try {
                            String currency;
                            int logId = set.getInt("id");
                            int userId = set.getInt("user_id");
                            int totalPayout = set.getInt("total_payout");
                            if (!SubscriptionHabboClub.claimPayDay(habbo, totalPayout, currency = set.getString("currency"))) continue block24;
                            PreparedStatement stm2 = connection.prepareStatement("UPDATE logs_hc_payday SET claimed = 1 WHERE id = ?");
                            try {
                                stm2.setInt(1, logId);
                                stm2.execute();
                                continue block24;
                            }
                            finally {
                                if (stm2 != null) {
                                    stm2.close();
                                }
                                continue block24;
                            }
                        }
                        catch (Exception e) {
                            SubscriptionManager.LOGGER.error("Exception processing HC payday for user #{}", (Object)set.getInt("user_id"), (Object)e);
                        }
                    }
                    break block31;
                    {
                        continue block24;
                        break;
                    }
                    break;
                }
                finally {
                    if (set != null) {
                        set.close();
                    }
                }
            }
            catch (SQLException e) {
                SubscriptionManager.LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    public static void processClubBadge(Habbo habbo) {
        SubscriptionHabboClub.progressAchievement(habbo.getHabboInfo());
    }

    public static boolean claimPayDay(Habbo habbo, int amount, String currency) {
        if (habbo == null) {
            return false;
        }
        switch (currency.toLowerCase()) {
            case "credits": 
            case "coins": 
            case "credit": 
            case "coin": {
                habbo.getClient().getHabbo().giveCredits(amount);
                break;
            }
            case "diamonds": 
            case "diamond": {
                int pointCurrency = 5;
                habbo.getClient().getHabbo().givePoints(pointCurrency, amount);
                break;
            }
            case "duckets": 
            case "ducket": 
            case "pixels": 
            case "pixel": {
                int pointCurrency = 0;
                habbo.getClient().getHabbo().givePoints(pointCurrency, amount);
                break;
            }
            default: {
                int pointCurrency = -1;
                try {
                    pointCurrency = Integer.parseInt(currency);
                }
                catch (NumberFormatException ex) {
                    LOGGER.error("Couldn't convert the type point currency {} on HC PayDay. The number must be a integer and positive.", (Object)pointCurrency);
                }
                if (pointCurrency < 0) break;
                habbo.getClient().getHabbo().givePoints(pointCurrency, amount);
            }
        }
        habbo.alert(Emulator.getTexts().getValue("subscriptions.hc.payday.message", "Woohoo HC Payday has arrived! You have received %amount% credits to your purse. Enjoy!").replace("%amount%", "" + amount));
        return true;
    }

    private static void progressAchievement(HabboInfo habboInfo) {
        HabboStats stats = habboInfo.getHabboStats();
        Achievement achievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement(ACHIEVEMENT_NAME);
        if (achievement != null) {
            int progressToSet;
            int toIncrease;
            int currentProgress = stats.getAchievementProgress(achievement);
            if (currentProgress == -1) {
                currentProgress = 0;
            }
            if ((toIncrease = Math.max((progressToSet = (int)Math.ceil((double)stats.getPastTimeAsClub() / 2678400.0)) - currentProgress, 0)) > 0) {
                AchievementManager.progressAchievement(habboInfo.getId(), achievement, toIncrease);
            }
        }
    }
}

