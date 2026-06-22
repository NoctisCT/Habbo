/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.achievements;

import com.eu.habbo.habbohotel.achievements.AchievementCategories;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import gnu.trove.map.hash.THashMap;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Achievement {
    public final int id;
    public final String name;
    public final AchievementCategories category;
    public final THashMap<Integer, AchievementLevel> levels = new THashMap();

    public Achievement(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.category = AchievementCategories.valueOf(set.getString("category").toUpperCase());
        this.addLevel(new AchievementLevel(set));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addLevel(AchievementLevel level) {
        THashMap<Integer, AchievementLevel> tHashMap = this.levels;
        synchronized (tHashMap) {
            this.levels.put(level.level, level);
        }
    }

    public AchievementLevel getLevelForProgress(int progress) {
        AchievementLevel l = null;
        if (progress > 0) {
            for (AchievementLevel level : this.levels.values()) {
                if (progress < level.progress || l != null && l.level > level.level) continue;
                l = level;
            }
        }
        return l;
    }

    public AchievementLevel getNextLevel(int currentLevel) {
        Object l = null;
        for (AchievementLevel level : this.levels.values()) {
            if (level.level != currentLevel + 1) continue;
            return level;
        }
        return null;
    }

    public AchievementLevel firstLevel() {
        return this.levels.get(1);
    }

    public void clearLevels() {
        this.levels.clear();
    }
}

