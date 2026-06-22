/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Rank;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import gnu.trove.set.hash.THashSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BadgesComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(BadgesComponent.class);
    private final THashSet<HabboBadge> badges = new THashSet();

    public BadgesComponent(Habbo habbo) {
        this.badges.addAll(BadgesComponent.loadBadges(habbo));
    }

    private static THashSet<HabboBadge> loadBadges(Habbo habbo) {
        THashSet<HabboBadge> badgesList = new THashSet<HabboBadge>();
        Set<String> staffBadges = Emulator.getGameEnvironment().getPermissionsManager().getStaffBadges();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_badges WHERE user_id = ?");){
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    HabboBadge badge = new HabboBadge(set, habbo);
                    if (staffBadges.contains(badge.getCode())) {
                        boolean delete = true;
                        for (Rank rank : Emulator.getGameEnvironment().getPermissionsManager().getRanksByBadgeCode(badge.getCode())) {
                            if (rank.getId() != habbo.getHabboInfo().getRank().getId()) continue;
                            delete = false;
                            break;
                        }
                        if (delete) {
                            BadgesComponent.deleteBadge(habbo.getHabboInfo().getId(), badge.getCode());
                            continue;
                        }
                    }
                    badgesList.add(badge);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return badgesList;
    }

    public static void resetSlots(Habbo habbo) {
        for (HabboBadge badge : habbo.getInventory().getBadgesComponent().getBadges()) {
            if (badge.getSlot() == 0) continue;
            badge.setSlot(0);
            badge.needsUpdate(true);
            Emulator.getThreading().run(badge);
        }
    }

    public static ArrayList<HabboBadge> getBadgesOfflineHabbo(int userId) {
        ArrayList<HabboBadge> badgesList = new ArrayList<HabboBadge>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_badges WHERE slot_id > 0 AND user_id = ? ORDER BY slot_id ASC");){
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    badgesList.add(new HabboBadge(set, null));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return badgesList;
    }

    public static HabboBadge createBadge(String code, Habbo habbo) {
        HabboBadge badge = new HabboBadge(0, code, 0, habbo);
        badge.run();
        habbo.getInventory().getBadgesComponent().addBadge(badge);
        return badge;
    }

    public static void deleteBadge(int userId, String badge) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE users_badges FROM users_badges WHERE user_id = ? AND badge_code LIKE ?");){
            statement.setInt(1, userId);
            statement.setString(2, badge);
            statement.execute();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<HabboBadge> getWearingBadges() {
        THashSet<HabboBadge> tHashSet = this.badges;
        synchronized (tHashSet) {
            ArrayList<HabboBadge> badgesList = new ArrayList<HabboBadge>();
            for (HabboBadge badge : this.badges) {
                if (badge.getSlot() == 0) continue;
                badgesList.add(badge);
            }
            badgesList.sort(new Comparator<HabboBadge>(){

                @Override
                public int compare(HabboBadge o1, HabboBadge o2) {
                    return o1.getSlot() - o2.getSlot();
                }
            });
            return badgesList;
        }
    }

    public THashSet<HabboBadge> getBadges() {
        return this.badges;
    }

    public boolean hasBadge(String badge) {
        return this.getBadge(badge) != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HabboBadge getBadge(String badgeCode) {
        THashSet<HabboBadge> tHashSet = this.badges;
        synchronized (tHashSet) {
            for (HabboBadge badge : this.badges) {
                if (!badge.getCode().equalsIgnoreCase(badgeCode)) continue;
                return badge;
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addBadge(HabboBadge badge) {
        THashSet<HabboBadge> tHashSet = this.badges;
        synchronized (tHashSet) {
            this.badges.add(badge);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HabboBadge removeBadge(String badge) {
        THashSet<HabboBadge> tHashSet = this.badges;
        synchronized (tHashSet) {
            for (HabboBadge b : this.badges) {
                if (!b.getCode().equalsIgnoreCase(badge)) continue;
                this.badges.remove(b);
                return b;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeBadge(HabboBadge badge) {
        THashSet<HabboBadge> tHashSet = this.badges;
        synchronized (tHashSet) {
            this.badges.remove(badge);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        THashSet<HabboBadge> tHashSet = this.badges;
        synchronized (tHashSet) {
            this.badges.clear();
        }
    }
}

