/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.campaign.calendar;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.campaign.calendar.CalendarCampaign;
import com.eu.habbo.habbohotel.campaign.calendar.CalendarRewardClaimed;
import com.eu.habbo.habbohotel.campaign.calendar.CalendarRewardObject;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.events.calendar.AdventCalendarProductComposer;
import com.eu.habbo.plugin.events.users.calendar.UserClaimRewardEvent;
import gnu.trove.map.hash.THashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarCampaign.class);
    private static final Map<Integer, CalendarCampaign> calendarCampaigns = new THashMap<Integer, CalendarCampaign>();
    public static double HC_MODIFIER;

    public CalendarManager() {
        long millis = System.currentTimeMillis();
        this.reload();
        LOGGER.info("Calendar Manager -> Loaded! ({} MS)", (Object)(System.currentTimeMillis() - millis));
    }

    public void dispose() {
        calendarCampaigns.clear();
    }

    public boolean reload() {
        ResultSet set;
        PreparedStatement statement;
        Connection connection;
        this.dispose();
        try {
            connection = Emulator.getDatabase().getDataSource().getConnection();
            try {
                statement = connection.prepareStatement("SELECT * FROM calendar_campaigns WHERE enabled = 1");
                try {
                    set = statement.executeQuery();
                    try {
                        while (set.next()) {
                            calendarCampaigns.put(set.getInt("id"), new CalendarCampaign(set));
                        }
                    }
                    finally {
                        if (set != null) {
                            set.close();
                        }
                    }
                }
                finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            }
            finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return false;
        }
        try {
            connection = Emulator.getDatabase().getDataSource().getConnection();
            try {
                statement = connection.prepareStatement("SELECT * FROM calendar_rewards");
                try {
                    set = statement.executeQuery();
                    try {
                        while (set.next()) {
                            CalendarCampaign campaign = calendarCampaigns.get(set.getInt("campaign_id"));
                            if (campaign == null) continue;
                            campaign.addReward(new CalendarRewardObject(set));
                        }
                    }
                    finally {
                        if (set != null) {
                            set.close();
                        }
                    }
                }
                finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            }
            finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return false;
        }
        HC_MODIFIER = Emulator.getConfig().getDouble("hotel.calendar.pixels.hc_modifier", 2.0);
        return true;
    }

    public void addCampaign(CalendarCampaign campaign) {
        block22: {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO calendar_campaigns ( name, image, start_timestamp, total_days, lock_expired) VALUES (?, ?, ?, ? , ?)", 1);){
                statement.setString(1, campaign.getName());
                statement.setString(2, campaign.getImage());
                statement.setInt(3, campaign.getStartTimestamp());
                statement.setInt(4, campaign.getTotalDays());
                statement.setBoolean(5, campaign.getLockExpired());
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating calendar campaign failed, no rows affected.");
                }
                try (ResultSet generatedKeys = statement.getGeneratedKeys();){
                    if (generatedKeys.next()) {
                        campaign.setId(generatedKeys.getInt(1));
                        break block22;
                    }
                    throw new SQLException("Creating calendar campaign failed, no ID found.");
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
        calendarCampaigns.put(campaign.getId(), campaign);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public boolean deleteCampaign(CalendarCampaign campaign) {
        calendarCampaigns.remove(campaign.getId());
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();){
            boolean bl;
            block14: {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM calendar_campaigns WHERE id = ? LIMIT 1");
                try {
                    statement.setInt(1, campaign.getId());
                    bl = statement.execute();
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

    public CalendarCampaign getCalendarCampaign(String campaignName) {
        return calendarCampaigns.values().stream().filter(cc -> Objects.equals(cc.getName(), campaignName)).findFirst().orElse(null);
    }

    public Map<Integer, CalendarCampaign> getCalendarCampaigns() {
        return calendarCampaigns;
    }

    public void claimCalendarReward(Habbo habbo, String campaignName, int day, boolean force) {
        CalendarCampaign campaign = calendarCampaigns.values().stream().filter(cc -> Objects.equals(cc.getName(), campaignName)).findFirst().orElse(null);
        if (campaign == null) {
            return;
        }
        if (habbo.getHabboStats().calendarRewardsClaimed.stream().noneMatch(claimed -> claimed.getCampaignId() == campaign.getId() && claimed.getDay() == day)) {
            int diff;
            Set<Integer> keys = campaign.getRewards().keySet();
            THashMap rewards = new THashMap();
            if (keys.isEmpty()) {
                return;
            }
            keys.forEach(key -> rewards.put(rewards.size() + 1, key));
            int rand = Emulator.getRandom().nextInt(rewards.size() - 1 + 1) + 1;
            int random = (Integer)rewards.get(rand);
            CalendarRewardObject object = campaign.getRewards().get(random);
            if (object == null) {
                return;
            }
            int daysBetween = (int)ChronoUnit.DAYS.between(new Timestamp((long)campaign.getStartTimestamp().intValue() * 1000L).toInstant(), new Date().toInstant());
            if (daysBetween >= 0 && daysBetween <= campaign.getTotalDays() && (((diff = daysBetween - day) <= 2 || !campaign.getLockExpired()) && diff >= 0 || force && habbo.hasPermission("acc_calendar_force"))) {
                if (Emulator.getPluginManager().fireEvent(new UserClaimRewardEvent(habbo, campaign, day, object, force)).isCancelled()) {
                    return;
                }
                habbo.getHabboStats().calendarRewardsClaimed.add(new CalendarRewardClaimed(habbo.getHabboInfo().getId(), campaign.getId(), day, object.getId(), new Timestamp(System.currentTimeMillis())));
                habbo.getClient().sendResponse(new AdventCalendarProductComposer(true, object, habbo));
                object.give(habbo);
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO calendar_rewards_claimed (user_id, campaign_id, day, reward_id, timestamp) VALUES (?, ?, ?, ?, ?)");){
                    statement.setInt(1, habbo.getHabboInfo().getId());
                    statement.setInt(2, campaign.getId());
                    statement.setInt(3, day);
                    statement.setInt(4, object.getId());
                    statement.setInt(5, Emulator.getIntUnixTimestamp());
                    statement.execute();
                }
                catch (SQLException e) {
                    LOGGER.error("Caught SQL exception", e);
                }
            }
        }
    }
}

