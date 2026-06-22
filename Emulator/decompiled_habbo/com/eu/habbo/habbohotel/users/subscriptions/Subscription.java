/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users.subscriptions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.subscriptions.SubscriptionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Subscription {
    public static final String HABBO_CLUB = "HABBO_CLUB";
    private final int id;
    private final int userId;
    private final String subscriptionType;
    private final int timestampStart;
    private int duration;
    private boolean active;

    public Subscription(Integer id, Integer userId, String subscriptionType, Integer timestampStart, Integer duration, Boolean active) {
        this.id = id;
        this.userId = userId;
        this.subscriptionType = subscriptionType;
        this.timestampStart = timestampStart;
        this.duration = duration;
        this.active = active;
    }

    public int getSubscriptionId() {
        return this.id;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getSubscriptionType() {
        return this.subscriptionType;
    }

    public int getDuration() {
        return this.duration;
    }

    public void addDuration(int amount) {
        this.duration += amount;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE `users_subscriptions` SET `duration` = ? WHERE `id` = ? LIMIT 1");){
            statement.setInt(1, this.duration);
            statement.setInt(2, this.id);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            SubscriptionManager.LOGGER.error("Caught SQL exception", e);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE `users_subscriptions` SET `active` = ? WHERE `id` = ? LIMIT 1");){
            statement.setInt(1, this.active ? 1 : 0);
            statement.setInt(2, this.id);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            SubscriptionManager.LOGGER.error("Caught SQL exception", e);
        }
    }

    public int getRemaining() {
        return this.timestampStart + this.duration - Emulator.getIntUnixTimestamp();
    }

    public int getTimestampStart() {
        return this.timestampStart;
    }

    public int getTimestampEnd() {
        return this.timestampStart + this.duration;
    }

    public boolean isActive() {
        return this.active;
    }

    public void onCreated() {
    }

    public void onExtended(int duration) {
    }

    public void onExpired() {
    }
}

